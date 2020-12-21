package com.github.client.protocol.headers;

import com.github.client.protocol.base.ChatCommand;
import com.github.client.protocol.base.SessionInitiationHeader;

public class LoginHeader extends SessionInitiationHeader {
    public LoginHeader(String username, String password) {
        super(ChatCommand.LOGIN, username, password);
    }
}
