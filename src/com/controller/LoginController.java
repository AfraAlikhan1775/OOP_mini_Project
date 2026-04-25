package com.controller;

import com.dao.admin.UserDAO;
import com.model.User;
import com.session.StudentSession;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Screen;
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
            String role = user.getRole();

            if ("Admin".equalsIgnoreCase(role)) {
                openDashboard("/com/view/admin/admin.fxml", "Admin Dashboard");

            } else if ("Student".equalsIgnoreCase(role)) {
                StudentSession.setUsername(username);

                if (userDAO.isDefaultPassword(username)) {
                    openChangePassword(username);
                } else {
                    openDashboard("/com/view/Student/student_main.fxml", "Student Dashboard");
                }

            } else if ("Lecturer".equalsIgnoreCase(role)) {
                openLecturerDashboard(username);

            } else if ("Technical Officer".equalsIgnoreCase(role)) {
                openTechnicalOfficerDashboard(username);

            } else {
                statusLabel.setText("Unknown role: " + role);
            }

        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Dashboard loading error. Check console.");
        }
    }

    private void openDashboard(String fxmlPath, String title) throws Exception {
        URL resource = getClass().getResource(fxmlPath);

        if (resource == null) {
            throw new IllegalArgumentException("FXML not found: " + fxmlPath);
        }

        Parent root = FXMLLoader.load(resource);

        Stage stage = (Stage) usernameField.getScene().getWindow();

        stage.setMaximized(false);
        stage.setResizable(true);

        double width = Screen.getPrimary().getVisualBounds().getWidth();
        double height = Screen.getPrimary().getVisualBounds().getHeight();

        Scene scene = new Scene(root, width, height);

        stage.setScene(scene);
        stage.setTitle(title);
        stage.setX(Screen.getPrimary().getVisualBounds().getMinX());
        stage.setY(Screen.getPrimary().getVisualBounds().getMinY());
        stage.setWidth(width);
        stage.setHeight(height);
        stage.setMaximized(true);
        stage.show();
    }

    private void openLecturerDashboard(String lecturerEmpId) throws Exception {
        URL resource = getClass().getResource("/com/view/Lec_N/main_layout.fxml");

        if (resource == null) {
            throw new IllegalArgumentException("FXML not found: /com/view/Lec_N/main_layout.fxml");
        }

        FXMLLoader loader = new FXMLLoader(resource);
        Parent root = loader.load();

        com.controller.Lecturer.MainController controller = loader.getController();
        controller.setLecturerEmpId(lecturerEmpId);

        openDashboardWithRoot(root, "Lecturer Dashboard");
    }

    private void openTechnicalOfficerDashboard(String username) throws Exception {
        URL resource = getClass().getResource("/com/view/techOfficer/to_dashboard.fxml");

        if (resource == null) {
            throw new IllegalArgumentException("FXML not found: /com/view/techOfficer/to_dashboard.fxml");
        }

        FXMLLoader loader = new FXMLLoader(resource);
        Parent root = loader.load();

        com.controller.techOfficerControllers.TODashboardController controller = loader.getController();
        controller.setLoggedInUser(username);

        openDashboardWithRoot(root, "Technical Officer Dashboard");
    }

    private void openDashboardWithRoot(Parent root, String title) {
        Stage stage = (Stage) usernameField.getScene().getWindow();

        stage.setMaximized(false);
        stage.setResizable(true);

        double width = Screen.getPrimary().getVisualBounds().getWidth();
        double height = Screen.getPrimary().getVisualBounds().getHeight();

        stage.setScene(new Scene(root, width, height));
        stage.setTitle(title);
        stage.setX(Screen.getPrimary().getVisualBounds().getMinX());
        stage.setY(Screen.getPrimary().getVisualBounds().getMinY());
        stage.setWidth(width);
        stage.setHeight(height);
        stage.setMaximized(true);
        stage.show();
    }

    private void openChangePassword(String username) throws Exception {
        URL resource = getClass().getResource("/com/view/Student/change_password.fxml");

        if (resource == null) {
            throw new IllegalArgumentException("FXML not found: /com/view/Student/change_password.fxml");
        }

        FXMLLoader loader = new FXMLLoader(resource);
        Parent root = loader.load();

        com.controller.Student.ChangePasswordController controller = loader.getController();
        controller.setUsername(username);

        Stage stage = (Stage) usernameField.getScene().getWindow();

        stage.setMaximized(false);
        stage.setResizable(false);
        stage.setScene(new Scene(root, 900, 600));
        stage.setTitle("Change Password");
        stage.centerOnScreen();
        stage.show();
    }

    @FXML
    private void handleClear() {
        usernameField.clear();
        passwordField.clear();
        statusLabel.setText("");
        usernameField.requestFocus();
    }
}