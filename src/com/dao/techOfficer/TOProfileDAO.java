package com.dao.techOfficer;

import com.database.DatabaseInitializer;
import com.model.TechnicalOfficer;

import java.sql.*;

public class TOProfileDAO {

    public TOProfileDAO() {
        ensureUserProfilePicColumn();
    }

    private void ensureUserProfilePicColumn() {
        try (Connection conn = DatabaseInitializer.getConnection()) {
            if (!columnExists(conn, "users", "profile_pic")) {
                try (Statement st = conn.createStatement()) {
                    st.execute("ALTER TABLE users ADD COLUMN profile_pic VARCHAR(500)");
                }
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

    public TechnicalOfficer getTOProfile(String empId) {
        String sql = """
                SELECT *
                FROM technical_officer
                WHERE emp_id = ?
                LIMIT 1
                """;

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, empId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    TechnicalOfficer officer = new TechnicalOfficer();

                    officer.setEmpId(rs.getString("emp_id"));
                    officer.setFirstName(rs.getString("first_name"));
                    officer.setLastName(rs.getString("last_name"));
                    officer.setNic(rs.getString("nic"));

                    Date dob = rs.getDate("dob");
                    if (dob != null) {
                        officer.setDob(dob.toLocalDate());
                    }

                    officer.setGender(rs.getString("gender"));
                    officer.setImagePath(rs.getString("image_path"));
                    officer.setDistrict(rs.getString("district"));
                    officer.setEmail(rs.getString("email"));
                    officer.setPhone(rs.getString("phone"));
                    officer.setAddress(rs.getString("address"));
                    officer.setDepartment(rs.getString("department"));
                    officer.setPosition(rs.getString("position"));
                    officer.setShiftType(rs.getString("shift_type"));
                    officer.setAssignedLab(rs.getString("assigned_lab"));

                    return officer;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public String getUserProfilePicture(String empId) {
        String sql = "SELECT profile_pic FROM users WHERE username = ? LIMIT 1";

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, empId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("profile_pic");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean updateUserProfilePicture(String empId, String profilePicPath) {
        String sql = "UPDATE users SET profile_pic = ? WHERE username = ?";

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, profilePicPath);
            ps.setString(2, empId);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean checkOldPassword(String empId, String oldPassword) {
        String sql = """
                SELECT 1
                FROM users
                WHERE username = ?
                  AND password = ?
                LIMIT 1
                """;

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, empId);
            ps.setString(2, oldPassword);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updatePassword(String empId, String newPassword) {
        String sql = "UPDATE users SET password = ? WHERE username = ?";

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, newPassword);
            ps.setString(2, empId);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}