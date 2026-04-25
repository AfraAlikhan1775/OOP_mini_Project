package com.dao.student;

import com.database.DatabaseInitializer;
import com.model.student.ExamMedicalCourse;
import com.model.student.MedicalRequest;
import com.model.student.MedicalSession;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MedicalDAO {

    public MedicalDAO() {
        createMedicalTablesIfNotExists();
    }

    private void createMedicalTablesIfNotExists() {
        try (Connection conn = DatabaseInitializer.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("""
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
            """);

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
                    status VARCHAR(20) DEFAULT 'Pending',
                    FOREIGN KEY (medical_id) REFERENCES medical(medical_id) ON DELETE CASCADE
                )
            """);

            addColumnIfMissing(conn, "medical", "student_id", "VARCHAR(100)");
            addColumnIfMissing(conn, "medical", "medical_data", "LONGBLOB");
            addColumnIfMissing(conn, "medical", "medical_start_date", "DATE");
            addColumnIfMissing(conn, "medical", "medical_end_date", "DATE");
            addColumnIfMissing(conn, "medical", "batch", "VARCHAR(10)");
            addColumnIfMissing(conn, "medical", "department", "VARCHAR(50)");
            addColumnIfMissing(conn, "medical", "added_by", "VARCHAR(50)");
            addColumnIfMissing(conn, "medical", "approved_at", "TIMESTAMP NULL");
            addColumnIfMissing(conn, "medical", "reject_reason", "TEXT");
            addColumnIfMissing(conn, "medical", "created_at", "TIMESTAMP DEFAULT CURRENT_TIMESTAMP");

            addColumnIfMissing(conn, "medical_selected_session", "medical_for", "VARCHAR(20) DEFAULT 'ATTENDANCE'");
            addColumnIfMissing(conn, "medical_selected_session", "exam_type", "VARCHAR(50) NULL");
            addColumnIfMissing(conn, "medical_selected_session", "exam_date", "DATE NULL");

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

    public List<MedicalSession> getSessionsForDateGap(String regNo, LocalDate start, LocalDate end) {
        List<MedicalSession> list = new ArrayList<>();

        String sql = """
            SELECT
                ag.id AS attendance_group_id,
                ag.course_id,
                ag.session_id,
                COALESCE(s.session_name, ag.session_id) AS session_name,
                ag.type,
                DATE(ag.attendance_date) AS attendance_date
            FROM attendance_record ar
            INNER JOIN attendance_group ag ON ar.group_id = ag.id
            LEFT JOIN session s
                ON s.course_id = ag.course_id
               AND s.session_id = ag.session_id
            WHERE ar.reg_no = ?
              AND UPPER(ar.status) = 'ABSENT'
              AND DATE(ag.attendance_date) BETWEEN ? AND ?
              AND NOT EXISTS (
                    SELECT 1
                    FROM medical_selected_session mss
                    INNER JOIN medical m ON m.medical_id = mss.medical_id
                    WHERE m.reg_no = ar.reg_no
                      AND mss.attendance_group_id = ag.id
                      AND UPPER(COALESCE(mss.medical_for, 'ATTENDANCE')) = 'ATTENDANCE'
                      AND UPPER(m.status) IN ('PENDING', 'APPROVED', 'VERIFIED')
              )
            ORDER BY ag.attendance_date DESC, ag.course_id, ag.session_id
        """;

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, regNo);
            pst.setDate(2, Date.valueOf(start));
            pst.setDate(3, Date.valueOf(end));

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

    public List<MedicalSession> getAbsentExamsForDateGap(String regNo, LocalDate start, LocalDate end) {
        List<MedicalSession> list = new ArrayList<>();

        String batchPrefix = getBatchPrefix(regNo);

        String sql = """
            SELECT DISTINCT course_id, exam_type, exam_date
            FROM (
                SELECT
                    sm.course_id,
                    'Quiz 1' AS exam_type,
                    CURRENT_DATE AS exam_date
                FROM student_marks sm
                WHERE sm.reg_no LIKE ?
                  AND COALESCE(sm.quiz1, 0) > 0
                  AND NOT EXISTS (
                        SELECT 1
                        FROM student_marks x
                        WHERE x.reg_no = ?
                          AND x.course_id = sm.course_id
                          AND COALESCE(x.quiz1, 0) > 0
                  )

                UNION

                SELECT
                    sm.course_id,
                    'Quiz 2' AS exam_type,
                    CURRENT_DATE AS exam_date
                FROM student_marks sm
                WHERE sm.reg_no LIKE ?
                  AND COALESCE(sm.quiz2, 0) > 0
                  AND NOT EXISTS (
                        SELECT 1
                        FROM student_marks x
                        WHERE x.reg_no = ?
                          AND x.course_id = sm.course_id
                          AND COALESCE(x.quiz2, 0) > 0
                  )

                UNION

                SELECT
                    sm.course_id,
                    'Quiz 3' AS exam_type,
                    CURRENT_DATE AS exam_date
                FROM student_marks sm
                WHERE sm.reg_no LIKE ?
                  AND COALESCE(sm.quiz3, 0) > 0
                  AND NOT EXISTS (
                        SELECT 1
                        FROM student_marks x
                        WHERE x.reg_no = ?
                          AND x.course_id = sm.course_id
                          AND COALESCE(x.quiz3, 0) > 0
                  )

                UNION

                SELECT
                    sm.course_id,
                    'Assignment' AS exam_type,
                    CURRENT_DATE AS exam_date
                FROM student_marks sm
                WHERE sm.reg_no LIKE ?
                  AND COALESCE(sm.assignment_mark, 0) > 0
                  AND NOT EXISTS (
                        SELECT 1
                        FROM student_marks x
                        WHERE x.reg_no = ?
                          AND x.course_id = sm.course_id
                          AND COALESCE(x.assignment_mark, 0) > 0
                  )

                UNION

                SELECT
                    sm.course_id,
                    'Mid Exam' AS exam_type,
                    CURRENT_DATE AS exam_date
                FROM student_marks sm
                WHERE sm.reg_no LIKE ?
                  AND COALESCE(sm.mid_exam, 0) > 0
                  AND NOT EXISTS (
                        SELECT 1
                        FROM student_marks x
                        WHERE x.reg_no = ?
                          AND x.course_id = sm.course_id
                          AND COALESCE(x.mid_exam, 0) > 0
                  )

                UNION

                SELECT
                    sm.course_id,
                    'Final Theory' AS exam_type,
                    CURRENT_DATE AS exam_date
                FROM student_marks sm
                WHERE sm.reg_no LIKE ?
                  AND COALESCE(sm.final_theory, 0) > 0
                  AND NOT EXISTS (
                        SELECT 1
                        FROM student_marks x
                        WHERE x.reg_no = ?
                          AND x.course_id = sm.course_id
                          AND COALESCE(x.final_theory, 0) > 0
                  )

                UNION

                SELECT
                    sm.course_id,
                    'Final Practical' AS exam_type,
                    CURRENT_DATE AS exam_date
                FROM student_marks sm
                WHERE sm.reg_no LIKE ?
                  AND COALESCE(sm.final_practical, 0) > 0
                  AND NOT EXISTS (
                        SELECT 1
                        FROM student_marks x
                        WHERE x.reg_no = ?
                          AND x.course_id = sm.course_id
                          AND COALESCE(x.final_practical, 0) > 0
                  )
            ) exams
            WHERE NOT EXISTS (
                SELECT 1
                FROM medical m
                INNER JOIN medical_selected_session mss
                    ON m.medical_id = mss.medical_id
                WHERE m.reg_no = ?
                  AND mss.course_id = exams.course_id
                  AND UPPER(COALESCE(mss.medical_for, '')) = 'EXAM'
                  AND UPPER(COALESCE(mss.exam_type, '')) = UPPER(exams.exam_type)
                  AND UPPER(m.status) IN ('PENDING', 'APPROVED', 'VERIFIED')
            )
            ORDER BY course_id, exam_type
        """;

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            int i = 1;

            for (int n = 0; n < 7; n++) {
                pst.setString(i++, batchPrefix + "%");
                pst.setString(i++, regNo);
            }

            pst.setString(i, regNo);

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                list.add(new MedicalSession(
                        rs.getString("course_id"),
                        rs.getString("exam_type"),
                        rs.getString("exam_date")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<ExamMedicalCourse> getStudentCoursesForExamMedical(String regNo) {
        List<ExamMedicalCourse> list = new ArrayList<>();

        String batchPrefix = getBatchPrefix(regNo);

        String sql = """
            SELECT DISTINCT course_id, course_name FROM (
                SELECT c.course_id, c.course_name
                FROM course_registration cr
                INNER JOIN courses c ON cr.course_id = c.course_id
                WHERE cr.reg_no = ?

                UNION

                SELECT c.course_id, c.course_name
                FROM student s
                INNER JOIN courses c
                    ON c.department = s.department
                   AND c.year = s.year_no
                WHERE s.reg_no = ?

                UNION

                SELECT c.course_id, c.course_name
                FROM student_marks sm
                INNER JOIN courses c ON sm.course_id = c.course_id
                WHERE sm.reg_no = ?

                UNION

                SELECT c.course_id, c.course_name
                FROM student_marks sm
                INNER JOIN courses c ON sm.course_id = c.course_id
                WHERE sm.reg_no LIKE ?
            ) x
            ORDER BY course_id
        """;

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, regNo);
            pst.setString(2, regNo);
            pst.setString(3, regNo);
            pst.setString(4, batchPrefix + "%");

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

    public boolean submitMedical(String regNo, String filePath, LocalDate startDate,
                                 LocalDate endDate, String reason,
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
             session_name, type, attendance_date, medical_for,
             exam_type, exam_date, status)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'Pending')
        """;

        try (Connection conn = DatabaseInitializer.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement medicalPst = conn.prepareStatement(medicalSql, Statement.RETURN_GENERATED_KEYS);
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

                    if ("EXAM".equalsIgnoreCase(s.getMedicalFor())) {
                        sessionPst.setNull(2, Types.INTEGER);
                    } else {
                        sessionPst.setInt(2, s.getAttendanceGroupId());
                    }

                    sessionPst.setString(3, s.getCourseId());
                    sessionPst.setString(4, s.getSessionId());
                    sessionPst.setString(5, s.getSessionName());
                    sessionPst.setString(6, s.getType());
                    sessionPst.setDate(7, Date.valueOf(s.getAttendanceDate()));
                    sessionPst.setString(8, s.getMedicalFor());
                    sessionPst.setString(9, s.getExamType());

                    if (s.getExamDate() == null) {
                        sessionPst.setNull(10, Types.DATE);
                    } else {
                        sessionPst.setDate(10, Date.valueOf(s.getExamDate()));
                    }

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

    public boolean submitExamMedical(String regNo, String filePath, LocalDate startDate,
                                     LocalDate endDate, String reason,
                                     String courseId, String examType, LocalDate examDate) {

        List<MedicalSession> sessions = new ArrayList<>();
        MedicalSession s = new MedicalSession(courseId, examType, examDate.toString());
        s.setSelected(true);
        sessions.add(s);

        return submitMedical(regNo, filePath, startDate, endDate, reason, sessions);
    }

    public List<MedicalRequest> getStudentMedicalRequests(String regNo) {
        List<MedicalRequest> list = new ArrayList<>();

        String sql = """
            SELECT
                m.medical_id,
                m.reg_no,
                m.file_path,
                m.start_date,
                m.end_date,
                m.reason,
                m.status,
                m.approved_by,
                m.submitted_at,
                COALESCE(MAX(mss.medical_for), 'ATTENDANCE') AS medical_for
            FROM medical m
            LEFT JOIN medical_selected_session mss
                ON m.medical_id = mss.medical_id
            WHERE m.reg_no = ?
            GROUP BY
                m.medical_id, m.reg_no, m.file_path, m.start_date, m.end_date,
                m.reason, m.status, m.approved_by, m.submitted_at
            ORDER BY m.submitted_at DESC
        """;

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, regNo);
            ResultSet rs = pst.executeQuery();

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
                        rs.getString("submitted_at"),
                        rs.getString("medical_for")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    private String getBatchPrefix(String regNo) {
        int index = regNo.lastIndexOf("/");
        if (index == -1) return regNo;
        return regNo.substring(0, index + 1);
    }
}