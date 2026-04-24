package com.dao.Lecturer;

import com.database.DatabaseInitializer;
import com.model.student.MedicalRequest;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MedicalApprovalDAO {

    public MedicalApprovalDAO() {
        createMedicalTablesIfNotExists();
    }

    private void createMedicalTablesIfNotExists() {
        String medicalTable = """
                CREATE TABLE IF NOT EXISTS medical (
                    medical_id INT AUTO_INCREMENT PRIMARY KEY,
                    reg_no VARCHAR(50) NOT NULL,
                    file_path VARCHAR(500) NOT NULL,
                    start_date DATE NOT NULL,
                    end_date DATE NOT NULL,
                    reason TEXT,
                    status VARCHAR(20) DEFAULT 'Pending',
                    approved_by VARCHAR(50),
                    approved_at TIMESTAMP NULL,
                    reject_reason TEXT,
                    submitted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
                """;

        String selectedSessionTable = """
                CREATE TABLE IF NOT EXISTS medical_selected_session (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    medical_id INT NOT NULL,
                    attendance_group_id INT NOT NULL,
                    course_id VARCHAR(100) NOT NULL,
                    session_id VARCHAR(100) NOT NULL,
                    session_name VARCHAR(150),
                    type VARCHAR(30),
                    attendance_date DATE NOT NULL,
                    status VARCHAR(20) DEFAULT 'Pending',
                    FOREIGN KEY (medical_id) REFERENCES medical(medical_id) ON DELETE CASCADE
                )
                """;

        try (Connection conn = DatabaseInitializer.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(medicalTable);
            stmt.execute(selectedSessionTable);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<MedicalRequest> getPendingMedicals() {
        List<MedicalRequest> list = new ArrayList<>();

        String sql = """
                SELECT * FROM medical
                WHERE status = 'Pending'
                ORDER BY submitted_at DESC
                """;

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                list.add(new MedicalRequest(
                        rs.getInt("medical_id"),
                        rs.getString("reg_no"),
                        rs.getString("file_path"),
                        rs.getString("start_date"),
                        rs.getString("end_date"),
                        rs.getString("reason"),
                        rs.getString("status"),
                        rs.getString("approved_by"),
                        rs.getString("submitted_at")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public boolean approveMedical(int medicalId, String lecturerId) {
        String updateMedical = """
                UPDATE medical
                SET status = 'Approved',
                    approved_by = ?,
                    approved_at = CURRENT_TIMESTAMP
                WHERE medical_id = ?
                AND status = 'Pending'
                """;

        String updateSelectedSessions = """
                UPDATE medical_selected_session
                SET status = 'Approved'
                WHERE medical_id = ?
                """;

        String updateAttendance = """
                UPDATE attendance_record ar
                INNER JOIN medical_selected_session mss
                    ON ar.attendance_group_id = mss.attendance_group_id
                INNER JOIN medical m
                    ON m.medical_id = mss.medical_id
                SET ar.status = 'Medical'
                WHERE mss.medical_id = ?
                AND ar.reg_no = m.reg_no
                """;

        try (Connection conn = DatabaseInitializer.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement pst1 = conn.prepareStatement(updateMedical);
                 PreparedStatement pst2 = conn.prepareStatement(updateSelectedSessions);
                 PreparedStatement pst3 = conn.prepareStatement(updateAttendance)) {

                pst1.setString(1, lecturerId);
                pst1.setInt(2, medicalId);
                int affected = pst1.executeUpdate();

                if (affected == 0) {
                    conn.rollback();
                    return false;
                }

                pst2.setInt(1, medicalId);
                pst2.executeUpdate();

                pst3.setInt(1, medicalId);
                pst3.executeUpdate();

                conn.commit();
                return true;

            } catch (Exception e) {
                conn.rollback();
                e.printStackTrace();
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean rejectMedical(int medicalId, String lecturerId, String rejectReason) {
        String updateMedical = """
                UPDATE medical
                SET status = 'Rejected',
                    approved_by = ?,
                    approved_at = CURRENT_TIMESTAMP,
                    reject_reason = ?
                WHERE medical_id = ?
                AND status = 'Pending'
                """;

        String updateSelectedSessions = """
                UPDATE medical_selected_session
                SET status = 'Rejected'
                WHERE medical_id = ?
                """;

        try (Connection conn = DatabaseInitializer.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement pst1 = conn.prepareStatement(updateMedical);
                 PreparedStatement pst2 = conn.prepareStatement(updateSelectedSessions)) {

                pst1.setString(1, lecturerId);
                pst1.setString(2, rejectReason);
                pst1.setInt(3, medicalId);

                int affected = pst1.executeUpdate();

                if (affected == 0) {
                    conn.rollback();
                    return false;
                }

                pst2.setInt(1, medicalId);
                pst2.executeUpdate();

                conn.commit();
                return true;

            } catch (Exception e) {
                conn.rollback();
                e.printStackTrace();
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}