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

import java.net.URL;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label statusLabel;

    private final UserDAO userDAO = new UserDAO();

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Please enter username and password!");
            return;
        }

        User user = userDAO.validateLogin(username, password);

        if (user == null) {
            statusLabel.setText("Invalid username or password!");
            return;
        }

        try {
            if ("Admin".equals(user.getRole())) {
                openPage("/com/view/admin/admin.fxml", "Admin Dashboard");

            } else if ("Student".equals(user.getRole())) {

                if (userDAO.isDefaultPassword(username)) {
                    URL resource = getClass().getResource("/com/view/Student/change_password.fxml");

                    if (resource == null) {
                        statusLabel.setText("Change password page not found!");
                        return;
                    }

                    FXMLLoader loader = new FXMLLoader(resource);
                    Parent root = loader.load();

                    com.controller.Student.ChangePasswordController controller = loader.getController();
                    controller.setUsername(username);

                    Stage stage = (Stage) usernameField.getScene().getWindow();
                    stage.setScene(new Scene(root));
                    stage.setTitle("Change Password");
                    stage.setResizable(false);
                    stage.show();

                } else {
                    openPage("/com/view/Student/student_main.fxml", "Student Dashboard");
                }

            } else if ("Lecturer".equals(user.getRole())) {
                openPage("/com/view/Lec_N/main_layout.fxml", "Lecturer Dashboard");

            } else if ("Technical Officer".equals(user.getRole())) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/view/techOfficer/to_dashboard.fxml"));
                Parent root = loader.load();
                com.controller.techOfficerControllers.TODashboardController controller = loader.getController();
                controller.setLoggedInUser(username);

                Stage stage = (Stage) usernameField.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Technical Officer Dashboard");
                stage.setMaximized(true);
                stage.show();

            } else {
                statusLabel.setText("Unknown role!");
            }

        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Dashboard loading error: " + e.getMessage());
        }
    }

    private void openPage(String fxmlPath, String title) throws Exception {
        URL resource = getClass().getResource(fxmlPath);

        if (resource == null) {
            throw new IllegalArgumentException("FXML file not found: " + fxmlPath);
        }

        FXMLLoader loader = new FXMLLoader(resource);
        Parent root = loader.load();

        Stage stage = (Stage) usernameField.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle(title);
        stage.setMaximized(true);
        stage.show();
    }
}