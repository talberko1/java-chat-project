package com.github.server.protocol;

import com.google.gson.JsonObject;

public class ChatPacket {
    private final byte flags;
    private final JsonObject headers;
    private final JsonObject payload;

    public ChatPacket(byte flags, JsonObject headers, JsonObject payload) {
        this.flags = flags;
        this.headers = headers;
        this.payload = payload;
    }

    public byte getFlags() {
        return this.flags;
    }

    public JsonObject getHeaders() {
        return this.headers;
    }

    public JsonObject getPayload() {
        return this.payload;
    }
}
