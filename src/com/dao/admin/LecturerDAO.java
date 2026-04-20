package com.dao.lecturer;

import com.database.DatabaseInitializer;

import java.sql.*;

public class LecturerDAO {

    public LecturerDAO() {
        createTable();
    }

    public void createTable() {
        String sql = """
                CREATE TABLE IF NOT EXISTS lecturer (
                    id INT PRIMARY KEY AUTO_INCREMENT,
                    user_id INT UNIQUE NOT NULL,
                    first_name VARCHAR(100),
                    last_name VARCHAR(100),
                    emp_no VARCHAR(50) UNIQUE,
                    nic VARCHAR(50),
                    dob DATE,
                    gender VARCHAR(10),
                    profile_pic VARCHAR(255),
                    email VARCHAR(100),
                    phone VARCHAR(20),
                    address TEXT,
                    department VARCHAR(50),
                    specialization VARCHAR(100),
                    FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE CASCADE
                )
                """;
        try (Connection conn = DatabaseInitializer.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Get lecturer profile by user_id (used after login)
    public LecturerProfile getProfileByUserId(int userId) {
        String sql = "SELECT * FROM lecturer WHERE user_id = ?";
        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, userId);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return mapResultSet(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Update profile — username and password NOT included (per requirements)
    public boolean updateProfile(int userId, String firstName, String lastName,
                                 String nic, String dob, String gender,
                                 String profilePic, String email, String phone,
                                 String address, String specialization) {
        String sql = """
                UPDATE lecturer
                SET first_name=?, last_name=?, nic=?, dob=?, gender=?,
                    profile_pic=?, email=?, phone=?, address=?, specialization=?
                WHERE user_id=?
                """;
        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, firstName);
            pst.setString(2, lastName);
            pst.setString(3, nic);
            pst.setDate(4, dob != null ? java.sql.Date.valueOf(dob) : null);
            pst.setString(5, gender);
            pst.setString(6, profilePic);
            pst.setString(7, email);
            pst.setString(8, phone);
            pst.setString(9, address);
            pst.setString(10, specialization);
            pst.setInt(11, userId);
            pst.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Get lecturer's internal id by user_id (needed for linking courses)
    public int getLecturerIdByUserId(int userId) {
        String sql = "SELECT id FROM lecturer WHERE user_id = ?";
        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, userId);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) return rs.getInt("id");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    private LecturerProfile mapResultSet(ResultSet rs) throws SQLException {
        LecturerProfile p = new LecturerProfile();
        p.id = rs.getInt("id");
        p.userId = rs.getInt("user_id");
        p.firstName = rs.getString("first_name");
        p.lastName = rs.getString("last_name");
        p.empNo = rs.getString("emp_no");
        p.nic = rs.getString("nic");
        p.dob = rs.getDate("dob") != null ? rs.getDate("dob").toLocalDate().toString() : null;
        p.gender = rs.getString("gender");
        p.profilePic = rs.getString("profile_pic");
        p.email = rs.getString("email");
        p.phone = rs.getString("phone");
        p.address = rs.getString("address");
        p.department = rs.getString("department");
        p.specialization = rs.getString("specialization");
        return p;
    }


    public static class LecturerProfile {
        public int id, userId;
        public String firstName, lastName, empNo, nic, dob, gender;
        public String profilePic, email, phone, address, department, specialization;

        public String getFullName() {
            return firstName + " " + lastName;
        }
    }
}