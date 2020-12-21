package com.github.server;

import com.github.server.protocol.ChatPacket;

public interface IRequestHandler {
    void handleRequest(UserThread sender, ChatPacket request);
}
