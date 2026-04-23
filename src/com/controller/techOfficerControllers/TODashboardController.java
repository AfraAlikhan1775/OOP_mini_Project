package com.controller.techOfficerControllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TODashboardController {

    @FXML private StackPane contentArea;
    @FXML private Label dateLabel;

    private String loggedInUsername;

    @FXML
    public void initialize() {
        if (dateLabel != null) {
            dateLabel.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMMM yyyy")));
        }
        loadContent("/com/view/techOfficer/to_home.fxml");
    }

    public void setLoggedInUser(String username) {
        this.loggedInUsername = username;
        loadContent("/com/view/techOfficer/to_home.fxml");
    }

    public String getLoggedInUsername() {
        return loggedInUsername;
    }

    public void loadContent(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Object controller = loader.getController();

            if (controller instanceof TOProfileController) {
                ((TOProfileController) controller).setEmpId(loggedInUsername);
            } else if (controller instanceof TOMedicalController) {
                ((TOMedicalController) controller).setAddedBy(loggedInUsername);
            } else if (controller instanceof TOHomeController) {
                ((TOHomeController) controller).setEmpId(loggedInUsername);
            } else if (controller instanceof TONoticeController) {
                ((TONoticeController) controller).setEmpId(loggedInUsername);
            } else if (controller instanceof TOAttendanceController) {
                ((TOAttendanceController) controller).setDashboardController(this);
            } else if (controller instanceof AddAttendanceController) {
                ((AddAttendanceController) controller).setDashboardController(this);
            } else if (controller instanceof AddSessionController) {
                ((AddSessionController) controller).setDashboardController(this);
            }

            contentArea.getChildren().setAll(root);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleHome() {
        loadContent("/com/view/techOfficer/to_home.fxml");
    }

    @FXML
    private void handleProfile() {
        loadContent("/com/view/techOfficer/to_profile.fxml");
    }

    @FXML
    private void handleAttendance() {
        loadContent("/com/view/techOfficer/to_attendance.fxml");
    }

    @FXML
    private void handleMedical() {
        loadContent("/com/view/techOfficer/to_medical.fxml");
    }

    @FXML
    private void handleNotices() {
        loadContent("/com/view/techOfficer/to_notices.fxml");
    }

    @FXML
    private void handleTimetable() {
        loadContent("/com/view/techOfficer/to_timetable.fxml");
    }

    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/view/Login.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) contentArea.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("FMS Login");
            stage.setMaximized(false);
            stage.setWidth(919);
            stage.setHeight(560);
            stage.centerOnScreen();
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}