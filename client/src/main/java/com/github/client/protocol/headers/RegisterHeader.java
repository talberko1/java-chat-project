package com.github.client.protocol.headers;

import com.github.client.protocol.base.ChatCommand;
import com.github.client.protocol.base.SessionInitiationHeader;

public class RegisterHeader extends SessionInitiationHeader {
    public RegisterHeader(String username, String password) {
        super(ChatCommand.REGISTER, username, password);
    }
}
