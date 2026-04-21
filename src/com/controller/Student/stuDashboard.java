package com.controller.student;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;

public class StudentController {

    @FXML
    private StackPane contentArea;

    public void initialize() {
        loadUI("/com/view/student/stuDashboard.fxml");
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
    private void gotoCourse() {
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
    private void handleMedical() {
        loadUI("/com/view/student/stuMedical.fxml");
    }
    @FXML
    private void handleYourDetails() {
        loadUI("/com/view/student/stuDetails.fxml");
    }
}