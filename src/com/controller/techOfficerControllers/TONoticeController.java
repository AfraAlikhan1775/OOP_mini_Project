package com.controller.techOfficerControllers;

import com.dao.admin.NoticeDAO;
import com.dao.admin.TechnicalOfficerDAO;
import com.model.Notice;
import com.model.TechnicalOfficer;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.awt.Desktop;
import java.io.File;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TONoticeController {

    @FXML private TextField searchField;
    @FXML private ComboBox<String> categoryFilter;
    @FXML private VBox noticeContainer;
    @FXML private Label statusLabel;

    private final NoticeDAO noticeDAO = new NoticeDAO();
    private final TechnicalOfficerDAO technicalOfficerDAO = new TechnicalOfficerDAO();

    private String empId;
    private String department = "All";

    @FXML
    public void initialize() {
        categoryFilter.getItems().setAll("All", "Today", "This Week", "With PDF");
        categoryFilter.setValue("All");

        categoryFilter.valueProperty().addListener((obs, oldValue, newValue) -> loadNotices());
        loadNotices();
    }

    public void setEmpId(String empId) {
        this.empId = empId;

        TechnicalOfficer officer = technicalOfficerDAO.getTOByEmpId(empId);

        if (officer != null && officer.getDepartment() != null && !officer.getDepartment().isBlank()) {
            department = officer.getDepartment();
        } else {
            department = "All";
        }

        loadNotices();
    }

    @FXML
    private void handleSearch() {
        loadNotices();
    }

    @FXML
    private void handleRefresh() {
        searchField.clear();
        categoryFilter.setValue("All");
        loadNotices();
    }

    private void loadNotices() {
        if (noticeContainer == null) return;

        List<Notice> allNotices = noticeDAO.getVisibleNotices(
                "Technical Officer",
                "All",
                department
        );

        String keyword = searchField == null || searchField.getText() == null
                ? ""
                : searchField.getText().trim().toLowerCase();

        String filter = categoryFilter == null || categoryFilter.getValue() == null
                ? "All"
                : categoryFilter.getValue();

        List<Notice> filtered = new ArrayList<>();

        for (Notice notice : allNotices) {
            boolean keywordOk =
                    keyword.isEmpty()
                            || contains(notice.getTitle(), keyword)
                            || contains(notice.getDescription(), keyword)
                            || contains(notice.getPdfName(), keyword);

            boolean filterOk = switch (filter) {
                case "Today" -> isToday(notice.getCreatedAt());
                case "This Week" -> isThisWeek(notice.getCreatedAt());
                case "With PDF" -> notice.getPdfName() != null && !notice.getPdfName().isBlank();
                default -> true;
            };

            if (keywordOk && filterOk) {
                filtered.add(notice);
            }
        }

        renderNotices(filtered);

        if (statusLabel != null) {
            statusLabel.setText(filtered.size() + " notice(s) loaded");
        }
    }

    private void renderNotices(List<Notice> notices) {
        noticeContainer.getChildren().clear();

        if (notices == null || notices.isEmpty()) {
            Label empty = new Label("No notices available for your department.");
            empty.setStyle("-fx-text-fill:#64748b; -fx-font-size:14px;");
            noticeContainer.getChildren().add(empty);
            return;
        }

        for (Notice notice : notices) {
            noticeContainer.getChildren().add(createNoticeCard(notice));
        }
    }

    private VBox createNoticeCard(Notice notice) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(18));
        card.setStyle(
                "-fx-background-color:white;" +
                        "-fx-background-radius:14;" +
                        "-fx-border-radius:14;" +
                        "-fx-border-color:#dbe3ea;" +
                        "-fx-effect:dropshadow(gaussian, rgba(0,0,0,0.08), 10, 0, 0, 2);"
        );

        Label titleLabel = new Label("📢 " + safe(notice.getTitle()));
        titleLabel.setWrapText(true);
        titleLabel.setStyle("-fx-font-size:18px; -fx-font-weight:bold; -fx-text-fill:#0f172a;");

        Label descLabel = new Label(safe(notice.getDescription()));
        descLabel.setWrapText(true);
        descLabel.setStyle("-fx-font-size:13px; -fx-text-fill:#334155;");

        Label targetLabel = new Label(
                "Department: " + safe(notice.getDepartmentTarget()) +
                        "   |   Role: " + safe(notice.getRoleTarget()) +
                        "   |   Batch: " + safe(notice.getBatchTarget())
        );
        targetLabel.setStyle("-fx-font-size:12px; -fx-text-fill:#475569;");

        Label dateLabel = new Label("Created: " + formatTimestamp(notice.getCreatedAt()));
        dateLabel.setStyle("-fx-font-size:12px; -fx-text-fill:#64748b;");

        HBox footer = new HBox(10);
        footer.setStyle("-fx-padding:8 0 0 0;");

        Label pdfLabel = new Label(hasPdf(notice) ? "Attachment: " + notice.getPdfName() : "No attachment");
        pdfLabel.setStyle("-fx-font-size:12px; -fx-text-fill:#475569;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button viewBtn = new Button("View PDF");
        viewBtn.setDisable(!hasPdf(notice));
        viewBtn.setStyle(
                "-fx-background-color:#2563eb;" +
                        "-fx-text-fill:white;" +
                        "-fx-font-weight:bold;" +
                        "-fx-background-radius:8;"
        );
        viewBtn.setOnAction(e -> openPdf(notice));

        footer.getChildren().addAll(pdfLabel, spacer, viewBtn);

        card.getChildren().addAll(titleLabel, descLabel, targetLabel, dateLabel, footer);
        return card;
    }

    private void openPdf(Notice notice) {
        try {
            if (!hasPdf(notice)) {
                statusLabel.setText("This notice has no PDF.");
                return;
            }

            File file = noticeDAO.exportPdfToTempFile(notice.getId(), notice.getPdfName());

            if (file != null && Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(file);
                statusLabel.setText("Opened: " + notice.getPdfName());
            } else {
                statusLabel.setText("Cannot open PDF.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Failed to open PDF.");
        }
    }

    private boolean hasPdf(Notice notice) {
        return notice.getPdfName() != null && !notice.getPdfName().isBlank();
    }

    private boolean contains(String text, String keyword) {
        return text != null && text.toLowerCase().contains(keyword);
    }

    private boolean isToday(Timestamp timestamp) {
        if (timestamp == null) return false;
        return timestamp.toLocalDateTime().toLocalDate().equals(LocalDate.now());
    }

    private boolean isThisWeek(Timestamp timestamp) {
        if (timestamp == null) return false;

        LocalDate date = timestamp.toLocalDateTime().toLocalDate();
        LocalDate now = LocalDate.now();

        LocalDate start = now.minusDays(now.getDayOfWeek().getValue() - 1);
        LocalDate end = start.plusDays(6);

        return !date.isBefore(start) && !date.isAfter(end);
    }

    private String formatTimestamp(Timestamp timestamp) {
        if (timestamp == null) return "N/A";
        return new SimpleDateFormat("dd MMM yyyy hh:mm a").format(timestamp);
    }

    private String safe(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }
}