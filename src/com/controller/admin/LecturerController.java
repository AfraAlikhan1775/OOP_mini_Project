package com.controller.admin;

import com.dao.admin.LecturerDAO;
import com.model.Lecturer;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
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
        if (lecturerCardContainer == null) {
            return;
        }

        lecturerCardContainer.getChildren().clear();

        for (Lecturer lecturer : lecturers) {
            HBox card = createLecturerCard(lecturer);
            lecturerCardContainer.getChildren().add(card);
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

        if (lecturer.getImagePath() != null && !lecturer.getImagePath().isBlank()) {
            File file = new File(lecturer.getImagePath());
            if (file.exists()) {
                imageView.setImage(new Image(file.toURI().toString()));
            }
        }

        VBox detailsBox = new VBox(8);

        Label title = new Label(lecturer.getEmpId() + " - " + lecturer.getFirstName() + " " + lecturer.getLastName());
        title.setStyle("-fx-font-size:18px; -fx-font-weight:bold; -fx-text-fill:#0b1f36;");

        Label department = new Label("Department: " + valueOrEmpty(lecturer.getDepartment()));
        Label specialization = new Label("Specialization: " + valueOrEmpty(lecturer.getSpecialization()));
        Label designation = new Label("Designation: " + valueOrEmpty(lecturer.getDesignation()));
        Label email = new Label("Email: " + valueOrEmpty(lecturer.getEmail()));

        detailsBox.getChildren().addAll(title, department, specialization, designation, email);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button viewBtn = new Button("View");
        viewBtn.setPrefWidth(100);
        viewBtn.setStyle("-fx-background-color:#0b1f36; -fx-text-fill:white;");

        Button removeBtn = new Button("Remove");
        removeBtn.setPrefWidth(100);
        removeBtn.setStyle("-fx-background-color:#b22222; -fx-text-fill:white;");

        VBox buttonBox = new VBox(10, viewBtn, removeBtn);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        card.getChildren().addAll(imageView, detailsBox, spacer, buttonBox);

        return card;
    }

    private String valueOrEmpty(String value) {
        return value == null ? "" : value;
    }
}