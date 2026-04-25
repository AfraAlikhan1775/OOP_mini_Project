package com.controller.Lecturer;

import com.dao.Lecturer.MarksDAO;
import com.model.Lecturerr.MarksGroup;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class LecturerMarksGroupController {

    @FXML private VBox cardContainer;
    @FXML private TextField searchField;
    @FXML private Label statusLabel;

    private String lecturerId;
    private final MarksDAO dao = new MarksDAO();

    public void setLecturerId(String id) {
        this.lecturerId = id;
        loadCards();
    }

    @FXML
    public void initialize() {
    }

    private void loadCards() {
        if (cardContainer == null) return;

        cardContainer.getChildren().clear();

        if (lecturerId == null || lecturerId.isBlank()) {
            cardContainer.getChildren().add(new Label("Lecturer ID not loaded."));
            return;
        }

        List<MarksGroup> list = dao.getGroups(lecturerId);

        if (list.isEmpty()) {
            Label empty = new Label("No marks groups found.");
            empty.setStyle("-fx-font-size:16px; -fx-text-fill:#555;");
            cardContainer.getChildren().add(empty);
            statusLabel.setText("No groups available.");
            return;
        }

        for (MarksGroup g : list) {
            cardContainer.getChildren().add(createCard(g));
        }

        statusLabel.setText(list.size() + " marks group(s) loaded.");
    }

    @FXML
    private void handleSearch() {
        if (lecturerId == null || lecturerId.isBlank()) {
            statusLabel.setText("Lecturer ID not loaded.");
            return;
        }

        String keyword = searchField.getText() == null
                ? ""
                : searchField.getText().trim().toLowerCase();

        List<MarksGroup> list = dao.getGroups(lecturerId);

        if (!keyword.isEmpty()) {
            list = list.stream()
                    .filter(g ->
                            safe(g.getCourseId()).contains(keyword) ||
                                    safe(g.getExamType()).contains(keyword) ||
                                    safe(g.getSemester()).contains(keyword) ||
                                    safe(g.getYear()).contains(keyword) ||
                                    safe(g.getAcademicYear()).contains(keyword) ||
                                    safe(g.getExamDate()).contains(keyword)
                    )
                    .collect(Collectors.toList());
        }

        cardContainer.getChildren().clear();

        if (list.isEmpty()) {
            Label empty = new Label("No matching marks groups found.");
            empty.setStyle("-fx-font-size:16px; -fx-text-fill:#555;");
            cardContainer.getChildren().add(empty);
            statusLabel.setText("No matching results.");
            return;
        }

        for (MarksGroup g : list) {
            cardContainer.getChildren().add(createCard(g));
        }

        statusLabel.setText(list.size() + " result(s) found.");
    }

    @FXML
    private void handleCreateGroup() {
        if (lecturerId == null || lecturerId.isBlank()) {
            statusLabel.setText("Lecturer ID not loaded.");
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add Marks");
        dialog.setHeaderText("Enter exam details and student marks");

        ButtonType finishButton = new ButtonType("Finish", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(finishButton, ButtonType.CANCEL);

        VBox root = new VBox(12);
        root.setPadding(new Insets(15));

        GridPane topForm = new GridPane();
        topForm.setHgap(10);
        topForm.setVgap(8);

        TextField courseIdField = new TextField();

        ComboBox<String> yearBox = new ComboBox<>();
        yearBox.getItems().addAll("1", "2", "3", "4");

        ComboBox<String> semesterBox = new ComboBox<>();
        semesterBox.getItems().addAll("1", "2");

        TextField academicYearField = new TextField();

        ComboBox<String> examTypeBox = new ComboBox<>();
        examTypeBox.getItems().addAll(
                "Quiz 1",
                "Quiz 2",
                "Quiz 3",
                "Assignment",
                "Mid Exam",
                "Final Theory",
                "Final Practical"
        );

        DatePicker examDatePicker = new DatePicker();

        Label courseError = new Label();
        courseError.setStyle("-fx-text-fill:red; -fx-font-size:11px;");

        courseIdField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null || newVal.trim().isEmpty()) {
                courseError.setText("");
                courseIdField.setStyle("");
                return;
            }

            if (!dao.courseExists(newVal.trim())) {
                courseError.setText("Course does not exist.");
                courseIdField.setStyle("-fx-border-color:red;");
            } else {
                courseError.setText("");
                courseIdField.setStyle("");
            }
        });

        topForm.add(new Label("Course ID:"), 0, 0);
        topForm.add(courseIdField, 1, 0);
        topForm.add(courseError, 1, 1);

        topForm.add(new Label("Year:"), 0, 2);
        topForm.add(yearBox, 1, 2);

        topForm.add(new Label("Semester:"), 0, 3);
        topForm.add(semesterBox, 1, 3);

        topForm.add(new Label("Academic Year:"), 0, 4);
        topForm.add(academicYearField, 1, 4);

        topForm.add(new Label("Exam Type:"), 0, 5);
        topForm.add(examTypeBox, 1, 5);

        topForm.add(new Label("Exam Date:"), 0, 6);
        topForm.add(examDatePicker, 1, 6);

        Separator separator = new Separator();

        GridPane entryForm = new GridPane();
        entryForm.setHgap(10);
        entryForm.setVgap(8);

        TextField regNoField = new TextField();
        TextField markField = new TextField();
        Button addBtn = new Button("Add");

        Label regError = new Label();
        regError.setStyle("-fx-text-fill:red; -fx-font-size:11px;");

        Label markError = new Label();
        markError.setStyle("-fx-text-fill:red; -fx-font-size:11px;");

        entryForm.add(new Label("Reg No:"), 0, 0);
        entryForm.add(regNoField, 1, 0);

        entryForm.add(new Label("Mark:"), 2, 0);
        entryForm.add(markField, 3, 0);

        entryForm.add(addBtn, 4, 0);
        entryForm.add(regError, 1, 1);
        entryForm.add(markError, 3, 1);

        TableView<MarkRow> table = new TableView<>();
        table.setPrefHeight(250);

        TableColumn<MarkRow, String> regCol = new TableColumn<>("Reg No");
        regCol.setCellValueFactory(data -> data.getValue().regNoProperty());
        regCol.setPrefWidth(220);

        TableColumn<MarkRow, String> markCol = new TableColumn<>("Mark");
        markCol.setCellValueFactory(data -> data.getValue().markProperty());
        markCol.setPrefWidth(220);

        table.getColumns().addAll(regCol, markCol);

        ObservableList<MarkRow> rows = FXCollections.observableArrayList();
        table.setItems(rows);

        addBtn.setOnAction(e -> {
            regError.setText("");
            markError.setText("");

            String regNo = regNoField.getText() == null ? "" : regNoField.getText().trim();
            String markText = markField.getText() == null ? "" : markField.getText().trim();

            if (regNo.isEmpty()) {
                regError.setText("Enter reg no.");
                return;
            }

            if (!dao.studentExists(regNo)) {
                regError.setText("Student does not exist.");
                return;
            }

            double mark;

            try {
                mark = Double.parseDouble(markText);

                if (mark < 0 || mark > 100) {
                    markError.setText("Mark must be 0 to 100.");
                    return;
                }

            } catch (NumberFormatException ex) {
                markError.setText("Enter valid mark.");
                return;
            }

            boolean alreadyAdded = rows.stream()
                    .anyMatch(r -> r.getRegNo().equalsIgnoreCase(regNo));

            if (alreadyAdded) {
                regError.setText("This reg no is already added.");
                return;
            }

            rows.add(new MarkRow(regNo, String.valueOf(mark)));
            regNoField.clear();
            markField.clear();
        });

        root.getChildren().addAll(topForm, separator, entryForm, table);
        dialog.getDialogPane().setContent(root);

        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == finishButton) {
            String courseId = courseIdField.getText() == null ? "" : courseIdField.getText().trim();
            String year = yearBox.getValue();
            String semester = semesterBox.getValue();
            String academicYear = academicYearField.getText() == null ? "" : academicYearField.getText().trim();
            String examType = examTypeBox.getValue();
            String examDate = examDatePicker.getValue() == null
                    ? null
                    : examDatePicker.getValue().toString();

            if (courseId.isEmpty()
                    || year == null
                    || semester == null
                    || academicYear.isEmpty()
                    || examType == null
                    || examDate == null) {

                statusLabel.setText("Please fill exam details including exam date.");
                return;
            }

            if (!dao.courseExists(courseId)) {
                statusLabel.setText("Course does not exist.");
                return;
            }

            if (!dao.isCourseCoordinator(courseId, lecturerId)) {
                statusLabel.setText("You are not the coordinator of this course.");
                return;
            }

            if (rows.isEmpty()) {
                statusLabel.setText("Add at least one student mark.");
                return;
            }

            MarksGroup group = new MarksGroup(
                    0,
                    courseId,
                    year,
                    semester,
                    academicYear,
                    examType,
                    lecturerId,
                    examDate
            );

            int groupId = dao.createGroupAndReturnId(group);

            if (groupId == -1) {
                statusLabel.setText("Failed to create marks group. It may already exist.");
                return;
            }

            boolean allSaved = true;

            for (MarkRow row : rows) {
                double mark = Double.parseDouble(row.getMark());
                boolean ok = dao.addMark(groupId, row.getRegNo(), mark);

                if (!ok) {
                    allSaved = false;
                }
            }

            if (allSaved) {
                statusLabel.setText("Marks saved successfully.");
            } else {
                statusLabel.setText("Group created, but some marks failed to save.");
            }

            loadCards();
        }
    }

    private HBox createCard(MarksGroup g) {
        HBox card = new HBox(20);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setStyle("""
                -fx-background-color:white;
                -fx-padding:20;
                -fx-border-color:#d9e2ec;
                -fx-border-radius:10;
                -fx-background-radius:10;
                """);
        card.setPrefHeight(145);

        VBox details = new VBox(6);

        Label title = new Label(g.getExamType());
        title.setStyle("-fx-font-size:18px; -fx-font-weight:bold; -fx-text-fill:#0b1f36;");

        details.getChildren().addAll(
                title,
                new Label("Course: " + g.getCourseId()),
                new Label("Year: " + g.getYear() + " | Semester: " + g.getSemester()),
                new Label("Academic Year: " + g.getAcademicYear()),
                new Label("Exam Date: " + (g.getExamDate() == null ? "-" : g.getExamDate()))
        );

        Region space = new Region();
        HBox.setHgrow(space, Priority.ALWAYS);

        Button view = new Button("View");
        view.setStyle("-fx-background-color:#0b1f36; -fx-text-fill:white;");
        view.setOnAction(e -> handleView(g));

        Button remove = new Button("Remove");
        remove.setStyle("-fx-background-color:#c62828; -fx-text-fill:white;");
        remove.setOnAction(e -> {
            dao.deleteGroup(g.getGroupId());
            statusLabel.setText("Marks group removed.");
            loadCards();
        });

        VBox btnBox = new VBox(10, view, remove);
        btnBox.setAlignment(Pos.CENTER_RIGHT);

        card.getChildren().addAll(details, space, btnBox);
        return card;
    }

    private void handleView(MarksGroup g) {
        List<MarkRow> marks = dao.getMarksByGroup(g.getGroupId());

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Marks View");
        dialog.setHeaderText(g.getExamType() + " - " + g.getCourseId());

        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

        VBox box = new VBox(10);
        box.setPadding(new Insets(15));

        Label info = new Label(
                "Course ID: " + g.getCourseId() + "\n" +
                        "Year: " + g.getYear() + "\n" +
                        "Semester: " + g.getSemester() + "\n" +
                        "Academic Year: " + g.getAcademicYear() + "\n" +
                        "Exam Date: " + (g.getExamDate() == null ? "-" : g.getExamDate())
        );

        TableView<MarkRow> table = new TableView<>();
        table.setPrefHeight(300);

        TableColumn<MarkRow, String> regCol = new TableColumn<>("Reg No");
        regCol.setCellValueFactory(data -> data.getValue().regNoProperty());
        regCol.setPrefWidth(220);

        TableColumn<MarkRow, String> markCol = new TableColumn<>("Mark");
        markCol.setCellValueFactory(data -> data.getValue().markProperty());
        markCol.setPrefWidth(220);

        table.getColumns().addAll(regCol, markCol);
        table.setItems(FXCollections.observableArrayList(marks));

        box.getChildren().addAll(info, table);
        dialog.getDialogPane().setContent(box);
        dialog.showAndWait();
    }

    private String safe(String value) {
        return value == null ? "" : value.toLowerCase();
    }
}