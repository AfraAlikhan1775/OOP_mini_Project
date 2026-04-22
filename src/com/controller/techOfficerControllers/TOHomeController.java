package com.controller.techOfficerControllers;

import com.dao.AttendanceDAO;
import com.dao.MedicalDAO;
import com.dao.NoticeDAO;
import com.dao.admin.StudentDAO;
import javafx.concurrent.Task;
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
        welcomeLabel.setText("Welcome back, " + empId + "!");

        Task<DashboardCounts> task = new Task<>() {
            @Override
            protected DashboardCounts call() {
                DashboardCounts counts = new DashboardCounts();
                counts.students = new StudentDAO().getAllStudents().size();
                counts.attendance = new AttendanceDAO().getAllAttendance().size();
                counts.medicals = new MedicalDAO().getAllMedicals().size();
                counts.notices = new NoticeDAO().getNoticesForRole("Technical Officer").size();
                return counts;
            }
        };

        task.setOnSucceeded(e -> {
            DashboardCounts counts = task.getValue();
            totalStudentsLabel.setText(String.valueOf(counts.students));
            todayAttendanceLabel.setText(String.valueOf(counts.attendance));
            medicalCountLabel.setText(String.valueOf(counts.medicals));
            noticeCountLabel.setText(String.valueOf(counts.notices));
        });

        task.setOnFailed(e -> {
            Throwable ex = task.getException();
            if (ex != null) ex.printStackTrace();
        });

        Thread thread = new Thread(task, "to-home-dashboard-loader");
        thread.setDaemon(true);
        thread.start();
    }

    private static class DashboardCounts {
        int students;
        int attendance;
        int medicals;
        int notices;
    }
}
