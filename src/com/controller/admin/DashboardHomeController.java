package com.controller.admin;

import com.dao.admin.DashboardDAO;
import com.model.admin.Course;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class DashboardHomeController {

    @FXML private Label lecturerCountLabel;
    @FXML private Label techOfficerCountLabel;
    @FXML private Label studentCountLabel;
    @FXML private Label courseCountLabel;

    @FXML private ListView<String> recentUsersList;
    @FXML private ListView<String> recentNoticesList;

    @FXML private TableView<Course> courseTable;
    @FXML private TableColumn<Course, String> colCode;
    @FXML private TableColumn<Course, String> colName;
    @FXML private TableColumn<Course, String> colCoordinator;
    @FXML private TableColumn<Course, Integer> colCredits;

    private final DashboardDAO dashboardDAO = new DashboardDAO();

    @FXML
    public void initialize() {
        loadCounts();
        loadRecentUsers();
        loadRecentNotices();
        loadCourseTable();
    }

    private void loadCounts() {
        lecturerCountLabel.setText(String.valueOf(dashboardDAO.getLecturerCount()));
        techOfficerCountLabel.setText(String.valueOf(dashboardDAO.getTechOfficerCount()));
        studentCountLabel.setText(String.valueOf(dashboardDAO.getStudentCount()));
        courseCountLabel.setText(String.valueOf(dashboardDAO.getCourseCount()));
    }

    private void loadRecentUsers() {
        recentUsersList.setItems(FXCollections.observableArrayList(dashboardDAO.getRecentUsers()));
    }

    private void loadRecentNotices() {
        recentNoticesList.setItems(FXCollections.observableArrayList(dashboardDAO.getRecentNotices()));
    }

    private void loadCourseTable() {
        colCode.setCellValueFactory(new PropertyValueFactory<>("courseId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        colCoordinator.setCellValueFactory(new PropertyValueFactory<>("coordinator"));
        colCredits.setCellValueFactory(new PropertyValueFactory<>("credits"));

        courseTable.setItems(FXCollections.observableArrayList(dashboardDAO.getRecentCourses()));
    }
}