package com.github.client.protocol.base;

public abstract class ChatHeader {
    protected final ChatCommand command;

    public ChatHeader(ChatCommand command) {
        this.command = command;
    }

    public ChatCommand getCommand() {
        return this.command;
    }
}
