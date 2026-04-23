package com.dao;

import com.database.DatabaseInitializer;
import com.model.admin.AttendanceGroup;
import com.model.admin.AttendanceRecord;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AttendanceDAO {

    public AttendanceDAO() {
        createTables();
    }

    public void createTables() {
        String attendanceGroupTable = """
                CREATE TABLE IF NOT EXISTS attendance_group (
                    id INT PRIMARY KEY AUTO_INCREMENT,
                    year_no INT NOT NULL,
                    course_id VARCHAR(100) NOT NULL,
                    session_id VARCHAR(100) NOT NULL,
                    type VARCHAR(20) NOT NULL,
                    attendance_date DATE NOT NULL,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
                """;

        String attendanceRecordTable = """
                CREATE TABLE IF NOT EXISTS attendance_record (
                    id INT PRIMARY KEY AUTO_INCREMENT,
                    group_id INT NOT NULL,
                    reg_no VARCHAR(100) NOT NULL,
                    status VARCHAR(20) NOT NULL,
                    FOREIGN KEY (group_id) REFERENCES attendance_group(id) ON DELETE CASCADE
                )
                """;

        try (Connection conn = DatabaseInitializer.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(attendanceGroupTable);
            stmt.execute(attendanceRecordTable);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<String> getCourseIdsByYearAndSemester(int year, int semester) {
        List<String> list = new ArrayList<>();

        String sql = """
                SELECT course_id
                FROM courses
                WHERE year = ? AND semester = ?
                ORDER BY course_id
                """;

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, year);
            pst.setInt(2, semester);

            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                list.add(rs.getString("course_id"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<String> getSessionIdsByCourseAndType(String courseId, String type) {
        List<String> list = new ArrayList<>();

        String sql = """
                SELECT session_id
                FROM session
                WHERE course_id = ? AND type = ?
                ORDER BY session_id
                """;

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, courseId);
            pst.setString(2, type);

            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                list.add(rs.getString("session_id"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public int insertAttendanceGroup(AttendanceGroup group) {
        String sql = """
                INSERT INTO attendance_group (year_no, course_id, session_id, type, attendance_date)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pst.setInt(1, group.getYear());
            pst.setString(2, group.getCourseId());
            pst.setString(3, group.getSessionId());
            pst.setString(4, group.getType());
            pst.setDate(5, Date.valueOf(group.getAttendanceDate()));

            pst.executeUpdate();

            ResultSet rs = pst.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }

    public void insertAttendanceRecords(List<AttendanceRecord> records) {
        String sql = """
                INSERT INTO attendance_record (group_id, reg_no, status)
                VALUES (?, ?, ?)
                """;

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            for (AttendanceRecord record : records) {
                pst.setInt(1, record.getGroupId());
                pst.setString(2, record.getRegNo());
                pst.setString(3, record.getStatus());
                pst.addBatch();
            }

            pst.executeBatch();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<String> getStudentRegNosByPrefix(String prefix) {
        List<String> list = new ArrayList<>();

        String sql = """
                SELECT reg_no
                FROM student
                WHERE reg_no LIKE ?
                ORDER BY reg_no
                """;

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, prefix + "%");

            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                list.add(rs.getString("reg_no"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public boolean attendanceAlreadyExists(int year, String courseId, String sessionId, String type, LocalDate date) {
        String sql = """
                SELECT COUNT(*)
                FROM attendance_group
                WHERE year_no = ? AND course_id = ? AND session_id = ? AND type = ? AND attendance_date = ?
                """;

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, year);
            pst.setString(2, courseId);
            pst.setString(3, sessionId);
            pst.setString(4, type);
            pst.setDate(5, Date.valueOf(date));

            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public List<AttendanceGroup> getAllAttendanceGroups() {
        List<AttendanceGroup> list = new ArrayList<>();

        String sql = """
                SELECT *
                FROM attendance_group
                ORDER BY attendance_date DESC, id DESC
                """;

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                AttendanceGroup group = new AttendanceGroup();
                group.setId(rs.getInt("id"));
                group.setYear(rs.getInt("year_no"));
                group.setCourseId(rs.getString("course_id"));
                group.setSessionId(rs.getString("session_id"));
                group.setType(rs.getString("type"));
                group.setAttendanceDate(rs.getDate("attendance_date").toLocalDate());

                list.add(group);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<AttendanceRecord> getAttendanceByGroupId(int groupId) {
        List<AttendanceRecord> list = new ArrayList<>();

        String sql = """
                SELECT *
                FROM attendance_record
                WHERE group_id = ?
                ORDER BY reg_no
                """;

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, groupId);

            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                AttendanceRecord record = new AttendanceRecord();
                record.setId(rs.getInt("id"));
                record.setGroupId(rs.getInt("group_id"));
                record.setRegNo(rs.getString("reg_no"));
                record.setStatus(rs.getString("status"));
                list.add(record);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public int countByStatus(int groupId, String status) {
        String sql = """
                SELECT COUNT(*)
                FROM attendance_record
                WHERE group_id = ? AND status = ?
                """;

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, groupId);
            pst.setString(2, status);

            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public Set<String> getExistingRegNos(List<AttendanceRecord> list) {
        Set<String> set = new HashSet<>();

        for (AttendanceRecord record : list) {
            if (record.getRegNo() != null) {
                set.add(record.getRegNo().trim().toUpperCase());
            }
        }

        return set;
    }
}