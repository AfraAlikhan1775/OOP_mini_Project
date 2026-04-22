package com.controller.admin;

import com.dao.admin.CourseDAO;
import com.model.admin.Course;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class AddCourseController {

    @FXML private ComboBox<String> departmentComboBox;
    @FXML private ComboBox<String> yearComboBox;
    @FXML private ComboBox<String> semesterComboBox;
    @FXML private TextField courseIdField;
    @FXML private TextField courseNameField;
    @FXML private TextField coordinatorField;
    @FXML private TextField creditsField;
    @FXML private ImageView courseImageView;

    private File selectedFile;

    private final CourseDAO courseDAO = new CourseDAO();

    @FXML
    public void initialize() {
        departmentComboBox.getItems().addAll("ICT", "BST", "ET");
        yearComboBox.getItems().addAll("1", "2", "3", "4");
        semesterComboBox.getItems().addAll("1", "2");
    }

    @FXML
    private void handleUploadImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
        );

        File file = fileChooser.showOpenDialog(courseImageView.getScene().getWindow());

        if (file != null) {
            selectedFile = file;
            courseImageView.setImage(new Image(file.toURI().toString()));
        }
    }

    @FXML
    private void handleAddCourse() {
        String department = departmentComboBox.getValue();
        String year = yearComboBox.getValue();
        String semester = semesterComboBox.getValue();
        String courseId = courseIdField.getText().trim();
        String courseName = courseNameField.getText().trim();
        String coordinator = coordinatorField.getText().trim();
        String creditsText = creditsField.getText().trim();

        if (department == null || year == null || semester == null ||
                courseId.isEmpty() || courseName.isEmpty() ||
                coordinator.isEmpty() || creditsText.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please fill all required fields.");
            return;
        }

        int credits;
        try {
            credits = Integer.parseInt(creditsText);
            if (credits <= 0) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "Credits must be greater than 0.");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Credits must be a number.");
            return;
        }

        if (courseDAO.existsByCourseId(courseId)) {
            showAlert(Alert.AlertType.ERROR, "Duplicate Error", "Course ID already exists.");
            return;
        }

        if (!courseDAO.isValidLecturer(coordinator)) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Invalid Lecturer Employee ID.");
            return;
        }

        String imagePath = selectedFile != null ? selectedFile.getAbsolutePath() : null;

        Course course = new Course(
                department,
                year,
                semester,
                courseId,
                courseName,
                coordinator,
                credits,
                imagePath,
                "Active"
        );

        boolean saved = courseDAO.saveCourse(course);

        if (saved) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Course added successfully.");
            Stage stage = (Stage) courseIdField.getScene().getWindow();
            stage.close();
        } else {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to save course.");
        }
    }

    @FXML
    private void handleClear() {
        departmentComboBox.setValue(null);
        yearComboBox.setValue(null);
        semesterComboBox.setValue(null);
        courseIdField.clear();
        courseNameField.clear();
        coordinatorField.clear();
        creditsField.clear();
        courseImageView.setImage(null);
        selectedFile = null;
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}