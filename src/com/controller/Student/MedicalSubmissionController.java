package com.controller.Student;

import com.dao.student.StudentMedicalDAO;
import com.model.student.ExamMedicalCourse;
import com.model.student.MedicalSession;
import com.session.StudentSession;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.time.LocalDate;
import java.util.List;

public class MedicalSubmissionController {

    @FXML private ComboBox<String> medicalTypeBox;

    @FXML private VBox attendanceBox;
    @FXML private VBox examBox;

    @FXML private TableView<MedicalSession> sessionTable;
    @FXML private TableColumn<MedicalSession, String> colCourse;
    @FXML private TableColumn<MedicalSession, String> colSession;
    @FXML private TableColumn<MedicalSession, String> colType;
    @FXML private TableColumn<MedicalSession, String> colDate;

    @FXML private ComboBox<ExamMedicalCourse> examCourseBox;
    @FXML private ComboBox<String> examTypeBox;
    @FXML private DatePicker examDatePicker;

    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private TextArea reasonArea;
    @FXML private Label fileNameLabel;
    @FXML private Label messageLabel;

    private File selectedFile;
    private final StudentMedicalDAO dao = new StudentMedicalDAO();

    @FXML
    public void initialize() {
        medicalTypeBox.setItems(FXCollections.observableArrayList("ATTENDANCE", "EXAM"));
        medicalTypeBox.getSelectionModel().select("ATTENDANCE");

        examTypeBox.setItems(FXCollections.observableArrayList(
                "Mid Exam",
                "Final Theory",
                "Final Practical"
        ));

        sessionTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        colCourse.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getCourseId()));

        colSession.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getSessionName()));

        colType.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getType()));

        colDate.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getAttendanceDate()));

        handleMedicalTypeChange();
        loadAbsentSessions();
        loadExamCourses();
    }

    @FXML
    private void handleMedicalTypeChange() {
        String type = medicalTypeBox.getValue();

        boolean isExam = "EXAM".equalsIgnoreCase(type);

        attendanceBox.setVisible(!isExam);
        attendanceBox.setManaged(!isExam);

        examBox.setVisible(isExam);
        examBox.setManaged(isExam);

        if (isExam) {
            showMessage("Exam medical selected.");
        } else {
            showMessage("Attendance medical selected.");
        }
    }

    private void loadAbsentSessions() {
        String regNo = StudentSession.getUsername();

        if (regNo == null || regNo.isBlank()) {
            showMessage("Student session not found.");
            return;
        }

        sessionTable.setItems(FXCollections.observableArrayList(
                dao.getAbsentSessions(regNo)
        ));
    }

    private void loadExamCourses() {
        String regNo = StudentSession.getUsername();

        if (regNo == null || regNo.isBlank()) {
            return;
        }

        examCourseBox.setItems(FXCollections.observableArrayList(
                dao.getStudentCoursesForExamMedical(regNo)
        ));
    }

    @FXML
    private void handleChooseFile() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choose Medical PDF");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
        );

        selectedFile = chooser.showOpenDialog(reasonArea.getScene().getWindow());

        if (selectedFile != null) {
            fileNameLabel.setText(selectedFile.getName());
            showMessage("File selected.");
        }
    }

    @FXML
    private void handleSubmitMedical() {
        String regNo = StudentSession.getUsername();

        if (regNo == null || regNo.isBlank()) {
            showMessage("Student session not found.");
            return;
        }

        if (!validateCommonFields()) {
            return;
        }

        boolean success;

        if ("EXAM".equalsIgnoreCase(medicalTypeBox.getValue())) {
            success = submitExamMedical(regNo);
        } else {
            success = submitAttendanceMedical(regNo);
        }

        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Medical submitted successfully.");
            handleClear();
            loadAbsentSessions();
            loadExamCourses();
        } else {
            showMessage("Medical submission failed.");
        }
    }

    private boolean validateCommonFields() {
        if (startDatePicker.getValue() == null) {
            showMessage("Select start date.");
            return false;
        }

        if (endDatePicker.getValue() == null) {
            showMessage("Select end date.");
            return false;
        }

        if (endDatePicker.getValue().isBefore(startDatePicker.getValue())) {
            showMessage("End date cannot be before start date.");
            return false;
        }

        if (reasonArea.getText() == null || reasonArea.getText().trim().isEmpty()) {
            showMessage("Enter reason.");
            return false;
        }

        if (selectedFile == null) {
            showMessage("Choose medical PDF.");
            return false;
        }

        return true;
    }

    private boolean submitAttendanceMedical(String regNo) {
        List<MedicalSession> selectedSessions = sessionTable.getSelectionModel().getSelectedItems();

        if (selectedSessions == null || selectedSessions.isEmpty()) {
            showMessage("Select at least one absent session.");
            return false;
        }

        return dao.submitAttendanceMedical(
                regNo,
                selectedFile.getAbsolutePath(),
                startDatePicker.getValue(),
                endDatePicker.getValue(),
                reasonArea.getText().trim(),
                selectedSessions
        );
    }

    private boolean submitExamMedical(String regNo) {
        ExamMedicalCourse course = examCourseBox.getValue();
        String examType = examTypeBox.getValue();
        LocalDate examDate = examDatePicker.getValue();

        if (course == null) {
            showMessage("Select course.");
            return false;
        }

        if (examType == null || examType.isBlank()) {
            showMessage("Select exam type.");
            return false;
        }

        if (examDate == null) {
            showMessage("Select exam date.");
            return false;
        }

        return dao.submitExamMedical(
                regNo,
                selectedFile.getAbsolutePath(),
                startDatePicker.getValue(),
                endDatePicker.getValue(),
                reasonArea.getText().trim(),
                course.getCourseId(),
                examType,
                examDate
        );
    }

    @FXML
    private void handleClear() {
        startDatePicker.setValue(null);
        endDatePicker.setValue(null);
        examDatePicker.setValue(null);

        reasonArea.clear();

        selectedFile = null;
        fileNameLabel.setText("No file selected");

        sessionTable.getSelectionModel().clearSelection();
        examCourseBox.getSelectionModel().clearSelection();
        examTypeBox.getSelectionModel().clearSelection();

        showMessage("");
    }

    private void showMessage(String message) {
        messageLabel.setText(message);
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}