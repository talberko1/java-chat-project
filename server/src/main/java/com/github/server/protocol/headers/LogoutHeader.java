package com.github.server.protocol.headers;

public class LogoutHeader extends ChatHeader {
    public LogoutHeader() {
        super(ChatCommand.LOGOUT);
    }
}
