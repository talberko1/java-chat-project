package com.github.client.panels;

import com.github.client.MainFrame;
import com.github.client.protocol.ChatAPI;

import javax.swing.*;
import java.awt.*;

public class RegisterPanel extends JPanel {
    private static final Color BACKGROUND_COLOR = new Color(0, 255, 0);
    private static final String REGISTER_FORMAT = "register %s %s";

    private final MainFrame parent;
    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private final JButton registerButton;
    private final ChatAPI api;

    public RegisterPanel(MainFrame parent) {
        this.parent = parent;
        this.api = parent.getClientAPI();

        setBackground(BACKGROUND_COLOR);
        setLayout(new SpringLayout());

        JLabel usernameLabel = new JLabel("Username:");
        usernameField = new JTextField();

        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField();

        registerButton = new JButton("Sign Up");
        registerButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            this.api.register(username, password);
        });

        JPanel pageContainer = new JPanel();
        pageContainer.setLayout(new BoxLayout(pageContainer, BoxLayout.Y_AXIS));

        JPanel usernameEntryContainer = new JPanel();
        usernameEntryContainer.setLayout(new BoxLayout(usernameEntryContainer, BoxLayout.X_AXIS));

        JPanel passwordEntryContainer = new JPanel();
        passwordEntryContainer.setLayout(new BoxLayout(passwordEntryContainer, BoxLayout.X_AXIS));

        usernameEntryContainer.add(usernameLabel);
        usernameEntryContainer.add(usernameField);

        passwordEntryContainer.add(passwordLabel);
        passwordEntryContainer.add(passwordField);

        pageContainer.add(usernameEntryContainer);
        pageContainer.add(passwordEntryContainer);
        pageContainer.add(registerButton);

        add(pageContainer);

    }

    public String getUsername() {
        return usernameField.getText();
    }

    public void clearFields() {
        usernameField.setText("");
        passwordField.setText("");
    }
}
