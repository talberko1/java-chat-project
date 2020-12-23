package com.github.server.protocol.headers;

public class RegisterHeader extends SessionInitiationHeader {
    public RegisterHeader(String username, String password) {
        super(ChatCommand.REGISTER, username, password);
    }
}
