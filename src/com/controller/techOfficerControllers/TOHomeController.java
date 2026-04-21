package com.controller.techOfficerControllers;

import com.dao.AttendanceDAO;
import com.dao.MedicalDAO;
import com.dao.NoticeDAO;
import com.dao.admin.StudentDAO;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class TOHomeController {

    @FXML private Label welcomeLabel;
    @FXML private Label totalStudentsLabel;
    @FXML private Label todayAttendanceLabel;
    @FXML private Label medicalCountLabel;
    @FXML private Label noticeCountLabel;

    private String empId;

    public void setEmpId(String empId) {
        this.empId = empId;
        loadDashboardData();
    }

    private void loadDashboardData() {
        Platform.runLater(() -> {
            welcomeLabel.setText("Welcome back, " + empId + "!");

            StudentDAO studentDAO = new StudentDAO();
            totalStudentsLabel.setText(String.valueOf(studentDAO.getAllStudents().size()));

            AttendanceDAO attendanceDAO = new AttendanceDAO();
            // Count total attendance records as a simple metric
            todayAttendanceLabel.setText(String.valueOf(attendanceDAO.getAllAttendance().size()));

            MedicalDAO medicalDAO = new MedicalDAO();
            medicalCountLabel.setText(String.valueOf(medicalDAO.getAllMedicals().size()));

            NoticeDAO noticeDAO = new NoticeDAO();
            noticeCountLabel.setText(String.valueOf(noticeDAO.getNoticesForRole("Technical Officer").size()));
        });
    }
}
