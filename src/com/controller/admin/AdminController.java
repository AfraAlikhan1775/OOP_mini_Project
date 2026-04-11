package com.controller.admin;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class AdminController{
    @FXML
    private StackPane contentArea;

    public void initialize()  {
        loadUI("/com/view/admin/dash_boardhome.fxml");
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
    private void handleDashboard(){
        loadUI("/com/view/admin/dash_boardhome.fxml");
    }

    @FXML
    private void handleUserprofile(){
        loadUI("/com/view/admin/student.fxml");
    }

    @FXML
    private void handleLecturer(){
        loadUI("/com/view/admin/lecturer.fxml");
    }

    @FXML
    private void handleTO(){
        loadUI("/com/view/admin/to.fxml");
    }

    @FXML
    private void handleCourse(){
        loadUI("/com/view/admin/course.fxml");
    }

    @FXML
    private void handleTimetable(){
        loadUI("/com/view/admin/timetable.fxml");
    }

    @FXML
    private void handleNotice(){
        loadUI("/com/view/admin/notice.fxml");
    }

    @FXML
    private void handleStudents(){
        loadUI("/com/view/admin/student.fxml");
    }


}