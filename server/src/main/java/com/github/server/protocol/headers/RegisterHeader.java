package com.github.server.protocol.headers;

import com.github.server.protocol.base.ChatCommand;
import com.github.server.protocol.base.SessionInitiationHeader;

public class RegisterHeader extends SessionInitiationHeader {
    public RegisterHeader(String username, String password) {
        super(ChatCommand.REGISTER, username, password);
    }
}
