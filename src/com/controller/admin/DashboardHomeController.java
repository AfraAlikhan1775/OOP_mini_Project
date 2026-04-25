package com.controller.admin;

import com.dao.admin.DashboardDAO;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

public class DashboardHomeController {

    @FXML private Label lecturerCountLabel;
    @FXML private Label techOfficerCountLabel;
    @FXML private Label studentCountLabel;
    @FXML private Label courseCountLabel;

    @FXML private ListView<String> recentUsersList;
    @FXML private ListView<String> recentNoticesList;

    private final DashboardDAO dashboardDAO = new DashboardDAO();

    @FXML
    public void initialize() {
        loadCounts();
        loadRecentUsers();
        loadRecentNotices();
    }

    private void loadCounts() {
        lecturerCountLabel.setText(String.valueOf(dashboardDAO.getLecturerCount()));
        techOfficerCountLabel.setText(String.valueOf(dashboardDAO.getTechOfficerCount()));
        studentCountLabel.setText(String.valueOf(dashboardDAO.getStudentCount()));
        courseCountLabel.setText(String.valueOf(dashboardDAO.getCourseCount()));
    }

    private void loadRecentUsers() {
        recentUsersList.setItems(
                FXCollections.observableArrayList(dashboardDAO.getRecentUsers())
        );
    }

    private void loadRecentNotices() {
        recentNoticesList.setItems(
                FXCollections.observableArrayList(dashboardDAO.getRecentNotices())
        );
    }
}