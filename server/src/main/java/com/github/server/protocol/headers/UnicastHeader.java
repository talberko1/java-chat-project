package com.github.server.protocol.headers;

public class UnicastHeader extends ChatMessageHeader {
    private final String to;

    public UnicastHeader(String from, String contentType, String to) {
        super(ChatCommand.UNICAST, from, contentType);
        this.to = to;
    }

    public String getTo() {
        return this.to;
    }
}
