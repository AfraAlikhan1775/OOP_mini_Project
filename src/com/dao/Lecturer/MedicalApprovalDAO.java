package com.dao.Lecturer;

import com.database.DatabaseInitializer;
import com.model.Lecturerr.LecturerMedicalRequest;

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

    public List<LecturerMedicalRequest> getMedicalRequestsForLecturer(String lecturerEmpId) {
        List<LecturerMedicalRequest> list = new ArrayList<>();

        String sql = """
                SELECT
                    m.medical_id,
                    m.reg_no,
                    CONCAT(COALESCE(st.first_name, ''), ' ', COALESCE(st.last_name, '')) AS student_name,
                    mss.course_id,
                    mss.session_id,
                    mss.session_name,
                    mss.type,
                    mss.attendance_date,
                    m.start_date,
                    m.end_date,
                    m.reason,
                    m.file_path,
                    m.status,
                    m.submitted_at
                FROM medical m
                INNER JOIN medical_selected_session mss
                    ON m.medical_id = mss.medical_id
                INNER JOIN courses c
                    ON mss.course_id = c.course_id
                LEFT JOIN student st
                    ON m.reg_no = st.reg_no
                WHERE c.coordinator = ?
                ORDER BY
                    CASE m.status
                        WHEN 'Pending' THEN 1
                        WHEN 'Approved' THEN 2
                        WHEN 'Rejected' THEN 3
                        ELSE 4
                    END,
                    m.submitted_at DESC
                """;

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, lecturerEmpId);

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    list.add(new LecturerMedicalRequest(
                            rs.getInt("medical_id"),
                            rs.getString("reg_no"),
                            rs.getString("student_name"),
                            rs.getString("course_id"),
                            rs.getString("session_id"),
                            rs.getString("session_name"),
                            rs.getString("type"),
                            rs.getString("attendance_date"),
                            rs.getString("start_date"),
                            rs.getString("end_date"),
                            rs.getString("reason"),
                            rs.getString("file_path"),
                            rs.getString("status"),
                            rs.getString("submitted_at")
                    ));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public boolean approveMedical(int medicalId, String lecturerEmpId) {
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
                    ON ar.group_id = mss.attendance_group_id
                INNER JOIN medical m
                    ON m.medical_id = mss.medical_id
                SET ar.status = 'MEDICAL',
                    ar.medical_status = 'Approved'
                WHERE mss.medical_id = ?
                  AND ar.reg_no = m.reg_no
                """;

        try (Connection conn = DatabaseInitializer.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement p1 = conn.prepareStatement(updateMedical);
                 PreparedStatement p2 = conn.prepareStatement(updateSelectedSessions);
                 PreparedStatement p3 = conn.prepareStatement(updateAttendance)) {

                p1.setString(1, lecturerEmpId);
                p1.setInt(2, medicalId);

                int affected = p1.executeUpdate();

                if (affected == 0) {
                    conn.rollback();
                    return false;
                }

                p2.setInt(1, medicalId);
                p2.executeUpdate();

                p3.setInt(1, medicalId);
                p3.executeUpdate();

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

    public boolean rejectMedical(int medicalId, String lecturerEmpId, String rejectReason) {
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

            try (PreparedStatement p1 = conn.prepareStatement(updateMedical);
                 PreparedStatement p2 = conn.prepareStatement(updateSelectedSessions)) {

                p1.setString(1, lecturerEmpId);
                p1.setString(2, rejectReason);
                p1.setInt(3, medicalId);

                int affected = p1.executeUpdate();

                if (affected == 0) {
                    conn.rollback();
                    return false;
                }

                p2.setInt(1, medicalId);
                p2.executeUpdate();

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