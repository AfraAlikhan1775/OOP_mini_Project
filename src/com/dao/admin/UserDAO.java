package com.dao.admin;

import com.database.DatabaseInitializer;
import com.model.User;

import java.sql.*;

public class UserDAO {

    public UserDAO() {
        createTable();
        ensureRefIdColumn();
        insertDefaultAdmin();
    }

    public void createTable() {
        String sql = """
                CREATE TABLE IF NOT EXISTS users (
                    user_id INT PRIMARY KEY AUTO_INCREMENT,
                    username VARCHAR(50) UNIQUE NOT NULL,
                    password VARCHAR(255) NOT NULL,
                    role ENUM('Admin', 'Lecturer', 'Student', 'Technical Officer') NOT NULL,
                    profile_pic VARCHAR(255),
                    ref_id VARCHAR(100)
                )
                """;

        try (Connection conn = DatabaseInitializer.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void ensureRefIdColumn() {
        try (Connection conn = DatabaseInitializer.getConnection()) {
            if (!columnExists(conn, "users", "ref_id")) {
                try (Statement st = conn.createStatement()) {
                    st.execute("ALTER TABLE users ADD COLUMN ref_id VARCHAR(100)");
                }
            }

            try (Statement st = conn.createStatement()) {
                st.executeUpdate("UPDATE users SET ref_id = username WHERE ref_id IS NULL OR ref_id = ''");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean columnExists(Connection conn, String table, String column) throws SQLException {
        DatabaseMetaData metaData = conn.getMetaData();

        try (ResultSet rs = metaData.getColumns(null, null, table, column)) {
            if (rs.next()) return true;
        }

        try (ResultSet rs = metaData.getColumns(null, null, table.toUpperCase(), column.toUpperCase())) {
            return rs.next();
        }
    }

    public void insertDefaultAdmin() {
        String checkSql = "SELECT COUNT(*) FROM users WHERE username = ?";
        String insertSql = """
                INSERT INTO users (username, password, role, profile_pic, ref_id)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement checkPst = conn.prepareStatement(checkSql)) {

            checkPst.setString(1, "admin");
            ResultSet rs = checkPst.executeQuery();

            if (rs.next() && rs.getInt(1) == 0) {
                try (PreparedStatement insertPst = conn.prepareStatement(insertSql)) {
                    insertPst.setString(1, "admin");
                    insertPst.setString(2, "admin");
                    insertPst.setString(3, "Admin");
                    insertPst.setString(4, null);
                    insertPst.setString(5, "admin");
                    insertPst.executeUpdate();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean saveUser(User u) {
        String sql = """
                INSERT INTO users (username, password, role, profile_pic, ref_id)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, u.getUserName());

            if ("admin".equalsIgnoreCase(u.getUserName())) {
                pst.setString(2, "admin");
                pst.setString(3, "Admin");
            } else {
                pst.setString(2, "12345");
                pst.setString(3, u.getRole());
            }

            pst.setString(4, u.getProfPic());
            pst.setString(5, u.getRefId() == null || u.getRefId().isBlank()
                    ? u.getUserName()
                    : u.getRefId());

            pst.executeUpdate();
            return true;

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

            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setUserName(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setRole(rs.getString("role"));
                user.setProfPic(rs.getString("profile_pic"));
                user.setRefId(rs.getString("ref_id"));
                return user;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean isDefaultPassword(String username) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, username);
            pst.setString(2, "admin".equalsIgnoreCase(username) ? "admin" : "12345");

            ResultSet rs = pst.executeQuery();
            return rs.next();

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updatePassword(String username, String newPassword) {
        String sql = "UPDATE users SET password = ? WHERE username = ?";

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, newPassword);
            pst.setString(2, username);

            return pst.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateProfilePic(String username, String profilePicPath) {
        String sql = "UPDATE users SET profile_pic = ? WHERE username = ?";

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, profilePicPath);
            pst.setString(2, username);

            return pst.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isAdminPasswordCorrect(String password) {
        String sql = "SELECT COUNT(*) FROM users WHERE username='admin' AND password=?";

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, password);

            ResultSet rs = pst.executeQuery();
            return rs.next() && rs.getInt(1) > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean existsByUsername(String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, username);

            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}