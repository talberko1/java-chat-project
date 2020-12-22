package com.github.server.protocol.headers;

import com.github.server.protocol.base.ChatCommand;
import com.github.server.protocol.base.ChatMessageHeader;

public class BroadcastHeader extends ChatMessageHeader {
    public BroadcastHeader(String from, String contentType) {
        super(ChatCommand.BROADCAST, from, contentType);
    }
}
