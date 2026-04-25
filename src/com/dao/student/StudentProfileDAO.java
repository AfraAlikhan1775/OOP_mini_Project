package com.dao.student;

import com.database.DatabaseInitializer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class StudentProfileDAO {

    public boolean checkOldPassword(String username, String oldPassword) {
        String sql = "SELECT 1 FROM users WHERE username = ? AND password = ? LIMIT 1";

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, oldPassword);

            ResultSet rs = ps.executeQuery();
            return rs.next();

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updatePassword(String username, String newPassword) {
        String sql = "UPDATE users SET password = ? WHERE username = ?";

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, newPassword);
            ps.setString(2, username);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateProfilePicture(String username, String profilePicPath) {
        String sql = "UPDATE users SET profile_pic = ? WHERE username = ?";

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, profilePicPath);
            ps.setString(2, username);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getProfilePicture(String username) {
        String sql = "SELECT profile_pic FROM users WHERE username = ? LIMIT 1";

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getString("profile_pic");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}