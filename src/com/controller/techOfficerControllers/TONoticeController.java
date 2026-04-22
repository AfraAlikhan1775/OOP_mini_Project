package com.controller.techOfficerControllers;

import com.dao.NoticeDAO;
import com.model.Notice;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.util.List;

public class TONoticeController {

    @FXML private TextField searchField;
    @FXML private ComboBox<String> categoryFilter;
    @FXML private VBox noticeContainer;
    @FXML private Label statusLabel;

    private final NoticeDAO noticeDAO = new NoticeDAO();
    private static final String ROLE = "Technical Officer";

    @FXML
    public void initialize() {
        categoryFilter.getItems().addAll("General", "Academic", "Exam", "Event", "Urgent");
        loadNotices();

        categoryFilter.valueProperty().addListener((obs, old, val) -> {
            if (val != null) filterByCategory(val);
        });
    }

    private void loadNotices() {
        List<Notice> notices = noticeDAO.getNoticesForRole(ROLE);
        renderNotices(notices);
    }

    @FXML
    private void handleSearch() {
        String keyword = searchField.getText();
        if (keyword == null || keyword.trim().isEmpty()) {
            loadNotices();
            return;
        }
        List<Notice> notices = noticeDAO.searchNotices(keyword.trim(), ROLE);
        renderNotices(notices);
    }

    private void filterByCategory(String category) {
        List<Notice> notices = noticeDAO.filterByCategory(category, ROLE);
        renderNotices(notices);
    }

    @FXML
    private void handleRefresh() {
        searchField.clear();
        categoryFilter.setValue(null);
        loadNotices();
        statusLabel.setText("Notices refreshed.");
    }

    private void renderNotices(List<Notice> notices) {
        Platform.runLater(() -> {
            noticeContainer.getChildren().clear();

            if (notices.isEmpty()) {
                Label noData = new Label("No notices available.");
                noData.setStyle("-fx-text-fill: #94a3b8; -fx-font-style: italic;");
                noticeContainer.getChildren().add(noData);
                return;
            }

            for (Notice n : notices) {
                VBox card = new VBox(8);
                card.getStyleClass().add("notice-card");

                Label title = new Label("📌 " + n.getTitle());
                title.getStyleClass().add("notice-title");

                Label badge = new Label(n.getCategory());
                badge.getStyleClass().add("badge-" + n.getCategory().toLowerCase());

                Label meta = new Label("Posted by " + n.getPostedBy() + " on " + n.getPostedDate());
                meta.getStyleClass().add("notice-meta");

                Label content = new Label(n.getContent());
                content.getStyleClass().add("notice-content");
                content.setWrapText(true);

                card.getChildren().addAll(title, badge, meta, content);
                noticeContainer.getChildren().add(card);
            }
        });
    }
}
