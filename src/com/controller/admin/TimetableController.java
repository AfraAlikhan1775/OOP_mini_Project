package com.controller.admin;

import com.dao.admin.TimetableDAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.geometry.Pos;

import java.io.IOException;
import java.util.List;

public class TimetableController {

    @FXML
    private VBox cardContainer;
    @FXML
    private TextField searchField;
    @FXML
    private ComboBox<String> categoryComboBox;

    private final TimetableDAO timetableDAO = new TimetableDAO();

    @FXML
    public void initialize() {
        categoryComboBox.getItems().addAll("All", "ET", "ICT", "BST");
        categoryComboBox.setValue("All");
        loadAllCards();
    }

    private void loadAllCards() {
        List<TimetableDAO.TimetableSummary> list = timetableDAO.getAllTimetableSummaries();
        renderCards(list);
    }

    @FXML
    private void handleSearch() {
        String keyword = searchField.getText();
        if (keyword == null || keyword.isBlank()) {
            loadAllCards();
            return;
        }
        renderCards(timetableDAO.searchTimetableSummaries(keyword));
    }

    @FXML
    private void handleFilter() {
        String selected = categoryComboBox.getValue();
        if (selected == null || selected.equals("All")) {
            loadAllCards();
            return;
        }
        renderCards(timetableDAO.filterByDepartment(selected));
    }

    private void renderCards(List<TimetableDAO.TimetableSummary> list) {
        cardContainer.getChildren().clear();

        for (TimetableDAO.TimetableSummary summary : list) {
            HBox card = new HBox();
            card.setSpacing(15);
            card.setStyle("-fx-background-color:white; -fx-background-radius:12; -fx-border-radius:12; -fx-border-color:#d9e2ec; -fx-padding:20;");
            card.setPrefHeight(90);

            Label title = new Label(summary.getDepartment() + " - Level " + summary.getLevel() + " - Semester " + summary.getSemester());
            title.setStyle("-fx-font-size:18px; -fx-font-weight:bold; -fx-text-fill:#0b1f36;");

            VBox leftBox = new VBox(title);
            leftBox.setSpacing(8);

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            Button viewBtn = new Button("View");
            viewBtn.setStyle("-fx-background-color:#0b1f36; -fx-text-fill:white;");
            viewBtn.setOnAction(e -> openFullTimetable(summary.getId(), summary.getDepartment(), summary.getLevel(), summary.getSemester()));

            Button removeBtn = new Button("Remove");
            removeBtn.setStyle("-fx-background-color:#b22222; -fx-text-fill:white;");

            HBox buttonBox = new HBox(viewBtn, removeBtn);
            buttonBox.setSpacing(10);
            buttonBox.setAlignment(Pos.CENTER_RIGHT);

            card.getChildren().addAll(leftBox, spacer, buttonBox);
            cardContainer.getChildren().add(card);
        }
    }

    @FXML
    private void navigateAddtimetable() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/view/admin/add_timetable.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Add Timetable");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.showAndWait();

            loadAllCards();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openFullTimetable(int groupId, String department, int level, int semester) {

        List<com.model.admin.TimetableSessionInput> sessions =
                timetableDAO.getSessionsByGroupId(groupId);

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
        String[] times = {
                "08:00", "09:00", "10:00", "11:00", "12:00",
                "13:00", "14:00", "15:00", "16:00", "17:00"
        };

        // 🔷 Top-left corner
        Label corner = new Label("Day / Time");
        corner.setPrefSize(120, 45);
        corner.setAlignment(Pos.CENTER);
        corner.setStyle("-fx-background-color:#0b1f36; -fx-text-fill:white; -fx-font-weight:bold;");
        grid.add(corner, 0, 0);

        // 🔷 Top row = TIMES
        for (int i = 0; i < times.length; i++) {
            Label timeLabel = new Label(times[i]);
            timeLabel.setPrefSize(120, 45);
            timeLabel.setAlignment(Pos.CENTER);
            timeLabel.setStyle("-fx-background-color:#0b1f36; -fx-text-fill:white; -fx-font-weight:bold;");
            grid.add(timeLabel, i + 1, 0);
        }

        // 🔷 First column = DAYS
        for (int i = 0; i < days.length; i++) {
            Label dayLabel = new Label(days[i]);
            dayLabel.setPrefSize(120, 80);
            dayLabel.setAlignment(Pos.CENTER);
            dayLabel.setStyle("-fx-background-color:white; -fx-border-color:#d9e2ec; -fx-font-weight:bold;");
            grid.add(dayLabel, 0, i + 1);

            // create empty cells
            for (int j = 0; j < times.length; j++) {
                StackPane cell = new StackPane();
                cell.setPrefSize(120, 80);
                cell.setStyle("-fx-background-color:white; -fx-border-color:#d9e2ec;");
                grid.add(cell, j + 1, i + 1);
            }
        }

        // 🔥 Fill sessions
        for (com.model.admin.TimetableSessionInput session : sessions) {

            int row = getDayRow(session.getDayName());   // day → row
            int col = getTimeColumn(session.getStartTime()); // time → column

            if (row != -1 && col != -1) {

                VBox box = new VBox(3);
                box.setAlignment(Pos.CENTER);
                box.setStyle("-fx-background-color:#dcecff; -fx-border-color:#d9e2ec; -fx-padding:5;");

                Label subject = new Label(session.getSubject());
                subject.setStyle("-fx-font-weight:bold;");
                subject.setWrapText(true);

                Label lec = new Label(session.getLecturer());
                Label room = new Label(session.getRoom());

                box.getChildren().addAll(subject, lec, room);

                grid.add(box, col, row);
            }
        }

        Button updateBtn = new Button("Update");
        updateBtn.setStyle("-fx-background-color:#0b1f36; -fx-text-fill:white;");
        updateBtn.setPrefWidth(120);

        HBox bottom = new HBox(updateBtn);
        bottom.setAlignment(Pos.CENTER_RIGHT);

        root.getChildren().addAll(header, grid, bottom);

        ScrollPane scroll = new ScrollPane(root);
        scroll.setFitToWidth(true);

        Scene scene = new Scene(scroll, 1200, 700);
        stage.setScene(scene);
        stage.setResizable(false);
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

        String t = time.trim();

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
