package com.github.client.panels;

import com.github.client.ChatConstants;
import com.github.client.MainFrame;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;

public class ChatPanel extends JPanel {
    private static final Color BACKGROUND_COLOR = new Color(0, 255, 0);

    private final MainFrame parent;

    private final JButton exitButton;
    private final StyledDocument messages;
    private final JTextField messageField;
    private final JButton sendButton;
    private final JLabel users;

    public ChatPanel(MainFrame parent) {
        this.parent = parent;

        setLayout(new GridLayout(3, 2));

        exitButton = new JButton("Exit");
        exitButton.setBackground(Color.RED);
        exitButton.setForeground(Color.WHITE);
        exitButton.addActionListener(e -> {
            this.parent.logout();
        });
        add(exitButton);

        JTextPane messagesArea = new JTextPane();
        JScrollPane scrollablePane = new JScrollPane(messagesArea);
        messagesArea.setEditable(false);
        add(scrollablePane);
        messages = messagesArea.getStyledDocument();

        messageField = new JTextField();
        add(messageField);

        sendButton = new JButton("Send");
        sendButton.setBackground(Color.GREEN);
        sendButton.setForeground(Color.WHITE);
        sendButton.addActionListener(e -> {
            String data = messageField.getText();
            if (!data.isEmpty()) {
                if (data.startsWith("@msg")) {
                    String[] maybeUnicast = data.split("@msg");
                    if (maybeUnicast.length > 1) {
                        String metadata = maybeUnicast[1].trim();
                        System.out.println(metadata);
                        int separator = metadata.indexOf(" ");
                        if (separator != -1) {
                            String name = metadata.substring(0, separator);
                            String message = metadata.substring(separator + 1);
                            this.parent.unicast(name, ChatConstants.CONTENT_TEXT, message);
                            messageField.setText("");
                            addLine(this.parent.getUsername(), message);
                        }
                    }
                }
                else {
                    this.parent.broadcast(ChatConstants.CONTENT_TEXT, data);
                    messageField.setText("");
                    addLine(this.parent.getUsername(), data);
                }
            }
        });
        add(sendButton);
        users = new JLabel();
        add(users);

        setBackground(BACKGROUND_COLOR);
    }

    public void updateOnline(JsonArray names) {
        StringBuilder online = new StringBuilder();
        for (JsonElement nameElement : names) {
            String name = nameElement.getAsString();
            online.append(name).append(" ");
        }
        users.setText(online.toString());

    }

    public void addLine(String senderName, String data) {
        try {
            messages.insertString(messages.getLength(), senderName + ": " + data + "\n", null);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    public void addImage(String senderName, Image image) {
        try {
            Style style = messages.addStyle("StyleName", null);
            StyleConstants.setIcon(style, new ImageIcon(image));
            messages.insertString(messages.getLength(), senderName + ":" + "\n", null);
            messages.insertString(messages.getLength(), "\n", style);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }


}
