package com.dao.student;

import com.database.DatabaseInitializer;
import com.model.admin.Course;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentCourseDAO {

    public String getStudentDepartment(String regNo) {
        String sql = "SELECT department FROM student WHERE reg_no = ?";

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, regNo);

            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getString("department");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    public int getStudentYearFromRegNo(String regNo) {
        if (regNo == null) return 0;

        if (regNo.startsWith("TG/2024/")) return 1;
        if (regNo.startsWith("TG/2023/")) return 2;
        if (regNo.startsWith("TG/2022/")) return 3;
        if (regNo.startsWith("TG/2021/")) return 4;

        return 0;
    }

    public List<Course> getCoursesForStudent(String regNo) {
        List<Course> courses = new ArrayList<>();

        String department = getStudentDepartment(regNo);
        int year = getStudentYearFromRegNo(regNo);

        if (department.isBlank() || year == 0) {
            return courses;
        }

        String sql = """
                SELECT id, department, year, semester, course_id, course_name,
                       coordinator, credits, image_path, status
                FROM courses
                WHERE department = ?
                  AND CAST(year AS CHAR) = ?
                  AND (status IS NULL OR status = 'Active')
                ORDER BY semester, course_id
                """;

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, department);
            pst.setString(2, String.valueOf(year));

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                Course c = new Course();
                c.setId(rs.getInt("id"));
                c.setDepartment(rs.getString("department"));
                c.setYear(rs.getString("year"));
                c.setSemester(rs.getString("semester"));
                c.setCourseId(rs.getString("course_id"));
                c.setCourseName(rs.getString("course_name"));
                c.setCoordinator(rs.getString("coordinator"));
                c.setCredits(rs.getInt("credits"));
                c.setImagePath(rs.getString("image_path"));
                c.setStatus(rs.getString("status"));

                courses.add(c);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return courses;
    }
}