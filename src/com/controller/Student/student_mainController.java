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
        loadUI("/com/view/Student/stuDashboard.fxml");
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
        loadUI("/com/view/Student/stuDashboard.fxml");
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

}