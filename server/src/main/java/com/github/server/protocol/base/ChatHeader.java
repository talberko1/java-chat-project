package com.github.server.protocol.base;

public abstract class ChatHeader {
    protected final ChatCommand command;

    public ChatHeader(ChatCommand command) {
        this.command = command;
    }

    public ChatCommand getCommand() {
        return this.command;
    }

    @Override
    public String toString() {
        return command.name();
    }
}
