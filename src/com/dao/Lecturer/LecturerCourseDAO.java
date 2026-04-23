package com.dao.Lecturer;

import com.database.DatabaseInitializer;
import com.model.admin.Course;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class LecturerCourseDAO {

    public List<Course> getCoursesByCoordinator(String empId) {
        List<Course> courses = new ArrayList<>();

        String sql = """
                SELECT *
                FROM courses
                WHERE coordinator = ?
                ORDER BY year, semester, course_name
                """;

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            System.out.println("Searching courses for coordinator = " + empId);

            pst.setString(1, empId);

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    Course course = new Course(
                            rs.getString("department"),
                            rs.getString("year"),
                            rs.getString("semester"),
                            rs.getString("course_id"),
                            rs.getString("course_name"),
                            rs.getString("coordinator"),
                            rs.getInt("credits"),
                            rs.getString("image_path"),
                            rs.getString("status")
                    );
                    course.setId(rs.getInt("id"));
                    courses.add(course);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return courses;
    }
}