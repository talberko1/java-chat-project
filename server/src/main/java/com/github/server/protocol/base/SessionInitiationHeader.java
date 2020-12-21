package com.github.server.protocol.base;

public abstract class SessionInitiationHeader extends ChatHeader {
    protected final String username;
    protected final String password;
    public SessionInitiationHeader(ChatCommand command, String username, String password) {
        super(command);
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }
}
