package com.github.client;

import java.io.IOException;
import java.util.Properties;

public class Application {
    public static void main(String[] args) {
        try {
            Properties properties = new Properties();
            properties.load(Application.class.getClassLoader().getResourceAsStream("config.properties"));

            String serverIp = properties.getProperty("server.ip");
            int port = Integer.parseInt(properties.getProperty("server.port"));

            String firebaseKeyPath = properties.getProperty("firebase.key.path");
            String title = properties.getProperty("app.title");
            int width = Integer.parseInt(properties.getProperty("app.width"));
            int height = Integer.parseInt(properties.getProperty("app.height"));


            MainFrame window = new MainFrame(title, serverIp, port, firebaseKeyPath);
            window.setSize(width, height);
            window.setLocationRelativeTo(null);
            window.setVisible(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
