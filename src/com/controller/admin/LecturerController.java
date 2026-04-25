package com.controller.admin;

import com.model.Lecturer;
import com.dao.admin.LecturerDAO;
import com.dao.admin.UserDAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import java.util.Optional;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;

public class LecturerController {

    @FXML private VBox lecturerCardContainer;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> categoryComboBox;

    private final LecturerDAO lecturerDAO = new LecturerDAO();

    @FXML
    public void initialize() {
        categoryComboBox.getItems().addAll("All", "ICT", "BST", "ET");
        categoryComboBox.setValue("All");
        loadLecturers();
    }

    @FXML
    private void navigateAddLecturer() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/view/admin/add_lecturer.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.setTitle("Add Lecturer");
            stage.showAndWait();

            loadLecturers();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Cannot open Add Lecturer page.");
        }
    }

    @FXML
    private void navigateAddStudent() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/view/admin/add_student.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.setTitle("Add Student");
            stage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Cannot open Add Student page.");
        }
    }

    @FXML
    private void navigateAddTechnicalOfficer() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/view/admin/add_technicalofficer.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.setTitle("Add Technical Officer");
            stage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Cannot open Add Technical Officer page.");
        }
    }

    @FXML
    private void handleSearch() {
        String keyword = searchField.getText();

        if (keyword == null || keyword.isBlank()) {
            loadLecturers();
            return;
        }

        renderLecturers(lecturerDAO.searchLecturers(keyword.trim()));
    }
    private void openViewLecturer(Lecturer lecturer) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/view/admin/view_lecturer.fxml"));
            Parent root = loader.load();

            ViewLecturerController controller = loader.getController();
            controller.setLecturer(lecturer);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.setTitle("Lecturer Details");
            stage.showAndWait();

            loadLecturers();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Cannot open lecturer details page.");
        }
    }

    @FXML
    private void handleFilter() {
        String selected = categoryComboBox.getValue();

        if (selected == null || selected.equals("All")) {
            loadLecturers();
            return;
        }

        renderLecturers(lecturerDAO.filterByDepartment(selected));
    }

    private void loadLecturers() {
        renderLecturers(lecturerDAO.getAllLecturers());
    }

    private void renderLecturers(List<Lecturer> lecturers) {
        lecturerCardContainer.getChildren().clear();

        if (lecturers == null || lecturers.isEmpty()) {
            Label emptyLabel = new Label("No lecturers found.");
            emptyLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #555;");
            lecturerCardContainer.getChildren().add(emptyLabel);
            return;
        }

        for (Lecturer lecturer : lecturers) {
            lecturerCardContainer.getChildren().add(createLecturerCard(lecturer));
        }
    }

    private HBox createLecturerCard(Lecturer lecturer) {
        HBox card = new HBox(20);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPrefHeight(140);
        card.setStyle("-fx-background-color:white; -fx-background-radius:14; -fx-border-radius:14; -fx-border-color:#d9e2ec; -fx-padding:18;");

        ImageView imageView = new ImageView();
        imageView.setFitWidth(90);
        imageView.setFitHeight(90);
        imageView.setPreserveRatio(true);

        if (lecturer.getRegPic() != null && !lecturer.getRegPic().isBlank()) {
            File file = new File(lecturer.getRegPic());
            if (file.exists()) {
                imageView.setImage(new Image(file.toURI().toString()));
            }
        }

        VBox detailsBox = new VBox(8);

        Label nameLabel = new Label(lecturer.getFirstName() + " " + lecturer.getLastName());
        nameLabel.setStyle("-fx-font-size:18px; -fx-font-weight:bold; -fx-text-fill:#0b1f36;");

        Label empIdLabel = new Label("Employee ID: " + valueOrEmpty(lecturer.getEmployeeId()));
        Label departmentLabel = new Label("Department: " + valueOrEmpty(lecturer.getDepartment()));
        Label statusLabel = new Label("Status: " + valueOrEmpty(lecturer.getStatus()));

        if ("Active".equalsIgnoreCase(lecturer.getStatus())) {
            statusLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
        } else {
            statusLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        }

        detailsBox.getChildren().addAll(nameLabel, empIdLabel, departmentLabel, statusLabel);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button viewBtn = new Button("View");
        viewBtn.setPrefWidth(100);
        viewBtn.setStyle("-fx-background-color:#0b1f36; -fx-text-fill:white;");
        viewBtn.setOnAction(e -> openViewLecturer(lecturer));

        Button removeBtn = new Button("Remove");
        removeBtn.setPrefWidth(100);
        removeBtn.setStyle("-fx-background-color:#b22222; -fx-text-fill:white;");
        removeBtn.setOnAction(e -> handleRemoveLecturer(lecturer));

        VBox buttonBox = new VBox(10, viewBtn, removeBtn);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        card.getChildren().addAll(imageView, detailsBox, spacer, buttonBox);

        return card;
    }


    private void handleRemoveLecturer(Lecturer lecturer) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Remove Lecturer");
        confirm.setHeaderText(null);
        confirm.setContentText("Are you sure you want to remove lecturer " + lecturer.getFirstName() + "?");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                boolean deleted = lecturerDAO.deleteLecturerByEmpId(lecturer.getEmployeeId());

                if (deleted) {
                    loadLecturers();
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Lecturer removed successfully.");
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to remove lecturer.");
                }
            }
        });
    }

    private String valueOrEmpty(String value) {
        return value == null ? "" : value;
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    private boolean askAdminPassword() {

        TextInputDialog dialog = new TextInputDialog();
        dialog.setHeaderText("Enter Admin Password");

        Optional<String> result = dialog.showAndWait();

        if (result.isEmpty()) return false;

        UserDAO userDAO = new UserDAO();

        if (!userDAO.isAdminPasswordCorrect(result.get())) {
            showAlert(Alert.AlertType.ERROR, "Wrong Password", "Incorrect password");
            return false;
        }

        return true;
    }


}