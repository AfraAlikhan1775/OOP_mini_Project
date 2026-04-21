package com.controller.Student;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;

public class StuDashboardController {

    @FXML
    private StackPane contentArea;

    @FXML
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
}