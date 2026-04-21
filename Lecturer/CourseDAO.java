package com.dao.lecturer;

import com.database.DatabaseInitializer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CourseDAO {

    public CourseDAO() {
        createTables();
    }

    public void createTables() {
        String courseTable = """
                CREATE TABLE IF NOT EXISTS course (
                    id INT PRIMARY KEY AUTO_INCREMENT,
                    course_code VARCHAR(20) UNIQUE NOT NULL,
                    course_name VARCHAR(100) NOT NULL,
                    credits INT NOT NULL,
                    department VARCHAR(50) NOT NULL,
                    level_no INT NOT NULL,
                    semester INT NOT NULL,
                    session_type ENUM('Theory', 'Practical', 'Both') NOT NULL,
                    lecturer_id INT,
                    FOREIGN KEY (lecturer_id) REFERENCES lecturer(id) ON DELETE SET NULL
                )
                """;

        String materialTable = """
                CREATE TABLE IF NOT EXISTS course_material (
                    id INT PRIMARY KEY AUTO_INCREMENT,
                    course_id INT NOT NULL,
                    title VARCHAR(100) NOT NULL,
                    description TEXT,
                    file_path VARCHAR(255),
                    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (course_id) REFERENCES course(id) ON DELETE CASCADE
                )
                """;

        try (Connection conn = DatabaseInitializer.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(courseTable);
            stmt.execute(materialTable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Get all courses assigned to a specific lecturer
    public List<CourseSummary> getCoursesByLecturerId(int lecturerId) {
        String sql = "SELECT * FROM course WHERE lecturer_id = ? ORDER BY level_no, semester";
        List<CourseSummary> list = new ArrayList<>();
        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, lecturerId);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) list.add(mapCourse(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // Add a material to a course
    public boolean addMaterial(int courseId, String title, String description, String filePath) {
        String sql = "INSERT INTO course_material (course_id, title, description, file_path) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, courseId);
            pst.setString(2, title);
            pst.setString(3, description);
            pst.setString(4, filePath);
            pst.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Get all materials for a course
    public List<MaterialSummary> getMaterialsByCourseId(int courseId) {
        String sql = "SELECT * FROM course_material WHERE course_id = ? ORDER BY uploaded_at DESC";
        List<MaterialSummary> list = new ArrayList<>();
        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, courseId);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    MaterialSummary m = new MaterialSummary();
                    m.id = rs.getInt("id");
                    m.courseId = rs.getInt("course_id");
                    m.title = rs.getString("title");
                    m.description = rs.getString("description");
                    m.filePath = rs.getString("file_path");
                    m.uploadedAt = rs.getString("uploaded_at");
                    list.add(m);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    private CourseSummary mapCourse(ResultSet rs) throws SQLException {
        CourseSummary c = new CourseSummary();
        c.id = rs.getInt("id");
        c.courseCode = rs.getString("course_code");
        c.courseName = rs.getString("course_name");
        c.credits = rs.getInt("credits");
        c.department = rs.getString("department");
        c.levelNo = rs.getInt("level_no");
        c.semester = rs.getInt("semester");
        c.sessionType = rs.getString("session_type");
        return c;
    }

    public static class CourseSummary {
        public int id, credits, levelNo, semester;
        public String courseCode, courseName, department, sessionType;
    }

    public static class MaterialSummary {
        public int id, courseId;
        public String title, description, filePath, uploadedAt;
    }
}