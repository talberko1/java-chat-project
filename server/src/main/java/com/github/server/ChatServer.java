package com.github.server;

import com.github.server.protocol.ChatAPI;
import com.github.server.protocol.ChatPacket;
import com.github.server.protocol.base.ChatCommand;
import com.github.server.protocol.base.ChatHeader;
import com.github.server.protocol.headers.*;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ChatServer extends BaseServer implements IRequestHandler {
    private static final String SPACE = " ";
    private static final String UNKNOWN_COMMAND_RESPONSE = "unknown command";
    private static final String TARGET_NOT_FOUND_MESSAGE = "target user not found";
    private static final String REGISTER_SUCCESS_MESSAGE = "successfully registered";
    private static final String LOGIN_SUCCESS_MESSAGE = "successfully logged in";
    private static final String USERNAME_TAKEN_MESSAGE = "username already taken, choose another";
    private static final String USER_NOT_FOUND_MESSAGE = "the user was not found";
    private static final String INCORRECT_PASSWORD_MESSAGE = "the password you have entered was incorrect";
    private static final String USER_JOINED = SPACE + "has joined the chat!";
    private static final String USER_LEFT = SPACE + "has left the chat!";

    private static final String FAILURE_STATUS = "ERROR";
    private static final String SUCCESS_STATUS = "OK";

    private static final String NEW_CONNECTION = "[+] new connection";
    public static final String LOGOUT_RESPONSE = "BYE";
    public static final String DIRECT_SUCCESS_MESSAGE = "Successfully sent to ";
    public static final String USER_MISMATCH = "user mismatch";
    public static final String BROADCAST_SUCCESS_MESSAGE = "Successfully sent broadcast message";

    private final Map<UserThread, String> onlineUsers = new HashMap<UserThread, String>();
    private final BaseDBConnector userDBConnector;

    public ChatServer(String serverIp, int port, BaseDBConnector databaseConnector) throws IOException {
        super(serverIp, port);
        this.userDBConnector = databaseConnector;
    }

    @Override
    public void handleClient(Socket client) throws IOException {
        UserThread userThread = new UserThread(this, client);
        userThread.start();
        System.out.println(NEW_CONNECTION);
    }

    @Override
    public void onServerError(IOException e) {
        System.err.println(e.getMessage());
        stop();
    }

    //Synchronized!!!
    public void handleRequest(UserThread user, ChatPacket request) {
        System.out.println("request: " + request);
        ChatHeader header = request.getHeader();
        ChatCommand command = header.getCommand();
        String data = request.getData();
        switch (command) {
            case REGISTER:
                RegisterHeader registerHeader = (RegisterHeader) header;
                handleRegister(user, registerHeader.getUsername(), registerHeader.getPassword());
                break;
            case LOGIN:
                LoginHeader loginHeader = (LoginHeader) header;
                handleLogin(user, loginHeader.getUsername(), loginHeader.getPassword());
                break;
            case LOGOUT:
                handleLogout(user);
                break;
            case UNICAST:
                handleUnicast(user, (UnicastHeader) header, data);
                break;
            case MULTICAST:
                handleMulticast(user, (MulticastHeader) header, data);
                break;
            case BROADCAST:
                handleBroadcast(user, (BroadcastHeader) header, data);
                break;
            default:
                respond(user, new ResponseHeader(ChatCommand.UNKNOWN, FAILURE_STATUS), UNKNOWN_COMMAND_RESPONSE);
                break;
        }
    }

    public void handleRegister(UserThread user, String username, String password) {
        if (!userDBConnector.containsUser(username)) {
            userDBConnector.addUser(username, DigestUtils.sha256Hex(password));
            onlineUsers.put(user, username);
            respond(user, new ResponseHeader(ChatCommand.REGISTER, SUCCESS_STATUS), REGISTER_SUCCESS_MESSAGE);
            handleBroadcast(user, new BroadcastHeader(username, ChatAPI.CONTENT_TEXT), username + USER_JOINED);
        } else {
            respond(user, new ResponseHeader(ChatCommand.REGISTER, FAILURE_STATUS), USERNAME_TAKEN_MESSAGE);
        }
    }

    public void handleLogin(UserThread user, String username, String password) {
        if (userDBConnector.containsUser(username)) {
            if (userDBConnector.passwordMatches(username, DigestUtils.sha256Hex(password))) {
                onlineUsers.put(user, username);
                respond(user, new ResponseHeader(ChatCommand.LOGIN, SUCCESS_STATUS), LOGIN_SUCCESS_MESSAGE);
                handleBroadcast(user, new BroadcastHeader(username, ChatAPI.CONTENT_TEXT), username + USER_JOINED);
            } else {
                respond(user, new ResponseHeader(ChatCommand.LOGIN, FAILURE_STATUS), INCORRECT_PASSWORD_MESSAGE);
            }
        } else {
            respond(user, new ResponseHeader(ChatCommand.LOGIN, FAILURE_STATUS), USER_NOT_FOUND_MESSAGE);
        }
    }

    public void handleLogout(UserThread user) {
        respond(user, new ResponseHeader(ChatCommand.LOGOUT, SUCCESS_STATUS), LOGOUT_RESPONSE);
        String username = onlineUsers.get(user);
        handleBroadcast(user, new BroadcastHeader(username, ChatAPI.CONTENT_TEXT), username + USER_LEFT);
        onlineUsers.remove(user);
        user.disconnect();
    }

    public void handleUnicast(UserThread user, UnicastHeader header, String data) {
        String to = header.getTo();
        String from = header.getFrom();
        if (from.equals(onlineUsers.get(user))) {
            for (UserThread target : onlineUsers.keySet()) {
                String username = onlineUsers.get(target);
                if (to.equals(username)) {
                    respond(user, header, data);
                    respond(target, new ResponseHeader(ChatCommand.UNICAST, SUCCESS_STATUS),
                            DIRECT_SUCCESS_MESSAGE + username);
                    return;
                }
            }
            respond(user, new ResponseHeader(ChatCommand.UNICAST, FAILURE_STATUS), TARGET_NOT_FOUND_MESSAGE);
        } else {
            respond(user, new ResponseHeader(ChatCommand.UNICAST, FAILURE_STATUS), USER_MISMATCH);
        }
    }

    public void handleMulticast(UserThread user, MulticastHeader header, String data) {
        String[] to = header.getTo();
        String from = header.getFrom();
        if (from.equals(onlineUsers.get(user))) {
            for (String toUser : to) {
                for (UserThread target : onlineUsers.keySet()) {
                    String username = onlineUsers.get(target);
                    if (toUser.equals(username)) {
                        respond(user, header, data);
                        respond(target, new ResponseHeader(ChatCommand.MULTICAST, SUCCESS_STATUS),
                                DIRECT_SUCCESS_MESSAGE + username);
                        break;
                    }
                }
                respond(user, new ResponseHeader(ChatCommand.UNICAST, FAILURE_STATUS), TARGET_NOT_FOUND_MESSAGE);
            }
        } else {
            respond(user, new ResponseHeader(ChatCommand.MULTICAST, FAILURE_STATUS), USER_MISMATCH);
        }
    }

    public void handleBroadcast(UserThread sender, BroadcastHeader header, String data) {
        String from = onlineUsers.get(sender);
        if (from.equals(header.getFrom())) {
            for (UserThread other : onlineUsers.keySet()) {
                if (sender != other) {
                    respond(other, header, data);
                }
            }
            respond(sender, new ResponseHeader(ChatCommand.BROADCAST, SUCCESS_STATUS), BROADCAST_SUCCESS_MESSAGE);
        } else {
            respond(sender, new ResponseHeader(ChatCommand.BROADCAST, FAILURE_STATUS), USER_MISMATCH);
        }
    }

    public void respond(UserThread user, ChatHeader header, String data) {
        user.getApi().send(new ChatPacket(header, data));
    }
}
