package com.controller.techOfficerControllers;

import com.dao.AttendanceDAO;
import com.model.admin.AttendanceGroup;
import com.model.admin.AttendanceRecord;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class AddAttendanceController {

    @FXML private ComboBox<Integer> yearBox;
    @FXML private ComboBox<Integer> semesterBox;
    @FXML private ComboBox<String> courseBox;
    @FXML private ComboBox<String> sessionBox;
    @FXML private ComboBox<String> typeBox;
    @FXML private DatePicker datePicker;

    @FXML private TextField regNoField;
    @FXML private ComboBox<String> statusBox;

    @FXML private TableView<AttendanceRecord> attendanceTable;
    @FXML private TableColumn<AttendanceRecord, String> regNoCol;
    @FXML private TableColumn<AttendanceRecord, String> statusCol;

    @FXML private Label infoLabel;

    private final AttendanceDAO attendanceDAO = new AttendanceDAO();
    private final List<AttendanceRecord> tempRecords = new ArrayList<>();
    private TODashboardController dashboardController;

    public void setDashboardController(TODashboardController dashboardController) {
        this.dashboardController = dashboardController;
    }

    @FXML
    public void initialize() {
        yearBox.getItems().addAll(1, 2, 3, 4);
        semesterBox.getItems().addAll(1, 2);
        typeBox.getItems().addAll("Theory", "Practical");
        statusBox.getItems().addAll("PRESENT", "MEDICAL");
        statusBox.setValue("PRESENT");
        datePicker.setValue(LocalDate.now());

        regNoCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getRegNo()));
        statusCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStatus()));

        yearBox.setOnAction(e -> loadCourses());
        semesterBox.setOnAction(e -> loadCourses());
        courseBox.setOnAction(e -> loadSessions());
        typeBox.setOnAction(e -> loadSessions());
    }

    private void loadCourses() {
        courseBox.getItems().clear();
        sessionBox.getItems().clear();

        Integer year = yearBox.getValue();
        Integer semester = semesterBox.getValue();

        if (year == null || semester == null) return;

        courseBox.getItems().addAll(attendanceDAO.getCourseIdsByYearAndSemester(year, semester));
    }

    private void loadSessions() {
        sessionBox.getItems().clear();

        String courseId = courseBox.getValue();
        String type = typeBox.getValue();

        if (courseId == null || type == null) return;

        sessionBox.getItems().addAll(attendanceDAO.getSessionIdsByCourseAndType(courseId, type));
    }

    @FXML
    private void handleAddStudent() {
        String regNo = regNoField.getText();

        if (regNo == null || regNo.isBlank()) {
            showAlert(Alert.AlertType.WARNING, "Validation", "Please enter registration number.");
            return;
        }

        if (!isHeaderValid()) {
            showAlert(Alert.AlertType.WARNING, "Validation", "Please select year, semester, course, session, type and date first.");
            return;
        }

        regNo = regNo.trim().toUpperCase();

        for (AttendanceRecord record : tempRecords) {
            if (record.getRegNo().equalsIgnoreCase(regNo)) {
                showAlert(Alert.AlertType.WARNING, "Duplicate", "This registration number is already added.");
                return;
            }
        }

        AttendanceRecord record = new AttendanceRecord();
        record.setRegNo(regNo);
        record.setStatus(statusBox.getValue());

        tempRecords.add(record);
        refreshTable();

        regNoField.clear();
        regNoField.requestFocus();
    }

    @FXML
    private void handleRemoveSelected() {
        AttendanceRecord selected = attendanceTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Selection", "Please select a row to remove.");
            return;
        }

        tempRecords.remove(selected);
        refreshTable();
    }

    @FXML
    private void handleFinish() {
        if (!isHeaderValid()) {
            showAlert(Alert.AlertType.WARNING, "Validation", "Please fill all attendance details.");
            return;
        }

        if (tempRecords.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation", "Please add at least one student.");
            return;
        }

        int year = yearBox.getValue();
        String courseId = courseBox.getValue();
        String sessionId = sessionBox.getValue();
        String type = typeBox.getValue();
        LocalDate date = datePicker.getValue();

        if (attendanceDAO.attendanceAlreadyExists(year, courseId, sessionId, type, date)) {
            showAlert(Alert.AlertType.WARNING, "Duplicate Attendance", "Attendance already exists for this date, course, session and type.");
            return;
        }

        AttendanceGroup group = new AttendanceGroup(year, courseId, sessionId, type, date);
        int groupId = attendanceDAO.insertAttendanceGroup(group);

        if (groupId == -1) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to save attendance group.");
            return;
        }

        String prefix = getRegPrefixByYear(year);
        List<String> batchStudents = attendanceDAO.getStudentRegNosByPrefix(prefix);
        Set<String> existingRegs = attendanceDAO.getExistingRegNos(tempRecords);

        List<AttendanceRecord> finalRecords = new ArrayList<>();

        for (AttendanceRecord record : tempRecords) {
            record.setGroupId(groupId);
            finalRecords.add(record);
        }

        for (String regNo : batchStudents) {
            String normalized = regNo.trim().toUpperCase();
            if (!existingRegs.contains(normalized)) {
                AttendanceRecord absentRecord = new AttendanceRecord();
                absentRecord.setGroupId(groupId);
                absentRecord.setRegNo(normalized);
                absentRecord.setStatus("ABSENT");
                finalRecords.add(absentRecord);
            }
        }

        attendanceDAO.insertAttendanceRecords(finalRecords);

        showAlert(Alert.AlertType.INFORMATION, "Success", "Attendance saved successfully.");

        if (dashboardController != null) {
            dashboardController.loadContent("/com/view/techOfficer/to_attendance.fxml");
        } else {
            clearForm();
        }
    }

    @FXML
    private void handleBack() {
        if (dashboardController != null) {
            dashboardController.loadContent("/com/view/techOfficer/to_attendance.fxml");
        }
    }

    private String getRegPrefixByYear(int year) {
        return switch (year) {
            case 1 -> "TG/2024/";
            case 2 -> "TG/2023/";
            case 3 -> "TG/2022/";
            case 4 -> "TG/2021/";
            default -> "";
        };
    }

    private boolean isHeaderValid() {
        return yearBox.getValue() != null
                && semesterBox.getValue() != null
                && courseBox.getValue() != null
                && sessionBox.getValue() != null
                && typeBox.getValue() != null
                && datePicker.getValue() != null;
    }

    private void refreshTable() {
        attendanceTable.setItems(FXCollections.observableArrayList(tempRecords));
        infoLabel.setText(tempRecords.size() + " student(s) added");
    }

    private void clearForm() {
        yearBox.setValue(null);
        semesterBox.setValue(null);
        courseBox.getItems().clear();
        sessionBox.getItems().clear();
        typeBox.setValue(null);
        datePicker.setValue(LocalDate.now());
        regNoField.clear();
        statusBox.setValue("PRESENT");
        tempRecords.clear();
        refreshTable();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}