package com.controller.Student;

import com.dao.admin.NoticeDAO;
import com.dao.student.StudentAcademicDAO;
import com.model.Notice;
import com.session.StudentSession;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.awt.Desktop;
import java.io.File;
import java.util.List;

public class stuNoticeController {

    @FXML private VBox noticeCardBox;
    @FXML private TextField searchField;

    private final StudentAcademicDAO academicDAO = new StudentAcademicDAO();
    private final NoticeDAO noticeDAO = new NoticeDAO();

    @FXML
    public void initialize() {
        loadNotices();
    }

    @FXML
    private void handleSearch() {
        String keyword = searchField.getText();

        if (keyword == null || keyword.isBlank()) {
            loadNotices();
            return;
        }

        keyword = keyword.trim().toLowerCase();

        String finalKeyword = keyword;
        List<Notice> filtered = academicDAO.getStudentNotices(StudentSession.getUsername())
                .stream()
                .filter(n ->
                        safe(n.getTitle()).toLowerCase().contains(finalKeyword) ||
                                safe(n.getDescription()).toLowerCase().contains(finalKeyword) ||
                                safe(n.getPdfName()).toLowerCase().contains(finalKeyword)
                )
                .toList();

        renderNotices(filtered);
    }

    private void loadNotices() {
        renderNotices(academicDAO.getStudentNotices(StudentSession.getUsername()));
    }

    private void renderNotices(List<Notice> notices) {
        noticeCardBox.getChildren().clear();

        if (notices == null || notices.isEmpty()) {
            noticeCardBox.getChildren().add(new Label("No notices available."));
            return;
        }

        for (Notice n : notices) {
            noticeCardBox.getChildren().add(createNoticeCard(n));
        }
    }

    private HBox createNoticeCard(Notice notice) {
        HBox card = new HBox(18);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setStyle("""
                -fx-background-color:white;
                -fx-padding:18;
                -fx-background-radius:14;
                -fx-border-color:#d9e2ec;
                -fx-border-radius:14;
                """);

        VBox details = new VBox(8);

        Label title = new Label(safe(notice.getTitle()));
        title.setStyle("-fx-font-size:18px; -fx-font-weight:bold; -fx-text-fill:#0b1f36;");

        Label desc = new Label(safe(notice.getDescription()));
        desc.setWrapText(true);

        Label target = new Label("Batch: " + safe(notice.getBatchTarget())
                + " | Department: " + safe(notice.getDepartmentTarget()));

        Label pdf = new Label("PDF: " + safe(notice.getPdfName()));

        details.getChildren().addAll(title, desc, target, pdf);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button viewBtn = new Button("View");
        viewBtn.setStyle("-fx-background-color:#0b1f36; -fx-text-fill:white;");
        viewBtn.setOnAction(e -> handleViewNotice(notice));

        card.getChildren().addAll(details, spacer, viewBtn);
        return card;
    }

    private void handleViewNotice(Notice notice) {
        try {
            if (notice.getPdfName() == null || notice.getPdfName().isBlank()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("Notice Description");
                alert.setContentText(notice.getDescription());
                alert.showAndWait();
                return;
            }

            File file = noticeDAO.exportPdfToTempFile(notice.getId(), notice.getPdfName());

            if (file != null) {
                Desktop.getDesktop().open(file);
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText(notice.getTitle());
                alert.setContentText(notice.getDescription());
                alert.showAndWait();
            }

        } catch (Exception e) {
            e.printStackTrace();

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Cannot open notice");
            alert.setContentText("PDF cannot be opened.");
            alert.showAndWait();
        }
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }
}