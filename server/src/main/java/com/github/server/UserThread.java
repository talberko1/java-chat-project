package com.github.server;

import com.github.server.protocol.ChatPacket;
import com.google.gson.Gson;

import java.io.*;
import java.net.Socket;

public class UserThread extends Thread {
    private static final Gson gson = new Gson();
    private final IRequestHandler requestHandler;
    private final Socket socket;
    private final BufferedReader in;
    private final PrintWriter out;

    public UserThread(IRequestHandler requestHandlerInterface, Socket socket) throws IOException {
        requestHandler = requestHandlerInterface;
        this.socket = socket;
        this.in = new BufferedReader(new InputStreamReader(new DataInputStream(socket.getInputStream())));
        this.out = new PrintWriter(new OutputStreamWriter(new DataOutputStream(socket.getOutputStream())));
    }

    @Override
    public void run() {
        try {
            ChatPacket request = receive();
            requestHandler.handleRequest(this, request);

        } catch (IOException e) {
            disconnect();
        }
    }

    public void disconnect() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

}
