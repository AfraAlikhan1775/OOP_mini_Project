package com.controller;

//import com.dao.CourseDAO;
//import com.dao.NoticeDAO;
import com.dao.UserDAO;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;

public class AdminController {

    @FXML private ListView<String> recentUsersList;
    @FXML private ListView<String> recentNoticesList;
    @FXML private TableView<String> courseTable;

    private final UserDAO userDAO = new UserDAO();
    //private final NoticeDAO noticeDAO = new NoticeDAO();
    //private final CourseDAO courseDAO = new CourseDAO();

    @FXML
    public void initialize() {
        loadDashboard();
    }

    private void loadDashboard() {
        //recentUsersList.getItems().setAll(userDAO.getRecentUsers());
        //recentNoticesList.getItems().setAll(noticeDAO.getRecentNotices());
        //courseTable.getItems().setAll(courseDAO.getCourses());
    }

    @FXML
    private void handleDashboard() {
        loadDashboard();
    }

    @FXML
    private void handleUsers() {
        //recentUsersList.getItems().setAll(userDAO.getRecentUsers());
    }

    @FXML
    private void handleCourse() {
        //courseTable.getItems().setAll(courseDAO.getCourses());
    }

    @FXML
    private void handleNotice() {
        //recentNoticesList.getItems().setAll(noticeDAO.getRecentNotices());
    }

    @FXML
    private void handleTimetable() {
        System.out.println("Timetable clicked");
    }

    @FXML
    private void handleLecturers() {
        System.out.println("Lecturers clicked");
    }

    @FXML
    private void handleTo() {
        System.out.println("Tech Officers clicked");
    }

    @FXML
    private void handleStudents() {
        System.out.println("Students clicked");
    }
}