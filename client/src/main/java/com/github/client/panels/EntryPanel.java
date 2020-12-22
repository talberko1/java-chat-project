package com.github.client.panels;

import com.github.client.MainFrame;

import javax.swing.*;
import java.awt.*;

public class EntryPanel extends JPanel {
    private static final String REGISTER_BUTTON_TEXT = "Sign Up";
    private static final String LOGIN_BUTTON_TEXT = "Sign In";
    private static final Color BACKGROUND_COLOR = new Color(0, 255, 0);
    private final MainFrame parent;
    private final JButton registerButton;
    private final JButton loginButton;
    public EntryPanel(MainFrame parentWindow) {
        parent = parentWindow;

        setBackground(Color.LIGHT_GRAY);
        setLayout (new BoxLayout (this, BoxLayout.Y_AXIS));
        setBackground(BACKGROUND_COLOR);

        registerButton = new JButton(REGISTER_BUTTON_TEXT);
        registerButton.setBackground(Color.BLUE);
        registerButton.setForeground(Color.WHITE);
        registerButton.addActionListener(e -> {
            parent.showRegisterPanel();
        });
        add(registerButton);

        loginButton = new JButton(LOGIN_BUTTON_TEXT);
        loginButton.setBackground(Color.BLUE);
        loginButton.setForeground(Color.WHITE);
        loginButton.addActionListener(e -> {
            parent.showLoginPanel();
        });
        add(loginButton);
    }
}
