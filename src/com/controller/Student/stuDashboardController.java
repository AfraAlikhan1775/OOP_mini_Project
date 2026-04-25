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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.io.File;

public class stuDashboardController {

    @FXML private Label nameLabel;
    @FXML private Label emailLabel;
    @FXML private Label courseLabel;
    @FXML private Label departmentLabel;
    @FXML private Label yearLabel;

    @FXML private ImageView studentProfileImage;

    @FXML private ImageView mentorImage;
    @FXML private Label mentorNameLabel;
    @FXML private Label mentorIdLabel;
    @FXML private Label mentorEmailLabel;
    @FXML private Label mentorPhoneLabel;
    @FXML private Label mentorDepartmentLabel;

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
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("time"));
        subjectColumn.setCellValueFactory(new PropertyValueFactory<>("subject"));
        lecturerColumn.setCellValueFactory(new PropertyValueFactory<>("lecturer"));
        roomColumn.setCellValueFactory(new PropertyValueFactory<>("room"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
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
        departmentLabel.setText("Department: " + data.getDepartment());
        yearLabel.setText("Year: " + data.getYear());

        setImage(studentProfileImage, data.getStudentProfilePic(), "/com/Resources/images/icon/stu.png");

        mentorNameLabel.setText(data.getMentorName());
        mentorIdLabel.setText("Mentor ID: " + data.getMentorId());
        mentorEmailLabel.setText("Email: " + data.getMentorEmail());
        mentorPhoneLabel.setText("Phone: " + data.getMentorPhone());
        mentorDepartmentLabel.setText("Department: " + data.getMentorDepartment());
        setImage(mentorImage, data.getMentorPhoto(), "/com/Resources/images/icon/stu.png");

        noticesBox.getChildren().clear();
        for (String notice : data.getNotices()) {
            Label label = new Label(notice);
            label.setWrapText(true);
            label.setStyle("-fx-font-size: 13;");
            noticesBox.getChildren().add(label);
        }

        timetableTable.setItems(FXCollections.observableArrayList(data.getTodayRows()));
    }

    private void setImage(ImageView imageView, String imagePath, String defaultResource) {
        try {
            if (imagePath != null && !imagePath.isBlank() && !imagePath.equals("-")) {
                File file = new File(imagePath);

                if (file.exists()) {
                    imageView.setImage(new Image(file.toURI().toString()));
                    return;
                }

                if (imagePath.startsWith("file:") || imagePath.startsWith("http")) {
                    imageView.setImage(new Image(imagePath));
                    return;
                }
            }

            imageView.setImage(new Image(getClass().getResource(defaultResource).toExternalForm()));

        } catch (Exception e) {
            imageView.setImage(new Image(getClass().getResource(defaultResource).toExternalForm()));
        }
    }

    private void showDefaultState(String message) {
        nameLabel.setText("Student");
        emailLabel.setText(message);
        courseLabel.setText("Course: -");
        departmentLabel.setText("Department: -");
        yearLabel.setText("Year: -");

        mentorNameLabel.setText("-");
        mentorIdLabel.setText("Mentor ID: -");
        mentorEmailLabel.setText("Email: -");
        mentorPhoneLabel.setText("Phone: -");
        mentorDepartmentLabel.setText("Department: -");

        noticesBox.getChildren().clear();
        noticesBox.getChildren().add(new Label("• No notices available"));

        timetableTable.setItems(FXCollections.observableArrayList());
    }
}