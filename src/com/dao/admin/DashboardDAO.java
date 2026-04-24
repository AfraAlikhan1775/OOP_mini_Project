package com.dao.admin;

import com.database.DatabaseInitializer;
import com.model.admin.Course;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class DashboardDAO {

    public int getLecturerCount() {
        String sql = "SELECT COUNT(*) FROM lecturer";
        return getCount(sql);
    }

    public int getTechOfficerCount() {
        String sql = "SELECT COUNT(*) FROM technical_officer";
        return getCount(sql);
    }

    public int getStudentCount() {
        String sql = "SELECT COUNT(*) FROM student";
        return getCount(sql);
    }

    public int getCourseCount() {
        String sql = "SELECT COUNT(*) FROM courses";
        return getCount(sql);
    }

    private int getCount(String sql) {
        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<String> getRecentUsers() {
        List<String> users = new ArrayList<>();

        String sql = """
                SELECT username, role
                FROM users
                ORDER BY user_id DESC
                LIMIT 5
                """;

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                String user = rs.getString("username") + " - " + rs.getString("role");
                users.add(user);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return users;
    }

    public List<String> getRecentNotices() {
        List<String> notices = new ArrayList<>();

        String sql = """
                SELECT title
                FROM notices
                ORDER BY id DESC
                LIMIT 5
                """;

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                notices.add(rs.getString("title"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return notices;
    }

    public List<Course> getRecentCourses() {
        List<Course> courses = new ArrayList<>();

        String sql = """
                SELECT course_id, course_name, coordinator, credits
                FROM courses
                ORDER BY user_id DESC
                LIMIT 5
                """;

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                Course course = new Course();
                course.setCourseId(rs.getString("course_id"));
                course.setCourseName(rs.getString("course_name"));
                course.setCoordinator(rs.getString("coordinator"));
                course.setCredits(rs.getInt("credits"));
                courses.add(course);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return courses;
    }
}