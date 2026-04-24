package com.controller.Student;

import com.database.DatabaseInitializer;
import com.session.StudentSession;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class stuProfileController {

    @FXML private Label fullNameLabel;
    @FXML private Label emailLabel;
    @FXML private Label courseLabel;
    @FXML private Label contactLabel;
    @FXML private Label regNoLabel;
    @FXML private Label departmentLabel;
    @FXML private Label yearLabel;

    @FXML
    public void initialize() {
        loadProfile();
    }

    private void loadProfile() {
        String username = StudentSession.getUsername();

        if (username == null || username.isBlank()) {
            fullNameLabel.setText("No logged student");
            return;
        }

        String sql = """
                SELECT *
                FROM student
                WHERE reg_no = ?
                """;

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, username);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    String firstName = value(rs.getString("first_name"));
                    String lastName = value(rs.getString("last_name"));

                    fullNameLabel.setText((firstName + " " + lastName).trim());
                    emailLabel.setText(value(rs.getString("email")));
                    courseLabel.setText(value(rs.getString("degrea")));
                    contactLabel.setText(value(rs.getString("phone")));
                    regNoLabel.setText(value(rs.getString("reg_no")));
                    departmentLabel.setText(value(rs.getString("department")));
                    yearLabel.setText(value(rs.getString("year_no")));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            fullNameLabel.setText("Error loading profile");
        }
    }

    private String value(String s) {
        return (s == null || s.isBlank()) ? "-" : s;
    }
}