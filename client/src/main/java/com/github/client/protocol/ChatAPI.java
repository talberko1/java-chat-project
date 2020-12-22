package com.github.client.protocol;

import com.github.client.protocol.base.ChatHeader;
import com.github.client.protocol.base.ChatMessageHeader;
import com.github.client.protocol.headers.*;
import com.google.gson.Gson;

import java.io.*;
import java.net.Socket;

public class ChatAPI {
    public static final String CONTENT_TEXT = "text";
    public static final String CONTENT_IMAGE = "image";
    private final Socket socket;
    private final BufferedReader in;
    private final PrintWriter out;
    private final Gson gson;

    public ChatAPI(String serverIp, int port) throws IOException {
        socket = new Socket(serverIp, port);
        in = new BufferedReader(new InputStreamReader(new DataInputStream(socket.getInputStream())));
        out = new PrintWriter(new OutputStreamWriter(new DataOutputStream(socket.getOutputStream())));
        gson = new Gson();
    }

    public ChatPacket receive() throws IOException {
        return gson.fromJson(in.readLine(), ChatPacket.class);
    }

    private void send(ChatPacket packet) {
        String request = gson.toJson(packet);
        out.println(request);
        out.flush();
    }

    public void register(String username, String password) {
        ChatHeader header = new RegisterHeader(username, password);
        ChatPacket packet = new ChatPacket(header, "");
        send(packet);
    }

    public void login(String username, String password) {
        ChatHeader header = new LoginHeader(username, password);
        ChatPacket packet = new ChatPacket(header, "");
        send(packet);
    }

    public void logout() {
        ChatHeader header = new LogoutHeader();
        ChatPacket packet = new ChatPacket(header, "");
        send(packet);
    }

    public void unicast(String from, String contentType, String to, String data) {
        ChatMessageHeader header = new UnicastHeader(from, contentType, to);
        ChatPacket packet = new ChatPacket(header, data);
        send(packet);
    }

    public void multicast(String from, String contentType, String[] to, String data) {
        ChatMessageHeader header = new MulticastHeader(from, contentType, to);
        ChatPacket packet = new ChatPacket(header, data);
        send(packet);
    }

    public void broadcast(String from, String contentType, String data) {
        ChatMessageHeader header = new BroadcastHeader(from, contentType);
        ChatPacket packet = new ChatPacket(header, data);
        send(packet);
    }

    public void disconnect() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
