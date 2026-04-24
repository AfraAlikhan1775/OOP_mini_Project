package com.dao.student;

import com.database.DatabaseInitializer;
import com.model.student.MedicalRequest;
import com.model.student.MedicalSession;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MedicalDAO {

    public MedicalDAO() {
        createMedicalTablesIfNotExists();
        ensureAttendanceRecordGroupColumn();
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

    private void ensureAttendanceRecordGroupColumn() {
        String checkSql = """
                SELECT COUNT(*) 
                FROM INFORMATION_SCHEMA.COLUMNS
                WHERE TABLE_SCHEMA = DATABASE()
                AND TABLE_NAME = 'attendance_record'
                AND COLUMN_NAME = 'attendance_group_id'
                """;

        String alterSql = """
                ALTER TABLE attendance_record
                ADD COLUMN attendance_group_id INT
                """;

        try (Connection conn = DatabaseInitializer.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(checkSql)) {

            if (rs.next() && rs.getInt(1) == 0) {
                stmt.execute(alterSql);
            }

        } catch (Exception e) {
            // This will not stop old features.
            e.printStackTrace();
        }
    }

    public List<MedicalRequest> getStudentMedicalRequests(String regNo) {
        List<MedicalRequest> list = new ArrayList<>();

        String sql = """
                SELECT * FROM medical
                WHERE reg_no = ?
                ORDER BY submitted_at DESC
                """;

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, regNo);

            try (ResultSet rs = pst.executeQuery()) {
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
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<MedicalSession> getSessionsForDateGap(String regNo, LocalDate start, LocalDate end) {
        List<MedicalSession> list = new ArrayList<>();

        String sql = """
                SELECT 
                    ag.id AS attendance_group_id,
                    ag.course_id,
                    ag.session_id,
                    ag.type,
                    ag.attendance_date,
                    COALESCE(s.session_name, ag.session_id) AS session_name
                FROM attendance_group ag
                LEFT JOIN session s
                    ON ag.course_id = s.course_id
                    AND ag.session_id = s.session_id
                    AND ag.type = s.type
                WHERE ag.year_no = (
                    SELECT year_no FROM student WHERE reg_no = ?
                )
                AND ag.attendance_date BETWEEN ? AND ?
                ORDER BY ag.attendance_date, ag.course_id, ag.session_id
                """;

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, regNo);
            pst.setDate(2, Date.valueOf(start));
            pst.setDate(3, Date.valueOf(end));

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    list.add(new MedicalSession(
                            rs.getInt("attendance_group_id"),
                            rs.getString("course_id"),
                            rs.getString("session_id"),
                            rs.getString("session_name"),
                            rs.getString("type"),
                            rs.getString("attendance_date")
                    ));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public boolean submitMedical(String regNo, String filePath, LocalDate startDate,
                                 LocalDate endDate, String reason,
                                 List<MedicalSession> selectedSessions) {

        if (selectedSessions == null || selectedSessions.isEmpty()) {
            return false;
        }

        if (!checkTwentyPercentLimit(regNo, selectedSessions)) {
            return false;
        }

        String medicalSql = """
                INSERT INTO medical
                (reg_no, file_path, start_date, end_date, reason, status)
                VALUES (?, ?, ?, ?, ?, 'Pending')
                """;

        String sessionSql = """
                INSERT INTO medical_selected_session
                (medical_id, attendance_group_id, course_id, session_id,
                 session_name, type, attendance_date, status)
                VALUES (?, ?, ?, ?, ?, ?, ?, 'Pending')
                """;

        try (Connection conn = DatabaseInitializer.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement medicalPst =
                         conn.prepareStatement(medicalSql, Statement.RETURN_GENERATED_KEYS);
                 PreparedStatement sessionPst =
                         conn.prepareStatement(sessionSql)) {

                medicalPst.setString(1, regNo);
                medicalPst.setString(2, filePath);
                medicalPst.setDate(3, Date.valueOf(startDate));
                medicalPst.setDate(4, Date.valueOf(endDate));
                medicalPst.setString(5, reason);

                medicalPst.executeUpdate();

                ResultSet keys = medicalPst.getGeneratedKeys();

                if (!keys.next()) {
                    conn.rollback();
                    return false;
                }

                int medicalId = keys.getInt(1);

                for (MedicalSession s : selectedSessions) {
                    sessionPst.setInt(1, medicalId);
                    sessionPst.setInt(2, s.getAttendanceGroupId());
                    sessionPst.setString(3, s.getCourseId());
                    sessionPst.setString(4, s.getSessionId());
                    sessionPst.setString(5, s.getSessionName());
                    sessionPst.setString(6, s.getType());
                    sessionPst.setDate(7, Date.valueOf(s.getAttendanceDate()));
                    sessionPst.addBatch();
                }

                sessionPst.executeBatch();
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

    private boolean checkTwentyPercentLimit(String regNo, List<MedicalSession> selectedSessions) {
        try (Connection conn = DatabaseInitializer.getConnection()) {

            for (MedicalSession s : selectedSessions) {
                String courseId = s.getCourseId();

                int totalSessions = getTotalSessionsForCourse(conn, courseId);
                int approvedMedical = getApprovedMedicalCount(conn, regNo, courseId);
                int nowSelected = countSelectedCourse(selectedSessions, courseId);

                int allowed = Math.max(1, (int) Math.floor(totalSessions * 0.20));

                if ((approvedMedical + nowSelected) > allowed) {
                    return false;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private int getTotalSessionsForCourse(Connection conn, String courseId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM attendance_group WHERE course_id = ?";

        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, courseId);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }

        return 0;
    }

    private int getApprovedMedicalCount(Connection conn, String regNo, String courseId) throws SQLException {
        String sql = """
                SELECT COUNT(*)
                FROM medical m
                INNER JOIN medical_selected_session mss
                    ON m.medical_id = mss.medical_id
                WHERE m.reg_no = ?
                AND mss.course_id = ?
                AND m.status = 'Approved'
                """;

        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, regNo);
            pst.setString(2, courseId);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }

        return 0;
    }

    private int countSelectedCourse(List<MedicalSession> list, String courseId) {
        int count = 0;

        for (MedicalSession s : list) {
            if (courseId != null && courseId.equals(s.getCourseId())) {
                count++;
            }
        }

        return count;
    }
}