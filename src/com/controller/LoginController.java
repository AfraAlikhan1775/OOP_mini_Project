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


        User user = userDAO.validateLogin(username, password);


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

        System.out.println("Redirecting to dashboard for role: " + user.getRole());
    }
}