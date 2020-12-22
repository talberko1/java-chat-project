package com.github.server;

public class ChatProtocolRequest {
    private final String data;
    public ChatProtocolRequest(String clientData) {
        data = clientData;
    }
    public String getData() {
        return data;
    }
}
