package com.github.server.protocol.headers;

public class MulticastHeader extends ChatMessageHeader {
    private final String[] to;

    public MulticastHeader(String from, String contentType, String[] to) {
        super(ChatCommand.MULTICAST, from, contentType);
        this.to = to;
    }

    public String[] getTo() {
        return this.to;
    }
}
