package com.controller.techOfficerControllers;

import com.dao.admin.TechnicalOfficerDAO;
import com.dao.admin.TimetableDAO;
import com.model.TechnicalOfficer;
import com.model.admin.TimetableSessionInput;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Button;

import java.util.List;

public class TOTimetableController {

    @FXML private Label deptLabel;
    @FXML private TableView<TimetableDAO.TimetableSummary> groupTable;
    @FXML private TableColumn<TimetableDAO.TimetableSummary, String> colGroupId;
    @FXML private TableColumn<TimetableDAO.TimetableSummary, String> colDept;
    @FXML private TableColumn<TimetableDAO.TimetableSummary, String> colLevel;
    @FXML private TableColumn<TimetableDAO.TimetableSummary, String> colSemester;
    @FXML private TableColumn<TimetableDAO.TimetableSummary, String> colSessions;
    @FXML private TableColumn<TimetableDAO.TimetableSummary, Void> colView;

    @FXML private TableView<TimetableSessionInput> sessionTable;
    @FXML private TableColumn<TimetableSessionInput, String> colSubject;
    @FXML private TableColumn<TimetableSessionInput, String> colDay;
    @FXML private TableColumn<TimetableSessionInput, String> colStartTime;
    @FXML private TableColumn<TimetableSessionInput, String> colEndTime;
    @FXML private TableColumn<TimetableSessionInput, String> colLecturer;
    @FXML private TableColumn<TimetableSessionInput, String> colRoom;
    @FXML private TableColumn<TimetableSessionInput, String> colType;

    @FXML private Label statusLabel;

    private String empId;
    private String department;
    private final TimetableDAO timetableDAO = new TimetableDAO();
    private final TechnicalOfficerDAO toDAO = new TechnicalOfficerDAO();

    public void setEmpId(String empId) {
        this.empId = empId;
        TechnicalOfficer to = toDAO.getTOByEmpId(empId);
        if (to != null && to.getDepartment() != null) {
            this.department = to.getDepartment();
            deptLabel.setText("Department: " + this.department);
            setupTables();
            loadGroups();
        } else {
            deptLabel.setText("Department: Not Assigned");
            statusLabel.setText("You are not assigned to a department.");
        }
    }

    private void setupTables() {
        // Group Table Columns
        colGroupId.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getId())));
        colDept.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDepartment()));
        colLevel.setCellValueFactory(cellData -> new SimpleStringProperty("Level " + cellData.getValue().getLevel()));
        colSemester.setCellValueFactory(cellData -> new SimpleStringProperty("Semester " + cellData.getValue().getSemester()));
        colSessions.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getSessionCount())));

        colView.setCellFactory(param -> new TableCell<>() {
            private final Button viewBtn = new Button("👁️ View");

            {
                viewBtn.setStyle("-fx-background-color: #1a73e8; -fx-text-fill: white; -fx-cursor: hand; -fx-background-radius: 5;");
                viewBtn.setOnAction(event -> {
                    TimetableDAO.TimetableSummary group = getTableView().getItems().get(getIndex());
                    loadSessions(group.getId());
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : viewBtn);
            }
        });

        // Session Table Columns
        colSubject.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSubject()));
        colDay.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDayName()));
        colStartTime.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStartTime()));
        colEndTime.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEndTime()));
        colLecturer.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getLecturer()));
        colRoom.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRoom()));
        colType.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSessionType()));
    }

    private void loadGroups() {
        if (department == null) return;
        List<TimetableDAO.TimetableSummary> groups = timetableDAO.filterByDepartment(department);
        Platform.runLater(() -> groupTable.setItems(FXCollections.observableArrayList(groups)));
    }

    private void loadSessions(int groupId) {
        List<TimetableSessionInput> sessions = timetableDAO.getSessionsByGroupId(groupId);
        Platform.runLater(() -> {
            sessionTable.setItems(FXCollections.observableArrayList(sessions));
            statusLabel.setText("Loaded " + sessions.size() + " sessions.");
        });
    }
}
