package com.controller.Student;

import com.dao.admin.UserDAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;

import java.net.URL;

public class ChangePasswordController {

    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label messageLabel;

    private String username;
    private final UserDAO userDAO = new UserDAO();

    public void setUsername(String username) {
        this.username = username;
    }

    @FXML
    private void handleChangePassword() {
        String newPassword = newPasswordField.getText().trim();
        String confirmPassword = confirmPasswordField.getText().trim();

        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            messageLabel.setText("Please fill all fields.");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            messageLabel.setText("Passwords do not match.");
            return;
        }

        if (newPassword.equals("12345")) {
            messageLabel.setText("Please enter a new password.");
            return;
        }

        boolean updated = userDAO.updatePassword(username, newPassword);

        if (updated) {
            try {
                URL resource = getClass().getResource("/com/Resources/view/Student/student_main.fxml");

                if (resource == null) {
                    messageLabel.setText("Student main page not found.");
                    return;
                }

                Parent root = FXMLLoader.load(resource);
                Stage stage = (Stage) newPasswordField.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Student Dashboard");
                stage.setMaximized(true);
                stage.show();

            } catch (Exception e) {
                e.printStackTrace();
                messageLabel.setText("Error loading student dashboard.");
            }
        } else {
            messageLabel.setText("Failed to update password.");
        }
    }
}