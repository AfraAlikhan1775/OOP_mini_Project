package com.controller.Lecturer;

import com.dao.admin.CourseDAO;
import com.dao.admin.DashboardDAO;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class DashboardController {

    @FXML private Label courseCountLabel;
    @FXML private Label studentCountLabel;
    @FXML private Label noticeCountLabel;

    private final CourseDAO courseDAO = new CourseDAO();
    private final DashboardDAO dashboardDAO = new DashboardDAO();

    @FXML
    public void initialize() {
        loadDashboardData();
    }

    private void loadDashboardData() {
        try {
            int courseCount = courseDAO.getAllCourses().size();
            int studentCount = dashboardDAO.getStudentCount();
            int noticeCount = dashboardDAO.getRecentNotices().size();

            if (courseCountLabel != null) {
                courseCountLabel.setText(String.valueOf(courseCount));
            }
            if (studentCountLabel != null) {
                studentCountLabel.setText(String.valueOf(studentCount));
            }
            if (noticeCountLabel != null) {
                noticeCountLabel.setText(String.valueOf(noticeCount));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


