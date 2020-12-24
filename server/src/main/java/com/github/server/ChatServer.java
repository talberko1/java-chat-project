package com.github.server;

import com.github.server.base.BaseDBConnector;
import com.github.server.base.BaseServer;
import com.github.server.protocol.ChatCommand;
import com.github.server.protocol.ChatPacket;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.IOException;
import java.net.Socket;
import java.util.*;

import static com.github.server.ChatConstants.*;

public class ChatServer extends BaseServer implements IRequestHandler {


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
        synchronized (this) {
            System.out.println(request.toString());
            JsonObject header = request.getHeader();
            JsonElement commandElement = header.get(HEADER_COMMAND_PROPERTY);
            if (commandElement != null) {
                ChatCommand command = ChatCommand.valueOf(commandElement.getAsString());
                switch (command) {
                    case REGISTER:
                        handleRegisterCommand(user, request);
                        break;
                    case LOGIN:
                        handleLoginCommand(user, request);
                        break;
                    case LOGOUT:
                        handleLogoutCommand(user);
                        break;
                    case UNICAST:
                        handleUnicastCommand(user, request);
                        break;
                    case MULTICAST:
                        handleMulticastCommand(user, request);
                        break;
                    case BROADCAST:
                        handleBroadcastCommand(user, request);
                        break;
                    default:
                        reply(user, ChatCommand.UNKNOWN, STATUS_ERROR, UNKNOWN_COMMAND_RESPONSE);
                        break;
                }
            }
        }
    }

    public void handleRegisterCommand(UserThread user, ChatPacket request) {
        JsonObject requestPayload = request.getPayload();
        String username = requestPayload.get(PAYLOAD_USERNAME_PROPERTY).getAsString();
        String password = requestPayload.get(PAYLOAD_PASSWORD_PROPERTY).getAsString();
        if (!userDBConnector.containsUser(username)) {
            userDBConnector.addUser(username, DigestUtils.sha256Hex(password));
            onlineUsers.put(user, username);

            reply(user, ChatCommand.REGISTER, STATUS_OK, REGISTER_SUCCESS_MESSAGE);

            JsonObject payload = generatePayload(username + USER_JOINED);
            JsonObject header = generateHeader(ChatCommand.BROADCAST, payload.size());
            header.addProperty(HEADER_PAYLOAD_TYPE_PROPERTY, PAYLOAD_TEXT);

            broadcast(user, header, payload);
        } else {
            reply(user, ChatCommand.REGISTER, STATUS_ERROR, USERNAME_TAKEN_MESSAGE);
        }
    }

    public void handleLoginCommand(UserThread user, ChatPacket request) {
        JsonObject requestPayload = request.getPayload();
        String username = requestPayload.get(PAYLOAD_USERNAME_PROPERTY).getAsString();
        String password = requestPayload.get(PAYLOAD_PASSWORD_PROPERTY).getAsString();
        if (userDBConnector.containsUser(username)) {
            if (userDBConnector.passwordMatches(username, DigestUtils.sha256Hex(password))) {
                onlineUsers.put(user, username);
                reply(user, ChatCommand.LOGIN, STATUS_OK, LOGIN_SUCCESS_MESSAGE);

                JsonObject payload = generatePayload(username + USER_JOINED);
                JsonObject header = generateHeader(ChatCommand.BROADCAST, payload.size());
                header.addProperty(HEADER_PAYLOAD_TYPE_PROPERTY, PAYLOAD_TEXT);

                broadcast(user, header, payload);
            } else {
                reply(user, ChatCommand.LOGIN, STATUS_ERROR, INCORRECT_PASSWORD_MESSAGE);
            }
        } else {
            reply(user, ChatCommand.LOGIN, STATUS_ERROR, USER_NOT_FOUND_MESSAGE);
        }
    }

    public void handleLogoutCommand(UserThread user) {
        reply(user, ChatCommand.LOGOUT, STATUS_OK, LOGOUT_RESPONSE);
        String username = onlineUsers.get(user);

        JsonObject payload = generatePayload(username + USER_LEFT);
        JsonObject header = generateHeader(ChatCommand.BROADCAST, payload.size());
        header.addProperty(HEADER_PAYLOAD_TYPE_PROPERTY, PAYLOAD_TEXT);

        broadcast(user, header, payload);
        onlineUsers.remove(user);
        user.disconnect();
    }

    public void handleUnicastCommand(UserThread user, ChatPacket request) {
        JsonObject requestHeader = request.getHeader();
        JsonElement targetElement = requestHeader.get(HEADER_TO_PROPERTY);
        if (targetElement != null) {
            String targetName = targetElement.getAsString();
            if (unicast(user, targetName, request.getPayload())) {
                reply(user, ChatCommand.UNICAST, STATUS_OK, String.format(DIRECT_SUCCESS_MESSAGE, targetName));
            }
            else {
                reply(user, ChatCommand.UNICAST, STATUS_ERROR, String.format(UNICAST_USER_NOT_FOUND, targetName));
            }
        } else {
            reply(user, ChatCommand.UNICAST, STATUS_ERROR, HEADER_TO_PROPERTY_MISSING);
        }
    }

    public void handleMulticastCommand(UserThread user, ChatPacket request) {
        JsonObject header = request.getHeader();
        JsonElement targetsElement = header.get(HEADER_TO_PROPERTY);
        if (targetsElement != null) {
            JsonArray targets = targetsElement.getAsJsonArray();
            for (JsonElement targetElement : targets) {
                String targetName = targetElement.getAsString();
                if (unicast(user, targetName, request.getPayload())) {
                    reply(user, ChatCommand.MULTICAST, STATUS_OK, String.format(DIRECT_SUCCESS_MESSAGE, targetName));
                }
                else {
                    reply(user, ChatCommand.MULTICAST, STATUS_ERROR, String.format(UNICAST_USER_NOT_FOUND, targetName));
                }
            }
        } else {
            reply(user, ChatCommand.MULTICAST, STATUS_ERROR, HEADER_TO_PROPERTY_MISSING);
        }

    }

    public void handleBroadcastCommand(UserThread sender, ChatPacket request) {
        String senderName = onlineUsers.get(sender);

        JsonObject payload = request.getPayload();
        JsonObject header = generateHeader(ChatCommand.BROADCAST, payload.size());
        header.addProperty(HEADER_FROM_PROPERTY, senderName);

        broadcast(sender, header, payload);

        reply(sender, ChatCommand.BROADCAST, STATUS_OK, BROADCAST_SUCCESS_MESSAGE);
    }

    public void reply(UserThread user, ChatCommand command, String status, String message) {
        JsonObject payload = generatePayload(message);
        JsonObject header = generateHeader(command, payload.size());
        header.addProperty(HEADER_STATUS_PROPERTY, status);
        send(user, header, payload);
    }

    public JsonObject generatePayload(String message) {
        JsonObject payload = new JsonObject();
        payload.addProperty(PAYLOAD_DATA_PROPERTY, message);
        return payload;
    }

    public JsonObject generateHeader(ChatCommand command, int payloadLength) {
        JsonObject header = new JsonObject();
        header.addProperty(HEADER_COMMAND_PROPERTY, command.name());
        header.addProperty(HEADER_PAYLOAD_LENGTH_PROPERTY, payloadLength);
        return header;
    }

    public boolean unicast(UserThread user, String targetName, JsonObject payload) {
        String senderName = onlineUsers.get(user);
        for (UserThread target : onlineUsers.keySet()) {
            if (onlineUsers.get(target).equals(targetName)) {
                JsonObject header = generateHeader(ChatCommand.UNICAST, payload.size());
                header.addProperty(HEADER_FROM_PROPERTY, senderName);
                send(target, header, payload);
                return true;
            }
        }
        return false;
    }

    public void broadcast(UserThread ignored, JsonObject header, JsonObject payload) {
        for (UserThread other : onlineUsers.keySet()) {
            if (ignored != other) {
                send(other, header, payload);
            }
        }
    }

    public void send(UserThread user, JsonObject header, JsonObject payload) {
        user.send(header, payload);
    }
}
