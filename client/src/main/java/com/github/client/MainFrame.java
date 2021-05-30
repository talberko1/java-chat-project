package com.github.client;

import com.github.client.protocol.ChatCommand;
import com.github.client.protocol.ChatPacket;
import com.github.client.panels.ChatPanel;
import com.github.client.panels.EntryPanel;
import com.github.client.panels.LoginPanel;
import com.github.client.panels.RegisterPanel;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Bucket;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.StorageClient;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;

import static com.github.client.ChatConstants.*;

public class MainFrame extends JFrame {
    private static final String ENTRY_PANEL = "entry";
    private static final String REGISTER_PANEL = "register";
    private static final String LOGIN_PANEL = "login";
    private static final String CHAT_PANEL = "chat";
    private static final String INITIAL_NAME = "guest";
    public static final String PAYLOAD_ONLINE_PROPERTY = "online";
    public static final String SERVER = "{server}";
    private final EntryPanel entryPanel;
    private final RegisterPanel registerPanel;
    private final LoginPanel loginPanel;
    private final ChatPanel chatPanel;
    private final CardLayout layout;
    private final JPanel container;
    private final FirebaseApp firebaseApp;
    private String username;
    private final Socket socket;
    private final BufferedReader in;
    private final PrintWriter out;
    private final Gson gson;
    private final Thread receiveThread;
    private boolean listening;

    public MainFrame(String title, String serverIp, int port, String firebaseKeyPath) throws IOException {
        super(title);
        username = INITIAL_NAME;
        this.socket = new Socket(serverIp, port);
        this.in = new BufferedReader(new InputStreamReader(new DataInputStream(socket.getInputStream())));
        this.out = new PrintWriter(new OutputStreamWriter(new DataOutputStream(socket.getOutputStream())));
        gson = new Gson();
        listening = true;
        receiveThread = new Thread(() -> {
            while (listening) {
                try {
                    ChatPacket response = this.receive();
                    if (response == null) System.exit(0);
                    handleResponse(response);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        receiveThread.start();
        FileInputStream serviceAccount = new FileInputStream(firebaseKeyPath);
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();
        firebaseApp = FirebaseApp.initializeApp(options);
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

        this.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e){
                try {
                    close();
                } catch (IOException | InterruptedException ioException) {
                    ioException.printStackTrace();
                }
            }
        });

        pack();
        showEntryPanel();
    }

    public void close() throws InterruptedException, IOException {
        listening = false;
        socket.close();
        System.exit(0);
    }

    public ChatPacket receive() throws IOException {
        try {
            String response = in.readLine();
            System.out.println(response);
            return gson.fromJson(response, ChatPacket.class);
        }
        catch(SocketException e) {
            return null;
        }
    }

    public void send(JsonObject header, JsonObject payload) {
        String request = gson.toJson(new ChatPacket(header, payload));
        System.out.println(request);
        out.println(request);
        out.flush();
    }

    public String getUsername() {
        return username;
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
        JsonObject header = response.getHeader();
        JsonElement commandElement = header.get(HEADER_COMMAND_PROPERTY);
        if (commandElement != null) {
            ChatCommand command = ChatCommand.valueOf(commandElement.getAsString());
            switch (command) {
                case REGISTER:
                    handleRegister(response);
                    break;
                case LOGIN:
                    handleLogin(response);
                    break;
                case LOGOUT:
                    handleLogout(response);
                    break;
                case UNICAST:
                    handleUnicast(response);
                    break;
                case MULTICAST:
                    handleMulticast(response);
                    break;
                case BROADCAST:
                    handleBroadcast(response);
                    break;
                case INFO:
                    handleInfo(response);
                    break;
                default:
                    break;
            }
        }
    }

    public void handleRegister(ChatPacket response) {
        JsonObject header = response.getHeader();
        JsonElement statusElement = header.get(HEADER_STATUS_PROPERTY);
        if (statusElement != null) {
            String status = statusElement.getAsString();
            if (status.equals(STATUS_OK)) {
                this.username = registerPanel.getUsername();
                registerPanel.clearFields();
                showChatPanel();
            }
        }
    }

    public void handleLogin(ChatPacket response) {
        JsonObject header = response.getHeader();
        JsonElement statusElement = header.get(HEADER_STATUS_PROPERTY);
        if (statusElement != null) {
            String status = statusElement.getAsString();
            if (status.equals(STATUS_OK)) {
                this.username = loginPanel.getUsername();
                loginPanel.clearFields();
                showChatPanel();
            }
        }
    }

    public void handleLogout(ChatPacket response) {
        JsonObject header = response.getHeader();
        JsonElement statusElement = header.get(HEADER_STATUS_PROPERTY);
        if (statusElement != null) {
            String status = statusElement.getAsString();
            if (status.equals(STATUS_OK)) {
                disconnect();
                showEntryPanel();
            }
        }
    }

    public void handleUnicast(ChatPacket response) {
        JsonObject header = response.getHeader();
        JsonObject payload = response.getPayload();
        JsonElement statusElement = header.get(HEADER_STATUS_PROPERTY);
        // if its an acknowledgement response
        if (statusElement != null) {
            String status = statusElement.getAsString();
            if (status.equals(STATUS_OK)) {
                // server acknowledged the unicast request
                JsonElement payloadDataElement = payload.get(PAYLOAD_DATA_PROPERTY);
                if (payloadDataElement != null) {
                    String payloadData = payloadDataElement.getAsString();
                    // do something with the success response
                }
            }
        }
        // someone sent this client a message.
        else {
            updateChatMessages(response);
        }
    }

    public void handleMulticast(ChatPacket response) {
        JsonObject header = response.getHeader();
        JsonObject payload = response.getPayload();
        JsonElement statusElement = header.get(HEADER_STATUS_PROPERTY);
        if (statusElement != null) {
            String status = statusElement.getAsString();
            if (status.equals(STATUS_OK)) {
                // server acknowledged the multicast request
                JsonElement payloadDataElement = payload.get(PAYLOAD_DATA_PROPERTY);
                if (payloadDataElement != null) {
                    String payloadData = payloadDataElement.getAsString();
                    // do something with the success response
                }
            }
        }
        // someone sent this client a message.
        else {
            updateChatMessages(response);
        }
    }

    public void handleBroadcast(ChatPacket response) {
        JsonObject header = response.getHeader();
        JsonObject payload = response.getPayload();
        JsonElement statusElement = header.get(HEADER_STATUS_PROPERTY);
        if (statusElement != null) {
            String status = statusElement.getAsString();
            if (status.equals(STATUS_OK)) {
                // server acknowledged the broadcast request
                JsonElement payloadDataElement = payload.get(PAYLOAD_DATA_PROPERTY);
                if (payloadDataElement != null) {
                    String payloadData = payloadDataElement.getAsString();
                    // do something with the success response
                }
            }
        }
        // someone sent this client a message.
        else {
            updateChatMessages(response);
        }
    }

    public void handleInfo(ChatPacket response) {
        JsonObject payload = response.getPayload();
        JsonElement onlineElement = payload.get(PAYLOAD_ONLINE_PROPERTY);
        if (onlineElement != null) {
            JsonArray online = onlineElement.getAsJsonArray();
            chatPanel.updateOnline(online);
            JsonElement dataElement = payload.get(PAYLOAD_DATA_PROPERTY);
            if (dataElement != null) {
                String data = dataElement.getAsString();
                chatPanel.addLine(SERVER, data);
            }
        }

    }

    public void disconnect() {
        this.username = INITIAL_NAME;
    }

    public void initiateSession(ChatCommand command, String username, String password) {
        JsonObject payload = generateSessionInitiationPayload(username, password);
        JsonObject header = generateHeader(command, payload.size());
        send(header, payload);
    }

    public void logout() {
        JsonObject payload = new JsonObject();
        JsonObject header = generateHeader(ChatCommand.LOGOUT, payload.size());
        send(header, payload);
    }

    public void unicast(String targetName, String payloadType, String data) {
        JsonObject payload = generatePayload(data);
        JsonObject header = generateHeader(ChatCommand.UNICAST, payload.size());
        header.addProperty(HEADER_PAYLOAD_TYPE_PROPERTY, payloadType);
        header.addProperty(HEADER_TO_PROPERTY, targetName);
        send(header, payload);
    }

    public void multicast(String[] targetNames, String payloadType, String data) {
        JsonObject payload = generatePayload(data);
        JsonObject header = generateHeader(ChatCommand.MULTICAST, payload.size());
        header.addProperty(HEADER_PAYLOAD_TYPE_PROPERTY, payloadType);
        JsonArray targets = new JsonArray();
        for (String name : targetNames) {
            targets.add(name);
        }
        header.add(HEADER_TO_PROPERTY, targets);
        send(header, payload);
    }

    public void broadcast(String payloadType, String data) {
        JsonObject payload = generatePayload(data);
        JsonObject header = generateHeader(ChatCommand.BROADCAST, payload.size());
        header.addProperty(HEADER_PAYLOAD_TYPE_PROPERTY, payloadType);
        send(header, payload);
    }

    public JsonObject generateSessionInitiationPayload(String username, String password) {
        JsonObject payload = new JsonObject();
        payload.addProperty(PAYLOAD_USERNAME_PROPERTY, username);
        payload.addProperty(PAYLOAD_PASSWORD_PROPERTY, password);
        return payload;
    }

    public JsonObject generatePayload(String data) {
        JsonObject payload = new JsonObject();
        payload.addProperty(PAYLOAD_DATA_PROPERTY, data);
        return payload;
    }

    public JsonObject generateHeader(ChatCommand command, int payloadLength) {
        JsonObject header = new JsonObject();
        header.addProperty(HEADER_COMMAND_PROPERTY, command.name());
        header.addProperty(HEADER_PAYLOAD_LENGTH_PROPERTY, payloadLength);
        return header;
    }

    public void updateChatMessages(ChatPacket message) {
        JsonObject header = message.getHeader();
        JsonObject payload = message.getPayload();
        JsonElement payloadTypeElement = header.get(HEADER_PAYLOAD_TYPE_PROPERTY);
        if (payloadTypeElement != null) {
            String payloadType = payloadTypeElement.getAsString();
            JsonElement payloadDataElement = payload.get(PAYLOAD_DATA_PROPERTY);
            if (payloadDataElement != null) {
                String payloadData = payloadDataElement.getAsString();
                JsonElement senderNameElement = header.get(HEADER_FROM_PROPERTY);
                String senderName;
                if (senderNameElement != null) {
                    senderName = senderNameElement.getAsString();
                }
                else {
                    senderName = SERVER;
                }
                if (payloadType.equals(CONTENT_TEXT)) {
                    chatPanel.addLine(senderName, payloadData);
                }
                else if (payloadType.equals(CONTENT_IMAGE)) {

                    chatPanel.addImage(senderName, urlToImage(payloadData));
                }
                else {
                    System.out.println("Received unknown payload type");
                }
            }
        }
    }

    public Image urlToImage(String urlString) {
        try {
            URL url = new URL(urlString);
            URLConnection connection = url.openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.88 Safari/537.36");
            return ImageIO.read(connection.getInputStream());
        }
        catch(IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
