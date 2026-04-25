package com.dao.student;

import com.database.DatabaseInitializer;
import com.model.student.StudentDashboardData;
import com.model.student.TodayTimetableRow;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class StudentDashboardDAO {

    public StudentDashboardData getDashboardData(String username) {
        StudentDashboardData data = new StudentDashboardData();

        try (Connection conn = DatabaseInitializer.getConnection()) {

            loadStudentBasicData(conn, username, data);

            if (data.getRegNo() == null || data.getRegNo().equals("-")) {
                return data;
            }

            loadMentorData(conn, data.getMentorId(), data);
            data.getNotices().addAll(loadRecentNotices(conn, data.getDepartment(), data.getYear()));

            // User asked: Today timetable as Wednesday
            data.getTodayRows().addAll(loadWednesdayTimetable(conn, data.getDepartment(), data.getYear()));

        } catch (Exception e) {
            e.printStackTrace();
        }

        return data;
    }

    private void loadStudentBasicData(Connection conn, String username, StudentDashboardData data) {
        String sql = """
                SELECT
                    s.reg_no,
                    s.first_name,
                    s.last_name,
                    s.email,
                    s.department,
                    s.degrea,
                    s.year_no,
                    s.mentor_id,
                    s.image_path,
                    u.profile_pic
                FROM student s
                LEFT JOIN users u ON s.reg_no = u.username
                WHERE s.reg_no = ?
                LIMIT 1
                """;

        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, username);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    String firstName = safe(rs.getString("first_name"));
                    String lastName = safe(rs.getString("last_name"));

                    data.setRegNo(safe(rs.getString("reg_no")));
                    data.setFullName((firstName + " " + lastName).trim());
                    data.setEmail(safe(rs.getString("email")));
                    data.setDepartment(safe(rs.getString("department")));
                    data.setCourse(safe(rs.getString("degrea")));
                    data.setYear(safe(rs.getString("year_no")));
                    data.setMentorId(safe(rs.getString("mentor_id")));

                    String userProfilePic = safe(rs.getString("profile_pic"));
                    String studentImagePath = safe(rs.getString("image_path"));

                    if (!userProfilePic.equals("-")) {
                        data.setStudentProfilePic(userProfilePic);
                    } else {
                        data.setStudentProfilePic(studentImagePath);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadMentorData(Connection conn, String mentorId, StudentDashboardData data) {
        if (mentorId == null || mentorId.isBlank() || mentorId.equals("-")) {
            return;
        }

        String sql = """
                SELECT
                    emp_id,
                    first_name,
                    last_name,
                    email,
                    contact_number,
                    department,
                    reg_pic
                FROM lecturer
                WHERE emp_id = ?
                LIMIT 1
                """;

        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, mentorId);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    String firstName = safe(rs.getString("first_name"));
                    String lastName = safe(rs.getString("last_name"));

                    data.setMentorName((firstName + " " + lastName).trim());
                    data.setMentorEmail(safe(rs.getString("email")));
                    data.setMentorPhone(safe(rs.getString("contact_number")));
                    data.setMentorDepartment(safe(rs.getString("department")));
                    data.setMentorPhoto(safe(rs.getString("reg_pic")));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<String> loadRecentNotices(Connection conn, String department, String year) {
        List<String> notices = new ArrayList<>();

        String sql = """
                SELECT title
                FROM notices
                WHERE (role_target = 'Student' OR role_target = 'All' OR role_target IS NULL OR role_target = '')
                  AND (department_target = ? OR department_target = 'All' OR department_target IS NULL OR department_target = '')
                  AND (batch_target = ? OR batch_target = 'All' OR batch_target IS NULL OR batch_target = '')
                ORDER BY created_at DESC
                LIMIT 5
                """;

        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, department);
            pst.setString(2, year);

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    notices.add("• " + safe(rs.getString("title")));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (notices.isEmpty()) {
            notices.add("• No notices available");
        }

        return notices;
    }

    private List<TodayTimetableRow> loadWednesdayTimetable(Connection conn, String department, String year) {
        List<TodayTimetableRow> rows = new ArrayList<>();

        String sql = """
                SELECT
                    ts.start_time,
                    ts.end_time,
                    ts.subject,
                    ts.lecturer,
                    ts.room,
                    ts.session_type
                FROM timetable_group tg
                INNER JOIN timetable_session ts ON tg.id = ts.timetable_group_id
                WHERE tg.department = ?
                  AND CAST(tg.level_no AS CHAR) = ?
                  AND ts.day_name = 'Wednesday'
                ORDER BY ts.start_time
                """;

        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, department);
            pst.setString(2, year);

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    String start = safe(rs.getString("start_time"));
                    String end = safe(rs.getString("end_time"));

                    rows.add(new TodayTimetableRow(
                            start + " - " + end,
                            safe(rs.getString("subject")),
                            safe(rs.getString("lecturer")),
                            safe(rs.getString("room")),
                            safe(rs.getString("session_type"))
                    ));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return rows;
    }

    private String safe(String value) {
        return value == null || value.isBlank() ? "-" : value.trim();
    }
}