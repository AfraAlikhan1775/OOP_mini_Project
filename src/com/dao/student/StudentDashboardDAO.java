package com.dao.student;

import com.database.DatabaseInitializer;
import com.model.student.StudentDashboardData;
import com.model.student.TodayTimetableRow;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.DayOfWeek;
import java.time.LocalDate;
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

            data.setAttendancePercentage(calculateAttendance(conn, data.getRegNo()));
            data.setCourseCount(String.valueOf(countRegisteredCourses(conn, data.getRegNo())));
            data.setMedicalCount(String.valueOf(countMedicalRecords(conn, data.getRegNo())));

            data.getNotices().addAll(loadRecentNotices(conn, data.getDepartment(), data.getYear()));
            data.getTodayRows().addAll(loadTodayTimetable(conn, data.getDepartment(), data.getYear()));

        } catch (Exception e) {
            e.printStackTrace();
        }

        return data;
    }

    private void loadStudentBasicData(Connection conn, String username, StudentDashboardData data) {
        String sql = """
                SELECT
                    reg_no,
                    first_name,
                    last_name,
                    email,
                    department,
                    degrea,
                    year_no,
                    mentor_id
                FROM student
                WHERE reg_no = ?
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
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String calculateAttendance(Connection conn, String regNo) {
        String sql = """
                SELECT
                    SUM(CASE WHEN status IN ('PRESENT', 'MEDICAL') THEN 1 ELSE 0 END) AS present_count,
                    COUNT(*) AS total_count
                FROM attendance_record
                WHERE reg_no = ?
                """;

        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, regNo);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    int present = rs.getInt("present_count");
                    int total = rs.getInt("total_count");

                    if (total == 0) {
                        return "0%";
                    }

                    double percentage = (present * 100.0) / total;
                    return String.format("%.0f%%", percentage);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "0%";
    }

    private int countRegisteredCourses(Connection conn, String regNo) {
        String sql = "SELECT COUNT(*) FROM course_registration WHERE reg_no = ?";

        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, regNo);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    private int countMedicalRecords(Connection conn, String regNo) {
        String sql = "SELECT COUNT(*) FROM medical WHERE student_id = ?";

        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, regNo);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
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
                LIMIT 4
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

    private List<TodayTimetableRow> loadTodayTimetable(Connection conn, String department, String year) {
        List<TodayTimetableRow> rows = new ArrayList<>();

        String dayName = convertDay(LocalDate.now().getDayOfWeek());

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
                  AND ts.day_name = ?
                ORDER BY ts.start_time
                """;

        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, department);
            pst.setString(2, year);
            pst.setString(3, dayName);

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    String start = safe(rs.getString("start_time"));
                    String end = safe(rs.getString("end_time"));
                    String time = start + " - " + end;

                    rows.add(new TodayTimetableRow(
                            time,
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

    private String convertDay(DayOfWeek dayOfWeek) {
        return switch (dayOfWeek) {
            case MONDAY -> "Monday";
            case TUESDAY -> "Tuesday";
            case WEDNESDAY -> "Wednesday";
            case THURSDAY -> "Thursday";
            case FRIDAY -> "Friday";
            case SATURDAY -> "Saturday";
            case SUNDAY -> "Sunday";
        };
    }

    private String safe(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }
}