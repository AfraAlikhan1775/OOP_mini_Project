package com.dao.Lecturer;

import com.database.DatabaseInitializer;
import com.model.Lecturerr.LecturerProfileData;

import java.sql.*;

public class LecturerProfileDAO {

    public LecturerProfileDAO() {
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

    public LecturerProfileData getProfile(String empId) {
        LecturerProfileData data = new LecturerProfileData();

        String sql = "SELECT * FROM lecturer WHERE emp_id = ? LIMIT 1";

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, empId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    data.setEmpId(get(rs, "emp_id"));
                    data.setFirstName(get(rs, "first_name"));
                    data.setLastName(get(rs, "last_name"));
                    data.setNic(get(rs, "nic"));
                    data.setDob(get(rs, "dob"));
                    data.setGender(get(rs, "gender"));
                    data.setEmail(get(rs, "email"));
                    data.setPhone(firstAvailable(rs, "phone", "contact_number", "mobile"));
                    data.setAddress(get(rs, "address"));
                    data.setDepartment(get(rs, "department"));
                    data.setSpecialization(get(rs, "specialization"));
                    data.setPosition(firstAvailable(rs, "position", "designation"));
                    data.setAcademicPhoto(firstAvailable(rs, "reg_pic", "image_path", "photo"));
                }
            }

            data.setUserProfilePic(getUserProfilePicture(conn, empId));

        } catch (Exception e) {
            e.printStackTrace();
        }

        return data;
    }

    public boolean checkOldPassword(String empId, String oldPassword) {
        String sql = "SELECT 1 FROM users WHERE username = ? AND password = ? LIMIT 1";

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

    public boolean updateUserProfilePicture(String empId, String path) {
        String sql = "UPDATE users SET profile_pic = ? WHERE username = ?";

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, path);
            ps.setString(2, empId);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private String getUserProfilePicture(Connection conn, String empId) {
        String sql = "SELECT profile_pic FROM users WHERE username = ? LIMIT 1";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, empId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("profile_pic");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "-";
    }

    private String firstAvailable(ResultSet rs, String... columns) {
        for (String column : columns) {
            String value = get(rs, column);
            if (value != null && !value.isBlank() && !value.equals("-")) {
                return value;
            }
        }
        return "-";
    }

    private String get(ResultSet rs, String column) {
        try {
            ResultSetMetaData metaData = rs.getMetaData();

            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                if (metaData.getColumnLabel(i).equalsIgnoreCase(column)) {
                    String value = rs.getString(column);
                    return value == null || value.isBlank() ? "-" : value.trim();
                }
            }

        } catch (Exception ignored) {
        }

        return "-";
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
}