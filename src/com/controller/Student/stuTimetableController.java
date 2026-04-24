package com.controller.Student;

import com.dao.student.StudentAcademicDAO;
import com.model.student.StudentTimetableRow;
import com.session.StudentSession;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.List;

public class stuTimetableController {

    @FXML private VBox cardContainer;

    private final StudentAcademicDAO dao = new StudentAcademicDAO();

    @FXML
    public void initialize() {
        loadTimetableCard();
    }

    private void loadTimetableCard() {
        cardContainer.getChildren().clear();

        String regNo = StudentSession.getUsername();
        int year = dao.getYearFromRegNo(regNo);
        int semester = dao.getCurrentSemester();
        String department = dao.getStudentDepartment(regNo);

        List<StudentTimetableRow> sessions = dao.getStudentTimetable(regNo);

        if (sessions.isEmpty()) {
            cardContainer.getChildren().add(new Label("No timetable found for your batch."));
            return;
        }

        HBox card = new HBox(15);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPrefHeight(95);
        card.setStyle("""
                -fx-background-color:white;
                -fx-background-radius:12;
                -fx-border-radius:12;
                -fx-border-color:#d9e2ec;
                -fx-padding:20;
                """);

        VBox left = new VBox(8);

        Label title = new Label(department + " - Level " + year + " - Semester " + semester);
        title.setStyle("-fx-font-size:18px; -fx-font-weight:bold; -fx-text-fill:#0b1f36;");

        Label count = new Label("Total sessions: " + sessions.size());

        left.getChildren().addAll(title, count);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button viewBtn = new Button("View");
        viewBtn.setStyle("-fx-background-color:#0b1f36; -fx-text-fill:white;");
        viewBtn.setOnAction(e -> openFullTimetable(department, year, semester, sessions));

        card.getChildren().addAll(left, spacer, viewBtn);
        cardContainer.getChildren().add(card);
    }

    private void openFullTimetable(String department, int year, int semester, List<StudentTimetableRow> sessions) {
        Stage stage = new Stage();
        stage.setTitle("My Timetable");

        VBox root = new VBox(15);
        root.setStyle("-fx-background-color:#eaf1f8; -fx-padding:20;");

        Label header = new Label(department + " - Level " + year + " - Semester " + semester);
        header.setStyle("-fx-font-size:20px; -fx-font-weight:bold; -fx-text-fill:#0b1f36;");

        GridPane grid = new GridPane();
        grid.setHgap(4);
        grid.setVgap(4);

        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
        String[] times = {
                "08:00", "09:00", "10:00", "11:00", "12:00",
                "13:00", "14:00", "15:00", "16:00", "17:00"
        };

        Label corner = createHeaderCell("Day / Time");
        grid.add(corner, 0, 0);

        for (int i = 0; i < times.length; i++) {
            grid.add(createHeaderCell(times[i]), i + 1, 0);
        }

        for (int i = 0; i < days.length; i++) {
            grid.add(createDayCell(days[i]), 0, i + 1);

            for (int j = 0; j < times.length; j++) {
                StackPane empty = new StackPane();
                empty.setPrefSize(120, 85);
                empty.setStyle("-fx-background-color:white; -fx-border-color:#d9e2ec;");
                grid.add(empty, j + 1, i + 1);
            }
        }

        for (StudentTimetableRow row : sessions) {
            int r = getDayRow(row.getDay());
            int c = getTimeColumn(row.getTime());

            if (r != -1 && c != -1) {
                VBox box = new VBox(4);
                box.setAlignment(Pos.CENTER);
                box.setStyle("-fx-background-color:#dcecff; -fx-border-color:#8ab6f9; -fx-padding:5;");

                Label subject = new Label(row.getSubject());
                subject.setStyle("-fx-font-weight:bold;");
                subject.setWrapText(true);

                Label lec = new Label(row.getLecturer());
                Label room = new Label(row.getRoom());
                Label type = new Label(row.getType());

                box.getChildren().addAll(subject, lec, room, type);
                grid.add(box, c, r);
            }
        }

        root.getChildren().addAll(header, grid);

        ScrollPane scroll = new ScrollPane(root);
        scroll.setFitToWidth(true);

        stage.setScene(new Scene(scroll, 1200, 700));
        stage.setResizable(false);
        stage.show();
    }

    private Label createHeaderCell(String text) {
        Label label = new Label(text);
        label.setPrefSize(120, 45);
        label.setAlignment(Pos.CENTER);
        label.setStyle("-fx-background-color:#0b1f36; -fx-text-fill:white; -fx-font-weight:bold;");
        return label;
    }

    private Label createDayCell(String text) {
        Label label = new Label(text);
        label.setPrefSize(120, 85);
        label.setAlignment(Pos.CENTER);
        label.setStyle("-fx-background-color:white; -fx-border-color:#d9e2ec; -fx-font-weight:bold;");
        return label;
    }

    private int getDayRow(String day) {
        if (day == null) return -1;

        return switch (day.trim().toLowerCase()) {
            case "monday" -> 1;
            case "tuesday" -> 2;
            case "wednesday" -> 3;
            case "thursday" -> 4;
            case "friday" -> 5;
            default -> -1;
        };
    }

    private int getTimeColumn(String timeRange) {
        if (timeRange == null) return -1;

        String t = timeRange.split("-")[0].trim();

        return switch (t) {
            case "08:00", "8:00", "8.00" -> 1;
            case "09:00", "9:00", "9.00" -> 2;
            case "10:00", "10.00" -> 3;
            case "11:00", "11.00" -> 4;
            case "12:00", "12.00" -> 5;
            case "13:00", "1:00", "13.00" -> 6;
            case "14:00", "2:00", "14.00" -> 7;
            case "15:00", "3:00", "15.00" -> 8;
            case "16:00", "4:00", "16.00" -> 9;
            case "17:00", "5:00", "17.00" -> 10;
            default -> -1;
        };
    }
}