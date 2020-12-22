package com.github.server.protocol;

public class ChatFlags {
    public static final byte REGISTER = 0x1;
    public static final byte LOGIN = 0x2;
    public static final byte LOGOUT = 0x3;
    public static final byte UNICAST = 0x4;
    public static final byte MULTICAST = 0x5;
    public static final byte BROADCAST = 0x6;
    public static final byte UNKNOWN = 0x7;
    public static final byte REPLY = 0x8;
    public static final byte ERROR = 0x10;
}
