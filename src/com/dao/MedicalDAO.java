package com.dao;

import com.database.DatabaseInitializer;
import com.model.Medical;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MedicalDAO {

    public MedicalDAO() {
        createTablesIfNotExists();
    }

    private void createTablesIfNotExists() {
        String sql = """
                CREATE TABLE IF NOT EXISTS medical (
                    medical_id INT AUTO_INCREMENT PRIMARY KEY,
                    reg_no VARCHAR(50),
                    student_id VARCHAR(100),
                    file_path VARCHAR(500),
                    medical_data LONGBLOB,
                    start_date DATE,
                    end_date DATE,
                    medical_start_date DATE,
                    medical_end_date DATE,
                    batch VARCHAR(10),
                    department VARCHAR(50),
                    reason TEXT,
                    added_by VARCHAR(50),
                    status VARCHAR(20) DEFAULT 'Pending',
                    approved_by VARCHAR(50),
                    approved_at TIMESTAMP NULL,
                    reject_reason TEXT,
                    submitted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
                """;

        try (Connection conn = DatabaseInitializer.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(sql);

            addColumnIfMissing(conn, "medical", "reg_no", "VARCHAR(50)");
            addColumnIfMissing(conn, "medical", "student_id", "VARCHAR(100)");
            addColumnIfMissing(conn, "medical", "file_path", "VARCHAR(500)");
            addColumnIfMissing(conn, "medical", "medical_data", "LONGBLOB");
            addColumnIfMissing(conn, "medical", "start_date", "DATE");
            addColumnIfMissing(conn, "medical", "end_date", "DATE");
            addColumnIfMissing(conn, "medical", "medical_start_date", "DATE");
            addColumnIfMissing(conn, "medical", "medical_end_date", "DATE");
            addColumnIfMissing(conn, "medical", "batch", "VARCHAR(10)");
            addColumnIfMissing(conn, "medical", "department", "VARCHAR(50)");
            addColumnIfMissing(conn, "medical", "reason", "TEXT");
            addColumnIfMissing(conn, "medical", "added_by", "VARCHAR(50)");
            addColumnIfMissing(conn, "medical", "status", "VARCHAR(20) DEFAULT 'Pending'");
            addColumnIfMissing(conn, "medical", "approved_by", "VARCHAR(50)");
            addColumnIfMissing(conn, "medical", "approved_at", "TIMESTAMP NULL");
            addColumnIfMissing(conn, "medical", "reject_reason", "TEXT");
            addColumnIfMissing(conn, "medical", "submitted_at", "TIMESTAMP DEFAULT CURRENT_TIMESTAMP");
            addColumnIfMissing(conn, "medical", "created_at", "TIMESTAMP DEFAULT CURRENT_TIMESTAMP");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addColumnIfMissing(Connection conn, String table, String column, String definition) {
        try {
            DatabaseMetaData metaData = conn.getMetaData();

            try (ResultSet rs = metaData.getColumns(null, null, table, column)) {
                if (!rs.next()) {
                    try (Statement stmt = conn.createStatement()) {
                        stmt.executeUpdate("ALTER TABLE " + table + " ADD COLUMN " + column + " " + definition);
                    }
                }
            }

        } catch (Exception e) {
            System.out.println("Column check failed: " + column);
            e.printStackTrace();
        }
    }

    public List<Medical> getAllMedicals() {
        List<Medical> list = new ArrayList<>();

        String sql = """
                SELECT
                    medical_id,
                    COALESCE(student_id, reg_no) AS student_id,
                    batch,
                    department,
                    COALESCE(medical_start_date, start_date) AS medical_start_date,
                    COALESCE(medical_end_date, end_date) AS medical_end_date,
                    medical_data,
                    reason,
                    added_by,
                    status
                FROM medical
                ORDER BY submitted_at DESC, medical_id DESC
                """;

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                list.add(mapMedical(rs));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<Medical> searchMedicals(String keyword) {
        List<Medical> list = new ArrayList<>();

        String sql = """
                SELECT
                    medical_id,
                    COALESCE(student_id, reg_no) AS student_id,
                    batch,
                    department,
                    COALESCE(medical_start_date, start_date) AS medical_start_date,
                    COALESCE(medical_end_date, end_date) AS medical_end_date,
                    medical_data,
                    reason,
                    added_by,
                    status
                FROM medical
                WHERE COALESCE(student_id, reg_no) LIKE ?
                   OR reg_no LIKE ?
                   OR department LIKE ?
                   OR status LIKE ?
                   OR reason LIKE ?
                ORDER BY submitted_at DESC, medical_id DESC
                """;

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            String search = "%" + keyword + "%";

            pst.setString(1, search);
            pst.setString(2, search);
            pst.setString(3, search);
            pst.setString(4, search);
            pst.setString(5, search);

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    list.add(mapMedical(rs));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public boolean addMedical(Medical medical) {
        String sql = """
                INSERT INTO medical
                (reg_no, student_id, batch, department,
                 start_date, end_date, medical_start_date, medical_end_date,
                 medical_data, reason, added_by, status, submitted_at, created_at)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
                """;

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, medical.getStudentId());
            pst.setString(2, medical.getStudentId());
            pst.setString(3, medical.getBatch());
            pst.setString(4, medical.getDepartment());

            pst.setDate(5, toSqlDate(medical.getMedicalStartDate()));
            pst.setDate(6, toSqlDate(medical.getMedicalEndDate()));
            pst.setDate(7, toSqlDate(medical.getMedicalStartDate()));
            pst.setDate(8, toSqlDate(medical.getMedicalEndDate()));

            pst.setBytes(9, medical.getMedicalData());
            pst.setString(10, medical.getReason());
            pst.setString(11, medical.getAddedBy());
            pst.setString(12, medical.getStatus());

            return pst.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateMedical(Medical medical) {
        String sql = """
                UPDATE medical
                SET reg_no = ?,
                    student_id = ?,
                    batch = ?,
                    department = ?,
                    start_date = ?,
                    end_date = ?,
                    medical_start_date = ?,
                    medical_end_date = ?,
                    medical_data = ?,
                    reason = ?,
                    added_by = ?,
                    status = ?
                WHERE medical_id = ?
                """;

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, medical.getStudentId());
            pst.setString(2, medical.getStudentId());
            pst.setString(3, medical.getBatch());
            pst.setString(4, medical.getDepartment());

            pst.setDate(5, toSqlDate(medical.getMedicalStartDate()));
            pst.setDate(6, toSqlDate(medical.getMedicalEndDate()));
            pst.setDate(7, toSqlDate(medical.getMedicalStartDate()));
            pst.setDate(8, toSqlDate(medical.getMedicalEndDate()));

            pst.setBytes(9, medical.getMedicalData());
            pst.setString(10, medical.getReason());
            pst.setString(11, medical.getAddedBy());
            pst.setString(12, medical.getStatus());
            pst.setInt(13, medical.getMedicalId());

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

    private Medical mapMedical(ResultSet rs) throws SQLException {
        return new Medical(
                rs.getInt("medical_id"),
                rs.getString("student_id"),
                rs.getString("batch"),
                rs.getString("department"),
                rs.getString("medical_start_date"),
                rs.getString("medical_end_date"),
                rs.getBytes("medical_data"),
                rs.getString("reason"),
                rs.getString("added_by"),
                rs.getString("status")
        );
    }

    private Date toSqlDate(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return Date.valueOf(value);
    }
}