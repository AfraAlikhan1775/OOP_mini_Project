package com.dao;

import com.model.User;
import com.database.DatabaseInitializer;
import java.sql.*;

public class UserDAO {
    public User validateLogin(String userName, String password) {
        String query = "SELECT * FROM Users WHERE username = ? AND password_hash = ?";

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, userName);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id")); // Don't forget the ID!
                user.setUserName(rs.getString("username"));
                user.setRole(rs.getString("role")); // Important to set the role
                return user; // Return the filled "Lunch Box"
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Return nothing if no user was found
    }
}