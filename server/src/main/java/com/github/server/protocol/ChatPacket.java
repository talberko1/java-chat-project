package com.github.server.protocol;

import com.github.server.protocol.base.ChatHeader;

public class ChatPacket {
    private final ChatHeader header;
    private final String data;
    public ChatPacket(ChatHeader header, String data) {
        this.header = header;
        this.data = data;
    }

    public ChatHeader getHeader() {
        return this.header;
    }

    public String getData() {
        return this.data;
    }
}
