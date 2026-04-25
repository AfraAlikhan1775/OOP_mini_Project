package com.controller.Student;

import com.dao.student.StudentGradeDAO;
import com.model.student.StudentResultSheetRow;
import com.session.StudentSession;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

public class stuGradesController {

    @FXML private ComboBox<String> semesterBox;
    @FXML private Label messageLabel;

    @FXML private TableView<StudentResultSheetRow> myResultTable;
    @FXML private TableView<StudentResultSheetRow> batchResultTable;

    private final StudentGradeDAO dao = new StudentGradeDAO();

    @FXML
    public void initialize() {
        setupEmptyColumns(myResultTable);
        setupEmptyColumns(batchResultTable);

        semesterBox.setOnAction(e -> loadMyResult());

        loadSemesters();
    }

    private void loadSemesters() {
        String regNo = StudentSession.getUsername();

        if (regNo == null || regNo.isBlank()) {
            messageLabel.setText("Student session not found. Login again.");
            return;
        }

        List<String> semesters = dao.getSemestersForStudent(regNo);

        semesterBox.setItems(FXCollections.observableArrayList(semesters));

        if (semesters.isEmpty()) {
            messageLabel.setText("No semester found. Check student department/year and courses table.");
            return;
        }

        semesterBox.getSelectionModel().selectFirst();
        loadMyResult();
    }

    @FXML
    private void loadMyResult() {
        String regNo = StudentSession.getUsername();
        String semester = semesterBox.getValue();

        if (semester == null || semester.isBlank()) {
            messageLabel.setText("Please select semester.");
            return;
        }

        List<String> subjects = dao.getStudentSubjectCodes(regNo, semester);

        if (subjects.isEmpty()) {
            setupEmptyColumns(myResultTable);
            myResultTable.getItems().clear();
            messageLabel.setText("No subjects found for this semester.");
            return;
        }

        setupDynamicColumns(myResultTable, subjects);

        StudentResultSheetRow row = dao.getStudentResult(regNo, semester);
        myResultTable.setItems(FXCollections.observableArrayList(row));

        messageLabel.setText("Loaded: " + subjects.size() + " subject(s).");
    }

    @FXML
    private void loadBatchResult() {
        String regNo = StudentSession.getUsername();
        String semester = semesterBox.getValue();

        if (semester == null || semester.isBlank()) {
            messageLabel.setText("Please select semester.");
            return;
        }

        List<String> subjects = dao.getStudentSubjectCodes(regNo, semester);
        List<StudentResultSheetRow> rows = dao.getBatchResult(regNo, semester);

        setupDynamicColumns(batchResultTable, subjects);
        batchResultTable.setItems(FXCollections.observableArrayList(rows));

        messageLabel.setText("Whole batch result loaded.");
    }

    private void setupEmptyColumns(TableView<StudentResultSheetRow> table) {
        table.getColumns().clear();

        TableColumn<StudentResultSheetRow, String> regCol = new TableColumn<>("Reg No");
        regCol.setPrefWidth(170);
        regCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getRegNo()));

        TableColumn<StudentResultSheetRow, String> sgpaCol = new TableColumn<>("SGPA");
        sgpaCol.setPrefWidth(100);
        sgpaCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getSgpa()));

        TableColumn<StudentResultSheetRow, String> cgpaCol = new TableColumn<>("CGPA");
        cgpaCol.setPrefWidth(100);
        cgpaCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getCgpa()));

        table.getColumns().addAll(regCol, sgpaCol, cgpaCol);
        table.setPlaceholder(new Label("No result data found"));
    }

    private void setupDynamicColumns(TableView<StudentResultSheetRow> table, List<String> subjects) {
        table.getColumns().clear();

        TableColumn<StudentResultSheetRow, String> regCol = new TableColumn<>("Reg No");
        regCol.setPrefWidth(170);
        regCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getRegNo()));

        table.getColumns().add(regCol);

        for (String subject : subjects) {
            TableColumn<StudentResultSheetRow, String> col = new TableColumn<>(subject);
            col.setPrefWidth(115);
            col.setCellValueFactory(data ->
                    new SimpleStringProperty(data.getValue().getCourseGrade(subject)));

            table.getColumns().add(col);
        }

        TableColumn<StudentResultSheetRow, String> sgpaCol = new TableColumn<>("SGPA");
        sgpaCol.setPrefWidth(100);
        sgpaCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getSgpa()));

        TableColumn<StudentResultSheetRow, String> cgpaCol = new TableColumn<>("CGPA");
        cgpaCol.setPrefWidth(100);
        cgpaCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getCgpa()));

        table.getColumns().addAll(sgpaCol, cgpaCol);
    }

    @FXML
    private void refreshResult() {
        loadSemesters();
        setupEmptyColumns(batchResultTable);
        batchResultTable.getItems().clear();
    }
}