package com.controller.techOfficerControllers;

import com.dao.admin.SessionDAO;
import com.model.admin.SessionItem;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class AddSessionController {

    @FXML private ComboBox<Integer> yearBox;
    @FXML private ComboBox<Integer> semesterBox;
    @FXML private ComboBox<String> courseBox;
    @FXML private ComboBox<String> typeBox;

    @FXML private TextField sessionIdField;
    @FXML private TextField sessionNameField;
    @FXML private Label messageLabel;

    @FXML private TableView<SessionItem> sessionTable;
    @FXML private TableColumn<SessionItem, String> colCourse;
    @FXML private TableColumn<SessionItem, String> colSessionId;
    @FXML private TableColumn<SessionItem, String> colType;
    @FXML private TableColumn<SessionItem, String> colSessionName;
    @FXML private TableColumn<SessionItem, Number> colYear;
    @FXML private TableColumn<SessionItem, Number> colSemester;

    private final SessionDAO sessionDAO = new SessionDAO();
    private TODashboardController dashboardController;

    public void setDashboardController(TODashboardController dashboardController) {
        this.dashboardController = dashboardController;
    }

    @FXML
    public void initialize() {
        yearBox.getItems().addAll(1, 2, 3, 4);
        semesterBox.getItems().addAll(1, 2);
        typeBox.getItems().addAll("Theory", "Practical");

        yearBox.setOnAction(e -> loadCourses());
        semesterBox.setOnAction(e -> loadCourses());

        colCourse.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCourseId()));
        colSessionId.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getSessionId()));
        colType.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getType()));
        colSessionName.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getSessionName()));
        colYear.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getYear()));
        colSemester.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getSemester()));

        loadSessionTable();
    }

    private void loadCourses() {
        courseBox.getItems().clear();

        Integer year = yearBox.getValue();
        Integer semester = semesterBox.getValue();

        if (year == null || semester == null) return;

        courseBox.getItems().addAll(sessionDAO.getCourseIdsByYearAndSemester(year, semester));
    }

    @FXML
    private void handleAddSession() {
        Integer year = yearBox.getValue();
        Integer semester = semesterBox.getValue();
        String courseId = courseBox.getValue();
        String type = typeBox.getValue();
        String sessionId = sessionIdField.getText();
        String sessionName = sessionNameField.getText();

        if (year == null || semester == null || courseId == null || type == null ||
                sessionId == null || sessionId.isBlank()) {
            showMessage("Please fill all required fields.", true);
            return;
        }

        sessionId = sessionId.trim().toUpperCase();
        sessionName = sessionName == null ? "" : sessionName.trim();

        if (sessionDAO.existsSession(courseId, sessionId, type)) {
            showMessage("Session already exists.", true);
            return;
        }

        SessionItem item = new SessionItem(courseId, sessionId, type, sessionName, year, semester);

        if (sessionDAO.addSession(item)) {
            showMessage("Session added successfully.", false);
            sessionIdField.clear();
            sessionNameField.clear();
            loadSessionTable();
        } else {
            showMessage("Failed to add session.", true);
        }
    }

    @FXML
    private void handleBack() {
        if (dashboardController != null) {
            dashboardController.loadContent("/com/view/techOfficer/to_attendance.fxml");
        }
    }

    private void loadSessionTable() {
        sessionTable.setItems(FXCollections.observableArrayList(sessionDAO.getAllSessions()));
    }

    private void showMessage(String text, boolean error) {
        messageLabel.setText(text);
        if (error) {
            messageLabel.setStyle("-fx-text-fill:#fecaca; -fx-font-size:13px;");
        } else {
            messageLabel.setStyle("-fx-text-fill:#bbf7d0; -fx-font-size:13px;");
        }
    }
}