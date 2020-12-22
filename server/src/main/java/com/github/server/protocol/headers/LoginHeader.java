package com.github.server.protocol.headers;

import com.github.server.protocol.base.ChatCommand;
import com.github.server.protocol.base.SessionInitiationHeader;

public class LoginHeader extends SessionInitiationHeader {
    public LoginHeader(String username, String password) {
        super(ChatCommand.LOGIN, username, password);
    }
}
