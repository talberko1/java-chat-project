package com.github.client.protocol.base;

public abstract class ChatMessageHeader extends ChatHeader {
    protected final String from;
    protected final String contentType;
    public ChatMessageHeader(ChatCommand command, String from, String contentType) {
        super(command);
        this.from = from;
        this.contentType = contentType;
    }

    public String getFrom() {
        return this.from;
    }

    public String getContentType() {
        return this.contentType;
    }
}
