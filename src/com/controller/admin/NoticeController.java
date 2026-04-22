package com.controller.admin;

import com.dao.admin.NoticeDAO;
import com.model.Notice;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.awt.Desktop;
import java.io.File;
import java.util.List;
import java.util.Optional;

public class NoticeController {

    @FXML private VBox noticeCardContainer;
    @FXML private TextField searchField;

    private final NoticeDAO noticeDAO = new NoticeDAO();

    @FXML
    public void initialize() {
        loadNotices();
    }

    @FXML
    private void navigateAddNotice() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/view/admin/add_notice.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.showAndWait();

            loadNotices();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSearch() {

        String keyword = searchField.getText();

        if (keyword == null || keyword.isBlank()) {
            loadNotices();
            return;
        }

        keyword = keyword.trim().toLowerCase(); // normalize

        final String finalKeyword = keyword; // ✅ VERY IMPORTANT

        List<Notice> allNotices = noticeDAO.getAllNotices();

        List<Notice> filtered = allNotices.stream()
                .filter(n ->
                        n.getTitle().toLowerCase().contains(finalKeyword) ||
                                n.getDescription().toLowerCase().contains(finalKeyword) ||
                                n.getPdfName().toLowerCase().contains(finalKeyword) ||
                                n.getRoleTarget().toLowerCase().contains(finalKeyword) ||
                                n.getBatchTarget().toLowerCase().contains(finalKeyword) ||
                                n.getDepartmentTarget().toLowerCase().contains(finalKeyword)
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
        card.setStyle("-fx-background-color:white; -fx-background-radius:18; -fx-border-radius:18; -fx-border-color:#d9e2ec; -fx-padding:20;");
        card.setPrefHeight(140);

        VBox detailsBox = new VBox(10);

        Label title = new Label(notice.getTitle());
        title.setStyle("-fx-font-size:18px; -fx-font-weight:bold; -fx-text-fill:#0b1f36;");

        Label viewers = new Label(
                "Role: " + notice.getRoleTarget()
                        + " | Batch: " + notice.getBatchTarget()
                        + " | Department: " + notice.getDepartmentTarget()
        );

        Label pdf = new Label("PDF: " + notice.getPdfName());

        detailsBox.getChildren().addAll(title, viewers, pdf);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button viewBtn = new Button("Open PDF");
        viewBtn.setStyle("-fx-background-color:#0b1f36; -fx-text-fill:white;");
        viewBtn.setOnAction(e -> handleOpenPdf(notice));

        Button removeBtn = new Button("Remove");
        removeBtn.setStyle("-fx-background-color:#c62828; -fx-text-fill:white;");
        removeBtn.setOnAction(e -> handleRemove(notice));

        VBox buttonBox = new VBox(10, viewBtn, removeBtn);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        card.getChildren().addAll(detailsBox, spacer, buttonBox);
        return card;
    }

    private void handleOpenPdf(Notice notice) {
        try {
            File file = noticeDAO.exportPdfToTempFile(notice.getId(), notice.getPdfName());
            if (file != null) {
                Desktop.getDesktop().open(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleRemove(Notice notice) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Remove Notice");
        confirm.setHeaderText("Are you sure?");
        confirm.setContentText("Do you want to remove this notice?");

        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean deleted = noticeDAO.deleteNotice(notice.getId());
            if (deleted) {
                loadNotices();
            } else {
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setTitle("Delete Failed");
                error.setHeaderText(null);
                error.setContentText("Could not remove notice.");
                error.showAndWait();
            }
        }
    }
}