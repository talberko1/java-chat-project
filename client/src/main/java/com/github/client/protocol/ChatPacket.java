package com.github.client.protocol;

import com.google.gson.JsonObject;

public class ChatPacket {
    private final JsonObject header;
    private final JsonObject payload;

    public ChatPacket(JsonObject header, JsonObject payload) {
        this.header = header;
        this.payload = payload;
    }

    public JsonObject getHeader() {
        return this.header;
    }

    public JsonObject getPayload() {
        return this.payload;
    }
}
