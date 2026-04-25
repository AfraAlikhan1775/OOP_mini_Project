package com.controller.Student;

import com.session.StudentSession;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class student_mainController {

    @FXML private StackPane contentArea;

    @FXML
    public void initialize() {
        loadUI("/com/view/Student/stuDashboard.fxml");
    }

    private void loadUI(String fxmlPath) {
        try {
            Parent page = FXMLLoader.load(getClass().getResource(fxmlPath));
            contentArea.getChildren().setAll(page);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Page Load Error", "Cannot load: " + fxmlPath);
        }
    }

    @FXML
    private void handleDashboard() {
        loadUI("/com/view/Student/stuDashboard.fxml");
    }

    @FXML
    private void handleCourses() {
        loadUI("/com/view/Student/stuCourse.fxml");
    }

    @FXML
    private void handleAttendance() {
        loadUI("/com/view/Student/stuAttendance.fxml");
    }

    @FXML
    private void handleNotice() {
        loadUI("/com/view/Student/stuNotice.fxml");
    }

    @FXML
    private void handleGrades() {
        loadUI("/com/view/Student/stuGrades.fxml");
    }

    @FXML
    private void handleTimetable() {
        loadUI("/com/view/Student/stuTimetable.fxml");
    }

    @FXML
    private void handleMedical() {
        loadUI("/com/view/Student/stuMedical.fxml");
    }

    @FXML
    private void handleProfile() {
        loadUI("/com/view/Student/stuProfile.fxml");
    }

    /*
     * Your student_main.fxml currently has onAction="#logout".
     * So this method MUST exist.
     */
    @FXML
    private void logout() {
        openLoginPage();
    }

    /*
     * If another FXML uses onAction="#handleLogout",
     * this also works.
     */
    @FXML
    private void handleLogout() {
        openLoginPage();
    }

    private void openLoginPage() {
        try {
            StudentSession.clear();

            Parent root = FXMLLoader.load(getClass().getResource("/com/view/Login.fxml"));

            Stage stage = (Stage) contentArea.getScene().getWindow();

            stage.setMaximized(false);
            stage.setResizable(false);

            Scene loginScene = new Scene(root, 900, 600);

            stage.setScene(loginScene);
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