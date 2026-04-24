package com.controller.Lecturer;

import com.dao.Lecturer.MedicalApprovalDAO;
import com.model.Lecturerr.LecturerMedicalRequest;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.awt.Desktop;
import java.io.File;
import java.util.Optional;

public class LecturerMedicalController {

    @FXML private Label titleLabel;
    @FXML private Label statusLabel;

    @FXML private TableView<LecturerMedicalRequest> medicalTable;
    @FXML private TableColumn<LecturerMedicalRequest, String> colRegNo;
    @FXML private TableColumn<LecturerMedicalRequest, String> colStudentName;
    @FXML private TableColumn<LecturerMedicalRequest, String> colCourseId;
    @FXML private TableColumn<LecturerMedicalRequest, String> colSessionId;
    @FXML private TableColumn<LecturerMedicalRequest, String> colType;
    @FXML private TableColumn<LecturerMedicalRequest, String> colAttendanceDate;
    @FXML private TableColumn<LecturerMedicalRequest, String> colMedicalDates;
    @FXML private TableColumn<LecturerMedicalRequest, String> colReason;
    @FXML private TableColumn<LecturerMedicalRequest, String> colStatus;
    @FXML private TableColumn<LecturerMedicalRequest, Void> colActions;

    private String lecturerEmpId;

    private final MedicalApprovalDAO medicalDAO = new MedicalApprovalDAO();
    private final ObservableList<LecturerMedicalRequest> medicalList = FXCollections.observableArrayList();

    public void setLecturerEmpId(String lecturerEmpId) {
        this.lecturerEmpId = lecturerEmpId;
        loadMedicalRequests();
    }

    @FXML
    public void initialize() {
        setupTable();
    }

    private void setupTable() {
        colRegNo.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getRegNo()));
        colStudentName.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStudentName()));
        colCourseId.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCourseId()));
        colSessionId.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getSessionId()));
        colType.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getType()));
        colAttendanceDate.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getAttendanceDate()));
        colMedicalDates.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getStartDate() + " to " + data.getValue().getEndDate())
        );
        colReason.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getReason()));
        colStatus.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStatus()));

        medicalTable.setItems(medicalList);
        addActionButtons();
    }

    private void addActionButtons() {
        colActions.setCellFactory(param -> new TableCell<>() {

            private final Button openBtn = new Button("Open");
            private final Button approveBtn = new Button("Approve");
            private final Button rejectBtn = new Button("Reject");
            private final ButtonBar box = new ButtonBar();

            {
                openBtn.setStyle("-fx-background-color:#2563eb; -fx-text-fill:white;");
                approveBtn.setStyle("-fx-background-color:#16a34a; -fx-text-fill:white;");
                rejectBtn.setStyle("-fx-background-color:#dc2626; -fx-text-fill:white;");

                box.getButtons().addAll(openBtn, approveBtn, rejectBtn);

                openBtn.setOnAction(e -> openMedicalFile(getTableView().getItems().get(getIndex())));
                approveBtn.setOnAction(e -> approveMedical(getTableView().getItems().get(getIndex())));
                rejectBtn.setOnAction(e -> rejectMedical(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                    return;
                }

                LecturerMedicalRequest request = getTableView().getItems().get(getIndex());

                boolean pending = "Pending".equalsIgnoreCase(request.getStatus());
                approveBtn.setDisable(!pending);
                rejectBtn.setDisable(!pending);

                setGraphic(box);
            }
        });
    }

    @FXML
    private void handleRefresh() {
        loadMedicalRequests();
    }

    private void loadMedicalRequests() {
        medicalList.clear();

        if (lecturerEmpId == null || lecturerEmpId.isBlank()) {
            statusLabel.setText("Lecturer ID not found. Please login again.");
            return;
        }

        medicalList.addAll(medicalDAO.getMedicalRequestsForLecturer(lecturerEmpId));

        if (medicalList.isEmpty()) {
            statusLabel.setText("No medical submissions found for your courses.");
        } else {
            statusLabel.setText("Loaded " + medicalList.size() + " medical session request(s).");
        }
    }

    private void openMedicalFile(LecturerMedicalRequest request) {
        try {
            if (request.getFilePath() == null || request.getFilePath().isBlank()) {
                showAlert(Alert.AlertType.WARNING, "File Missing", "No file path saved for this medical.");
                return;
            }

            File file = new File(request.getFilePath());

            if (!file.exists()) {
                showAlert(Alert.AlertType.ERROR, "File Not Found", "Medical file not found:\n" + request.getFilePath());
                return;
            }

            Desktop.getDesktop().open(file);

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Open Error", "Cannot open medical file.");
        }
    }

    private void approveMedical(LecturerMedicalRequest request) {
        if (!confirm("Approve Medical", "Approve this medical request?")) {
            return;
        }

        boolean ok = medicalDAO.approveMedical(request.getMedicalId(), lecturerEmpId);

        if (ok) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Medical approved successfully.");
            loadMedicalRequests();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Medical approval failed.");
        }
    }

    private void rejectMedical(LecturerMedicalRequest request) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Reject Medical");
        dialog.setHeaderText(null);
        dialog.setContentText("Enter reject reason:");

        Optional<String> result = dialog.showAndWait();

        if (result.isEmpty() || result.get().trim().isEmpty()) {
            return;
        }

        boolean ok = medicalDAO.rejectMedical(request.getMedicalId(), lecturerEmpId, result.get().trim());

        if (ok) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Medical rejected successfully.");
            loadMedicalRequests();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Medical rejection failed.");
        }
    }

    private boolean confirm(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}