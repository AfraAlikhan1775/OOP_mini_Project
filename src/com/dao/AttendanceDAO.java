package com.dao;

import com.database.DatabaseInitializer;
import com.model.AttendanceEntry;
import com.model.AttendanceMarkRow;
import com.model.Session;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AttendanceDAO {

    public AttendanceDAO() {
        createTablesIfNotExists();
    }

    public void createTablesIfNotExists() {
        String sessionSql = """
                CREATE TABLE IF NOT EXISTS course_session (
                    course_id VARCHAR(50) NOT NULL,
                    session_id VARCHAR(50) NOT NULL,
                    semester VARCHAR(20) NOT NULL,
                    academic_year VARCHAR(20) NOT NULL,
                    session_type ENUM('Theory', 'Practical') NOT NULL,
                    session_date DATE NOT NULL,
                    lecturer_emp_id VARCHAR(50) NOT NULL,
                    hours DECIMAL(4,2) NOT NULL,
                    PRIMARY KEY (course_id, session_id),
                    FOREIGN KEY (course_id) REFERENCES courses(course_id)
                        ON UPDATE CASCADE
                        ON DELETE RESTRICT,
                    FOREIGN KEY (lecturer_emp_id) REFERENCES lecturer(emp_id)
                        ON UPDATE CASCADE
                        ON DELETE RESTRICT
                )
                """;

        String attendanceSql = """
                CREATE TABLE IF NOT EXISTS attendance (
                    course_id VARCHAR(50) NOT NULL,
                    session_id VARCHAR(50) NOT NULL,
                    reg_no VARCHAR(50) NOT NULL,
                    status ENUM('Present', 'Absent', 'Medical') NOT NULL,
                    PRIMARY KEY (course_id, session_id, reg_no),
                    FOREIGN KEY (course_id, session_id)
                        REFERENCES course_session(course_id, session_id)
                        ON UPDATE CASCADE
                        ON DELETE CASCADE,
                    FOREIGN KEY (reg_no)
                        REFERENCES student(reg_no)
                        ON UPDATE CASCADE
                        ON DELETE RESTRICT
                )
                """;

        try (Connection conn = DatabaseInitializer.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sessionSql);
            stmt.execute(attendanceSql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean courseExists(String courseId) {
        String sql = "SELECT 1 FROM courses WHERE course_id = ?";
        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, courseId);
            return pst.executeQuery().next();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean lecturerExists(String empId) {
        String sql = "SELECT 1 FROM lecturer WHERE emp_id = ?";
        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, empId);
            return pst.executeQuery().next();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean addSession(Session session, String semester, String academicYear) {
        String sql = """
                INSERT INTO course_session
                (course_id, session_id, semester, academic_year, session_type, session_date, lecturer_emp_id, hours)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, session.getCourseId());
            pst.setString(2, session.getSessionId());
            pst.setString(3, semester);
            pst.setString(4, academicYear);
            pst.setString(5, session.getSessionType());
            pst.setDate(6, Date.valueOf(session.getSessionDate()));
            pst.setString(7, session.getLecturerEmpId());
            pst.setDouble(8, session.getHours());
            return pst.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Session> getAllSessions() {
        List<Session> list = new ArrayList<>();
        String sql = "SELECT * FROM course_session ORDER BY session_date DESC, course_id";

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                list.add(new Session(
                        rs.getString("course_id"),
                        rs.getString("session_id"),
                        rs.getString("session_type"),
                        rs.getDate("session_date").toLocalDate(),
                        rs.getString("lecturer_emp_id"),
                        rs.getDouble("hours")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<String> getSessionsByCourseSemesterYear(String courseId, String semester, String academicYear) {
        List<String> list = new ArrayList<>();
        String sql = """
                SELECT session_id
                FROM course_session
                WHERE course_id = ? AND semester = ? AND academic_year = ?
                ORDER BY session_date
                """;

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, courseId);
            pst.setString(2, semester);
            pst.setString(3, academicYear);

            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                list.add(rs.getString("session_id"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public boolean saveAttendanceBulk(String courseId, String sessionId, List<AttendanceMarkRow> rows) {
        String sql = """
                INSERT INTO attendance (course_id, session_id, reg_no, status)
                VALUES (?, ?, ?, ?)
                ON DUPLICATE KEY UPDATE status = VALUES(status)
                """;

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            conn.setAutoCommit(false);

            for (AttendanceMarkRow row : rows) {
                pst.setString(1, courseId);
                pst.setString(2, sessionId);
                pst.setString(3, row.getRegNo());
                pst.setString(4, row.getStatus());
                pst.addBatch();
            }

            pst.executeBatch();
            conn.commit();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}