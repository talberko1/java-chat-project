package com.github.server;

public class ChatProtocolResponse {
    private int status;
    private String data;
    public ChatProtocolResponse(int responseStatus, String responseData) {
        status = responseStatus;
        data = responseData;
    }

}
