package com.dao.admin;

import com.database.DatabaseInitializer;
import com.model.admin.Course;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CourseDAO {

    public CourseDAO() {
        createTableIfNotExists();
    }

    public void createTableIfNotExists() {
        String sql = """
                CREATE TABLE IF NOT EXISTS courses (
                    id INT PRIMARY KEY AUTO_INCREMENT,
                    department VARCHAR(50) NOT NULL,
                    year VARCHAR(20) NOT NULL,
                    semester VARCHAR(20) NOT NULL,
                    course_id VARCHAR(50) NOT NULL UNIQUE,
                    course_name VARCHAR(150) NOT NULL,
                    coordinator VARCHAR(100) NOT NULL,
                    credits INT NOT NULL,
                    image_path VARCHAR(255),
                    status VARCHAR(30) DEFAULT 'Active'
                )
                """;

        try (Connection conn = DatabaseInitializer.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean saveCourse(Course course) {
        String sql = """
                INSERT INTO courses
                (department, year, semester, course_id, course_name, coordinator, credits, image_path, status)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, course.getDepartment());
            pst.setString(2, course.getYear());
            pst.setString(3, course.getSemester());
            pst.setString(4, course.getCourseId());
            pst.setString(5, course.getCourseName());
            pst.setString(6, course.getCoordinator());
            pst.setInt(7, course.getCredits());
            pst.setString(8, course.getImagePath());
            pst.setString(9, course.getStatus());

            return pst.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean existsByCourseId(String courseId) {
        String sql = "SELECT 1 FROM courses WHERE course_id = ?";

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, courseId.trim());
            ResultSet rs = pst.executeQuery();
            return rs.next();

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isValidLecturer(String employeeId) {
        String sql = "SELECT 1 FROM lecturer WHERE emp_id = ?";

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, employeeId.trim());
            ResultSet rs = pst.executeQuery();
            return rs.next();

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Course> getAllCourses() {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT * FROM courses ORDER BY department, year, semester, course_name";

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

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

        } catch (Exception e) {
            e.printStackTrace();
        }

        return courses;
    }

    public boolean deleteByCourseId(String courseId) {
        String sql = "DELETE FROM courses WHERE course_id = ?";

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, courseId);
            return pst.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Course> searchCourses(String keyword) {
        List<Course> courses = new ArrayList<>();
        String sql = """
                SELECT * FROM courses
                WHERE course_id LIKE ? OR course_name LIKE ? OR coordinator LIKE ?
                ORDER BY course_name
                """;

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            String like = "%" + keyword + "%";
            pst.setString(1, like);
            pst.setString(2, like);
            pst.setString(3, like);

            ResultSet rs = pst.executeQuery();

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

        } catch (Exception e) {
            e.printStackTrace();
        }

        return courses;
    }

    public List<Course> filterByDepartment(String department) {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT * FROM courses WHERE department = ? ORDER BY course_name";

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, department);
            ResultSet rs = pst.executeQuery();

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

        } catch (Exception e) {
            e.printStackTrace();
        }

        return courses;
    }
}