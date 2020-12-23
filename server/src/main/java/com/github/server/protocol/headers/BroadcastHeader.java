package com.github.server.protocol.headers;

public class BroadcastHeader extends ChatMessageHeader {
    public BroadcastHeader(String from, String contentType) {
        super(ChatCommand.BROADCAST, from, contentType);
    }
}
