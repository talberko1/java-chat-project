package com.github.client.panels;

import com.github.client.MainFrame;
import com.github.client.protocol.ChatCommand;

import javax.swing.*;
import java.awt.*;

public class LoginPanel extends JPanel {
    private static final Color BACKGROUND_COLOR = new Color(0, 255, 0);
    private static final String LOGIN_FORMAT = "login %s %s";
    private static final String LOGIN_BUTTON_TEXT = "Sign In";
    private static final String PASSWORD_LABEL_TEXT = "Password:";
    private static final String USERNAME_LABEL_TEXT = "Username:";

    private final MainFrame parent;

    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private final JButton loginButton;

    public LoginPanel(MainFrame parent) {
        this.parent = parent;

        setBackground(BACKGROUND_COLOR);
        setLayout(new GridLayout(3, 2));

        JLabel usernameLabel = new JLabel(USERNAME_LABEL_TEXT);
        usernameLabel.setForeground(Color.WHITE);
        usernameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        usernameField = new JTextField();

        JLabel passwordLabel = new JLabel(PASSWORD_LABEL_TEXT);
        passwordLabel.setForeground(Color.WHITE);
        passwordLabel.setHorizontalAlignment(SwingConstants.CENTER);
        passwordField = new JPasswordField();

        JLabel responseLabel = new JLabel();

        loginButton = new JButton(LOGIN_BUTTON_TEXT);
        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            this.parent.initiateSession(ChatCommand.LOGIN, username, password);
        });

        add(usernameLabel);
        add(usernameField);

        add(passwordLabel);
        add(passwordField);

        add(responseLabel);
        add(loginButton);
    }

    public String getUsername() {
        return usernameField.getText();
    }

    public void clearFields() {
        usernameField.setText("");
        passwordField.setText("");
    }
}
