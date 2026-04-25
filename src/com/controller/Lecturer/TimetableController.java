package com.controller.Lecturer;

import com.dao.Lecturer.LecturerTimetableDAO;
import com.model.admin.TimetableSessionInput;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class TimetableController {

    @FXML private Label titleLabel;
    @FXML private Label statusLabel;

    @FXML private ComboBox<String> dayFilter;

    @FXML private TableView<TimetableSessionInput> timetableTable;
    @FXML private TableColumn<TimetableSessionInput, String> colDay;
    @FXML private TableColumn<TimetableSessionInput, String> colSubject;
    @FXML private TableColumn<TimetableSessionInput, String> colTime;
    @FXML private TableColumn<TimetableSessionInput, String> colRoom;
    @FXML private TableColumn<TimetableSessionInput, String> colType;

    private final LecturerTimetableDAO timetableDAO = new LecturerTimetableDAO();

    private String lecturerEmpId;

    @FXML
    public void initialize() {
        colDay.setCellValueFactory(new PropertyValueFactory<>("dayName"));
        colSubject.setCellValueFactory(new PropertyValueFactory<>("subject"));
        colTime.setCellValueFactory(new PropertyValueFactory<>("time"));
        colRoom.setCellValueFactory(new PropertyValueFactory<>("room"));
        colType.setCellValueFactory(new PropertyValueFactory<>("sessionType"));

        dayFilter.getItems().setAll(
                "All", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday"
        );
        dayFilter.setValue("All");

        dayFilter.setOnAction(e -> loadTimetable());
    }

    public void setLecturerEmpId(String lecturerEmpId) {
        this.lecturerEmpId = lecturerEmpId;
        loadTimetable();
    }

    @FXML
    private void handleRefresh() {
        loadTimetable();
    }

    private void loadTimetable() {
        if (lecturerEmpId == null || lecturerEmpId.isBlank()) {
            statusLabel.setText("Lecturer session not found.");
            return;
        }

        String day = dayFilter.getValue();

        List<TimetableSessionInput> rows =
                timetableDAO.getLecturerTimetable(lecturerEmpId, day);

        timetableTable.setItems(FXCollections.observableArrayList(rows));

        if (rows.isEmpty()) {
            statusLabel.setText("No timetable sessions found for this lecturer.");
        } else {
            statusLabel.setText(rows.size() + " session(s) loaded.");
        }
    }
}