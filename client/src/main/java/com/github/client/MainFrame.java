package com.github.client;

import com.github.client.protocol.ChatAPI;
import com.github.client.protocol.ChatPacket;
import com.github.client.protocol.base.ChatCommand;
import com.github.client.protocol.base.ChatHeader;
import com.github.client.protocol.base.ChatMessageHeader;
import com.github.client.panels.ChatPanel;
import com.github.client.panels.EntryPanel;
import com.github.client.panels.LoginPanel;
import com.github.client.panels.RegisterPanel;
import com.github.client.protocol.headers.BroadcastHeader;
import com.github.client.protocol.headers.MulticastHeader;
import com.github.client.protocol.headers.UnicastHeader;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Bucket;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.StorageClient;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;

public class MainFrame extends JFrame {
    private static final String ENTRY_PANEL = "entry";
    private static final String REGISTER_PANEL = "register";
    private static final String LOGIN_PANEL = "login";
    private static final String CHAT_PANEL = "chat";
    private static final String INITIAL_NAME = "guest";
    private final ChatAPI api;
    private final EntryPanel entryPanel;
    private final RegisterPanel registerPanel;
    private final LoginPanel loginPanel;
    private final ChatPanel chatPanel;
    private final CardLayout layout;
    private final JPanel container;
    private final FirebaseApp firebaseApp;
    private String username;

    public MainFrame(String title, ChatAPI api, String firebaseKeyPath) throws IOException {
        super(title);
        this.api = api;
        Thread receiveThread = new Thread(() -> {
            while (true) {
                try {
                    ChatPacket response = this.api.receive();
                    handleResponse(response);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        username = INITIAL_NAME;

        FileInputStream serviceAccount = new FileInputStream(firebaseKeyPath);
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();
        firebaseApp = FirebaseApp.initializeApp(options);

        receiveThread.start();
        layout = new CardLayout();
        container = new JPanel(layout);

        entryPanel = new EntryPanel(this);
        registerPanel = new RegisterPanel(this);
        loginPanel = new LoginPanel(this);
        chatPanel = new ChatPanel(this);

        container.add(entryPanel, ENTRY_PANEL);
        container.add(registerPanel, REGISTER_PANEL);
        container.add(loginPanel, LOGIN_PANEL);
        container.add(chatPanel, CHAT_PANEL);
        add(container);
        pack();
        showEntryPanel();
    }

    public String getUsername() {
        return username;
    }

    public ChatAPI getClientAPI() {
        return api;
    }

    public void uploadImage(String imagePath) {
        try {
            StorageClient storageClient = StorageClient.getInstance(firebaseApp);
            File file = new File(imagePath);
            InputStream imageFile = new FileInputStream(file);
            String blobString = "images/" + file.getName();
            storageClient.bucket().create(blobString, imageFile, Bucket.BlobWriteOption.userProject("java-chat-project"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void showEntryPanel() {
        layout.show(container, ENTRY_PANEL);
    }

    public void showRegisterPanel() {
        layout.show(container, REGISTER_PANEL);
    }

    public void showLoginPanel() {
        layout.show(container, LOGIN_PANEL);
    }

    public void showChatPanel() {
        layout.show(container, CHAT_PANEL);
    }

    public void handleResponse(ChatPacket response) {
        ChatHeader header = response.getHeader();
        ChatCommand command = header.getCommand();
        String data = response.getData();
        switch (command) {
            case REGISTER:
                handleRegister(data);
                break;
            case LOGIN:
                handleLogin(data);
                break;
            case LOGOUT:
                handleLogout(data);
                break;
            case UNICAST:
                handleUnicast((UnicastHeader)header, data);
                break;
            case MULTICAST:
                handleMulticast((MulticastHeader)header, data);
                break;
            case BROADCAST:
                handleBroadcast((BroadcastHeader)header, data);
                break;
            default:
                break;
        }
    }

    private void handleRegister(String data) {
        if (data.equals("OK")) {
            this.username = registerPanel.getUsername();
            registerPanel.clearFields();
            showChatPanel();
        }
    }

    private void handleLogin(String data) {
        if (data.equals("OK")) {
            this.username = loginPanel.getUsername();
            loginPanel.clearFields();
            showChatPanel();
        }
    }

    private void handleLogout(String data) {
        if (data.equals("BYE")) {
            api.disconnect();
            showEntryPanel();
        }
    }

    private void handleUnicast(UnicastHeader header, String data) {
        String to = header.getTo();
        if (to.equals(this.username)) {
            handleMessage(header, data);
        }
    }

    private void handleMulticast(MulticastHeader header, String data) {
        String[] to = header.getTo();
        boolean found = false;
        for (int i = 0; i < to.length && !found; i++) {
            if (to[i].equals(this.username)) {
                found = true;
            }
        }
        if (found) {
            handleMessage(header, data);
        }
    }

    private void handleBroadcast(BroadcastHeader header, String data) {
        handleMessage(header, data);
    }

    public void handleMessage(ChatMessageHeader header, String data) {
        String contentType = header.getContentType();

        if (contentType.equals(ChatAPI.CONTENT_TEXT)) {
            chatPanel.updateMessages(data);
        }
        else if (contentType.equals(ChatAPI.CONTENT_IMAGE)) {
            try {
                chatPanel.updateMessages(urlToImage(data));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            System.err.println("received unsupported content");
        }
    }

    public Image urlToImage(String urlString) throws IOException {
        URL url = new URL(urlString);
        URLConnection connection = url.openConnection();
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.88 Safari/537.36");
        return ImageIO.read(connection.getInputStream());
    }
}
