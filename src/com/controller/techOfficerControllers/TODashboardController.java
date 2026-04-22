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
        dateLabel.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMMM yyyy")));
        loadUI("/com/view/techOfficer/to_home.fxml");
    }

    public void setLoggedInUser(String username) {
        this.loggedInUsername = username;
        // Reload home with context
        loadUI("/com/view/techOfficer/to_home.fxml");
    }

    public String getLoggedInUsername() {
        return loggedInUsername;
    }

    private void loadUI(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            // Pass the username to child controllers
            Object controller = loader.getController();
            if (controller instanceof TOProfileController) {
                ((TOProfileController) controller).setEmpId(loggedInUsername);
            } else if (controller instanceof TOAttendanceController) {
                ((TOAttendanceController) controller).setMarkedBy(loggedInUsername);
            } else if (controller instanceof TOMedicalController) {
                ((TOMedicalController) controller).setAddedBy(loggedInUsername);
//            } else if (controller instanceof TOTimetableController) {
//                ((TOTimetableController) controller).setEmpId(loggedInUsername);
//            } else if (controller instanceof TOHomeController) {
                ((TOHomeController) controller).setEmpId(loggedInUsername);
            }

            contentArea.getChildren().setAll(root);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleHome() {
        loadUI("/com/view/techOfficer/to_home.fxml");
    }

    @FXML
    private void handleProfile() {
        loadUI("/com/view/techOfficer/to_profile.fxml");
    }

    @FXML
    private void handleAttendance() {
        loadUI("/com/view/techOfficer/to_attendance.fxml");
    }

    @FXML
    private void handleMedical() {
        loadUI("/com/view/techOfficer/to_medical.fxml");
    }

    @FXML
    private void handleNotices() {
        loadUI("/com/view/techOfficer/to_notices.fxml");
    }

    @FXML
    private void handleTimetable() {
        loadUI("/com/view/techOfficer/to_timetable.fxml");
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
