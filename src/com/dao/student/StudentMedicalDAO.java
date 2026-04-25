package com.dao.student;

import com.database.DatabaseInitializer;
import com.model.student.ExamMedicalCourse;
import com.model.student.MedicalSession;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class StudentMedicalDAO {

    public StudentMedicalDAO() {
        createOrUpdateMedicalTables();
    }

    private void createOrUpdateMedicalTables() {
        try (Connection conn = DatabaseInitializer.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS medical_selected_session (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    medical_id INT NOT NULL,
                    attendance_group_id INT NULL,
                    course_id VARCHAR(100),
                    session_id VARCHAR(100),
                    session_name VARCHAR(150),
                    type VARCHAR(30),
                    attendance_date DATE,
                    medical_for VARCHAR(20) DEFAULT 'ATTENDANCE',
                    exam_type VARCHAR(50) NULL,
                    exam_date DATE NULL,
                    status VARCHAR(20) DEFAULT 'Pending'
                )
            """);

            addColumnIfMissing(conn, "medical_selected_session", "medical_for",
                    "VARCHAR(20) DEFAULT 'ATTENDANCE'");

            addColumnIfMissing(conn, "medical_selected_session", "exam_type",
                    "VARCHAR(50) NULL");

            addColumnIfMissing(conn, "medical_selected_session", "exam_date",
                    "DATE NULL");

            try {
                stmt.execute("ALTER TABLE medical_selected_session MODIFY attendance_group_id INT NULL");
            } catch (Exception ignored) {}

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addColumnIfMissing(Connection conn, String table, String column, String definition) {
        String sql = """
            SELECT COUNT(*)
            FROM INFORMATION_SCHEMA.COLUMNS
            WHERE TABLE_SCHEMA = DATABASE()
              AND TABLE_NAME = ?
              AND COLUMN_NAME = ?
        """;

        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, table);
            pst.setString(2, column);

            ResultSet rs = pst.executeQuery();

            if (rs.next() && rs.getInt(1) == 0) {
                try (Statement stmt = conn.createStatement()) {
                    stmt.execute("ALTER TABLE " + table + " ADD COLUMN " + column + " " + definition);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<MedicalSession> getAbsentSessions(String regNo) {
        List<MedicalSession> list = new ArrayList<>();

        String sql = """
            SELECT
                ag.id AS attendance_group_id,
                ag.course_id,
                ag.session_id,
                ag.type,
                ag.attendance_date,
                COALESCE(s.session_name, ag.session_id) AS session_name
            FROM attendance_record ar
            INNER JOIN attendance_group ag ON ar.group_id = ag.id
            LEFT JOIN session s ON ag.session_id = s.session_id
            WHERE ar.reg_no = ?
              AND UPPER(ar.status) = 'ABSENT'
            ORDER BY ag.attendance_date DESC
        """;

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, regNo);
            ResultSet rs = pst.executeQuery();

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

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<ExamMedicalCourse> getStudentCoursesForExamMedical(String regNo) {
        List<ExamMedicalCourse> list = new ArrayList<>();

        String sql = """
            SELECT DISTINCT c.course_id, c.course_name
            FROM attendance_record ar
            INNER JOIN attendance_group ag ON ar.group_id = ag.id
            INNER JOIN courses c ON ag.course_id = c.course_id
            WHERE ar.reg_no = ?
            ORDER BY c.course_id
        """;

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, regNo);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                list.add(new ExamMedicalCourse(
                        rs.getString("course_id"),
                        rs.getString("course_name")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public boolean submitAttendanceMedical(String regNo, String filePath,
                                           LocalDate startDate, LocalDate endDate,
                                           String reason,
                                           List<MedicalSession> selectedSessions) {

        if (selectedSessions == null || selectedSessions.isEmpty()) {
            return false;
        }

        String medicalSql = """
            INSERT INTO medical
            (reg_no, student_id, file_path, start_date, end_date,
             medical_start_date, medical_end_date, reason, status, submitted_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'Pending', CURRENT_TIMESTAMP)
        """;

        String sessionSql = """
            INSERT INTO medical_selected_session
            (medical_id, attendance_group_id, course_id, session_id,
             session_name, type, attendance_date, medical_for, status)
            VALUES (?, ?, ?, ?, ?, ?, ?, 'ATTENDANCE', 'Pending')
        """;

        try (Connection conn = DatabaseInitializer.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement medicalPst =
                         conn.prepareStatement(medicalSql, Statement.RETURN_GENERATED_KEYS);
                 PreparedStatement sessionPst = conn.prepareStatement(sessionSql)) {

                medicalPst.setString(1, regNo);
                medicalPst.setString(2, regNo);
                medicalPst.setString(3, filePath);
                medicalPst.setDate(4, Date.valueOf(startDate));
                medicalPst.setDate(5, Date.valueOf(endDate));
                medicalPst.setDate(6, Date.valueOf(startDate));
                medicalPst.setDate(7, Date.valueOf(endDate));
                medicalPst.setString(8, reason);
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
            return false;
        }
    }

    public boolean submitExamMedical(String regNo, String filePath,
                                     LocalDate startDate, LocalDate endDate,
                                     String reason,
                                     String courseId,
                                     String examType,
                                     LocalDate examDate) {

        String medicalSql = """
            INSERT INTO medical
            (reg_no, student_id, file_path, start_date, end_date,
             medical_start_date, medical_end_date, reason, status, submitted_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'Pending', CURRENT_TIMESTAMP)
        """;

        String examSql = """
            INSERT INTO medical_selected_session
            (medical_id, attendance_group_id, course_id, session_id,
             session_name, type, attendance_date, medical_for,
             exam_type, exam_date, status)
            VALUES (?, NULL, ?, ?, ?, 'EXAM', ?, 'EXAM',
                    ?, ?, 'Pending')
        """;

        try (Connection conn = DatabaseInitializer.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement medicalPst =
                         conn.prepareStatement(medicalSql, Statement.RETURN_GENERATED_KEYS);
                 PreparedStatement examPst = conn.prepareStatement(examSql)) {

                medicalPst.setString(1, regNo);
                medicalPst.setString(2, regNo);
                medicalPst.setString(3, filePath);
                medicalPst.setDate(4, Date.valueOf(startDate));
                medicalPst.setDate(5, Date.valueOf(endDate));
                medicalPst.setDate(6, Date.valueOf(startDate));
                medicalPst.setDate(7, Date.valueOf(endDate));
                medicalPst.setString(8, reason);
                medicalPst.executeUpdate();

                ResultSet keys = medicalPst.getGeneratedKeys();

                if (!keys.next()) {
                    conn.rollback();
                    return false;
                }

                int medicalId = keys.getInt(1);

                examPst.setInt(1, medicalId);
                examPst.setString(2, courseId);
                examPst.setString(3, examType);
                examPst.setString(4, examType);
                examPst.setDate(5, Date.valueOf(examDate));
                examPst.setString(6, examType);
                examPst.setDate(7, Date.valueOf(examDate));

                examPst.executeUpdate();

                conn.commit();
                return true;

            } catch (Exception e) {
                conn.rollback();
                e.printStackTrace();
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}