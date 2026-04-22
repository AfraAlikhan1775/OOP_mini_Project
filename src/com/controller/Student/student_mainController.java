package com.controller.Student;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;

public class student_mainController {

    @FXML
    private StackPane contentArea;

    @FXML
    public void initialize() {
        loadUI("/com/view/student/student_main.fxml");
    }

    private void loadUI(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();
            contentArea.getChildren().setAll(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDashboard() {
        loadUI("/com/view/student/stuDashboard.fxml");
    }

    @FXML
    private void handleAttendance() {
        loadUI("/com/view/student/stuAttendance.fxml");
    }

    @FXML
    private void handleTimetable() {
        loadUI("/com/view/student/stuTimetable.fxml");
    }

    @FXML
    private void handleNotice() {
        loadUI("/com/view/student/stuNotice.fxml");
    }

    @FXML
    private void handleGrades() {
        loadUI("/com/view/student/stuGrades.fxml");
    }

    @FXML
    private void handleCourse() {
        loadUI("/com/view/student/stuCourse.fxml");
    }

    @FXML
    private void handleMedical() {
        loadUI("/com/view/student/stuMedical.fxml");
    }

    @FXML
    private void handleProfile() {
        loadUI("/com/view/student/stuProfile.fxml");
    }

}