package com.github.client.panels;

import com.github.client.MainFrame;
import com.github.client.protocol.ChatAPI;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;

public class ChatPanel extends JPanel {
    private static final Color BACKGROUND_COLOR = new Color(0, 255, 0);

    private final MainFrame parent;
    private final ChatAPI api;

    private final JButton exitButton;
    private final StyledDocument messages;
    private final JTextField messageField;
    private final JButton sendButton;

    public ChatPanel(MainFrame parent) {
        this.parent = parent;
        this.api = parent.getClientAPI();

        setLayout(new GridBagLayout());

        exitButton = new JButton("Exit");
        exitButton.setBackground(Color.RED);
        exitButton.setForeground(Color.WHITE);
        exitButton.addActionListener(e -> {
            api.logout();
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
                this.api.broadcast(this.parent.getUsername(), ChatAPI.CONTENT_TEXT, data);
                messageField.setText("");
                updateMessages(data);
            }
        });
        add(sendButton);

        setBackground(BACKGROUND_COLOR);
    }

    public void updateMessages(String message) {
        try {
            messages.insertString(messages.getLength(), message + "\n", null);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    public void updateMessages(Image image) {
        try {
            Style style = messages.addStyle("StyleName", null);
            StyleConstants.setIcon(style, new ImageIcon(image));
            messages.insertString(messages.getLength(), "\n", style);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }


}
