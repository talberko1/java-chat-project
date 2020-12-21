package com.github.server.protocol.headers;

import com.github.server.protocol.base.ChatCommand;
import com.github.server.protocol.base.ChatMessageHeader;

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
