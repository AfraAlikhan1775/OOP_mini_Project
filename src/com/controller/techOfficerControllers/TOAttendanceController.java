package com.controller.techOfficerControllers;

import com.dao.AttendanceDAO;
import com.dao.CourseRegistrationDAO;
import com.model.AttendanceMarkRow;
import com.model.Session;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TOAttendanceController {

    @FXML private TextField searchField;
    @FXML private DatePicker datePicker;
    @FXML private ComboBox<String> courseFilter;
    @FXML private ComboBox<String> statusFilter;
    @FXML private TableView<Session> attendanceTable;
    @FXML private TableColumn<Session, String> colId;
    @FXML private TableColumn<Session, String> colStudentId;
    @FXML private TableColumn<Session, String> colCourse;
    @FXML private TableColumn<Session, String> colDate;
    @FXML private TableColumn<Session, String> colStatus;
    @FXML private TableColumn<Session, String> colMarkedBy;
    @FXML private TableColumn<Session, String> colRemarks;
    @FXML private TableColumn<Session, Void> colActions;
    @FXML private Label statusLabel;

    private final AttendanceDAO attendanceDAO = new AttendanceDAO();
    private final CourseRegistrationDAO registrationDAO = new CourseRegistrationDAO();

    @FXML
    public void initialize() {
        courseFilter.getItems().add("All");
        courseFilter.setValue("All");

        statusFilter.getItems().addAll("All", "Theory", "Practical");
        statusFilter.setValue("All");

        setupTableColumns();
        loadSessionData();

        datePicker.valueProperty().addListener((obs, oldVal, newVal) -> filterTable());
        courseFilter.valueProperty().addListener((obs, oldVal, newVal) -> filterTable());
        statusFilter.valueProperty().addListener((obs, oldVal, newVal) -> filterTable());
    }

    private void setupTableColumns() {
        colId.setText("Session ID");
        colStudentId.setText("Lecturer EMPID");
        colCourse.setText("Course ID");
        colDate.setText("Date");
        colStatus.setText("Type");
        colMarkedBy.setText("Hours");
        colRemarks.setText("Details");

        colId.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getSessionId()));
        colStudentId.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getLecturerEmpId()));
        colCourse.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCourseId()));
        colDate.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getSessionDate().toString()));
        colStatus.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getSessionType()));
        colMarkedBy.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getHours())));
        colRemarks.setCellValueFactory(c ->
                new SimpleStringProperty("Course: " + c.getValue().getCourseId() + " | Session: " + c.getValue().getSessionId()));

        colActions.setCellFactory(param -> new TableCell<>() {
            private final Button markBtn = new Button("Mark");
            private final HBox pane = new HBox(5, markBtn);

            {
                markBtn.setOnAction(event -> handleMarkAttendance(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });
    }

    private void loadSessionData() {
        List<Session> data = attendanceDAO.getAllSessions();
        attendanceTable.setItems(FXCollections.observableArrayList(data));

        courseFilter.getItems().setAll("All");
        for (Session s : data) {
            if (!courseFilter.getItems().contains(s.getCourseId())) {
                courseFilter.getItems().add(s.getCourseId());
            }
        }
    }

    @FXML
    private void handleSearch() {
        String keyword = searchField.getText();
        if (keyword == null || keyword.isBlank()) {
            loadSessionData();
            return;
        }

        List<Session> all = attendanceDAO.getAllSessions();
        List<Session> filtered = all.stream()
                .filter(s -> s.getCourseId().contains(keyword)
                        || s.getSessionId().contains(keyword)
                        || s.getLecturerEmpId().contains(keyword))
                .toList();

        attendanceTable.setItems(FXCollections.observableArrayList(filtered));
    }

    @FXML
    private void handleRefresh() {
        searchField.clear();
        datePicker.setValue(null);
        courseFilter.setValue("All");
        statusFilter.setValue("All");
        loadSessionData();
        statusLabel.setText("Data refreshed.");
    }

    private void filterTable() {
        LocalDate date = datePicker.getValue();
        String course = courseFilter.getValue();
        String type = statusFilter.getValue();

        List<Session> all = attendanceDAO.getAllSessions();
        List<Session> filtered = all.stream()
                .filter(s -> date == null || s.getSessionDate().equals(date))
                .filter(s -> course == null || "All".equals(course) || s.getCourseId().equals(course))
                .filter(s -> type == null || "All".equals(type) || s.getSessionType().equals(type))
                .toList();

        attendanceTable.setItems(FXCollections.observableArrayList(filtered));
    }

    @FXML
    private void handleAddAttendance() {
        showAddSessionDialog();
    }

    private void showAddSessionDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add Session");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(8);
        grid.setPadding(new Insets(20));

        TextField courseIdField = new TextField();
        TextField sessionIdField = new TextField();
        ComboBox<String> typeBox = new ComboBox<>(FXCollections.observableArrayList("Theory", "Practical"));
        DatePicker datePicker = new DatePicker(LocalDate.now());
        TextField lecturerField = new TextField();
        TextField hoursField = new TextField();
        ComboBox<String> semesterBox = new ComboBox<>(FXCollections.observableArrayList("1", "2"));
        TextField academicYearField = new TextField();

        Label courseError = new Label();
        courseError.setStyle("-fx-text-fill:red;");
        Label lecturerError = new Label();
        lecturerError.setStyle("-fx-text-fill:red;");

        courseIdField.textProperty().addListener((obs, o, n) -> {
            if (n == null || n.isBlank()) {
                courseError.setText("");
            } else if (!attendanceDAO.courseExists(n.trim())) {
                courseError.setText("Course does not exist");
            } else {
                courseError.setText("");
            }
        });

        lecturerField.textProperty().addListener((obs, o, n) -> {
            if (n == null || n.isBlank()) {
                lecturerError.setText("");
            } else if (!attendanceDAO.lecturerExists(n.trim())) {
                lecturerError.setText("Lecturer does not exist");
            } else {
                lecturerError.setText("");
            }
        });

        grid.add(new Label("Course ID:"), 0, 0);
        grid.add(courseIdField, 1, 0);
        grid.add(courseError, 1, 1);

        grid.add(new Label("Session ID:"), 0, 2);
        grid.add(sessionIdField, 1, 2);

        grid.add(new Label("Type:"), 0, 3);
        grid.add(typeBox, 1, 3);

        grid.add(new Label("Date:"), 0, 4);
        grid.add(datePicker, 1, 4);

        grid.add(new Label("Lecturer EMPID:"), 0, 5);
        grid.add(lecturerField, 1, 5);
        grid.add(lecturerError, 1, 6);

        grid.add(new Label("Hours:"), 0, 7);
        grid.add(hoursField, 1, 7);

        grid.add(new Label("Semester:"), 0, 8);
        grid.add(semesterBox, 1, 8);

        grid.add(new Label("Academic Year:"), 0, 9);
        grid.add(academicYearField, 1, 9);

        dialog.getDialogPane().setContent(grid);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                if (!attendanceDAO.courseExists(courseIdField.getText().trim())) {
                    statusLabel.setText("Course does not exist.");
                    return;
                }
                if (!attendanceDAO.lecturerExists(lecturerField.getText().trim())) {
                    statusLabel.setText("Lecturer does not exist.");
                    return;
                }

                Session session = new Session(
                        courseIdField.getText().trim(),
                        sessionIdField.getText().trim(),
                        typeBox.getValue(),
                        datePicker.getValue(),
                        lecturerField.getText().trim(),
                        Double.parseDouble(hoursField.getText().trim())
                );

                boolean saved = attendanceDAO.addSession(
                        session,
                        semesterBox.getValue(),
                        academicYearField.getText().trim()
                );

                statusLabel.setText(saved ? "Session saved successfully." : "Failed to save session.");
                handleRefresh();

            } catch (Exception e) {
                e.printStackTrace();
                statusLabel.setText("Invalid data.");
            }
        }
    }

    private void handleMarkAttendance(Session session) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Mark Attendance - " + session.getCourseId() + " / " + session.getSessionId());
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane topGrid = new GridPane();
        topGrid.setHgap(10);
        topGrid.setVgap(10);
        topGrid.setPadding(new Insets(10));

        ComboBox<String> semesterBox = new ComboBox<>(FXCollections.observableArrayList("1", "2"));
        TextField academicYearField = new TextField();

        topGrid.add(new Label("Semester:"), 0, 0);
        topGrid.add(semesterBox, 1, 0);
        topGrid.add(new Label("Academic Year:"), 0, 1);
        topGrid.add(academicYearField, 1, 1);

        TableView<AttendanceMarkRow> table = new TableView<>();
        TableColumn<AttendanceMarkRow, String> regNoCol = new TableColumn<>("Reg No");
        TableColumn<AttendanceMarkRow, String> nameCol = new TableColumn<>("Student Name");
        TableColumn<AttendanceMarkRow, String> statusCol = new TableColumn<>("Status");

        regNoCol.setCellValueFactory(c -> c.getValue().regNoProperty());
        nameCol.setCellValueFactory(c -> c.getValue().studentNameProperty());
        statusCol.setCellValueFactory(c -> c.getValue().statusProperty());

        statusCol.setCellFactory(col -> new TableCell<>() {
            private final ComboBox<String> combo = new ComboBox<>(
                    FXCollections.observableArrayList("Present", "Absent", "Medical")
            );

            {
                combo.valueProperty().addListener((obs, oldVal, newVal) -> {
                    AttendanceMarkRow row = getTableView().getItems().get(getIndex());
                    row.setStatus(newVal);
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    combo.setValue(item);
                    setGraphic(combo);
                }
            }
        });

        table.getColumns().addAll(regNoCol, nameCol, statusCol);
        table.setPrefHeight(350);

        Button loadBtn = new Button("Load Students");
        loadBtn.setOnAction(e -> {
            String sem = semesterBox.getValue();
            String year = academicYearField.getText().trim();

            if (sem == null || year.isBlank()) {
                statusLabel.setText("Select semester and academic year.");
                return;
            }

            List<String[]> students = registrationDAO.getRegisteredStudentsByCourse(session.getCourseId(), sem, year);
            List<AttendanceMarkRow> rows = new ArrayList<>();

            for (String[] s : students) {
                rows.add(new AttendanceMarkRow(s[0], s[1], "Present"));
            }

            table.setItems(FXCollections.observableArrayList(rows));
        });

        VBox content = new VBox(10, topGrid, loadBtn, table);
        content.setPadding(new Insets(10));
        dialog.getDialogPane().setContent(content);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean saved = attendanceDAO.saveAttendanceBulk(
                    session.getCourseId(),
                    session.getSessionId(),
                    table.getItems()
            );

            statusLabel.setText(saved ? "Attendance saved successfully." : "Failed to save attendance.");
        }
    }
}