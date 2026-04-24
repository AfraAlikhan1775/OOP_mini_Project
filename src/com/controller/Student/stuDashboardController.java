package com.controller.Student;

import com.dao.student.StudentDashboardDAO;
import com.model.student.StudentDashboardData;
import com.model.student.TodayTimetableRow;
import com.session.StudentSession;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

public class stuDashboardController {

    @FXML private Label nameLabel;
    @FXML private Label emailLabel;
    @FXML private Label courseLabel;

    @FXML private Label attendanceLabel;
    @FXML private Label courseCountLabel;
    @FXML private Label medicalCountLabel;

    @FXML private Label departmentLabel;
    @FXML private Label yearLabel;
    @FXML private Label mentorLabel;

    @FXML private VBox noticesBox;

    @FXML private TableView<TodayTimetableRow> timetableTable;
    @FXML private TableColumn<TodayTimetableRow, String> timeColumn;
    @FXML private TableColumn<TodayTimetableRow, String> subjectColumn;
    @FXML private TableColumn<TodayTimetableRow, String> lecturerColumn;
    @FXML private TableColumn<TodayTimetableRow, String> roomColumn;
    @FXML private TableColumn<TodayTimetableRow, String> typeColumn;

    private final StudentDashboardDAO dashboardDAO = new StudentDashboardDAO();

    @FXML
    public void initialize() {
        setupTable();
        loadDashboard();
    }

    private void setupTable() {
        if (timeColumn != null) {
            timeColumn.setCellValueFactory(new PropertyValueFactory<>("time"));
            subjectColumn.setCellValueFactory(new PropertyValueFactory<>("subject"));
            lecturerColumn.setCellValueFactory(new PropertyValueFactory<>("lecturer"));
            roomColumn.setCellValueFactory(new PropertyValueFactory<>("room"));
            typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        }
    }

    private void loadDashboard() {
        String username = StudentSession.getUsername();

        if (username == null || username.isBlank()) {
            showDefaultState("No logged student session found");
            return;
        }

        StudentDashboardData data = dashboardDAO.getDashboardData(username);

        nameLabel.setText(data.getFullName());
        emailLabel.setText(data.getEmail());
        courseLabel.setText("Course: " + data.getCourse());

        attendanceLabel.setText(data.getAttendancePercentage());
        courseCountLabel.setText(data.getCourseCount());
        medicalCountLabel.setText(data.getMedicalCount());

        departmentLabel.setText("Department: " + data.getDepartment());
        yearLabel.setText("Year: " + data.getYear());
        mentorLabel.setText("Mentor ID: " + data.getMentorId());

        noticesBox.getChildren().clear();
        for (String notice : data.getNotices()) {
            Label label = new Label(notice);
            label.setWrapText(true);
            label.setStyle("-fx-font-size: 12;");
            noticesBox.getChildren().add(label);
        }

        timetableTable.setItems(FXCollections.observableArrayList(data.getTodayRows()));
    }

    private void showDefaultState(String message) {
        nameLabel.setText("Student");
        emailLabel.setText(message);
        courseLabel.setText("Course: -");

        attendanceLabel.setText("0%");
        courseCountLabel.setText("0");
        medicalCountLabel.setText("0");

        departmentLabel.setText("Department: -");
        yearLabel.setText("Year: -");
        mentorLabel.setText("Mentor ID: -");

        noticesBox.getChildren().clear();
        noticesBox.getChildren().add(new Label("• No notices available"));

        timetableTable.setItems(FXCollections.observableArrayList());
    }
}