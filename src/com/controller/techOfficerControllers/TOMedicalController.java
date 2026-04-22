package com.controller.techOfficerControllers;

import com.dao.MedicalDAO;
import com.model.Medical;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class TOMedicalController {

    @FXML private TextField searchField;
    @FXML private ComboBox<String> departmentFilter;
    @FXML private ComboBox<String> statusFilter;
    @FXML private TableView<Medical> medicalTable;
    @FXML private TableColumn<Medical, String> colId;
    @FXML private TableColumn<Medical, String> colStudentId;
    @FXML private TableColumn<Medical, String> colDepartment;
    @FXML private TableColumn<Medical, String> colStartDate;
    @FXML private TableColumn<Medical, String> colEndDate;
    @FXML private TableColumn<Medical, String> colReason;
    @FXML private TableColumn<Medical, String> colStatus;
    @FXML private TableColumn<Medical, Void> colActions;
    @FXML private Label statusLabel;

    private String addedBy;
    private final MedicalDAO medicalDAO = new MedicalDAO();

    @FXML
    public void initialize() {
        departmentFilter.getItems().addAll("ICT", "BST", "ET");
        statusFilter.getItems().addAll("All", "Pending", "Verified", "Rejected");
        statusFilter.setValue("All");

        setupTableColumns();
        loadMedicalData();

        departmentFilter.valueProperty().addListener((obs, old, val) -> filterTable());
        statusFilter.valueProperty().addListener((obs, old, val) -> filterTable());
    }

    public void setAddedBy(String addedBy) {
        this.addedBy = addedBy;
    }

    private void setupTableColumns() {
        colId.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getMedicalId())));
        colStudentId.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStudentId()));
        colDepartment.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDepartment()));
        colStartDate.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMedicalStartDate()));
        colEndDate.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMedicalEndDate()));
        colReason.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getReason()));
        colStatus.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus()));

        colActions.setCellFactory(param -> new TableCell<>() {
            private final Button viewBtn = new Button("👁️");
            private final Button editBtn = new Button("✏️");
            private final Button delBtn = new Button("🗑️");
            private final HBox pane = new HBox(5, viewBtn, editBtn, delBtn);

            {
                viewBtn.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
                editBtn.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
                delBtn.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-text-fill: red;");

                viewBtn.setOnAction(event -> handleViewDocument(getTableView().getItems().get(getIndex())));
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

    private void loadMedicalData() {
        List<Medical> data = medicalDAO.getAllMedicals();
        Platform.runLater(() -> medicalTable.setItems(FXCollections.observableArrayList(data)));
    }

    @FXML
    private void handleSearch() {
        String keyword = searchField.getText();
        if (keyword == null || keyword.trim().isEmpty()) {
            loadMedicalData();
            return;
        }
        List<Medical> data = medicalDAO.searchMedicals(keyword.trim());
        medicalTable.setItems(FXCollections.observableArrayList(data));
    }

    @FXML
    private void handleRefresh() {
        searchField.clear();
        departmentFilter.setValue(null);
        statusFilter.setValue("All");
        loadMedicalData();
        statusLabel.setText("Data refreshed.");
    }

    private void filterTable() {
        String dept = departmentFilter.getValue();
        String status = statusFilter.getValue();

        List<Medical> allData = medicalDAO.getAllMedicals();
        List<Medical> filtered = allData.stream()
                .filter(m -> dept == null || (m.getDepartment() != null && m.getDepartment().equals(dept)))
                .filter(m -> status == null || "All".equals(status) || (m.getStatus() != null && m.getStatus().equals(status)))
                .toList();

        medicalTable.setItems(FXCollections.observableArrayList(filtered));
    }

    @FXML
    private void handleAddMedical() {
        showMedicalDialog(null);
    }

    private void handleEdit(Medical m) {
        showMedicalDialog(m);
    }

    private void handleDelete(Medical m) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Deletion");
        alert.setHeaderText("Delete Medical Record");
        alert.setContentText("Are you sure you want to delete this record for " + m.getStudentId() + "?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (medicalDAO.deleteMedical(m.getMedicalId())) {
                statusLabel.setText("Record deleted successfully.");
                handleRefresh();
            } else {
                statusLabel.setText("Failed to delete record.");
            }
        }
    }

    private void handleViewDocument(Medical m) {
        if (m.getMedicalData() != null && m.getMedicalData().length > 0) {
            statusLabel.setText("File exists in DB (" + m.getMedicalData().length + " bytes). Note: Real document viewer requires PDF rendering library.");
        } else {
            statusLabel.setText("No document attached to this record.");
        }
    }

    private void showMedicalDialog(Medical m) {
        Dialog<Medical> dialog = new Dialog<>();
        dialog.setTitle(m == null ? "Add Medical Record" : "Edit Medical Record");
        dialog.setHeaderText(null);

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField studentId = new TextField();
        ComboBox<String> department = new ComboBox<>(FXCollections.observableArrayList("ICT", "BST", "ET"));
        TextField batch = new TextField();
        DatePicker startDate = new DatePicker();
        DatePicker endDate = new DatePicker();
        TextField reason = new TextField();
        ComboBox<String> status = new ComboBox<>(FXCollections.observableArrayList("Pending", "Verified", "Rejected"));

        Button uploadBtn = new Button("Upload File");
        Label fileLabel = new Label("No file selected");
        final byte[][] fileData = new byte[1][1];

        uploadBtn.setOnAction(e -> {
            FileChooser fc = new FileChooser();
            File file = fc.showOpenDialog(dialog.getOwner());
            if (file != null) {
                try {
                    fileData[0] = Files.readAllBytes(file.toPath());
                    fileLabel.setText(file.getName());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        if (m != null) {
            studentId.setText(m.getStudentId());
            department.setValue(m.getDepartment());
            batch.setText(m.getBatch());
            if (m.getMedicalStartDate() != null) startDate.setValue(LocalDate.parse(m.getMedicalStartDate()));
            if (m.getMedicalEndDate() != null) endDate.setValue(LocalDate.parse(m.getMedicalEndDate()));
            reason.setText(m.getReason());
            status.setValue(m.getStatus());
            fileData[0] = m.getMedicalData();
            if (fileData[0] != null) fileLabel.setText("Existing file loaded");
        } else {
            status.setValue("Pending");
        }

        grid.add(new Label("Student ID:"), 0, 0); grid.add(studentId, 1, 0);
        grid.add(new Label("Department:"), 0, 1); grid.add(department, 1, 1);
        grid.add(new Label("Batch:"), 0, 2); grid.add(batch, 1, 2);
        grid.add(new Label("Start Date:"), 0, 3); grid.add(startDate, 1, 3);
        grid.add(new Label("End Date:"), 0, 4); grid.add(endDate, 1, 4);
        grid.add(new Label("Reason:"), 0, 5); grid.add(reason, 1, 5);
        grid.add(new Label("Status:"), 0, 6); grid.add(status, 1, 6);
        grid.add(uploadBtn, 0, 7); grid.add(fileLabel, 1, 7);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                if (studentId.getText().isEmpty() || startDate.getValue() == null || endDate.getValue() == null) {
                    return null;
                }
                if (m == null) {
                    return new Medical(studentId.getText(), batch.getText(), department.getValue(),
                            startDate.getValue().toString(), endDate.getValue().toString(),
                            fileData[0], reason.getText(), addedBy, status.getValue());
                } else {
                    return new Medical(m.getMedicalId(), studentId.getText(), batch.getText(), department.getValue(),
                            startDate.getValue().toString(), endDate.getValue().toString(),
                            fileData[0], reason.getText(), addedBy, status.getValue());
                }
            }
            return null;
        });

        Optional<Medical> result = dialog.showAndWait();
        result.ifPresent(med -> {
            boolean success = m == null ? medicalDAO.addMedical(med) : medicalDAO.updateMedical(med);
            if (success) {
                statusLabel.setText("Medical record saved successfully.");
                handleRefresh();
            } else {
                statusLabel.setText("Failed to save medical record.");
            }
        });
    }
}
