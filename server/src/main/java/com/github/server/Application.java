package com.github.server;

import java.io.IOException;
import java.util.Properties;

public class Application {

    public static void main(String[] args) {
        try {
            Properties properties = new Properties();
            properties.load(Application.class.getClassLoader().getResourceAsStream("config.properties"));
            String serverIp = properties.getProperty("server.ip");
            int port = Integer.parseInt(properties.getProperty("server.port"));
            String dbUri = properties.getProperty("db.uri");
            String dbName = properties.getProperty("db.name");
            String dbUsername = properties.getProperty("db.username");
            String dbPassword = properties.getProperty("db.password");
            String collectionName = properties.getProperty("db.collection.name");
            MongoDBConnector mongoDBConnector = new MongoDBConnector(dbUri, dbName, dbUsername, dbPassword, collectionName);
            ChatServer server = new ChatServer(serverIp, port, mongoDBConnector);
            Thread serverThread = new Thread(server);
            serverThread.start();
            System.out.println("Chat Server now running on " + serverIp + ":" + port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
