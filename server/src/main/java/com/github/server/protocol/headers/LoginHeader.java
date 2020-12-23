package com.github.server.protocol.headers;

public class LoginHeader extends SessionInitiationHeader {
    public LoginHeader(String username, String password) {
        super(ChatCommand.LOGIN, username, password);
    }
}
