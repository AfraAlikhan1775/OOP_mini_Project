package com.dao.Lecturer;

import com.database.DatabaseInitializer;
import com.model.admin.AttendanceGroup;
import com.model.admin.AttendanceRecord;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LecturerAttendanceDAO {

    public List<String> getLecturerCourseIds(String lecturerEmpId) {
        List<String> list = new ArrayList<>();

        String sql = """
                SELECT course_id
                FROM courses
                WHERE coordinator = ?
                ORDER BY course_id
                """;

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, lecturerEmpId);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(rs.getString("course_id"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<String> getTypesByCourse(String courseId) {
        List<String> list = new ArrayList<>();

        String sql = """
                SELECT DISTINCT type
                FROM attendance_group
                WHERE course_id = ?
                ORDER BY type
                """;

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, courseId);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(rs.getString("type"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<String> getSessionIdsByCourseAndType(String courseId, String type) {
        List<String> list = new ArrayList<>();

        String sql = """
                SELECT DISTINCT session_id
                FROM attendance_group
                WHERE course_id = ?
                  AND type = ?
                ORDER BY session_id
                """;

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, courseId);
            ps.setString(2, type);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(rs.getString("session_id"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<AttendanceGroup> getAttendanceGroups(
            String lecturerEmpId,
            String courseId,
            String type,
            String sessionId,
            LocalDate fromDate,
            LocalDate toDate
    ) {
        List<AttendanceGroup> list = new ArrayList<>();

        StringBuilder sql = new StringBuilder("""
                SELECT ag.*
                FROM attendance_group ag
                JOIN courses c ON ag.course_id = c.course_id
                WHERE c.coordinator = ?
                """);

        List<Object> params = new ArrayList<>();
        params.add(lecturerEmpId);

        if (courseId != null && !courseId.isBlank()) {
            sql.append(" AND ag.course_id = ? ");
            params.add(courseId);
        }

        if (type != null && !type.isBlank()) {
            sql.append(" AND ag.type = ? ");
            params.add(type);
        }

        if (sessionId != null && !sessionId.isBlank()) {
            sql.append(" AND ag.session_id = ? ");
            params.add(sessionId);
        }

        if (fromDate != null) {
            sql.append(" AND ag.attendance_date >= ? ");
            params.add(Date.valueOf(fromDate));
        }

        if (toDate != null) {
            sql.append(" AND ag.attendance_date <= ? ");
            params.add(Date.valueOf(toDate));
        }

        sql.append(" ORDER BY ag.attendance_date DESC, ag.id DESC ");

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                AttendanceGroup g = new AttendanceGroup();
                g.setId(rs.getInt("id"));
                g.setYear(rs.getInt("year_no"));
                g.setCourseId(rs.getString("course_id"));
                g.setSessionId(rs.getString("session_id"));
                g.setType(rs.getString("type"));
                g.setAttendanceDate(rs.getDate("attendance_date").toLocalDate());
                list.add(g);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<AttendanceRecord> getAttendanceRecords(int groupId) {
        List<AttendanceRecord> list = new ArrayList<>();

        String sql = """
                SELECT *
                FROM attendance_record
                WHERE group_id = ?
                ORDER BY reg_no
                """;

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, groupId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                AttendanceRecord r = new AttendanceRecord();
                r.setId(rs.getInt("id"));
                r.setGroupId(rs.getInt("group_id"));
                r.setRegNo(rs.getString("reg_no"));
                r.setStatus(rs.getString("status"));
                list.add(r);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public AttendanceSummary getSummary(int groupId) {
        int present = countStatus(groupId, "PRESENT");
        int absent = countStatus(groupId, "ABSENT");
        int medical = countStatus(groupId, "MEDICAL");

        return new AttendanceSummary(present, absent, medical);
    }

    private int countStatus(int groupId, String status) {
        String sql = """
                SELECT COUNT(*)
                FROM attendance_record
                WHERE group_id = ?
                  AND status = ?
                """;

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, groupId);
            ps.setString(2, status);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public static class AttendanceSummary {
        private final int present;
        private final int absent;
        private final int medical;

        public AttendanceSummary(int present, int absent, int medical) {
            this.present = present;
            this.absent = absent;
            this.medical = medical;
        }

        public int getPresent() {
            return present;
        }

        public int getAbsent() {
            return absent;
        }

        public int getMedical() {
            return medical;
        }

        public int getTotal() {
            return present + absent + medical;
        }

        public double getPercentage() {
            int total = getTotal();

            if (total == 0) {
                return 0;
            }

            return ((present + medical) * 100.0) / total;
        }
    }
}