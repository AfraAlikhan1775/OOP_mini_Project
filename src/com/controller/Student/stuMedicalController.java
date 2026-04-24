package com.controller.Student;

import com.dao.student.MedicalDAO;
import com.model.student.MedicalRequest;
import com.model.student.MedicalSession;
import com.session.StudentSession;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.time.LocalDate;
import java.util.List;

public class stuMedicalController {

    @FXML private VBox medicalCardBox;
    @FXML private VBox addMedicalBox;

    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private TextArea reasonArea;
    @FXML private Label fileLabel;
    @FXML private Label messageLabel;

    @FXML private TableView<MedicalSession> sessionTable;
    @FXML private TableColumn<MedicalSession, Boolean> selectColumn;
    @FXML private TableColumn<MedicalSession, String> dateColumn;
    @FXML private TableColumn<MedicalSession, String> courseColumn;
    @FXML private TableColumn<MedicalSession, String> sessionColumn;
    @FXML private TableColumn<MedicalSession, String> typeColumn;

    private final MedicalDAO medicalDAO = new MedicalDAO();
    private File selectedFile;

    @FXML
    public void initialize() {
        setupTable();

        addMedicalBox.setVisible(false);
        addMedicalBox.setManaged(false);

        loadMedicalCards();
    }

    private void setupTable() {
        sessionTable.setEditable(true);

        selectColumn.setCellValueFactory(cellData ->
                cellData.getValue().selectedProperty()
        );
        selectColumn.setCellFactory(CheckBoxTableCell.forTableColumn(selectColumn));

        dateColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getAttendanceDate()));

        courseColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getCourseId()));

        sessionColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(
                        cellData.getValue().getSessionId() + " - " + cellData.getValue().getSessionName()
                ));

        typeColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getType()));
    }

    @FXML
    private void handleShowAddMedical() {
        addMedicalBox.setVisible(true);
        addMedicalBox.setManaged(true);
        messageLabel.setText("");
    }

    @FXML
    private void handleChooseFile() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select Medical File");

        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Medical Files", "*.png", "*.jpg", "*.jpeg", "*.pdf")
        );

        selectedFile = chooser.showOpenDialog(fileLabel.getScene().getWindow());

        if (selectedFile != null) {
            fileLabel.setText(selectedFile.getAbsolutePath());
        }
    }

    @FXML
    private void handleLoadSessions() {
        String regNo = StudentSession.getUsername();

        if (regNo == null || regNo.isBlank()) {
            messageLabel.setText("Student session not found. Please login again.");
            return;
        }

        LocalDate start = startDatePicker.getValue();
        LocalDate end = endDatePicker.getValue();

        if (start == null || end == null) {
            messageLabel.setText("Please select start date and end date.");
            return;
        }

        if (end.isBefore(start)) {
            messageLabel.setText("End date cannot be before start date.");
            return;
        }

        List<MedicalSession> sessions = medicalDAO.getSessionsForDateGap(regNo, start, end);
        sessionTable.setItems(FXCollections.observableArrayList(sessions));

        if (sessions.isEmpty()) {
            messageLabel.setText("No absent sessions found for this date range.");
        } else {
            messageLabel.setText("Select absent sessions covered by your medical.");
        }
    }

    @FXML
    private void handleSubmitMedical() {
        String regNo = StudentSession.getUsername();

        if (regNo == null || regNo.isBlank()) {
            messageLabel.setText("Student session not found. Please login again.");
            return;
        }

        if (selectedFile == null) {
            messageLabel.setText("Please upload medical PDF/photo.");
            return;
        }

        LocalDate start = startDatePicker.getValue();
        LocalDate end = endDatePicker.getValue();

        if (start == null || end == null) {
            messageLabel.setText("Please select start date and end date.");
            return;
        }

        if (end.isBefore(start)) {
            messageLabel.setText("End date cannot be before start date.");
            return;
        }

        List<MedicalSession> selectedSessions = sessionTable.getItems()
                .stream()
                .filter(MedicalSession::isSelected)
                .toList();

        if (selectedSessions.isEmpty()) {
            messageLabel.setText("Please select at least one absent session.");
            return;
        }

        boolean saved = medicalDAO.submitMedical(
                regNo,
                selectedFile.getAbsolutePath(),
                start,
                end,
                reasonArea.getText(),
                selectedSessions
        );

        if (saved) {
            messageLabel.setText("Medical submitted successfully.");
            clearForm();
            loadMedicalCards();
        } else {
            messageLabel.setText("Medical not submitted. Check database connection or selected sessions.");
        }
    }

    private void clearForm() {
        selectedFile = null;
        fileLabel.setText("No file selected");
        startDatePicker.setValue(null);
        endDatePicker.setValue(null);
        reasonArea.clear();
        sessionTable.getItems().clear();

        addMedicalBox.setVisible(false);
        addMedicalBox.setManaged(false);
    }

    private void loadMedicalCards() {
        medicalCardBox.getChildren().clear();

        String regNo = StudentSession.getUsername();

        if (regNo == null || regNo.isBlank()) {
            medicalCardBox.getChildren().add(new Label("Student session not found."));
            return;
        }

        List<MedicalRequest> requests = medicalDAO.getStudentMedicalRequests(regNo);

        if (requests.isEmpty()) {
            Label empty = new Label("No medical submissions found.");
            empty.setStyle("-fx-font-size: 14px; -fx-text-fill: #555;");
            medicalCardBox.getChildren().add(empty);
            return;
        }

        for (MedicalRequest request : requests) {
            medicalCardBox.getChildren().add(createMedicalCard(request));
        }
    }

    private VBox createMedicalCard(MedicalRequest request) {
        VBox card = new VBox(8);
        card.setStyle("""
                -fx-background-color: white;
                -fx-padding: 16;
                -fx-background-radius: 12;
                -fx-border-color: #d9e2ec;
                -fx-border-radius: 12;
                """);

        HBox top = new HBox(12);

        Label title = new Label("Medical ID: " + request.getMedicalId());
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Label status = new Label(safe(request.getStatus()));
        status.setStyle(getStatusStyle(request.getStatus()));

        top.getChildren().addAll(title, status);

        Label date = new Label("Date: " + safe(request.getStartDate()) + " to " + safe(request.getEndDate()));
        Label reason = new Label("Reason: " + safe(request.getReason()));
        Label file = new Label("File: " + safe(request.getFilePath()));
        Label approvedBy = new Label("Approved By: " + safe(request.getApprovedBy()));
        Label submitted = new Label("Submitted At: " + safe(request.getSubmittedAt()));

        reason.setWrapText(true);
        file.setWrapText(true);

        card.getChildren().addAll(top, date, reason, file, approvedBy, submitted);

        return card;
    }

    private String getStatusStyle(String status) {
        String base = "-fx-padding: 4 10 4 10; -fx-background-radius: 20; -fx-font-weight: bold;";

        if ("Approved".equalsIgnoreCase(status) || "Verified".equalsIgnoreCase(status)) {
            return base + "-fx-background-color: #d1e7dd; -fx-text-fill: #0f5132;";
        }

        if ("Rejected".equalsIgnoreCase(status)) {
            return base + "-fx-background-color: #f8d7da; -fx-text-fill: #842029;";
        }

        return base + "-fx-background-color: #fff3cd; -fx-text-fill: #664d03;";
    }

    private String safe(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }
}