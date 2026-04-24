package com.controller.admin;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class TOController {

    @FXML
    private void navigateAddTO() {
        openWindow("/com/view/admin/add_technicalofficer.fxml", "Add Technical Officer");
    }

    @FXML
    private void navigateAddCourse() {
        openWindow("/com/view/admin/add_course.fxml", "Add Course");
    }

    @FXML
    private void navigateAddTimetable() {
        openWindow("/com/view/admin/add_timetable.fxml", "Add Timetable");
    }

    @FXML
    private void navigateAddNotice() {
        openWindow("/com/view/admin/add_notice.fxml", "Add Notice");
    }

    private void openWindow(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}