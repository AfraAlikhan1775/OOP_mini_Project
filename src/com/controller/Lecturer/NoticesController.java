package com.controller.Lecturer;

import com.dao.admin.NoticeDAO;
import com.model.Notice;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.awt.Desktop;
import java.io.File;
import java.util.List;

public class NoticesController {

    @FXML private VBox noticeCardContainer;
    @FXML private TextField searchField;

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

        String finalKeyword = keyword.trim().toLowerCase();

        List<Notice> filtered = noticeDAO.getAllNotices()
                .stream()
                .filter(n ->
                        value(n.getTitle()).toLowerCase().contains(finalKeyword) ||
                                value(n.getDescription()).toLowerCase().contains(finalKeyword) ||
                                value(n.getPdfName()).toLowerCase().contains(finalKeyword) ||
                                value(n.getRoleTarget()).toLowerCase().contains(finalKeyword) ||
                                value(n.getBatchTarget()).toLowerCase().contains(finalKeyword) ||
                                value(n.getDepartmentTarget()).toLowerCase().contains(finalKeyword)
                )
                .toList();

        renderNotices(filtered);
    }

    private void loadNotices() {
        renderNotices(noticeDAO.getAllNotices());
    }

    private void renderNotices(List<Notice> notices) {
        noticeCardContainer.getChildren().clear();

        if (notices == null || notices.isEmpty()) {
            Label empty = new Label("No notices found.");
            empty.setStyle("-fx-font-size:16px; -fx-text-fill:#555;");
            noticeCardContainer.getChildren().add(empty);
            return;
        }

        for (Notice notice : notices) {
            noticeCardContainer.getChildren().add(createNoticeCard(notice));
        }
    }

    private HBox createNoticeCard(Notice notice) {
        HBox card = new HBox(20);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPrefHeight(140);
        card.setStyle(
                "-fx-background-color:white;" +
                        "-fx-background-radius:18;" +
                        "-fx-border-radius:18;" +
                        "-fx-border-color:#d9e2ec;" +
                        "-fx-padding:20;"
        );

        VBox detailsBox = new VBox(10);

        Label title = new Label(value(notice.getTitle()));
        title.setStyle("-fx-font-size:18px; -fx-font-weight:bold; -fx-text-fill:#0b1f36;");

        Label description = new Label(value(notice.getDescription()));
        description.setWrapText(true);
        description.setStyle("-fx-font-size:14px; -fx-text-fill:#333333;");

        Label viewers = new Label(
                "Role: " + value(notice.getRoleTarget()) +
                        " | Batch: " + value(notice.getBatchTarget()) +
                        " | Department: " + value(notice.getDepartmentTarget())
        );
        viewers.setStyle("-fx-font-size:13px; -fx-text-fill:#555555;");

        Label pdf = new Label("PDF: " + value(notice.getPdfName()));
        pdf.setStyle("-fx-font-size:13px; -fx-text-fill:#555555;");

        detailsBox.getChildren().addAll(title, description, viewers, pdf);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button viewBtn = new Button("View");
        viewBtn.setPrefWidth(110);
        viewBtn.setStyle(
                "-fx-background-color:#0b1f36;" +
                        "-fx-text-fill:white;" +
                        "-fx-background-radius:6;"
        );
        viewBtn.setOnAction(e -> handleOpenPdf(notice));

        VBox buttonBox = new VBox(10, viewBtn);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        card.getChildren().addAll(detailsBox, spacer, buttonBox);
        return card;
    }

    private void handleOpenPdf(Notice notice) {
        try {
            File file = noticeDAO.exportPdfToTempFile(notice.getId(), notice.getPdfName());

            if (file != null && file.exists()) {
                Desktop.getDesktop().open(file);
            } else {
                showAlert(Alert.AlertType.WARNING, "No PDF", "PDF file not found.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Cannot open PDF.");
        }
    }

    private String value(String text) {
        return text == null ? "" : text;
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}