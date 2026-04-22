package com.controller.Student;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;


import java.net.URL;

public class StuDashboardController {


    @FXML
    private StackPane contentArea;

    @FXML
    public void initialize() {
        loadUI("/com/Resources/view/Student/stuDashboard.fxml");
    }

    private void loadUI(String fxml) {
        try {
            URL resource = getClass().getResource(fxml);

            if (resource == null) {
                System.out.println("FXML not found: " + fxml);
                return;
            }

            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();
            contentArea.getChildren().setAll(root);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDashboard() {
        loadUI("/com/Resources/view/Student/stuDashboard.fxml");
    }

    @FXML
    private void handleAttendance() {
        loadUI("/com/Resources/view/Student/stuAttendance.fxml");
    }

    @FXML
    private void handleTimetable() {
        loadUI("/com/Resources/view/Student/stuTimetable.fxml");
    }

    @FXML
    private void handleNotice() {
        loadUI("/com/Resources/view/Student/stuNotice.fxml");
    }

    @FXML
    private void handleGrades() {
        loadUI("/com/Resources/view/Student/stuGrades.fxml");
    }
}