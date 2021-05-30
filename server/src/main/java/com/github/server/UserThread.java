package com.github.server;

import com.github.server.protocol.ChatPacket;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.*;
import java.net.Socket;

public class UserThread extends Thread {
    private static final Gson gson = new Gson();
    private final IRequestHandler requestHandler;
    private final Socket socket;
    private final BufferedReader in;
    private final PrintWriter out;
    private boolean alive;

    public UserThread(IRequestHandler requestHandler, Socket socket) throws IOException {
        this.requestHandler = requestHandler;
        this.socket = socket;
        this.in = new BufferedReader(new InputStreamReader(new DataInputStream(socket.getInputStream())));
        this.out = new PrintWriter(new OutputStreamWriter(new DataOutputStream(socket.getOutputStream())));
        this.alive = true;
    }

    @Override
    public void run() {
        while (this.alive) {
            try {
                ChatPacket request = receive();
                if (request == null) disconnect();
                requestHandler.handleRequest(this, request);

            } catch (IOException e) {
                disconnect();
            }
        }
    }

    public void disconnect() {
        try {
            this.alive = false;
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ChatPacket receive() throws IOException {
        String request = in.readLine();
        return gson.fromJson(request, ChatPacket.class);
    }

    public void send(JsonObject header, JsonObject payload) {
        String response = gson.toJson(new ChatPacket(header, payload));
        System.out.println(response);
        out.println(response);
        out.flush();
    }

}
