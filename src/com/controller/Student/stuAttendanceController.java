package com.controller.Student;

import com.dao.student.StudentAcademicDAO;
import com.model.student.ExamEligibilityRow;
import com.model.student.StudentAttendanceDetail;
import com.model.student.StudentSubjectAttendance;
import com.session.StudentSession;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.List;

public class stuAttendanceController {

    @FXML private VBox mainAttendancePane;
    @FXML private VBox detailPane;
    @FXML private VBox eligibilityPane;

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

    @FXML private TableView<ExamEligibilityRow> eligibilityTable;
    @FXML private TableColumn<ExamEligibilityRow, String> courseCodeCol;
    @FXML private TableColumn<ExamEligibilityRow, String> eligibilityStatusCol;

    private final StudentAcademicDAO dao = new StudentAcademicDAO();

    @FXML
    public void initialize() {
        setupAttendanceTable();
        setupEligibilityTable();
        showCardPage();
        loadSubjects();
    }

    private void setupAttendanceTable() {
        idCol.setCellValueFactory(data ->
                new SimpleStringProperty(String.valueOf(data.getValue().getAttendanceId())));
        dateCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getDate()));
        sessionCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getSessionId()));
        statusCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getStatus()));
    }

    private void setupEligibilityTable() {
        courseCodeCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getCourseCode()));
        eligibilityStatusCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getStatus()));
    }

    private void loadSubjects() {
        subjectCardBox.getChildren().clear();

        String regNo = StudentSession.getUsername();
        List<StudentSubjectAttendance> subjects = dao.getStudentSubjects(regNo);

        if (subjects.isEmpty()) {
            subjectCardBox.getChildren().add(new Label("No attendance records found."));
            return;
        }

        for (StudentSubjectAttendance s : subjects) {
            subjectCardBox.getChildren().add(createSubjectCard(s));
        }
    }

    private VBox createSubjectCard(StudentSubjectAttendance s) {
        VBox card = new VBox(8);
        card.setStyle("""
                -fx-background-color:white;
                -fx-padding:18;
                -fx-background-radius:12;
                -fx-border-color:#d9e2ec;
                -fx-border-radius:12;
                -fx-cursor:hand;
                """);

        Label course = new Label(s.getCourseId() + " - " + s.getCourseName());
        course.setStyle("-fx-font-size:17px; -fx-font-weight:bold; -fx-text-fill:#0b1f36;");

        Label type = new Label("Type: " + s.getType());
        Label percent = new Label(String.format("Attendance: %.1f%%", s.getPercentage()));
        Label hint = new Label("Click to view details →");
        hint.setStyle("-fx-text-fill:#0d6efd; -fx-font-weight:bold;");

        card.getChildren().addAll(course, type, percent, hint);

        card.setOnMouseClicked(e -> openDetailsPage(s));

        card.setOnMouseEntered(e -> card.setStyle("""
                -fx-background-color:#f8fbff;
                -fx-padding:18;
                -fx-background-radius:12;
                -fx-border-color:#0d6efd;
                -fx-border-radius:12;
                -fx-cursor:hand;
                """));

        card.setOnMouseExited(e -> card.setStyle("""
                -fx-background-color:white;
                -fx-padding:18;
                -fx-background-radius:12;
                -fx-border-color:#d9e2ec;
                -fx-border-radius:12;
                -fx-cursor:hand;
                """));

        return card;
    }

    private void openDetailsPage(StudentSubjectAttendance s) {
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

        mainAttendancePane.setVisible(false);
        mainAttendancePane.setManaged(false);

        eligibilityPane.setVisible(false);
        eligibilityPane.setManaged(false);

        detailPane.setVisible(true);
        detailPane.setManaged(true);
    }

    private void drawBar(StudentSubjectAttendance s) {
        barBox.getChildren().clear();

        int total = s.getTotal();

        if (total == 0) {
            barBox.getChildren().add(new Label("No attendance records."));
            return;
        }

        addBarPart("Present " + s.getPresent(), s.getPresent(), total, "#198754");
        addBarPart("Medical " + s.getMedical(), s.getMedical(), total, "#0dcaf0");
        addBarPart("Rejected " + s.getRejectedMedical(), s.getRejectedMedical(), total, "#fd7e14");
        addBarPart("Absent " + s.getAbsent(), s.getAbsent(), total, "#dc3545");
    }

    private void addBarPart(String text, int count, int total, String color) {
        if (count <= 0) return;

        double percent = (double) count / total;

        Label part = new Label(text);
        part.setMaxWidth(Double.MAX_VALUE);
        part.setStyle(
                "-fx-background-color:" + color + ";" +
                        "-fx-text-fill:white;" +
                        "-fx-alignment:center;" +
                        "-fx-padding:10;" +
                        "-fx-font-weight:bold;"
        );

        part.prefWidthProperty().bind(barBox.widthProperty().multiply(percent));
        barBox.getChildren().add(part);
    }

    @FXML
    private void handleBackFromDetails() {
        showCardPage();
    }

    @FXML
    private void handleCheckEligibility() {
        String regNo = StudentSession.getUsername();

        eligibilityTable.setItems(FXCollections.observableArrayList(
                dao.getExamEligibility(regNo)
        ));

        mainAttendancePane.setVisible(false);
        mainAttendancePane.setManaged(false);

        detailPane.setVisible(false);
        detailPane.setManaged(false);

        eligibilityPane.setVisible(true);
        eligibilityPane.setManaged(true);
    }

    @FXML
    private void handleBackFromEligibility() {
        showCardPage();
    }

    private void showCardPage() {
        mainAttendancePane.setVisible(true);
        mainAttendancePane.setManaged(true);

        detailPane.setVisible(false);
        detailPane.setManaged(false);

        eligibilityPane.setVisible(false);
        eligibilityPane.setManaged(false);
    }
}