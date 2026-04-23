package com.controller.techOfficerControllers;

import com.dao.admin.TimetableDAO;
import com.model.admin.TimetableSessionInput;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class TOTimetableController {

    @FXML private Label deptLabel;
    @FXML private Label infoLabel;
    @FXML private Label statusLabel;
    @FXML private VBox cardContainer;
    @FXML private VBox sessionContainer;
    @FXML private ScrollPane sessionScrollPane;

    private final TimetableDAO timetableDAO = new TimetableDAO();

    @FXML
    public void initialize() {
        deptLabel.setText("Department: All");
        infoLabel.setText("Showing all department timetables");
        loadAllTimetableCards();
    }

    private void loadAllTimetableCards() {
        cardContainer.getChildren().clear();
        sessionContainer.getChildren().clear();

        List<TimetableDAO.TimetableSummary> allGroups = new ArrayList<>();

        allGroups.addAll(timetableDAO.filterByDepartment("ICT"));
        allGroups.addAll(timetableDAO.filterByDepartment("ET"));
        allGroups.addAll(timetableDAO.filterByDepartment("BST"));

        if (allGroups.isEmpty()) {
            Label empty = new Label("No timetable records found.");
            empty.setStyle("-fx-text-fill:#64748b; -fx-font-size:14px;");
            cardContainer.getChildren().add(empty);
            statusLabel.setText("0 timetable group(s) found");
            return;
        }

        for (TimetableDAO.TimetableSummary summary : allGroups) {
            HBox card = createCard(summary);
            cardContainer.getChildren().add(card);
        }

        statusLabel.setText(allGroups.size() + " timetable group(s) loaded");
    }

    private HBox createCard(TimetableDAO.TimetableSummary summary) {
        HBox card = new HBox();
        card.setSpacing(15);
        card.setPadding(new Insets(20));
        card.setStyle(
                "-fx-background-color:white; " +
                        "-fx-background-radius:12; " +
                        "-fx-border-radius:12; " +
                        "-fx-border-color:#d9e2ec;"
        );
        card.setPrefHeight(100);

        Label title = new Label(summary.getDepartment() + " - Level " + summary.getLevel() + " - Semester " + summary.getSemester());
        title.setStyle("-fx-font-size:18px; -fx-font-weight:bold; -fx-text-fill:#0b1f36;");

        Label details = new Label("Sessions: " + summary.getSessionCount());
        details.setStyle("-fx-font-size:13px; -fx-text-fill:#475569;");

        VBox leftBox = new VBox(8, title, details);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button viewBtn = new Button("View");
        viewBtn.setStyle("-fx-background-color:#0b1f36; -fx-text-fill:white; -fx-font-weight:bold;");
        viewBtn.setOnAction(e -> {
            loadSessionsToPanel(summary.getId());
            openFullTimetable(summary.getId(), summary.getDepartment(), summary.getLevel(), summary.getSemester());
        });

        VBox rightBox = new VBox(viewBtn);
        rightBox.setAlignment(Pos.CENTER);

        card.getChildren().addAll(leftBox, spacer, rightBox);
        return card;
    }

    private void loadSessionsToPanel(int groupId) {
        sessionContainer.getChildren().clear();

        List<TimetableSessionInput> sessions = timetableDAO.getSessionsByGroupId(groupId);

        if (sessions == null || sessions.isEmpty()) {
            Label empty = new Label("No sessions found.");
            empty.setStyle("-fx-text-fill:#64748b; -fx-font-size:13px;");
            sessionContainer.getChildren().add(empty);
            return;
        }

        for (TimetableSessionInput session : sessions) {
            VBox sessionCard = new VBox(8);
            sessionCard.setPadding(new Insets(14));
            sessionCard.setStyle(
                    "-fx-background-color:#f8fafc; " +
                            "-fx-background-radius:12; " +
                            "-fx-border-color:#e2e8f0; " +
                            "-fx-border-radius:12;"
            );

            Label subject = new Label("Subject: " + value(session.getSubject()));
            subject.setStyle("-fx-font-size:14px; -fx-font-weight:bold; -fx-text-fill:#1e293b;");

            Label dayTime = new Label("Day / Time: " + value(session.getDayName()) + " | " + value(session.getStartTime()) + " - " + value(session.getEndTime()));
            dayTime.setStyle("-fx-text-fill:#475569;");

            Label lecturer = new Label("Lecturer: " + value(session.getLecturer()));
            lecturer.setStyle("-fx-text-fill:#475569;");

            Label room = new Label("Room: " + value(session.getRoom()));
            room.setStyle("-fx-text-fill:#475569;");

            Label type = new Label("Type: " + value(session.getSessionType()));
            type.setStyle("-fx-text-fill:#475569;");

            sessionCard.getChildren().addAll(subject, dayTime, lecturer, room, type);
            sessionContainer.getChildren().add(sessionCard);
        }

        sessionScrollPane.setVvalue(0);
    }

    private void openFullTimetable(int groupId, String department, int level, int semester) {
        List<TimetableSessionInput> sessions = timetableDAO.getSessionsByGroupId(groupId);

        Stage stage = new Stage();
        stage.setTitle("Full Timetable");

        VBox root = new VBox(15);
        root.setStyle("-fx-background-color:#eaf1f8; -fx-padding:20;");

        Label header = new Label(department + " - Level " + level + " - Semester " + semester);
        header.setStyle("-fx-font-size:20px; -fx-font-weight:bold; -fx-text-fill:#0b1f36;");

        GridPane grid = new GridPane();
        grid.setHgap(4);
        grid.setVgap(4);

        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
        String[] times = {"08:00", "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00"};

        Label corner = new Label("Day / Time");
        corner.setPrefSize(120, 45);
        corner.setAlignment(Pos.CENTER);
        corner.setStyle("-fx-background-color:#0b1f36; -fx-text-fill:white; -fx-font-weight:bold;");
        grid.add(corner, 0, 0);

        for (int i = 0; i < times.length; i++) {
            Label timeLabel = new Label(times[i]);
            timeLabel.setPrefSize(120, 45);
            timeLabel.setAlignment(Pos.CENTER);
            timeLabel.setStyle("-fx-background-color:#0b1f36; -fx-text-fill:white; -fx-font-weight:bold;");
            grid.add(timeLabel, i + 1, 0);
        }

        for (int i = 0; i < days.length; i++) {
            Label dayLabel = new Label(days[i]);
            dayLabel.setPrefSize(120, 80);
            dayLabel.setAlignment(Pos.CENTER);
            dayLabel.setStyle("-fx-background-color:white; -fx-border-color:#d9e2ec; -fx-font-weight:bold;");
            grid.add(dayLabel, 0, i + 1);

            for (int j = 0; j < times.length; j++) {
                StackPane cell = new StackPane();
                cell.setPrefSize(120, 80);
                cell.setStyle("-fx-background-color:white; -fx-border-color:#d9e2ec;");
                grid.add(cell, j + 1, i + 1);
            }
        }

        for (TimetableSessionInput session : sessions) {
            int row = getDayRow(session.getDayName());
            int col = getTimeColumn(session.getStartTime());

            if (row != -1 && col != -1) {
                VBox box = new VBox(3);
                box.setAlignment(Pos.CENTER);
                box.setStyle("-fx-background-color:#dcecff; -fx-border-color:#d9e2ec; -fx-padding:5;");

                Label subject = new Label(value(session.getSubject()));
                subject.setStyle("-fx-font-weight:bold;");
                subject.setWrapText(true);

                Label lec = new Label(value(session.getLecturer()));
                Label room = new Label(value(session.getRoom()));

                box.getChildren().addAll(subject, lec, room);
                grid.add(box, col, row);
            }
        }

        root.getChildren().addAll(header, grid);

        ScrollPane scroll = new ScrollPane(root);
        scroll.setFitToWidth(true);

        Scene scene = new Scene(scroll, 1200, 700);
        stage.setScene(scene);
        stage.show();
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

    private int getTimeColumn(String time) {
        if (time == null) return -1;

        return switch (time.trim()) {
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

    private String value(Object obj) {
        return obj == null ? "" : obj.toString();
    }
}