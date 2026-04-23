package com.controller.Lecturer;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class MainController {

    @FXML
    private StackPane contentArea;

    private String lecturerEmpId;

    public void setLecturerEmpId(String lecturerEmpId) {
        this.lecturerEmpId = lecturerEmpId;
        System.out.println("Logged in Lecturer EMP ID = " + lecturerEmpId);
    }

    @FXML
    public void initialize() {
        loadUI("dashboard.fxml");
    }

    private void loadUI(String file) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/view/Lec_N/" + file));
            Parent root = loader.load();

            Object controller = loader.getController();

            if (controller instanceof LecturerCoursesController) {
                ((LecturerCoursesController) controller).setLecturerEmpId(lecturerEmpId);
            }

            if (controller instanceof LecturerMarksGroupController) {
                ((LecturerMarksGroupController) controller).setLecturerId(lecturerEmpId);
            }

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