package com.github.server.protocol;

import com.github.server.protocol.base.ChatCommand;
import com.github.server.protocol.base.ChatHeader;
import com.google.gson.JsonObject;

public class ChatPacket {
    public static final byte REPLY = 0x8;
    public static final byte ERROR = 0x10;
    public static final byte REGISTER = 0x1;
    public static final byte LOGIN = 0x2;
    public static final byte LOGOUT = 0x3;
    public static final byte UNICAST = 0x4;
    public static final byte MULTICAST = 0x5;
    public static final byte BROADCAST = 0x6;
    public static final byte UNKNOWN = 0x7;
    private final byte flags;
    private final JsonObject headers;
    private final String data;

    public ChatPacket(byte flags, JsonObject headers, String data) {
        this.flags = flags;
        this.headers = headers;
        this.data = data;
    }

    public byte getFlags() {
        return this.flags;
    }

    public JsonObject getHeaders() {
        return this.headers;
    }

    public String getData() {
        return this.data;
    }
}
