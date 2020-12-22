package com.github.server;

import java.io.IOException;
import java.util.Properties;

public class Application {

    private static final String URI = "mongodb://localhost:27017/";
    private static final String DB_NAME = "chat-server";
    private static final String DB_USERNAME = "";
    private static final String DB_PASSWORD = "";
    private static final String COLLECTION_NAME = "users";

    private static final String SERVER_ADDRESS = "127.0.0.1";
    private static final int SERVER_PORT = 5000;

    public static void main(String[] args) {
        try {
            MongoDBConnector mongoDBConnector = new MongoDBConnector(URI, DB_NAME, DB_USERNAME, DB_PASSWORD, COLLECTION_NAME);
            Properties properties = new Properties();
            properties.load(Application.class.getClassLoader().getResourceAsStream("config.properties"));
            String serverIp = properties.getProperty("server.ip");
            int port = Integer.parseInt(properties.getProperty("server.port"));
            ChatServer server = new ChatServer(serverIp, port, mongoDBConnector);
            Thread serverThread = new Thread(server);
            serverThread.start();
            System.out.println("Chat Server now running on " + SERVER_ADDRESS + ":" + SERVER_PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
