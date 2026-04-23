package com.controller.Lecturer;

import com.dao.Lecturer.LecturerMarksDAO;
import com.model.CourseAssessmentScheme;
import com.model.StudentMark;
import com.util.MarksCalculator;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.DoubleStringConverter;

import java.util.List;

public class LecturerMarksController {

    @FXML private Label courseTitleLabel;
    @FXML private TextField semesterField;
    @FXML private TextField academicYearField;
    @FXML private CheckBox theoryCheckBox;
    @FXML private CheckBox practicalCheckBox;
    @FXML private TableView<StudentMark> marksTable;

    @FXML private TableColumn<StudentMark, String> regNoCol;
    @FXML private TableColumn<StudentMark, String> studentNameCol;
    @FXML private TableColumn<StudentMark, Double> quiz1Col;
    @FXML private TableColumn<StudentMark, Double> quiz2Col;
    @FXML private TableColumn<StudentMark, Double> quiz3Col;
    @FXML private TableColumn<StudentMark, Double> assignmentCol;
    @FXML private TableColumn<StudentMark, Double> midCol;
    @FXML private TableColumn<StudentMark, Double> finalTheoryCol;
    @FXML private TableColumn<StudentMark, Double> finalPracticalCol;
    @FXML private TableColumn<StudentMark, Double> caCol;
    @FXML private TableColumn<StudentMark, Double> totalCol;

    @FXML private Label statusLabel;

    private final LecturerMarksDAO lecturerMarksDAO = new LecturerMarksDAO();
    private String courseId;

    public void setCourseId(String courseId) {
        this.courseId = courseId;
        courseTitleLabel.setText("Marks Entry - " + courseId);
        loadScheme();
    }

    @FXML
    public void initialize() {
        marksTable.setEditable(true);

        regNoCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getRegNo()));
        studentNameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStudentName()));

        setupEditableDoubleColumn(quiz1Col, StudentMark::getQuiz1, StudentMark::setQuiz1);
        setupEditableDoubleColumn(quiz2Col, StudentMark::getQuiz2, StudentMark::setQuiz2);
        setupEditableDoubleColumn(quiz3Col, StudentMark::getQuiz3, StudentMark::setQuiz3);
        setupEditableDoubleColumn(assignmentCol, StudentMark::getAssignment, StudentMark::setAssignment);
        setupEditableDoubleColumn(midCol, StudentMark::getMidExam, StudentMark::setMidExam);
        setupEditableDoubleColumn(finalTheoryCol, StudentMark::getFinalTheory, StudentMark::setFinalTheory);
        setupEditableDoubleColumn(finalPracticalCol, StudentMark::getFinalPractical, StudentMark::setFinalPractical);

        caCol.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getCaMark()).asObject());
        totalCol.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getFinalMark()).asObject());
    }

    private interface Getter {
        double get(StudentMark mark);
    }

    private interface Setter {
        void set(StudentMark mark, double value);
    }

    private void setupEditableDoubleColumn(TableColumn<StudentMark, Double> col, Getter getter, Setter setter) {
        col.setCellValueFactory(c -> new SimpleDoubleProperty(getter.get(c.getValue())).asObject());
        col.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        col.setOnEditCommit(event -> {
            StudentMark mark = event.getRowValue();
            double value = event.getNewValue() == null ? 0 : event.getNewValue();

            if (value < 0 || value > 100) {
                statusLabel.setText("All raw marks must be between 0 and 100.");
                marksTable.refresh();
                return;
            }

            setter.set(mark, value);
            recalculate(mark);
            marksTable.refresh();
        });
    }

    @FXML
    private void handleLoadStudents() {
        if (courseId == null || courseId.isBlank()) {
            statusLabel.setText("Course is not selected.");
            return;
        }

        String semester = semesterField.getText().trim();
        String academicYear = academicYearField.getText().trim();

        if (semester.isBlank() || academicYear.isBlank()) {
            statusLabel.setText("Enter semester and academic year.");
            return;
        }

        List<StudentMark> students = lecturerMarksDAO.getRegisteredStudentsForMarks(courseId, semester, academicYear);
        CourseAssessmentScheme scheme = getCurrentScheme();

        for (StudentMark mark : students) {
            recalculate(mark, scheme);
        }

        marksTable.setItems(FXCollections.observableArrayList(students));
        statusLabel.setText(students.size() + " student(s) loaded.");
    }

    @FXML
    private void handleSaveScheme() {
        if (courseId == null || courseId.isBlank()) {
            statusLabel.setText("Course is not selected.");
            return;
        }

        if (!theoryCheckBox.isSelected() && !practicalCheckBox.isSelected()) {
            statusLabel.setText("Select theory, practical, or both.");
            return;
        }

        CourseAssessmentScheme scheme = new CourseAssessmentScheme(
                courseId,
                theoryCheckBox.isSelected(),
                practicalCheckBox.isSelected()
        );

        boolean saved = lecturerMarksDAO.saveOrUpdateAssessmentScheme(scheme);
        statusLabel.setText(saved ? "Assessment scheme saved." : "Failed to save assessment scheme.");
    }

    @FXML
    private void handleSaveMarks() {
        CourseAssessmentScheme scheme = getCurrentScheme();

        if (scheme == null) {
            statusLabel.setText("Save assessment scheme first.");
            return;
        }

        for (StudentMark mark : marksTable.getItems()) {
            recalculate(mark, scheme);
        }

        boolean saved = lecturerMarksDAO.saveStudentMarksBulk(marksTable.getItems());
        marksTable.refresh();
        statusLabel.setText(saved ? "Marks saved successfully." : "Failed to save marks.");
    }

    private void loadScheme() {
        if (courseId == null || courseId.isBlank()) return;

        CourseAssessmentScheme scheme = lecturerMarksDAO.getAssessmentScheme(courseId);
        if (scheme != null) {
            theoryCheckBox.setSelected(scheme.isHasTheory());
            practicalCheckBox.setSelected(scheme.isHasPractical());
        }
    }

    private CourseAssessmentScheme getCurrentScheme() {
        if (courseId == null || courseId.isBlank()) return null;

        return new CourseAssessmentScheme(
                courseId,
                theoryCheckBox.isSelected(),
                practicalCheckBox.isSelected()
        );
    }

    private void recalculate(StudentMark mark) {
        recalculate(mark, getCurrentScheme());
    }

    private void recalculate(StudentMark mark, CourseAssessmentScheme scheme) {
        if (scheme == null) return;

        try {
            double ca = MarksCalculator.calculateCA(
                    mark.getQuiz1(),
                    mark.getQuiz2(),
                    mark.getQuiz3(),
                    mark.getAssignment(),
                    mark.getMidExam()
            );

            double total = MarksCalculator.calculateFinalMark(
                    ca,
                    mark.getFinalTheory(),
                    mark.getFinalPractical(),
                    scheme.isHasTheory(),
                    scheme.isHasPractical()
            );

            mark.setCaMark(ca);
            mark.setFinalMark(total);

        } catch (IllegalArgumentException e) {
            statusLabel.setText(e.getMessage());
        }
    }
}