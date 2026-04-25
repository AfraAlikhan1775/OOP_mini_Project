package com.controller.Lecturer;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.lang.reflect.Method;
import java.util.Optional;

public class MainController {

    @FXML private StackPane contentArea;
    @FXML private Label lecturerNameLabel;

    private String lecturerEmpId;

    public void setLecturerEmpId(String lecturerEmpId) {
        this.lecturerEmpId = lecturerEmpId;

        if (lecturerNameLabel != null) {
            lecturerNameLabel.setText(lecturerEmpId);
        }

        loadDashboard();
    }

    @FXML
    private void handleDashboard() {
        loadDashboard();
    }

    @FXML
    private void handleProfile() {
        loadProfile();
    }

    @FXML
    private void handleMedical() {
        loadUI("/com/view/Lec_N/lecturer_medical.fxml");
    }

    @FXML
    private void handleTimetable() {
        loadTimetable();
    }

    @FXML
    private void handleNotice() {
        loadNotices();
    }

    @FXML
    private void handleMarks() {
        loadMarks();
    }

    @FXML
    private void handleResults() {
        loadStudentResults();
    }

    @FXML
    private void handleAttendance() {
        loadAttendance();
    }

    @FXML
    private void handleLogout() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Logout");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to logout?");

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/com/view/Login.fxml")
                );

                Parent root = loader.load();

                Stage stage = (Stage) contentArea.getScene().getWindow();

                Scene scene = new Scene(root);
                stage.setScene(scene);

                stage.setMaximized(false);
                stage.setFullScreen(false);

                stage.setWidth(900);
                stage.setHeight(600);

                stage.setResizable(false);
                stage.setTitle("Login");
                stage.centerOnScreen();

            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Logout Error", "Cannot load login page.");
            }
        }
    }

    private void loadDashboard() {
        loadUI("/com/view/Lec_N/dashboard.fxml");
    }

    private void loadProfile() {
        loadUI("/com/view/Lec_N/profile.fxml");
    }

    private void loadTimetable() {
        loadUI("/com/view/Lec_N/timetable.fxml");
    }

    private void loadNotices() {
        loadUI("/com/view/Lec_N/notices.fxml");
    }

    private void loadMarks() {
        loadUI("/com/view/Lec_N/marks.fxml");
    }

    private void loadStudentResults() {
        loadUI("/com/view/Lec_N/lecturer_student_results.fxml");
    }

    private void loadAttendance() {
        loadUI("/com/view/Lec_N/attendance.fxml");
    }

    public void loadContent(String fxmlPath) {
        loadUI(fxmlPath);
    }

    private void loadUI(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Node node = loader.load();

            Object controller = loader.getController();
            passLecturerId(controller);

            contentArea.getChildren().setAll(node);

        } catch (Exception e) {
            e.printStackTrace();

            String error = e.getMessage();
            if (e.getCause() != null) {
                error = e.getCause().getMessage();
            }

            showError("Error loading: " + fxmlPath + "\n\n" + error);
        }
    }

    private void passLecturerId(Object controller) {
        if (controller == null || lecturerEmpId == null || lecturerEmpId.isBlank()) {
            return;
        }

        tryCall(controller, "setLecturerEmpId", lecturerEmpId);
        tryCall(controller, "setLecturerId", lecturerEmpId);
        tryCall(controller, "setEmployeeId", lecturerEmpId);
        tryCall(controller, "setEmpId", lecturerEmpId);
    }

    private void tryCall(Object controller, String methodName, String value) {
        try {
            Method method = controller.getClass().getMethod(methodName, String.class);
            method.invoke(controller, value);
        } catch (NoSuchMethodException ignored) {
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showError(String message) {
        Label label = new Label(message);
        label.setStyle("-fx-text-fill:red; -fx-font-size:16px;");
        contentArea.getChildren().setAll(label);
    }

    private void showAlert(String title, String message) {
        Alert error = new Alert(Alert.AlertType.ERROR);
        error.setTitle(title);
        error.setHeaderText(null);
        error.setContentText(message);
        error.showAndWait();
    }
}