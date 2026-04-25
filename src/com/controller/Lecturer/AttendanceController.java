package com.controller.Lecturer;

import com.dao.Lecturer.LecturerAttendanceDAO;
import com.model.admin.AttendanceGroup;
import com.model.admin.AttendanceRecord;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;

public class AttendanceController {

    @FXML private ComboBox<String> courseBox;
    @FXML private ComboBox<String> typeBox;
    @FXML private ComboBox<String> sessionBox;
    @FXML private DatePicker fromDatePicker;
    @FXML private DatePicker toDatePicker;
    @FXML private Label statusLabel;
    @FXML private VBox cardContainer;

    private String lecturerEmpId;

    private final LecturerAttendanceDAO dao = new LecturerAttendanceDAO();

    public void setLecturerEmpId(String lecturerEmpId) {
        this.lecturerEmpId = lecturerEmpId;
        loadCourses();
        loadAttendanceCards();
    }

    @FXML
    public void initialize() {
        typeBox.getItems().addAll("Theory", "Practical", "Tutorial");

        courseBox.setOnAction(e -> {
            loadTypes();
            loadSessions();
        });

        typeBox.setOnAction(e -> loadSessions());
    }

    private void loadCourses() {
        courseBox.getItems().clear();

        if (lecturerEmpId == null || lecturerEmpId.isBlank()) {
            statusLabel.setText("Lecturer ID not loaded.");
            return;
        }

        List<String> courses = dao.getLecturerCourseIds(lecturerEmpId);
        courseBox.setItems(FXCollections.observableArrayList(courses));

        if (courses.isEmpty()) {
            statusLabel.setText("No courses assigned to this lecturer.");
        }
    }

    private void loadTypes() {
        String courseId = courseBox.getValue();

        if (courseId == null || courseId.isBlank()) {
            return;
        }

        List<String> dbTypes = dao.getTypesByCourse(courseId);

        if (!dbTypes.isEmpty()) {
            typeBox.setItems(FXCollections.observableArrayList(dbTypes));
        }
    }

    private void loadSessions() {
        sessionBox.getItems().clear();

        String courseId = courseBox.getValue();
        String type = typeBox.getValue();

        if (courseId == null || courseId.isBlank() || type == null || type.isBlank()) {
            return;
        }

        List<String> sessions = dao.getSessionIdsByCourseAndType(courseId, type);
        sessionBox.setItems(FXCollections.observableArrayList(sessions));
    }

    @FXML
    private void handleFilter() {
        loadAttendanceCards();
    }

    @FXML
    private void handleReset() {
        courseBox.setValue(null);
        typeBox.setValue(null);
        sessionBox.setValue(null);
        fromDatePicker.setValue(null);
        toDatePicker.setValue(null);

        typeBox.setItems(FXCollections.observableArrayList("Theory", "Practical", "Tutorial"));
        sessionBox.getItems().clear();

        loadAttendanceCards();
    }

    private void loadAttendanceCards() {
        if (cardContainer == null) {
            return;
        }

        cardContainer.getChildren().clear();

        if (lecturerEmpId == null || lecturerEmpId.isBlank()) {
            cardContainer.getChildren().add(message("Lecturer ID not loaded."));
            return;
        }

        String courseId = courseBox == null ? null : courseBox.getValue();
        String type = typeBox == null ? null : typeBox.getValue();
        String sessionId = sessionBox == null ? null : sessionBox.getValue();
        LocalDate fromDate = fromDatePicker == null ? null : fromDatePicker.getValue();
        LocalDate toDate = toDatePicker == null ? null : toDatePicker.getValue();

        if (fromDate != null && toDate != null && fromDate.isAfter(toDate)) {
            statusLabel.setText("From date cannot be after To date.");
            return;
        }

        List<AttendanceGroup> groups = dao.getAttendanceGroups(
                lecturerEmpId,
                courseId,
                type,
                sessionId,
                fromDate,
                toDate
        );

        if (groups.isEmpty()) {
            cardContainer.getChildren().add(message("No attendance found for selected filters."));
            statusLabel.setText("No attendance records.");
            return;
        }

        for (AttendanceGroup group : groups) {
            cardContainer.getChildren().add(createCard(group));
        }

        statusLabel.setText(groups.size() + " attendance record(s) loaded.");
    }

    private VBox createCard(AttendanceGroup group) {
        LecturerAttendanceDAO.AttendanceSummary summary = dao.getSummary(group.getId());

        VBox card = new VBox(10);
        card.setPadding(new Insets(18));
        card.setStyle("""
                -fx-background-color:white;
                -fx-background-radius:16;
                -fx-border-color:#dbe3ea;
                -fx-border-radius:16;
                """);

        Label title = new Label("📘 " + group.getCourseId() + " | " + group.getType());
        title.setStyle("-fx-font-size:18px; -fx-font-weight:bold; -fx-text-fill:#0b1f36;");

        Label info1 = new Label("Year: " + group.getYear() + " | Session: " + group.getSessionId());
        info1.setStyle("-fx-text-fill:#475569;");

        Label info2 = new Label("Date: " + group.getAttendanceDate());
        info2.setStyle("-fx-text-fill:#475569;");

        Label summaryLabel = new Label(
                "Present: " + summary.getPresent()
                        + " | Absent: " + summary.getAbsent()
                        + " | Medical: " + summary.getMedical()
                        + " | Attendance: " + String.format("%.2f", summary.getPercentage()) + "%"
        );
        summaryLabel.setStyle("-fx-font-weight:bold; -fx-text-fill:#14532d;");

        ProgressBar bar = new ProgressBar(summary.getPercentage() / 100.0);
        bar.setPrefWidth(450);

        Button viewBtn = new Button("View Students");
        viewBtn.setStyle("-fx-background-color:#0b1f36; -fx-text-fill:white; -fx-font-weight:bold;");
        viewBtn.setOnAction(e -> openDetails(group));

        HBox bottom = new HBox(viewBtn);
        bottom.setAlignment(Pos.CENTER_RIGHT);

        card.getChildren().addAll(title, info1, info2, summaryLabel, bar, bottom);

        return card;
    }

    private void openDetails(AttendanceGroup group) {
        List<AttendanceRecord> records = dao.getAttendanceRecords(group.getId());

        VBox root = new VBox(12);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color:#f8fafc;");

        Label heading = new Label("Attendance Details");
        heading.setStyle("-fx-font-size:22px; -fx-font-weight:bold; -fx-text-fill:#0b1f36;");

        Label sub = new Label(
                "Course: " + group.getCourseId()
                        + " | Type: " + group.getType()
                        + " | Session: " + group.getSessionId()
                        + " | Date: " + group.getAttendanceDate()
        );
        sub.setStyle("-fx-text-fill:#475569;");

        TableView<AttendanceRecord> table = new TableView<>();
        table.setPrefHeight(450);

        TableColumn<AttendanceRecord, String> regCol = new TableColumn<>("Reg No");
        regCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getRegNo()));
        regCol.setPrefWidth(250);

        TableColumn<AttendanceRecord, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getStatus()));
        statusCol.setPrefWidth(180);

        table.getColumns().addAll(regCol, statusCol);
        table.setItems(FXCollections.observableArrayList(records));

        root.getChildren().addAll(heading, sub, table);

        Stage stage = new Stage();
        stage.setTitle("Attendance Details");
        stage.setScene(new Scene(root, 650, 560));
        stage.show();
    }

    private Label message(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size:15px; -fx-text-fill:#64748b;");
        return label;
    }
}