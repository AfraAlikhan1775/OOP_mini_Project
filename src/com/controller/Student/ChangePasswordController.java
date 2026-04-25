package com.controller.Student;

import com.dao.admin.UserDAO;
import com.session.StudentSession;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.net.URL;

public class ChangePasswordController {

    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label messageLabel;

    private String username;
    private String role;

    private final UserDAO userDAO = new UserDAO();

    public void setUsername(String username) {
        this.username = username;
        this.role = "Student";
        StudentSession.setUsername(username);
    }

    public void setUser(String username, String role) {
        this.username = username;
        this.role = role;

        if ("Student".equalsIgnoreCase(role)) {
            StudentSession.setUsername(username);
        }
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

        if (!updated) {
            messageLabel.setText("Failed to update password.");
            return;
        }

        try {
            openDashboardAfterPasswordChange();
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Error loading dashboard.");
        }
    }

    private void openDashboardAfterPasswordChange() throws Exception {
        if ("Student".equalsIgnoreCase(role)) {
            StudentSession.setUsername(username);
            openNormalDashboard("/com/view/Student/student_main.fxml", "Student Dashboard");
        } else if ("Lecturer".equalsIgnoreCase(role)) {
            openLecturerDashboard();
        } else if ("Technical Officer".equalsIgnoreCase(role)) {
            openTechnicalOfficerDashboard();
        } else {
            openNormalDashboard("/com/view/Login.fxml", "Login");
        }
    }

    private void openNormalDashboard(String fxmlPath, String title) throws Exception {
        URL resource = getClass().getResource(fxmlPath);

        if (resource == null) {
            messageLabel.setText("FXML not found: " + fxmlPath);
            return;
        }

        Parent root = FXMLLoader.load(resource);
        openFullScreen(root, title);
    }

    private void openLecturerDashboard() throws Exception {
        URL resource = getClass().getResource("/com/view/Lec_N/main_layout.fxml");

        if (resource == null) {
            messageLabel.setText("Lecturer dashboard not found.");
            return;
        }

        FXMLLoader loader = new FXMLLoader(resource);
        Parent root = loader.load();

        com.controller.Lecturer.MainController controller = loader.getController();
        controller.setLecturerEmpId(username);

        openFullScreen(root, "Lecturer Dashboard");
    }

    private void openTechnicalOfficerDashboard() throws Exception {
        URL resource = getClass().getResource("/com/view/techOfficer/to_dashboard.fxml");

        if (resource == null) {
            messageLabel.setText("Technical Officer dashboard not found.");
            return;
        }

        FXMLLoader loader = new FXMLLoader(resource);
        Parent root = loader.load();

        com.controller.techOfficerControllers.TODashboardController controller = loader.getController();
        controller.setLoggedInUser(username);

        openFullScreen(root, "Technical Officer Dashboard");
    }

    private void openFullScreen(Parent root, String title) {
        Stage stage = (Stage) newPasswordField.getScene().getWindow();

        double width = Screen.getPrimary().getVisualBounds().getWidth();
        double height = Screen.getPrimary().getVisualBounds().getHeight();

        stage.setResizable(true);
        stage.setScene(new Scene(root, width, height));
        stage.setTitle(title);
        stage.setMaximized(true);
        stage.show();
    }
}