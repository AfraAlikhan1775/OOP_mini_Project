package com.controller.techOfficerControllers;

import com.database.DatabaseInitializer;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class AddAttendanceController {

    private TODashboardController dashboardController;

    public void setDashboardController(TODashboardController dashboardController) {
        this.dashboardController = dashboardController;
    }

    @FXML private ComboBox<String> yearBox;
    @FXML private ComboBox<String> semesterBox;
    @FXML private ComboBox<String> courseBox;
    @FXML private ComboBox<String> sessionBox;
    @FXML private ComboBox<String> typeBox;
    @FXML private DatePicker datePicker;

    @FXML private TextField regNoField;
    @FXML private TableView<AttendanceRow> attendanceTable;
    @FXML private TableColumn<AttendanceRow, String> regNoColumn;
    @FXML private TableColumn<AttendanceRow, String> statusColumn;
    @FXML private Label messageLabel;

    private final javafx.collections.ObservableList<AttendanceRow> rows =
            FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        yearBox.getItems().setAll("1", "2", "3", "4");
        semesterBox.getItems().setAll("1", "2");
        typeBox.getItems().setAll("Theory", "Practical", "Tutorial");

        regNoColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getRegNo()));

        statusColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getStatus()));

        attendanceTable.setItems(rows);

        yearBox.setOnAction(e -> loadCourses());
        semesterBox.setOnAction(e -> loadCourses());
        courseBox.setOnAction(e -> loadSessions());
        typeBox.setOnAction(e -> loadSessions());

        datePicker.setValue(LocalDate.now());
    }

    private void loadCourses() {
        courseBox.getItems().clear();
        sessionBox.getItems().clear();

        if (yearBox.getValue() == null || semesterBox.getValue() == null) return;

        String sql = """
                SELECT course_id
                FROM courses
                WHERE year = ?
                  AND semester = ?
                  AND (status IS NULL OR status = 'Active')
                ORDER BY course_id
                """;

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, yearBox.getValue());
            pst.setString(2, semesterBox.getValue());

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                courseBox.getItems().add(rs.getString("course_id"));
            }

        } catch (Exception e) {
            e.printStackTrace();
            showMessage("Course loading failed.");
        }
    }

    private void loadSessions() {
        sessionBox.getItems().clear();

        if (courseBox.getValue() == null || typeBox.getValue() == null) return;

        String sql = """
                SELECT session_id
                FROM session
                WHERE course_id = ?
                  AND type = ?
                ORDER BY session_id
                """;

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, courseBox.getValue());
            pst.setString(2, typeBox.getValue());

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                sessionBox.getItems().add(rs.getString("session_id"));
            }

        } catch (Exception e) {
            e.printStackTrace();
            showMessage("Session loading failed.");
        }
    }

    @FXML
    private void handleAddStudent() {
        String regNo = regNoField.getText().trim();

        if (regNo.isEmpty()) {
            showMessage("Enter registration number.");
            return;
        }

        String requiredPrefix = getRegPrefixByYear(yearBox.getValue());

        if (requiredPrefix.isEmpty()) {
            showMessage("Select year first.");
            return;
        }

        if (!regNo.startsWith(requiredPrefix)) {
            showMessage("This student is not in selected year.");
            return;
        }

        for (AttendanceRow row : rows) {
            if (row.getRegNo().equalsIgnoreCase(regNo)) {
                showMessage("Student already added.");
                return;
            }
        }

        rows.add(new AttendanceRow(regNo, "PRESENT"));
        regNoField.clear();
        showMessage("Student added as PRESENT.");
    }

    @FXML
    private void handleFinish() {
        if (!validateHeader()) return;

        String prefix = getRegPrefixByYear(yearBox.getValue());

        if (prefix.isEmpty()) {
            showMessage("Invalid year selected.");
            return;
        }

        String groupSql = """
                INSERT INTO attendance_group
                (year_no, course_id, session_id, type, attendance_date)
                VALUES (?, ?, ?, ?, ?)
                """;

        String studentSql = """
                SELECT reg_no
                FROM student
                WHERE reg_no LIKE ?
                ORDER BY reg_no
                """;

        String recordSql = """
                INSERT INTO attendance_record
                (group_id, reg_no, status, medical_status)
                VALUES (?, ?, ?, NULL)
                """;

        try (Connection conn = DatabaseInitializer.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement groupPst =
                         conn.prepareStatement(groupSql, Statement.RETURN_GENERATED_KEYS);
                 PreparedStatement studentPst =
                         conn.prepareStatement(studentSql);
                 PreparedStatement recordPst =
                         conn.prepareStatement(recordSql)) {

                groupPst.setInt(1, Integer.parseInt(yearBox.getValue()));
                groupPst.setString(2, courseBox.getValue());
                groupPst.setString(3, sessionBox.getValue());
                groupPst.setString(4, typeBox.getValue());
                groupPst.setDate(5, Date.valueOf(datePicker.getValue()));
                groupPst.executeUpdate();

                ResultSet keys = groupPst.getGeneratedKeys();

                if (!keys.next()) {
                    conn.rollback();
                    showMessage("Attendance group create failed.");
                    return;
                }

                int groupId = keys.getInt(1);

                Set<String> presentStudents = new HashSet<>();
                for (AttendanceRow row : rows) {
                    presentStudents.add(row.getRegNo().toUpperCase());
                }

                studentPst.setString(1, prefix + "%");
                ResultSet studentRs = studentPst.executeQuery();

                int count = 0;

                while (studentRs.next()) {
                    String regNo = studentRs.getString("reg_no");

                    String status = presentStudents.contains(regNo.toUpperCase())
                            ? "PRESENT"
                            : "ABSENT";

                    recordPst.setInt(1, groupId);
                    recordPst.setString(2, regNo);
                    recordPst.setString(3, status);
                    recordPst.addBatch();

                    count++;
                }

                if (count == 0) {
                    conn.rollback();
                    showMessage("No students found for selected year.");
                    return;
                }

                recordPst.executeBatch();
                conn.commit();

                rows.clear();
                showMessage("Attendance saved. Present students saved, others marked ABSENT.");

            } catch (Exception e) {
                conn.rollback();
                e.printStackTrace();
                showMessage("Attendance save failed.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            showMessage("Database connection failed.");
        }
    }

    private String getRegPrefixByYear(String year) {
        if (year == null) return "";

        return switch (year) {
            case "1" -> "TG/2024/";
            case "2" -> "TG/2023/";
            case "3" -> "TG/2022/";
            case "4" -> "TG/2021/";
            default -> "";
        };
    }

    @FXML
    private void handleBack() {
        if (dashboardController != null) {
            dashboardController.loadContent("/com/view/techOfficer/to_attendance.fxml");
        }
    }

    @FXML
    private void handleClear() {
        rows.clear();
        regNoField.clear();
        showMessage("Cleared.");
    }

    private boolean validateHeader() {
        if (yearBox.getValue() == null) {
            showMessage("Select year.");
            return false;
        }

        if (semesterBox.getValue() == null) {
            showMessage("Select semester.");
            return false;
        }

        if (courseBox.getValue() == null) {
            showMessage("Select course.");
            return false;
        }

        if (sessionBox.getValue() == null) {
            showMessage("Select session.");
            return false;
        }

        if (typeBox.getValue() == null) {
            showMessage("Select type.");
            return false;
        }

        if (datePicker.getValue() == null) {
            showMessage("Select date.");
            return false;
        }

        return true;
    }

    private void showMessage(String msg) {
        if (messageLabel != null) {
            messageLabel.setText(msg);
        }
    }

    public static class AttendanceRow {
        private final String regNo;
        private final String status;

        public AttendanceRow(String regNo, String status) {
            this.regNo = regNo;
            this.status = status;
        }

        public String getRegNo() {
            return regNo;
        }

        public String getStatus() {
            return status;
        }
    }
}