package com.github.server;

public class ChatConstants {
    public static final String SPACE = " ";
    public static final String UNKNOWN_COMMAND_RESPONSE = "unknown command";
    public static final String UNICAST_USER_NOT_FOUND = "the user %s was not found";
    public static final String REGISTER_SUCCESS_MESSAGE = "successfully registered";
    public static final String LOGIN_SUCCESS_MESSAGE = "successfully logged in";
    public static final String USERNAME_TAKEN_MESSAGE = "username already taken, choose another";
    public static final String USER_NOT_FOUND_MESSAGE = "the user was not found";
    public static final String INCORRECT_PASSWORD_MESSAGE = "the password you have entered was incorrect";
    public static final String USER_JOINED = SPACE + "has joined the chat!";
    public static final String USER_LEFT = SPACE + "has left the chat!";

    public static final String NEW_CONNECTION = "[+] new connection";
    public static final String LOGOUT_RESPONSE = "BYE";
    public static final String DIRECT_SUCCESS_MESSAGE = "Successfully sent to ";
    public static final String BROADCAST_SUCCESS_MESSAGE = "Successfully sent broadcast message";
    public static final String PAYLOAD_DATA_PROPERTY = "data";
    public static final String PAYLOAD_USERNAME_PROPERTY = "username";
    public static final String PAYLOAD_PASSWORD_PROPERTY = "password";
    public static final String HEADER_COMMAND_PROPERTY = "command";
    public static final String HEADER_FROM_PROPERTY = "from";
    public static final String HEADER_PAYLOAD_LENGTH_PROPERTY = "payload-length";
    public static final String HEADER_STATUS_PROPERTY = "status";
    public static final String STATUS_OK = "OK";
    public static final String STATUS_ERROR = "ERROR";
    public static final String HEADER_TO_PROPERTY = "to";
    public static final String HEADER_TO_PROPERTY_MISSING = "header field 'to' is missing";
}
