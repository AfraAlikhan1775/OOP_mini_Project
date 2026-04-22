package com.view.Lec_N;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.net.URL;

public class MainController {

    @FXML
    private StackPane contentArea;

    @FXML
    public void initialize() {
        loadUI("dashboard.fxml");
    }

    private void loadUI(String file) {
        try {
            URL fxmlLocation = getClass().getResource("/com/view/Lec_N/" + file);

            if (fxmlLocation == null) {
                throw new RuntimeException("Cannot find FXML file: /com/view/Lec_N/" + file);
            }

            Parent root = FXMLLoader.load(fxmlLocation);
            contentArea.getChildren().setAll(root);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDashboard() {
        loadUI("dashboard.fxml");
    }
}