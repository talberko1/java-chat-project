package com.github.server;

import com.github.server.protocol.ChatAPI;
import com.github.server.protocol.ChatPacket;
import com.github.server.protocol.base.ChatHeader;
import com.github.server.protocol.base.ChatMessageHeader;
import com.github.server.protocol.headers.*;
import com.google.gson.
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.net.Socket;

public class UserThread extends Thread {
    private static final Gson gson = new GsonBuilder().registerTypeAdapterFactory(
            RuntimeTypeAdapterFactory.of(ChatHeader.class, "command")
                    .registerSubtype(RegisterHeader)
                    .registerSubtype(LoginHeader)
                    .registerSubtype(LogoutHeader)
                    .registerSubtype(UnicastHeader)
                    .registerSubtype(MulticastHeader)
                    .registerSubtype((BroadcastHeader))).create();
    private final IRequestHandler requestHandler;
    private final ChatAPI api;

    public UserThread(IRequestHandler requestHandlerInterface, Socket client) throws IOException {
        requestHandler = requestHandlerInterface;
        api = new ChatAPI(client);
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
        api.disconnect();
    }

    public ChatPacket receive() throws IOException {
        return api.receive();
    }

    public ChatAPI getApi() {
        return api;
    }

}
