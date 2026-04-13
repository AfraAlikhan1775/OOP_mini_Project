package com.controller;

import com.dao.UserDAO;
import com.model.User;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label statusLabel;

    private final UserDAO userDAO = new UserDAO();

    @FXML
    private void handleLogin() {

        String username = usernameField.getText();
        String password = passwordField.getText();


        User user = userDAO.validateLogin(username, password);

        if (user != null) {

            statusLabel.setText("Login Successful!");

            navigateToDashboard(user);

        } else {
            statusLabel.setText("Invalid username or password!");
        }
    }

    private void navigateToDashboard(User user) {

        try {

            if (user.getRole().equalsIgnoreCase("admin")) {


                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/view/admin/admin.fxml"));
                Parent root = loader.load();



                Stage stage = (Stage) usernameField.getScene().getWindow();

                Scene scene = new Scene(root);

                stage.setScene(scene);
                stage.setTitle("Admin Dashboard");
                stage.setMaximized(true);
                stage.show();

            } else {

                statusLabel.setText("Access denied! Not an admin.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Error loading dashboard!");
        }
    }
}