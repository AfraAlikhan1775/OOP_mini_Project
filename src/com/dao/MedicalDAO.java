package com.dao;

import com.database.DatabaseInitializer;
import com.model.Medical;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MedicalDAO {

    public MedicalDAO() {
        createTable();
    }

    public void createTable() {
        String sql = """
                CREATE TABLE IF NOT EXISTS medical (
                    medical_id         INT PRIMARY KEY AUTO_INCREMENT,
                    student_id         VARCHAR(100) NOT NULL,
                    medical_data       LONGBLOB,
                    medical_start_date DATE NOT NULL,
                    medical_end_date   DATE NOT NULL,
                    batch              VARCHAR(10),
                    department         VARCHAR(50),
                    reason             VARCHAR(255),
                    added_by           VARCHAR(50),
                    status             ENUM('Pending', 'Verified', 'Rejected') DEFAULT 'Pending',
                    created_at         TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
                """;

        try (Connection conn = DatabaseInitializer.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean addMedical(Medical m) {
        String sql = """
                INSERT INTO medical (student_id, medical_data, medical_start_date, medical_end_date,
                                     batch, department, reason, added_by, status)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, m.getStudentId());
            pst.setBytes(2, m.getMedicalData());
            pst.setString(3, m.getMedicalStartDate());
            pst.setString(4, m.getMedicalEndDate());
            pst.setString(5, m.getBatch());
            pst.setString(6, m.getDepartment());
            pst.setString(7, m.getReason());
            pst.setString(8, m.getAddedBy());
            pst.setString(9, m.getStatus() != null ? m.getStatus() : "Pending");

            pst.executeUpdate();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateMedical(Medical m) {
        String sql = """
                UPDATE medical
                SET student_id = ?, medical_data = ?, medical_start_date = ?, medical_end_date = ?,
                    batch = ?, department = ?, reason = ?, added_by = ?, status = ?
                WHERE medical_id = ?
                """;

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, m.getStudentId());
            pst.setBytes(2, m.getMedicalData());
            pst.setString(3, m.getMedicalStartDate());
            pst.setString(4, m.getMedicalEndDate());
            pst.setString(5, m.getBatch());
            pst.setString(6, m.getDepartment());
            pst.setString(7, m.getReason());
            pst.setString(8, m.getAddedBy());
            pst.setString(9, m.getStatus());
            pst.setInt(10, m.getMedicalId());

            return pst.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteMedical(int medicalId) {
        String sql = "DELETE FROM medical WHERE medical_id = ?";

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, medicalId);
            return pst.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Medical> getAllMedicals() {
        List<Medical> list = new ArrayList<>();
        String sql = "SELECT * FROM medical ORDER BY created_at DESC";

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                list.add(extractMedical(rs));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<Medical> getMedicalByStudent(String studentId) {
        List<Medical> list = new ArrayList<>();
        String sql = "SELECT * FROM medical WHERE student_id = ? ORDER BY created_at DESC";

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, studentId);

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    list.add(extractMedical(rs));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<Medical> searchMedicals(String keyword) {
        List<Medical> list = new ArrayList<>();
        String sql = """
                SELECT * FROM medical
                WHERE student_id LIKE ?
                   OR department LIKE ?
                   OR batch LIKE ?
                   OR reason LIKE ?
                   OR status LIKE ?
                ORDER BY created_at DESC
                """;

        String pattern = "%" + keyword + "%";

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, pattern);
            pst.setString(2, pattern);
            pst.setString(3, pattern);
            pst.setString(4, pattern);
            pst.setString(5, pattern);

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    list.add(extractMedical(rs));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<Medical> filterByDepartment(String department) {
        List<Medical> list = new ArrayList<>();
        String sql = "SELECT * FROM medical WHERE department = ? ORDER BY created_at DESC";

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, department);

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    list.add(extractMedical(rs));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    private Medical extractMedical(ResultSet rs) throws SQLException {
        Medical m = new Medical();
        m.setMedicalId(rs.getInt("medical_id"));
        m.setStudentId(rs.getString("student_id"));
        m.setMedicalData(rs.getBytes("medical_data"));
        m.setMedicalStartDate(rs.getString("medical_start_date"));
        m.setMedicalEndDate(rs.getString("medical_end_date"));
        m.setBatch(rs.getString("batch"));
        m.setDepartment(rs.getString("department"));
        m.setReason(rs.getString("reason"));
        m.setAddedBy(rs.getString("added_by"));
        m.setStatus(rs.getString("status"));
        return m;
    }
}
