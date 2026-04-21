package com.controller.techOfficerControllers;

import com.dao.AttendanceDAO;
import com.model.Attendance;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class TOAttendanceController {

    @FXML private TextField searchField;
    @FXML private DatePicker datePicker;
    @FXML private ComboBox<String> courseFilter;
    @FXML private ComboBox<String> statusFilter;
    @FXML private TableView<Attendance> attendanceTable;
    @FXML private TableColumn<Attendance, String> colId;
    @FXML private TableColumn<Attendance, String> colStudentId;
    @FXML private TableColumn<Attendance, String> colCourse;
    @FXML private TableColumn<Attendance, String> colDate;
    @FXML private TableColumn<Attendance, String> colStatus;
    @FXML private TableColumn<Attendance, String> colMarkedBy;
    @FXML private TableColumn<Attendance, String> colRemarks;
    @FXML private TableColumn<Attendance, Void> colActions;
    @FXML private Label statusLabel;

    private String markedBy;
    private final AttendanceDAO attendanceDAO = new AttendanceDAO();

    @FXML
    public void initialize() {
        courseFilter.getItems().addAll("ICT101", "ICT102", "BST101", "ET101");
        statusFilter.getItems().addAll("All", "Present", "Absent", "Late", "Excused");
        statusFilter.setValue("All");

        setupTableColumns();
        loadAttendanceData();

        datePicker.valueProperty().addListener((obs, oldVal, newVal) -> filterTable());
        courseFilter.valueProperty().addListener((obs, oldVal, newVal) -> filterTable());
        statusFilter.valueProperty().addListener((obs, oldVal, newVal) -> filterTable());
    }

    public void setMarkedBy(String markedBy) {
        this.markedBy = markedBy;
    }

    private void setupTableColumns() {
        colId.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getAttendanceId())));
        colStudentId.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStudentId()));
        colCourse.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCourseCode()));
        colDate.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAttendanceDate().toString()));
        colStatus.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus()));
        colMarkedBy.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMarkedBy()));
        colRemarks.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRemarks()));

        colActions.setCellFactory(param -> new TableCell<>() {
            private final Button editBtn = new Button("✏️");
            private final Button delBtn = new Button("🗑️");
            private final HBox pane = new HBox(5, editBtn, delBtn);

            {
                editBtn.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
                delBtn.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-text-fill: red;");

                editBtn.setOnAction(event -> handleEdit(getTableView().getItems().get(getIndex())));
                delBtn.setOnAction(event -> handleDelete(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });
    }

    private void loadAttendanceData() {
        List<Attendance> data = attendanceDAO.getAllAttendance();
        Platform.runLater(() -> attendanceTable.setItems(FXCollections.observableArrayList(data)));
    }

    @FXML
    private void handleSearch() {
        String keyword = searchField.getText();
        if (keyword == null || keyword.trim().isEmpty()) {
            loadAttendanceData();
            return;
        }
        List<Attendance> data = attendanceDAO.searchAttendance(keyword.trim());
        attendanceTable.setItems(FXCollections.observableArrayList(data));
    }

    @FXML
    private void handleRefresh() {
        searchField.clear();
        datePicker.setValue(null);
        courseFilter.setValue(null);
        statusFilter.setValue("All");
        loadAttendanceData();
        statusLabel.setText("Data refreshed.");
    }

    private void filterTable() {
        LocalDate date = datePicker.getValue();
        String course = courseFilter.getValue();
        String status = statusFilter.getValue();

        List<Attendance> allData = attendanceDAO.getAllAttendance();
        List<Attendance> filtered = allData.stream()
                .filter(a -> date == null || a.getAttendanceDate().equals(date))
                .filter(a -> course == null || a.getCourseCode().equals(course))
                .filter(a -> status == null || "All".equals(status) || a.getStatus().equals(status))
                .toList();

        attendanceTable.setItems(FXCollections.observableArrayList(filtered));
    }

    @FXML
    private void handleAddAttendance() {
        showAttendanceDialog(null);
    }

    private void handleEdit(Attendance a) {
        showAttendanceDialog(a);
    }

    private void handleDelete(Attendance a) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Deletion");
        alert.setHeaderText("Delete Attendance Record");
        alert.setContentText("Are you sure you want to delete this record for " + a.getStudentId() + "?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (attendanceDAO.deleteAttendance(a.getAttendanceId())) {
                statusLabel.setText("Record deleted successfully.");
                handleRefresh();
            } else {
                statusLabel.setText("Failed to delete record.");
            }
        }
    }

    private void showAttendanceDialog(Attendance a) {
        Dialog<Attendance> dialog = new Dialog<>();
        dialog.setTitle(a == null ? "Add Attendance" : "Edit Attendance");
        dialog.setHeaderText(null);

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField studentId = new TextField();
        studentId.setPromptText("Student ID (e.g. TG1774)");
        ComboBox<String> course = new ComboBox<>(FXCollections.observableArrayList("ICT101", "ICT102", "BST101", "ET101"));
        DatePicker date = new DatePicker(LocalDate.now());
        ComboBox<String> status = new ComboBox<>(FXCollections.observableArrayList("Present", "Absent", "Late", "Excused"));
        TextField remarks = new TextField();

        if (a != null) {
            studentId.setText(a.getStudentId());
            course.setValue(a.getCourseCode());
            date.setValue(a.getAttendanceDate());
            status.setValue(a.getStatus());
            remarks.setText(a.getRemarks());
        }

        grid.add(new Label("Student ID:"), 0, 0);
        grid.add(studentId, 1, 0);
        grid.add(new Label("Course:"), 0, 1);
        grid.add(course, 1, 1);
        grid.add(new Label("Date:"), 0, 2);
        grid.add(date, 1, 2);
        grid.add(new Label("Status:"), 0, 3);
        grid.add(status, 1, 3);
        grid.add(new Label("Remarks:"), 0, 4);
        grid.add(remarks, 1, 4);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                if (studentId.getText().isEmpty() || course.getValue() == null || date.getValue() == null || status.getValue() == null) {
                    return null; // Basic validation
                }
                if (a == null) {
                    return new Attendance(studentId.getText(), course.getValue(), date.getValue(), status.getValue(), markedBy, remarks.getText());
                } else {
                    return new Attendance(a.getAttendanceId(), studentId.getText(), course.getValue(), date.getValue(), status.getValue(), markedBy, remarks.getText());
                }
            }
            return null;
        });

        Optional<Attendance> result = dialog.showAndWait();
        result.ifPresent(attendance -> {
            boolean success;
            if (a == null) {
                success = attendanceDAO.addAttendance(attendance);
            } else {
                success = attendanceDAO.updateAttendance(attendance);
            }

            if (success) {
                statusLabel.setText("Attendance saved successfully.");
                handleRefresh();
            } else {
                statusLabel.setText("Failed to save attendance.");
            }
        });
    }
}
