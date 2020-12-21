package com.github.server.protocol.headers;

import com.github.server.protocol.base.ChatCommand;
import com.github.server.protocol.base.ChatHeader;

public class LogoutHeader extends ChatHeader {
    public LogoutHeader() {
        super(ChatCommand.LOGOUT);
    }
}
