package com.controller.techOfficerControllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class TODashboardController {

    @FXML private StackPane contentArea;

    private String loggedInUser;

    public void setLoggedInUser(String username) {
        this.loggedInUser = username;
    }

    @FXML
    public void initialize() {
        loadPage("/com/view/techOfficer/to_home.fxml");
    }

    private void loadPage(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Object controller = loader.getController();

            if (controller instanceof TOAttendanceController attendanceController) {
                attendanceController.setDashboardController(this);
            }

            if (controller instanceof AddAttendanceController addAttendanceController) {
                addAttendanceController.setDashboardController(this);
            }

            if (controller instanceof AddSessionController addSessionController) {
                addSessionController.setDashboardController(this);
            }

            if (controller instanceof TONoticeController noticeController) {
                noticeController.setEmpId(loggedInUser);
            }

            if (controller instanceof TOProfileController profileController) {
                profileController.setEmpId(loggedInUser);
            }

            contentArea.getChildren().setAll(root);

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Page Load Error", "Cannot load: " + fxmlPath);
        }
    }

    public void loadContent(String fxmlPath) {
        loadPage(fxmlPath);
    }

    @FXML
    private void handleHome() {
        loadPage("/com/view/techOfficer/to_home.fxml");
    }

    @FXML
    private void handleDashboard() {
        handleHome();
    }

    @FXML
    private void handleAttendance() {
        loadPage("/com/view/techOfficer/to_attendance.fxml");
    }

    @FXML
    private void handleMedical() {
        loadPage("/com/view/techOfficer/to_medical.fxml");
    }

    @FXML
    private void handleNotices() {
        loadPage("/com/view/techOfficer/to_notices.fxml");
    }

    @FXML
    private void handleNotice() {
        handleNotices();
    }

    @FXML
    private void handleTimetable() {
        loadPage("/com/view/techOfficer/to_timetable.fxml");
    }

    @FXML
    private void handleMarks() {
        loadPage("/com/view/techOfficer/to_marks.fxml");
    }

    @FXML
    private void handleProfile() {
        loadPage("/com/view/techOfficer/to_profile.fxml");
    }

    @FXML
    private void handleLogout() {
        logout();
    }

    private void logout() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/view/Login.fxml"));

            Stage stage = (Stage) contentArea.getScene().getWindow();

            stage.setMaximized(false);
            stage.setResizable(false);

            Scene scene = new Scene(root, 900, 600);
            stage.setScene(scene);
            stage.setTitle("Login");
            stage.setWidth(900);
            stage.setHeight(600);
            stage.setMinWidth(900);
            stage.setMinHeight(600);
            stage.centerOnScreen();
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Logout Error", "Cannot open login page.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}