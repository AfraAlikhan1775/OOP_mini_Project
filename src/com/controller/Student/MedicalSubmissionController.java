package com.controller.Student;

import com.dao.student.StudentMedicalDAO;
import com.database.DatabaseInitializer;
import com.model.student.MedicalSession;
import com.session.StudentSession;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import java.io.File;
import java.sql.*;
import java.util.List;

public class MedicalSubmissionController {

    @FXML private TableView<MedicalSession> sessionTable;
    @FXML private TableColumn<MedicalSession, String> colCourse;
    @FXML private TableColumn<MedicalSession, String> colSession;
    @FXML private TableColumn<MedicalSession, String> colType;
    @FXML private TableColumn<MedicalSession, String> colDate;

    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private TextArea reasonArea;
    @FXML private Label fileNameLabel;
    @FXML private Label messageLabel;

    private File selectedFile;

    private final StudentMedicalDAO dao = new StudentMedicalDAO();

    @FXML
    public void initialize() {
        sessionTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        colCourse.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getCourseId()));

        colSession.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getSessionName()));

        colType.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getType()));

        colDate.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getAttendanceDate()));

        loadAbsentSessions();
    }

    private void loadAbsentSessions() {
        String regNo = StudentSession.getUsername();

        if (regNo == null || regNo.isBlank()) {
            showMessage("Student session not found. Please login again.");
            return;
        }

        List<MedicalSession> list = dao.getAbsentSessions(regNo);
        sessionTable.setItems(FXCollections.observableArrayList(list));

        if (list.isEmpty()) {
            showMessage("No absent sessions available for medical submission.");
        } else {
            showMessage("Select absent session(s), choose PDF, then submit.");
        }
    }

    @FXML
    private void handleChooseFile() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choose Medical PDF");

        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
        );

        selectedFile = chooser.showOpenDialog(sessionTable.getScene().getWindow());

        if (selectedFile != null) {
            fileNameLabel.setText(selectedFile.getName());
            showMessage("File selected.");
        }
    }

    @FXML
    private void handleSubmitMedical() {
        String regNo = StudentSession.getUsername();

        if (regNo == null || regNo.isBlank()) {
            showMessage("Student session not found. Please login again.");
            return;
        }

        if (startDatePicker.getValue() == null) {
            showMessage("Select start date.");
            return;
        }

        if (endDatePicker.getValue() == null) {
            showMessage("Select end date.");
            return;
        }

        if (endDatePicker.getValue().isBefore(startDatePicker.getValue())) {
            showMessage("End date cannot be before start date.");
            return;
        }

        if (reasonArea.getText() == null || reasonArea.getText().trim().isEmpty()) {
            showMessage("Enter medical reason.");
            return;
        }

        if (selectedFile == null) {
            showMessage("Choose medical PDF file.");
            return;
        }

        List<MedicalSession> selectedSessions = sessionTable.getSelectionModel().getSelectedItems();

        if (selectedSessions == null || selectedSessions.isEmpty()) {
            showMessage("Select at least one absent session.");
            return;
        }

        String insertMedical = """
                INSERT INTO medical
                (reg_no, file_path, start_date, end_date, reason, status, submitted_at)
                VALUES (?, ?, ?, ?, ?, 'Pending', CURRENT_TIMESTAMP)
                """;

        String insertSession = """
                INSERT INTO medical_selected_session
                (medical_id, attendance_group_id, course_id, session_id, session_name, type, attendance_date, status)
                VALUES (?, ?, ?, ?, ?, ?, ?, 'Pending')
                """;

        try (Connection conn = DatabaseInitializer.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement medicalPst =
                         conn.prepareStatement(insertMedical, Statement.RETURN_GENERATED_KEYS);
                 PreparedStatement sessionPst =
                         conn.prepareStatement(insertSession)) {

                medicalPst.setString(1, regNo);
                medicalPst.setString(2, selectedFile.getAbsolutePath());
                medicalPst.setDate(3, Date.valueOf(startDatePicker.getValue()));
                medicalPst.setDate(4, Date.valueOf(endDatePicker.getValue()));
                medicalPst.setString(5, reasonArea.getText().trim());

                medicalPst.executeUpdate();

                ResultSet keys = medicalPst.getGeneratedKeys();
                if (!keys.next()) {
                    conn.rollback();
                    showMessage("Medical save failed.");
                    return;
                }

                int medicalId = keys.getInt(1);

                for (MedicalSession s : selectedSessions) {
                    sessionPst.setInt(1, medicalId);
                    sessionPst.setInt(2, s.getAttendanceGroupId());
                    sessionPst.setString(3, s.getCourseId());
                    sessionPst.setString(4, s.getSessionId());
                    sessionPst.setString(5, s.getSessionName());
                    sessionPst.setString(6, s.getType());
                    sessionPst.setDate(7, Date.valueOf(s.getAttendanceDate()));                    sessionPst.addBatch();
                }

                sessionPst.executeBatch();
                conn.commit();

                showAlert(Alert.AlertType.INFORMATION, "Success", "Medical submitted successfully.");
                handleClear();
                loadAbsentSessions();

            } catch (Exception e) {
                conn.rollback();
                e.printStackTrace();
                showMessage("Medical submission failed.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            showMessage("Database connection failed.");
        }
    }

    @FXML
    private void handleClear() {
        startDatePicker.setValue(null);
        endDatePicker.setValue(null);
        reasonArea.clear();
        selectedFile = null;

        if (fileNameLabel != null) {
            fileNameLabel.setText("No file selected");
        }

        if (sessionTable != null) {
            sessionTable.getSelectionModel().clearSelection();
        }

        showMessage("");
    }

    private void showMessage(String message) {
        if (messageLabel != null) {
            messageLabel.setText(message);
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}