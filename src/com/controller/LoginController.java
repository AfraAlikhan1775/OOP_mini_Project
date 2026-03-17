package com.controller;

import com.dao.UserDAO;
import com.model.User;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label statusLabel;

    private UserDAO userDAO = new UserDAO();

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        // 1. Pass the inputs to the DAO
        User user = userDAO.validateLogin(username, password);

        // 2. Check the result (The "Traffic Police" logic)
        if (user != null) {
            statusLabel.setText("Login Successful! Welcome, " + user.getRole());
            System.out.println("hi");

            // Navigate to the next screen here (e.g., Load Dashboard)
            navigateToDashboard(user);
        } else {
            statusLabel.setText("Invalid username or password!");
        }
    }

    private void navigateToDashboard(User user) {
        // Logic to switch screens (I can provide this code next)
        System.out.println("Redirecting to dashboard for role: " + user.getRole());
    }
}