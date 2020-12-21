package com.github.server.protocol;

import com.github.server.protocol.base.ChatHeader;
import com.github.server.protocol.base.ChatMessageHeader;
import com.github.server.protocol.headers.*;
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

    public ChatAPI(Socket socket) throws IOException {
        this.socket = socket;
        in = new BufferedReader(new InputStreamReader(new DataInputStream(socket.getInputStream())));
        out = new PrintWriter(new OutputStreamWriter(new DataOutputStream(socket.getOutputStream())));
        gson = new Gson();
    }

    public ChatPacket receive() throws IOException {
        String request = in.readLine();
        System.out.println(request);
        return gson.fromJson(request, ChatPacket.class);
    }

    public void send(ChatPacket packet) {
        String request = gson.toJson(packet);
        out.println(request);
        out.flush();
    }

    public void disconnect() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
