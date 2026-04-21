package com.controller;

import com.dao.admin.UserDAO;
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
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        User user = userDAO.validateLogin(username, password);

        if (user != null) {
            statusLabel.setText("Login successful!");

            try {
                if (user.getRole().equals("Admin")) {
                    openPage("/com/view/admin/admin.fxml", "Admin Dashboard");
                } else if (user.getRole().equals("Student")) {
                    openPage("/com/view/student/student_main.fxml", "Student Dashboard");
                } else if (user.getRole().equals("Lecturer")) {
                    openPage("/com/view/lecturer/lecturer_dashboard.fxml", "Lecturer Dashboard");
                } else if (user.getRole().equals("Technical Officer")) {
                    openPage("/com/view/technicalofficer/technical_officer_dashboard.fxml", "Technical Officer Dashboard");
                }
            } catch (Exception e) {
                e.printStackTrace();
                statusLabel.setText("Dashboard loading error!");
            }

        } else {
            statusLabel.setText("Invalid username or password!");
        }
    }

    private void openPage(String fxmlPath, String title) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
        Stage stage = (Stage) usernameField.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle(title);
        stage.setMaximized(true);
        stage.show();
    }
}