package com.github.client.protocol;

import com.github.client.protocol.base.ChatHeader;

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
