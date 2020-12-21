package com.github.client.protocol.headers;

import com.github.client.protocol.base.ChatCommand;
import com.github.client.protocol.base.ChatMessageHeader;

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