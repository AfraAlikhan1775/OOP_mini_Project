package com.dao;

import com.database.DatabaseInitializer;
import com.model.Attendance;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AttendanceDAO {

    public AttendanceDAO() {
        createTable();
    }

    public void createTable() {
        String sql = """
                CREATE TABLE IF NOT EXISTS attendance (
                    attendance_id   INT PRIMARY KEY AUTO_INCREMENT,
                    student_id      VARCHAR(100) NOT NULL,
                    course_code     VARCHAR(50) NOT NULL,
                    attendance_date DATE NOT NULL,
                    status          ENUM('Present', 'Absent', 'Late', 'Excused') NOT NULL,
                    marked_by       VARCHAR(50),
                    remarks         VARCHAR(255),
                    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    UNIQUE KEY unique_attendance (student_id, course_code, attendance_date)
                )
                """;

        try (Connection conn = DatabaseInitializer.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean addAttendance(Attendance a) {
        String sql = """
                INSERT INTO attendance (student_id, course_code, attendance_date, status, marked_by, remarks)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, a.getStudentId());
            pst.setString(2, a.getCourseCode());
            pst.setDate(3, Date.valueOf(a.getAttendanceDate()));
            pst.setString(4, a.getStatus());
            pst.setString(5, a.getMarkedBy());
            pst.setString(6, a.getRemarks());

            pst.executeUpdate();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateAttendance(Attendance a) {
        String sql = """
                UPDATE attendance
                SET student_id = ?, course_code = ?, attendance_date = ?,
                    status = ?, marked_by = ?, remarks = ?
                WHERE attendance_id = ?
                """;

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, a.getStudentId());
            pst.setString(2, a.getCourseCode());
            pst.setDate(3, Date.valueOf(a.getAttendanceDate()));
            pst.setString(4, a.getStatus());
            pst.setString(5, a.getMarkedBy());
            pst.setString(6, a.getRemarks());
            pst.setInt(7, a.getAttendanceId());

            return pst.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteAttendance(int attendanceId) {
        String sql = "DELETE FROM attendance WHERE attendance_id = ?";

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, attendanceId);
            return pst.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Attendance> getAllAttendance() {
        List<Attendance> list = new ArrayList<>();
        String sql = "SELECT * FROM attendance ORDER BY attendance_date DESC, student_id";

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                list.add(extractAttendance(rs));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<Attendance> getAttendanceByStudent(String studentId) {
        List<Attendance> list = new ArrayList<>();
        String sql = "SELECT * FROM attendance WHERE student_id = ? ORDER BY attendance_date DESC";

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, studentId);

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    list.add(extractAttendance(rs));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<Attendance> getAttendanceByCourseAndDate(String courseCode, java.time.LocalDate date) {
        List<Attendance> list = new ArrayList<>();
        String sql = "SELECT * FROM attendance WHERE course_code = ? AND attendance_date = ? ORDER BY student_id";

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, courseCode);
            pst.setDate(2, Date.valueOf(date));

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    list.add(extractAttendance(rs));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<Attendance> searchAttendance(String keyword) {
        List<Attendance> list = new ArrayList<>();
        String sql = """
                SELECT * FROM attendance
                WHERE student_id LIKE ?
                   OR course_code LIKE ?
                   OR status LIKE ?
                   OR remarks LIKE ?
                ORDER BY attendance_date DESC
                """;

        String pattern = "%" + keyword + "%";

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, pattern);
            pst.setString(2, pattern);
            pst.setString(3, pattern);
            pst.setString(4, pattern);

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    list.add(extractAttendance(rs));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    private Attendance extractAttendance(ResultSet rs) throws SQLException {
        return new Attendance(
                rs.getInt("attendance_id"),
                rs.getString("student_id"),
                rs.getString("course_code"),
                rs.getDate("attendance_date") != null ? rs.getDate("attendance_date").toLocalDate() : null,
                rs.getString("status"),
                rs.getString("marked_by"),
                rs.getString("remarks")
        );
    }
}
