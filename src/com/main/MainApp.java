package com.main;

import com.dao.admin.StudentDAO;
import com.dao.admin.UserDAO;
import com.database.DatabaseInitializer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/view/Login.fxml"));
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.setTitle("FMS Login");
        stage.show();
    }

    public static void main(String[] args) {
        DatabaseInitializer.initialize();
        new UserDAO();
        new StudentDAO();
        launch(args);
    }
}