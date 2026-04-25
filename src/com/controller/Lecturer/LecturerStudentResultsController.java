package com.controller.Lecturer;

import com.dao.Lecturer.LecturerStudentResultsDAO;
import com.model.Lecturerr.LecturerStudentResultRow;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

public class LecturerStudentResultsController {

    @FXML private ComboBox<String> departmentCombo;
    @FXML private ComboBox<String> yearCombo;
    @FXML private ComboBox<String> semesterCombo;
    @FXML private ComboBox<String> resultTypeCombo;

    @FXML private Button viewButton;
    @FXML private Button refreshButton;

    @FXML private TableView<LecturerStudentResultRow> resultTable;
    @FXML private Label messageLabel;

    private final LecturerStudentResultsDAO dao = new LecturerStudentResultsDAO();

    private String lecturerEmpId;

    public void setLecturerEmpId(String lecturerEmpId) {
        this.lecturerEmpId = lecturerEmpId;
    }

    public void setLecturerId(String lecturerEmpId) {
        this.lecturerEmpId = lecturerEmpId;
    }

    @FXML
    public void initialize() {
        resultTypeCombo.setItems(FXCollections.observableArrayList(
                "Marks", "Result", "Eligibility"
        ));
        resultTypeCombo.setValue("Marks");

        loadDepartments();

        departmentCombo.setOnAction(e -> loadYears());
        yearCombo.setOnAction(e -> loadSemesters());

        viewButton.setOnAction(e -> loadResults());
        refreshButton.setOnAction(e -> handleRefresh());

        messageLabel.setText("Select department, year, semester and view type.");
    }

    private void loadDepartments() {
        departmentCombo.setItems(FXCollections.observableArrayList(dao.getDepartments()));
        yearCombo.getItems().clear();
        semesterCombo.getItems().clear();
        resultTable.getItems().clear();
        resultTable.getColumns().clear();
    }

    private void loadYears() {
        String department = departmentCombo.getValue();

        yearCombo.getItems().clear();
        semesterCombo.getItems().clear();
        resultTable.getItems().clear();
        resultTable.getColumns().clear();

        if (department == null) return;

        yearCombo.setItems(FXCollections.observableArrayList(
                dao.getYears(department)
        ));
    }

    private void loadSemesters() {
        String department = departmentCombo.getValue();
        String year = yearCombo.getValue();

        semesterCombo.getItems().clear();
        resultTable.getItems().clear();
        resultTable.getColumns().clear();

        if (department == null || year == null) return;

        semesterCombo.setItems(FXCollections.observableArrayList(
                dao.getSemesters(department, year)
        ));
    }

    private void loadResults() {
        String department = departmentCombo.getValue();
        String year = yearCombo.getValue();
        String semester = semesterCombo.getValue();
        String viewType = resultTypeCombo.getValue();

        if (department == null || year == null || semester == null || viewType == null) {
            showAlert("Please select department, year, semester and view type.");
            return;
        }

        List<String> courseIds = dao.getCourseIdsByFilter(department, year, semester);

        List<LecturerStudentResultRow> rows =
                dao.getStudentSummaryResults(department, year, semester, viewType);

        buildDynamicTable(courseIds, viewType);

        resultTable.setItems(FXCollections.observableArrayList(rows));

        messageLabel.setText(rows.isEmpty()
                ? "No results found."
                : "Loaded " + rows.size() + " students.");
    }

    private void buildDynamicTable(List<String> courseIds, String viewType) {
        resultTable.getColumns().clear();

        TableColumn<LecturerStudentResultRow, String> regCol =
                new TableColumn<>("Reg No");
        regCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getRegNo()));
        regCol.setPrefWidth(150);

        TableColumn<LecturerStudentResultRow, String> nameCol =
                new TableColumn<>("Student Name");
        nameCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getStudentName()));
        nameCol.setPrefWidth(220);

        resultTable.getColumns().add(regCol);
        resultTable.getColumns().add(nameCol);

        for (String courseId : courseIds) {
            TableColumn<LecturerStudentResultRow, String> courseCol =
                    new TableColumn<>(courseId);

            courseCol.setCellValueFactory(data ->
                    new SimpleStringProperty(
                            data.getValue().getCourseValues().getOrDefault(courseId, "-")
                    ));

            courseCol.setPrefWidth("Marks".equalsIgnoreCase(viewType) ? 260 : 200);
            resultTable.getColumns().add(courseCol);
        }

        if (!"Eligibility".equalsIgnoreCase(viewType)) {
            TableColumn<LecturerStudentResultRow, String> sgpaCol =
                    new TableColumn<>("SGPA");
            sgpaCol.setCellValueFactory(data ->
                    new SimpleStringProperty(data.getValue().getSgpa()));
            sgpaCol.setPrefWidth(100);

            TableColumn<LecturerStudentResultRow, String> cgpaCol =
                    new TableColumn<>("CGPA");
            cgpaCol.setCellValueFactory(data ->
                    new SimpleStringProperty(data.getValue().getCgpa()));
            cgpaCol.setPrefWidth(100);

            resultTable.getColumns().add(sgpaCol);
            resultTable.getColumns().add(cgpaCol);
        }
    }

    private void handleRefresh() {
        departmentCombo.getSelectionModel().clearSelection();
        yearCombo.getItems().clear();
        semesterCombo.getItems().clear();
        resultTypeCombo.setValue("Marks");

        resultTable.getItems().clear();
        resultTable.getColumns().clear();

        loadDepartments();
        messageLabel.setText("Refreshed.");
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Student Results");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}