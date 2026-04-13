package com.controller.admin;

import com.dao.admin.TimetableDAO;
import com.model.admin.TimetableSessionInput;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class AddTimetableController {

    @FXML private ComboBox<String> department;
    @FXML private ComboBox<String> level;
    @FXML private ComboBox<String> semester;

    @FXML private TextField subject;
    @FXML private ComboBox<String> day;
    @FXML private TextField startTime;
    @FXML private TextField endTime;
    @FXML private TextField lecturer;
    @FXML private TextField room;
    @FXML private ComboBox<String> type;

    @FXML private TableView<TimetableSessionInput> sessionTable;
    @FXML private TableColumn<TimetableSessionInput, String> colSubject;
    @FXML private TableColumn<TimetableSessionInput, String> colDay;
    @FXML private TableColumn<TimetableSessionInput, String> colTime;
    @FXML private TableColumn<TimetableSessionInput, String> colLecturer;
    @FXML private TableColumn<TimetableSessionInput, String> colRoom;
    @FXML private TableColumn<TimetableSessionInput, String> colType;

    private final List<TimetableSessionInput> sessionList = new ArrayList<>();
    private final TimetableDAO timetableDAO = new TimetableDAO();

    @FXML
    public void initialize() {
        colSubject.setCellValueFactory(new PropertyValueFactory<>("subject"));
        colDay.setCellValueFactory(new PropertyValueFactory<>("dayName"));
        colTime.setCellValueFactory(new PropertyValueFactory<>("time"));
        colLecturer.setCellValueFactory(new PropertyValueFactory<>("lecturer"));
        colRoom.setCellValueFactory(new PropertyValueFactory<>("room"));
        colType.setCellValueFactory(new PropertyValueFactory<>("sessionType"));
    }

    @FXML
    private void handleAddSession() {
        String subjectValue = subject.getText();
        String dayValue = day.getValue();
        String startTimeValue = startTime.getText();
        String endTimeValue = endTime.getText();
        String lecturerValue = lecturer.getText();
        String roomValue = room.getText();
        String typeValue = type.getValue();

        if (subjectValue == null || subjectValue.isBlank() ||
                dayValue == null || dayValue.isBlank() ||
                startTimeValue == null || startTimeValue.isBlank() ||
                endTimeValue == null || endTimeValue.isBlank() ||
                lecturerValue == null || lecturerValue.isBlank() ||
                roomValue == null || roomValue.isBlank() ||
                typeValue == null || typeValue.isBlank()) {
            return;
        }

        TimetableSessionInput session = new TimetableSessionInput(
                subjectValue,
                dayValue,
                startTimeValue,
                endTimeValue,
                lecturerValue,
                roomValue,
                typeValue
        );

        sessionList.add(session);
        sessionTable.getItems().setAll(sessionList);

        subject.clear();
        day.setValue(null);
        startTime.clear();
        endTime.clear();
        lecturer.clear();
        room.clear();
        type.setValue(null);
    }

    @FXML
    private void handleFinish() {
        String departmentValue = department.getValue();
        String levelValue = level.getValue();
        String semesterValue = semester.getValue();

        if (departmentValue == null || levelValue == null || semesterValue == null) {
            return;
        }

        if (sessionList.isEmpty()) {
            return;
        }

        boolean saved = timetableDAO.saveTimetable(
                departmentValue,
                Integer.parseInt(levelValue),
                Integer.parseInt(semesterValue),
                sessionList
        );

        if (saved) {
            Stage stage = (Stage) sessionTable.getScene().getWindow();
            stage.close();
        }
    }
}