package com.github.server.protocol.headers;

import com.github.server.protocol.base.ChatCommand;
import com.github.server.protocol.base.ChatHeader;

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
