package com.controller.admin;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class AdminController {

    @FXML private StackPane contentArea;
    @FXML private Button logoutBtn;

    @FXML
    public void initialize() {
        handleDashboard();
    }

    @FXML
    private void handleDashboard() {
        loadPage("/com/view/admin/dash_boardhome.fxml");
    }

    @FXML
    private void handleUserprofile() {
        loadPage("/com/view/admin/student.fxml");
    }

    @FXML
    private void handleLecturer() {
        loadPage("/com/view/admin/lecturer.fxml");
    }

    @FXML
    private void handleTO() {
        loadPage("/com/view/admin/to.fxml");
    }

    @FXML
    private void handleStudents() {
        loadPage("/com/view/admin/student.fxml");
    }

    @FXML
    private void handleCourse() {
        loadPage("/com/view/admin/course.fxml");
    }

    @FXML
    private void handleTimetable() {
        loadPage("/com/view/admin/timetable.fxml");
    }

    @FXML
    private void handleNotice() {
        loadPage("/com/view/admin/notice.fxml");
    }

    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/view/Login.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) logoutBtn.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Login");
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadPage(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            contentArea.getChildren().setAll(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}