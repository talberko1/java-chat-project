package com.github.server.protocol.headers;

public class ResponseHeader extends ChatHeader {
    private final String status;

    public ResponseHeader(ChatCommand command, String status) {
        super(command);
        this.status = status;
    }

    public String getStatus() {
        return this.status;
    }
}
