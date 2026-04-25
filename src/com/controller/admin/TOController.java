package com.controller.admin;

import com.dao.admin.TechnicalOfficerDAO;
import com.model.TechnicalOfficer;
import com.dao.admin.UserDAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;
import java.util.Optional;

public class TOController {

    @FXML private VBox officerCardContainer;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> categoryComboBox;

    private final TechnicalOfficerDAO technicalOfficerDAO = new TechnicalOfficerDAO();

    @FXML
    public void initialize() {
        categoryComboBox.getItems().addAll("All", "ICT", "BST", "ET");
        categoryComboBox.setValue("All");
        loadTechnicalOfficers();
    }

    @FXML
    private void navigateAddTO() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/view/admin/add_technicalofficer.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Add Technical Officer");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.showAndWait();

            loadTechnicalOfficers();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Page Error", "Cannot open Add Technical Officer page.");
        }
    }

    @FXML
    private void handleSearch() {
        String keyword = searchField.getText();

        if (keyword == null || keyword.isBlank()) {
            loadTechnicalOfficers();
            return;
        }

        renderCards(technicalOfficerDAO.searchTechnicalOfficers(keyword.trim()));
    }

    @FXML
    private void handleFilter() {
        String selected = categoryComboBox.getValue();

        if (selected == null || selected.equals("All")) {
            loadTechnicalOfficers();
            return;
        }

        renderCards(technicalOfficerDAO.filterByDepartment(selected));
    }

    private void loadTechnicalOfficers() {
        renderCards(technicalOfficerDAO.getAllTechnicalOfficers());
    }

    private void renderCards(List<TechnicalOfficer> officers) {
        officerCardContainer.getChildren().clear();

        if (officers == null || officers.isEmpty()) {
            Label empty = new Label("No technical officers found.");
            empty.setStyle("-fx-font-size:16px; -fx-text-fill:#555;");
            officerCardContainer.getChildren().add(empty);
            return;
        }

        for (TechnicalOfficer officer : officers) {
            officerCardContainer.getChildren().add(createCard(officer));
        }
    }

    private HBox createCard(TechnicalOfficer officer) {
        HBox card = new HBox(20);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPrefHeight(155);
        card.setStyle(
                "-fx-background-color:white;" +
                        "-fx-background-radius:18;" +
                        "-fx-border-radius:18;" +
                        "-fx-border-color:#d9e2ec;" +
                        "-fx-padding:20;"
        );

        ImageView imageView = new ImageView();
        imageView.setFitWidth(85);
        imageView.setFitHeight(95);
        imageView.setPreserveRatio(false);

        if (officer.getImagePath() != null && !officer.getImagePath().isBlank()) {
            File file = new File(officer.getImagePath());
            if (file.exists()) {
                imageView.setImage(new Image(file.toURI().toString()));
            }
        }

        VBox detailsBox = new VBox(8);
        detailsBox.setAlignment(Pos.CENTER_LEFT);

        Label name = new Label(value(officer.getFirstName()) + " " + value(officer.getLastName()));
        name.setStyle("-fx-font-size:18px; -fx-font-weight:bold; -fx-text-fill:#0b1f36;");

        Label empId = new Label("Employee ID: " + value(officer.getEmpId()));
        Label department = new Label("Department: " + value(officer.getDepartment()));
        Label position = new Label("Position: " + value(officer.getPosition()));
        Label lab = new Label("Assigned Lab: " + value(officer.getAssignedLab()));

        empId.setStyle("-fx-font-size:14px;");
        department.setStyle("-fx-font-size:14px;");
        position.setStyle("-fx-font-size:14px;");
        lab.setStyle("-fx-font-size:14px;");

        detailsBox.getChildren().addAll(name, empId, department, position, lab);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button viewBtn = new Button("View");
        viewBtn.setPrefWidth(110);
        viewBtn.setStyle("-fx-background-color:#0b1f36; -fx-text-fill:white; -fx-background-radius:6;");
        viewBtn.setOnAction(e -> handleView(officer));

        Button removeBtn = new Button("Remove");
        removeBtn.setPrefWidth(110);
        removeBtn.setStyle("-fx-background-color:#c62828; -fx-text-fill:white; -fx-background-radius:6;");
        removeBtn.setOnAction(e -> handleRemove(officer));

        VBox buttonBox = new VBox(12, viewBtn, removeBtn);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        if (imageView.getImage() != null) {
            card.getChildren().addAll(imageView, detailsBox, spacer, buttonBox);
        } else {
            Region emptyImage = new Region();
            emptyImage.setPrefWidth(85);
            card.getChildren().addAll(emptyImage, detailsBox, spacer, buttonBox);
        }

        return card;
    }

    private void handleView(TechnicalOfficer officer) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/view/admin/view_technicalofficer.fxml"));
            Parent root = loader.load();

            ViewTechnicalOfficerController controller = loader.getController();
            controller.setTechnicalOfficer(officer);

            Stage stage = new Stage();
            stage.setTitle("Technical Officer Details");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.showAndWait();

            loadTechnicalOfficers();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Cannot open technical officer details page.");
        }
    }
    private void handleRemove(TechnicalOfficer officer) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Remove Technical Officer");
        confirm.setHeaderText("Are you sure?");
        confirm.setContentText("Remove " + officer.getEmpId() + "?");

        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean deleted = technicalOfficerDAO.deleteByEmpId(officer.getEmpId());

            if (deleted) {
                loadTechnicalOfficers();
            } else {
                showAlert(Alert.AlertType.ERROR, "Delete Failed", "Could not remove technical officer.");
            }
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
    }}