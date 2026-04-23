package com.controller.Lecturer;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.StackPane;
import javafx.scene.Parent;

import java.io.IOException;

public class MainController {

    @FXML
    private StackPane contentArea;

    @FXML
    public void initialize() {
        loadUI("dashboard.fxml");
    }

    private void loadUI(String file) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/view/Lec_N/" + file));
            contentArea.getChildren().clear();
            contentArea.getChildren().add(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDashboard() {
        loadUI("dashboard.fxml");
    }

    @FXML
    private void handleCourses() {
        loadUI("courses.fxml");
    }

    @FXML
    private void handleMaterials() {
        System.out.println("Materials clicked");
    }

    @FXML
    private void handleMarks() {
        loadUI("marks.fxml");
    }

    @FXML
    private void handleStudents() {
        loadUI("students.fxml");
    }

    @FXML
    private void handleAttendance() {
        loadUI("attendance.fxml");
    }

    @FXML
    private void handleNotices() {
        loadUI("notices.fxml");
    }

    @FXML
    private void handleTimetable() {
        loadUI("timetable.fxml");
    }

    @FXML
    private void handleProfile() {
        loadUI("profile.fxml");
    }
}