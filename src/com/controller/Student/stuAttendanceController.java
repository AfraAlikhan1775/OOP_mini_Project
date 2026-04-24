package com.controller.Student;

import com.dao.student.StudentAcademicDAO;
import com.model.student.StudentAttendanceDetail;
import com.model.student.StudentSubjectAttendance;
import com.session.StudentSession;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;

public class stuAttendanceController {

    @FXML private VBox subjectCardBox;
    @FXML private Label titleLabel;
    @FXML private Label percentageLabel;
    @FXML private HBox barBox;

    @FXML private Label presentLabel;
    @FXML private Label medicalLabel;
    @FXML private Label rejectedMedicalLabel;
    @FXML private Label absentLabel;

    @FXML private TableView<StudentAttendanceDetail> detailTable;
    @FXML private TableColumn<StudentAttendanceDetail, String> idCol;
    @FXML private TableColumn<StudentAttendanceDetail, String> dateCol;
    @FXML private TableColumn<StudentAttendanceDetail, String> sessionCol;
    @FXML private TableColumn<StudentAttendanceDetail, String> statusCol;

    private final StudentAcademicDAO dao = new StudentAcademicDAO();

    @FXML
    public void initialize() {
        setupTable();
        loadSubjects();
    }

    private void setupTable() {
        idCol.setCellValueFactory(data ->
                new SimpleStringProperty(String.valueOf(data.getValue().getAttendanceId())));

        dateCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getDate()));

        sessionCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getSessionId()));

        statusCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getStatus()));
    }

    private void loadSubjects() {
        subjectCardBox.getChildren().clear();

        String regNo = StudentSession.getUsername();

        List<StudentSubjectAttendance> subjects = dao.getStudentSubjects(regNo);

        if (subjects.isEmpty()) {
            subjectCardBox.getChildren().add(new Label("No attendance records found for this student."));
            return;
        }

        for (StudentSubjectAttendance s : subjects) {
            subjectCardBox.getChildren().add(createSubjectCard(s));
        }
    }

    private VBox createSubjectCard(StudentSubjectAttendance s) {
        VBox card = new VBox(7);
        card.setStyle("""
                -fx-background-color:white;
                -fx-padding:16;
                -fx-background-radius:12;
                -fx-border-color:#d9e2ec;
                -fx-border-radius:12;
                -fx-cursor:hand;
                """);

        Label course = new Label(s.getCourseId() + " - " + s.getCourseName());
        course.setStyle("-fx-font-size:16px; -fx-font-weight:bold; -fx-text-fill:#0b1f36;");

        Label type = new Label("Type: " + s.getType());
        Label percent = new Label(String.format("Attendance: %.1f%%", s.getPercentage()));

        card.getChildren().addAll(course, type, percent);
        card.setOnMouseClicked(e -> showSubjectDetails(s));

        return card;
    }

    private void showSubjectDetails(StudentSubjectAttendance s) {
        titleLabel.setText(s.getCourseId() + " - " + s.getCourseName() + " (" + s.getType() + ")");
        percentageLabel.setText(String.format("%.1f%%", s.getPercentage()));

        presentLabel.setText("Attend: " + s.getPresent());
        medicalLabel.setText("Medical: " + s.getMedical());
        rejectedMedicalLabel.setText("Rejected Medical: " + s.getRejectedMedical());
        absentLabel.setText("Absent: " + s.getAbsent());

        drawBar(s);

        String regNo = StudentSession.getUsername();
        detailTable.setItems(FXCollections.observableArrayList(
                dao.getAttendanceDetails(regNo, s.getCourseId(), s.getType())
        ));
    }

    private void drawBar(StudentSubjectAttendance s) {
        barBox.getChildren().clear();

        int total = s.getTotal();

        if (total == 0) {
            barBox.getChildren().add(new Label("No attendance records."));
            return;
        }

        addBarPart("Attend", s.getPresent(), total, "#198754");
        addBarPart("Medical", s.getMedical(), total, "#0dcaf0");
        addBarPart("Rejected", s.getRejectedMedical(), total, "#fd7e14");
        addBarPart("Absent", s.getAbsent(), total, "#dc3545");
    }

    private void addBarPart(String text, int count, int total, String color) {
        if (count <= 0) return;

        double width = Math.max(70, (count * 520.0) / total);

        Label part = new Label(text + " " + count);
        part.setMinWidth(width);
        part.setStyle(
                "-fx-background-color:" + color + ";" +
                        "-fx-text-fill:white;" +
                        "-fx-alignment:center;" +
                        "-fx-padding:8;" +
                        "-fx-font-weight:bold;"
        );

        barBox.getChildren().add(part);
    }
}