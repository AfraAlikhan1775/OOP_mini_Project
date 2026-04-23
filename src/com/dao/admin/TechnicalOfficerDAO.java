package com.dao.admin;

import com.database.DatabaseInitializer;
import com.model.TechnicalOfficer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class TechnicalOfficerDAO {

    public TechnicalOfficerDAO() {
        createTable();
    }

    public void createTable() {
        String sql = """
                CREATE TABLE IF NOT EXISTS technical_officer (
                    id INT PRIMARY KEY AUTO_INCREMENT,
                    first_name VARCHAR(100),
                    last_name VARCHAR(100),
                    emp_id VARCHAR(100) UNIQUE,
                    nic VARCHAR(50),
                    dob DATE,
                    gender VARCHAR(10),
                    image_path VARCHAR(255),
                    district VARCHAR(50),
                    email VARCHAR(100),
                    phone VARCHAR(20),
                    address TEXT,
                    department VARCHAR(50),
                    position VARCHAR(100),
                    shift_type VARCHAR(50),
                    assigned_lab VARCHAR(100)
                )
                """;

        try (Connection conn = DatabaseInitializer.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean existsByEmpId(String empId) {
        String sql = "SELECT COUNT(*) FROM technical_officer WHERE emp_id = ?";

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, empId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean saveTechnicalOfficer(TechnicalOfficer t) {
        String sql = """
                INSERT INTO technical_officer (
                    first_name, last_name, emp_id, nic, dob, gender, image_path, district,
                    email, phone, address,
                    department, position, shift_type, assigned_lab
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, t.getFirstName());
            pst.setString(2, t.getLastName());
            pst.setString(3, t.getEmpId());
            pst.setString(4, t.getNic());

            if (t.getDob() != null) {
                pst.setDate(5, java.sql.Date.valueOf(t.getDob()));
            } else {
                pst.setDate(5, null);
            }

            pst.setString(6, t.getGender());
            pst.setString(7, t.getImagePath());
            pst.setString(8, t.getDistrict());
            pst.setString(9, t.getEmail());
            pst.setString(10, t.getPhone());
            pst.setString(11, t.getAddress());
            pst.setString(12, t.getDepartment());
            pst.setString(13, t.getPosition());
            pst.setString(14, t.getShiftType());
            pst.setString(15, t.getAssignedLab());

            pst.executeUpdate();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public TechnicalOfficer getTOByEmpId(String empId) {
        String sql = "SELECT * FROM technical_officer WHERE emp_id = ?";

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, empId);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    TechnicalOfficer t = new TechnicalOfficer();

                    t.setFirstName(rs.getString("first_name"));
                    t.setLastName(rs.getString("last_name"));
                    t.setEmpId(rs.getString("emp_id"));
                    t.setNic(rs.getString("nic"));

                    java.sql.Date dob = rs.getDate("dob");
                    if (dob != null) {
                        t.setDob(dob.toLocalDate());
                    }

                    t.setGender(rs.getString("gender"));
                    t.setImagePath(rs.getString("image_path"));
                    t.setDistrict(rs.getString("district"));
                    t.setEmail(rs.getString("email"));
                    t.setPhone(rs.getString("phone"));
                    t.setAddress(rs.getString("address"));
                    t.setDepartment(rs.getString("department"));
                    t.setPosition(rs.getString("position"));
                    t.setShiftType(rs.getString("shift_type"));
                    t.setAssignedLab(rs.getString("assigned_lab"));

                    return t;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}