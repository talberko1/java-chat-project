package com.github.client.protocol.headers;

import com.github.client.protocol.base.ChatCommand;
import com.github.client.protocol.base.ChatMessageHeader;

public class BroadcastHeader extends ChatMessageHeader {
    public BroadcastHeader(String from, String contentType) {
        super(ChatCommand.BROADCAST, from, contentType);
    }
}
