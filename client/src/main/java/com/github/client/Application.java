package com.github.client;


import com.github.client.protocol.ChatAPI;

import java.io.IOException;
import java.util.Properties;

public class Application {
    private static final String TITLE = "Chat Application";

    public static void main(String[] args) {
        try {
            Properties properties = new Properties();
            properties.load(Application.class.getClassLoader().getResourceAsStream("config.properties"));

            String serverIp = properties.getProperty("server.ip");
            int port = Integer.parseInt(properties.getProperty("server.port"));

            String firebaseKeyPath = properties.getProperty("firebase.key.path");

            ChatAPI api = new ChatAPI(serverIp, port);
            MainFrame window = new MainFrame(TITLE, api, firebaseKeyPath);
            window.setLocationRelativeTo(null);
            window.setVisible(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
