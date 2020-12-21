package com.github.client;

public class ChatProtocolResponse {
    private final int status;
    private final String data;
    public ChatProtocolResponse(int responseStatus, String responseData) {
        status = responseStatus;
        data = responseData;
    }
    public String getData() {
        return data;
    }

    public int getStatus() {
        return status;
    }
}
