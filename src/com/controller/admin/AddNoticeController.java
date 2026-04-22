package com.controller.admin;

import com.dao.admin.NoticeDAO;
import com.model.Notice;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.nio.file.Files;

public class AddNoticeController {

    @FXML private TextField titleField;
    @FXML private TextArea descriptionArea;
    @FXML private ComboBox<String> roleComboBox;
    @FXML private ComboBox<String> batchComboBox;
    @FXML private ComboBox<String> departmentComboBox;
    @FXML private Label selectedPdfLabel;

    private File selectedPdfFile;

    private final NoticeDAO noticeDAO = new NoticeDAO();

    @FXML
    public void initialize() {
        roleComboBox.getItems().addAll("All", "Student", "Lecturer", "Technical Officer", "Admin");
        batchComboBox.getItems().addAll("All", "1", "2", "3", "4");
        departmentComboBox.getItems().addAll("All", "ICT", "BST", "ET");

        roleComboBox.setValue("All");
        batchComboBox.setValue("All");
        departmentComboBox.setValue("All");
        selectedPdfLabel.setText("No PDF selected");
    }

    @FXML
    private void handleChoosePdf() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
        );

        File file = fileChooser.showOpenDialog(titleField.getScene().getWindow());

        if (file != null) {
            selectedPdfFile = file;
            selectedPdfLabel.setText(file.getName());
        }
    }

    @FXML
    private void handleSaveNotice() {
        String title = titleField.getText().trim();
        String description = descriptionArea.getText().trim();
        String role = roleComboBox.getValue();
        String batch = batchComboBox.getValue();
        String department = departmentComboBox.getValue();

        if (title.isEmpty() || description.isEmpty() || selectedPdfFile == null ||
                role == null || batch == null || department == null) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please fill all fields and choose a PDF.");
            return;
        }

        try {
            byte[] pdfBytes = Files.readAllBytes(selectedPdfFile.toPath());

            Notice notice = new Notice(
                    title,
                    description,
                    selectedPdfFile.getName(),
                    pdfBytes,
                    role,
                    batch,
                    department
            );

            boolean saved = noticeDAO.saveNotice(notice);

            if (saved) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Notice saved successfully.");
                Stage stage = (Stage) titleField.getScene().getWindow();
                stage.close();
            } else {
                showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to save notice.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "File Error", "Could not read PDF file.");
        }
    }

    @FXML
    private void handleClear() {
        titleField.clear();
        descriptionArea.clear();
        roleComboBox.setValue("All");
        batchComboBox.setValue("All");
        departmentComboBox.setValue("All");
        selectedPdfFile = null;
        selectedPdfLabel.setText("No PDF selected");
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}