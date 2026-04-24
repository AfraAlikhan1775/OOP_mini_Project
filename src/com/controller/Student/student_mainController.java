package com.controller.Student;

import com.session.StudentSession;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class student_mainController {

    @FXML
    private StackPane contentArea;

    @FXML
    public void initialize() {
        loadUI("/com/view/Student/stuDashboard.fxml");
    }

    private void loadUI(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();
            contentArea.getChildren().setAll(root);
        } catch (Exception e) {
            e.printStackTrace();

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Load Error");
            alert.setContentText("Cannot load page: " + fxml);
            alert.showAndWait();
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
    private void handleTimetable() {
        loadUI("/com/view/Student/stuTimetable.fxml");
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
    private void handleMedical() {
        loadUI("/com/view/Student/stuMedical.fxml");
    }

    @FXML
    private void handleProfile() {
        loadUI("/com/view/Student/stuProfile.fxml");
    }

    @FXML
    private void handleLogout() {
        try {
            StudentSession.clear();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/view/Login.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) contentArea.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Login");
            stage.setMaximized(false);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}