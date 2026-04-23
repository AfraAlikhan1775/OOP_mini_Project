package com.dao;

import com.database.DatabaseInitializer;
import com.model.CourseRegistration;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CourseRegistrationDAO {

    public CourseRegistrationDAO() {
        createTableIfNotExists();
    }

    public void createTableIfNotExists() {
        String sql = """
                CREATE TABLE IF NOT EXISTS course_registration (
                    reg_no VARCHAR(50) NOT NULL,
                    course_id VARCHAR(50) NOT NULL,
                    semester VARCHAR(20) NOT NULL,
                    academic_year VARCHAR(20) NOT NULL,
                    registered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    PRIMARY KEY (reg_no, course_id, semester, academic_year),
                    FOREIGN KEY (reg_no) REFERENCES student(reg_no)
                        ON UPDATE CASCADE
                        ON DELETE CASCADE,
                    FOREIGN KEY (course_id) REFERENCES courses(course_id)
                        ON UPDATE CASCADE
                        ON DELETE CASCADE
                )
                """;

        try (Connection conn = DatabaseInitializer.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean registerCourse(CourseRegistration registration) {
        String sql = """
                INSERT INTO course_registration (reg_no, course_id, semester, academic_year)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, registration.getRegNo());
            pst.setString(2, registration.getCourseId());
            pst.setString(3, registration.getSemester());
            pst.setString(4, registration.getAcademicYear());

            return pst.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean unregisterCourse(String regNo, String courseId, String semester, String academicYear) {
        String sql = """
                DELETE FROM course_registration
                WHERE reg_no = ? AND course_id = ? AND semester = ? AND academic_year = ?
                """;

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, regNo);
            pst.setString(2, courseId);
            pst.setString(3, semester);
            pst.setString(4, academicYear);

            return pst.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<String[]> getRegisteredStudentsByCourse(String courseId, String semester, String academicYear) {
        List<String[]> students = new ArrayList<>();

        String sql = """
                SELECT s.reg_no, CONCAT(s.first_name, ' ', s.last_name) AS full_name
                FROM course_registration cr
                JOIN student s ON cr.reg_no = s.reg_no
                WHERE cr.course_id = ? AND cr.semester = ? AND cr.academic_year = ?
                ORDER BY s.reg_no
                """;

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, courseId);
            pst.setString(2, semester);
            pst.setString(3, academicYear);

            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                students.add(new String[]{
                        rs.getString("reg_no"),
                        rs.getString("full_name")
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return students;
    }

    public List<String[]> getAvailableCoursesForStudent(String regNo, String department, String semester, String year) {
        List<String[]> list = new ArrayList<>();

        String sql = """
                SELECT course_id, course_name
                FROM courses
                WHERE department = ? AND semester = ? AND year = ?
                ORDER BY course_name
                """;

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, department);
            pst.setString(2, semester);
            pst.setString(3, year);

            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                list.add(new String[]{
                        rs.getString("course_id"),
                        rs.getString("course_name")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<String[]> getRegisteredCoursesForStudent(String regNo, String semester, String academicYear) {
        List<String[]> list = new ArrayList<>();

        String sql = """
                SELECT c.course_id, c.course_name
                FROM course_registration cr
                JOIN courses c ON cr.course_id = c.course_id
                WHERE cr.reg_no = ? AND cr.semester = ? AND cr.academic_year = ?
                ORDER BY c.course_name
                """;

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, regNo);
            pst.setString(2, semester);
            pst.setString(3, academicYear);

            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                list.add(new String[]{
                        rs.getString("course_id"),
                        rs.getString("course_name")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}