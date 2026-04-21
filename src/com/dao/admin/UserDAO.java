package com.dao.admin;

import com.database.DatabaseInitializer;
import com.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class UserDAO {

    public UserDAO() {
        createTable();
        insertDefaultAdmin();
    }

    public void createTable() {
        String sql = """
                CREATE TABLE IF NOT EXISTS users (
                    user_id INT PRIMARY KEY AUTO_INCREMENT,
                    username VARCHAR(50) UNIQUE NOT NULL,
                    password VARCHAR(255) NOT NULL,
                    role ENUM('Admin', 'Lecturer', 'Student', 'Technical Officer') NOT NULL,
                    profile_pic VARCHAR(255)
                )
                """;

        try (Connection conn = DatabaseInitializer.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(sql);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void insertDefaultAdmin() {
        String checkSql = "SELECT COUNT(*) FROM users WHERE username = ?";
        String insertSql = """
                INSERT INTO users (username, password, role, profile_pic)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement checkPst = conn.prepareStatement(checkSql)) {

            checkPst.setString(1, "admin");

            try (ResultSet rs = checkPst.executeQuery()) {
                if (rs.next() && rs.getInt(1) == 0) {
                    try (PreparedStatement insertPst = conn.prepareStatement(insertSql)) {
                        insertPst.setString(1, "admin");
                        insertPst.setString(2, "admin");
                        insertPst.setString(3, "Admin");
                        insertPst.setString(4, null);
                        insertPst.executeUpdate();

                        System.out.println("Default admin created successfully.");
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean existsByUsername(String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, username);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean saveUser(User user) {
        String sql = """
                INSERT INTO users (username, password, role, profile_pic)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, user.getUserName());
            pst.setString(2, user.getPassword());
            pst.setString(3, user.getRole());
            pst.setString(4, user.getProfPic());

            int rows = pst.executeUpdate();
            return rows > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public User validateLogin(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, username);
            pst.setString(2, password);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setUserId(rs.getInt("user_id"));
                    user.setUserName(rs.getString("username"));
                    user.setPassword(rs.getString("password"));
                    user.setRole(rs.getString("role"));
                    user.setProfPic(rs.getString("profile_pic"));
                    return user;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean isDefaultPassword(String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ? AND password = ?";

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, username);
            pst.setString(2, "12345");

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean updatePassword(String username, String newPassword) {
        String sql = "UPDATE users SET password = ? WHERE username = ?";

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, newPassword);
            pst.setString(2, username);

            int rows = pst.executeUpdate();
            return rows > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public String getRoleByUsername(String username) {
        String sql = "SELECT role FROM users WHERE username = ?";

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, username);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("role");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}