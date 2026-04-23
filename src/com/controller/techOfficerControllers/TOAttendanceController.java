package com.controller.techOfficerControllers;

import com.dao.AttendanceDAO;
import com.model.admin.AttendanceGroup;
import com.model.admin.AttendanceRecord;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.List;

public class TOAttendanceController {

    @FXML private VBox cardContainer;

    private final AttendanceDAO attendanceDAO = new AttendanceDAO();
    private TODashboardController dashboardController;

    public void setDashboardController(TODashboardController dashboardController) {
        this.dashboardController = dashboardController;
    }

    @FXML
    public void initialize() {
        loadAttendanceCards();
    }

    @FXML
    private void handleOpenAddAttendance() {
        if (dashboardController != null) {
            dashboardController.loadContent("/com/view/techOfficer/add_attendance.fxml");
        } else {
            System.out.println("Dashboard controller not set.");
        }
    }

    @FXML
    private void handleOpenAddSession() {
        if (dashboardController != null) {
            dashboardController.loadContent("/com/view/techOfficer/add_session.fxml");
        } else {
            System.out.println("Dashboard controller not set.");
        }
    }

    private void loadAttendanceCards() {
        cardContainer.getChildren().clear();

        List<AttendanceGroup> groups = attendanceDAO.getAllAttendanceGroups();

        if (groups == null || groups.isEmpty()) {
            Label empty = new Label("No attendance records found.");
            empty.setStyle("-fx-font-size:14px; -fx-text-fill:#64748b;");
            cardContainer.getChildren().add(empty);
            return;
        }

        for (AttendanceGroup group : groups) {
            cardContainer.getChildren().add(createAttendanceCard(group));
        }
    }

    private VBox createAttendanceCard(AttendanceGroup group) {
        int presentCount = attendanceDAO.countByStatus(group.getId(), "PRESENT");
        int absentCount = attendanceDAO.countByStatus(group.getId(), "ABSENT");
        int medicalCount = attendanceDAO.countByStatus(group.getId(), "MEDICAL");

        VBox card = new VBox(10);
        card.setPadding(new Insets(18));
        card.setStyle(
                "-fx-background-color:white;" +
                        "-fx-background-radius:14;" +
                        "-fx-border-color:#dbe3ea;" +
                        "-fx-border-radius:14;"
        );

        Label title = new Label("📘 " + group.getCourseId() + " | " + group.getType());
        title.setStyle("-fx-font-size:18px; -fx-font-weight:bold; -fx-text-fill:#0f172a;");

        Label info1 = new Label("Year: " + group.getYear() + "   |   Session: " + group.getSessionId());
        info1.setStyle("-fx-font-size:13px; -fx-text-fill:#475569;");

        Label info2 = new Label("Date: " + group.getAttendanceDate());
        info2.setStyle("-fx-font-size:13px; -fx-text-fill:#475569;");

        Label summary = new Label("Present: " + presentCount + "   |   Absent: " + absentCount + "   |   Medical: " + medicalCount);
        summary.setStyle("-fx-font-size:13px; -fx-text-fill:#334155; -fx-font-weight:bold;");

        HBox bottom = new HBox();
        bottom.setAlignment(Pos.CENTER_RIGHT);

        Button viewBtn = new Button("View");
        viewBtn.setStyle("-fx-background-color:#0b1f36; -fx-text-fill:white; -fx-font-weight:bold;");
        viewBtn.setOnAction(e -> openDetails(group));

        bottom.getChildren().add(viewBtn);

        card.getChildren().addAll(title, info1, info2, summary, bottom);
        return card;
    }

    private void openDetails(AttendanceGroup group) {
        List<AttendanceRecord> records = attendanceDAO.getAttendanceByGroupId(group.getId());

        VBox content = new VBox(12);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color:#f8fafc;");

        Label header = new Label("Attendance Details - " + group.getCourseId() + " | " + group.getAttendanceDate());
        header.setStyle("-fx-font-size:18px; -fx-font-weight:bold; -fx-text-fill:#0b1f36;");
        content.getChildren().add(header);

        for (AttendanceRecord record : records) {
            HBox row = new HBox(15);
            row.setPadding(new Insets(12));
            row.setStyle("-fx-background-color:white; -fx-background-radius:10; -fx-border-color:#e2e8f0; -fx-border-radius:10;");

            Label reg = new Label(record.getRegNo());
            reg.setPrefWidth(260);
            reg.setStyle("-fx-font-size:13px; -fx-text-fill:#1e293b; -fx-font-weight:bold;");

            Label status = new Label(record.getStatus());
            status.setStyle("-fx-font-size:13px; -fx-text-fill:#475569;");

            row.getChildren().addAll(reg, status);
            content.getChildren().add(row);
        }

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);

        Stage stage = new Stage();
        stage.setTitle("Attendance Details");
        stage.setScene(new Scene(scrollPane, 700, 600));
        stage.show();
    }
}