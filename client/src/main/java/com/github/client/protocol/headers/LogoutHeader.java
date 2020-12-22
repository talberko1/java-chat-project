package com.github.client.protocol.headers;

import com.github.client.protocol.base.ChatCommand;
import com.github.client.protocol.base.ChatHeader;

public class LogoutHeader extends ChatHeader {
    public LogoutHeader() {
        super(ChatCommand.LOGOUT);
    }
}
