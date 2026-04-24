package com.controller.Lecturer;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

import java.net.URL;

public class MainController {

    @FXML
    private StackPane contentArea;

    private String lecturerEmpId;

    public void setLecturerEmpId(String lecturerEmpId) {
        this.lecturerEmpId = lecturerEmpId;
        System.out.println("Logged in Lecturer EMP ID = " + lecturerEmpId);
        loadUI("dashboard.fxml");
    }

    @FXML
    public void initialize() {
        // Do not load here. lecturerEmpId comes after login.
    }

    private void loadUI(String file) {
        try {
            URL resource = getClass().getResource("/com/view/Lec_N/" + file);

            if (resource == null) {
                showError("FXML not found: /com/view/Lec_N/" + file);
                return;
            }

            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();

            Object controller = loader.getController();

            if (controller instanceof LecturerCoursesController c) {
                c.setLecturerEmpId(lecturerEmpId);
            }

            if (controller instanceof LecturerMarksGroupController c) {
                c.setLecturerId(lecturerEmpId);
            }

            if (controller instanceof LecturerMedicalController c) {
                c.setLecturerEmpId(lecturerEmpId);
            }

            contentArea.getChildren().setAll(root);

        } catch (Exception e) {
            e.printStackTrace();
            showError("Cannot load page: " + file + "\n" + e.getMessage());
        }
    }

    private void showError(String message) {
        Label label = new Label(message);
        label.setStyle("-fx-text-fill:red; -fx-font-size:16px; -fx-padding:20;");
        contentArea.getChildren().setAll(label);
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
        loadUI("courses.fxml");
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
    private void handleMedical() {
        loadUI("lecturer_medical.fxml");
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