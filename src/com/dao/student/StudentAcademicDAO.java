package com.dao.student;

import com.database.DatabaseInitializer;
import com.model.Notice;
import com.model.student.StudentAttendanceDetail;
import com.model.student.StudentSubjectAttendance;
import com.model.student.StudentTimetableRow;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentAcademicDAO {

    public int getYearFromRegNo(String regNo) {
        if (regNo == null) return 1;

        if (regNo.startsWith("TG/2024/")) return 1;
        if (regNo.startsWith("TG/2023/")) return 2;
        if (regNo.startsWith("TG/2022/")) return 3;
        if (regNo.startsWith("TG/2021/")) return 4;

        return 1;
    }

    public int getCurrentSemester() {
        return 1;
    }

    public String getStudentDepartment(String regNo) {
        String sql = "SELECT department FROM student WHERE reg_no = ?";

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, regNo);

            ResultSet rs = pst.executeQuery();
            if (rs.next()) return rs.getString("department");

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    public List<StudentSubjectAttendance> getStudentSubjects(String regNo) {
        List<StudentSubjectAttendance> list = new ArrayList<>();

        int year = getYearFromRegNo(regNo);
        int semester = getCurrentSemester();

        String sql = """
                SELECT
                    c.course_id,
                    c.course_name,
                    ag.type,
                    COUNT(ar.id) AS total_count,
                    SUM(CASE WHEN ar.status = 'PRESENT' THEN 1 ELSE 0 END) AS present_count,
                    SUM(CASE WHEN ar.status = 'MEDICAL' THEN 1 ELSE 0 END) AS medical_count,
                    SUM(CASE WHEN ar.status = 'ABSENT' THEN 1 ELSE 0 END) AS absent_count
                FROM courses c
                INNER JOIN attendance_group ag
                    ON c.course_id = ag.course_id
                LEFT JOIN attendance_record ar
                    ON ag.id = ar.group_id
                    AND ar.reg_no = ?
                WHERE CAST(c.year AS CHAR) = ?
                AND CAST(c.semester AS CHAR) = ?
                AND ag.year_no = ?
                GROUP BY c.course_id, c.course_name, ag.type
                ORDER BY c.course_id, ag.type
                """;

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, regNo);
            pst.setString(2, String.valueOf(year));
            pst.setString(3, String.valueOf(semester));
            pst.setInt(4, year);

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                String courseId = rs.getString("course_id");
                String type = rs.getString("type");

                int rejectedMedical = getRejectedMedicalCount(conn, regNo, courseId, type);

                list.add(new StudentSubjectAttendance(
                        courseId,
                        rs.getString("course_name"),
                        type,
                        rs.getInt("total_count"),
                        rs.getInt("present_count"),
                        rs.getInt("medical_count"),
                        rejectedMedical,
                        rs.getInt("absent_count")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<StudentAttendanceDetail> getAttendanceDetails(String regNo, String courseId, String type) {
        List<StudentAttendanceDetail> list = new ArrayList<>();

        String sql = """
                SELECT
                    ar.id,
                    ag.attendance_date,
                    ag.session_id,
                    ar.status
                FROM attendance_record ar
                INNER JOIN attendance_group ag
                    ON ar.group_id = ag.id
                WHERE ar.reg_no = ?
                AND ag.course_id = ?
                AND ag.type = ?
                ORDER BY ag.attendance_date DESC, ar.id DESC
                """;

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, regNo);
            pst.setString(2, courseId);
            pst.setString(3, type);

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                list.add(new StudentAttendanceDetail(
                        rs.getInt("id"),
                        rs.getString("attendance_date"),
                        rs.getString("session_id"),
                        rs.getString("status")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    private int getRejectedMedicalCount(Connection conn, String regNo, String courseId, String type) {
        String sql = """
                SELECT COUNT(*)
                FROM medical m
                INNER JOIN medical_selected_session mss
                    ON m.medical_id = mss.medical_id
                WHERE m.reg_no = ?
                AND mss.course_id = ?
                AND mss.type = ?
                AND m.status = 'Rejected'
                """;

        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, regNo);
            pst.setString(2, courseId);
            pst.setString(3, type);

            ResultSet rs = pst.executeQuery();
            if (rs.next()) return rs.getInt(1);

        } catch (Exception ignored) {
        }

        return 0;
    }

    public List<Notice> getStudentNotices(String regNo) {
        List<Notice> list = new ArrayList<>();

        int year = getYearFromRegNo(regNo);
        String department = getStudentDepartment(regNo);

        String sql = """
                SELECT *
                FROM notices
                WHERE (role_target = 'All' OR role_target = 'Student')
                AND (batch_target = 'All' OR batch_target = ?)
                AND (department_target = 'All' OR department_target = ?)
                ORDER BY created_at DESC
                """;

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, String.valueOf(year));
            pst.setString(2, department);

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                Notice n = new Notice();
                n.setId(rs.getInt("id"));
                n.setTitle(rs.getString("title"));
                n.setDescription(rs.getString("description"));
                n.setPdfName(rs.getString("pdf_name"));
                n.setPdfData(rs.getBytes("pdf_data"));
                n.setRoleTarget(rs.getString("role_target"));
                n.setBatchTarget(rs.getString("batch_target"));
                n.setDepartmentTarget(rs.getString("department_target"));
                n.setCreatedAt(rs.getTimestamp("created_at"));
                list.add(n);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<StudentTimetableRow> getStudentTimetable(String regNo) {
        List<StudentTimetableRow> list = new ArrayList<>();

        int year = getYearFromRegNo(regNo);
        int semester = getCurrentSemester();
        String department = getStudentDepartment(regNo);

        String sql = """
                SELECT
                    ts.day_name,
                    ts.start_time,
                    ts.end_time,
                    ts.subject,
                    ts.lecturer,
                    ts.room,
                    ts.session_type
                FROM timetable_group tg
                INNER JOIN timetable_session ts
                    ON tg.id = ts.timetable_group_id
                WHERE tg.department = ?
                AND tg.level_no = ?
                AND tg.semester = ?
                ORDER BY
                    CASE ts.day_name
                        WHEN 'Monday' THEN 1
                        WHEN 'Tuesday' THEN 2
                        WHEN 'Wednesday' THEN 3
                        WHEN 'Thursday' THEN 4
                        WHEN 'Friday' THEN 5
                        ELSE 6
                    END,
                    ts.start_time
                """;

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, department);
            pst.setInt(2, year);
            pst.setInt(3, semester);

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                list.add(new StudentTimetableRow(
                        rs.getString("day_name"),
                        rs.getString("start_time") + " - " + rs.getString("end_time"),
                        rs.getString("subject"),
                        rs.getString("lecturer"),
                        rs.getString("room"),
                        rs.getString("session_type")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}