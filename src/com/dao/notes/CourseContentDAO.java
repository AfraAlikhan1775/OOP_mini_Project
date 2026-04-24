package com.dao.notes;

import com.database.DatabaseInitializer;
import com.model.admin.Course;
import com.model.notes.CourseAnnouncement;
import com.model.notes.CourseMaterial;

import java.io.File;
import java.io.FileInputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CourseContentDAO {

    public CourseContentDAO() {
        createTables();
    }

    private void createTables() {
        String materials = """
            CREATE TABLE IF NOT EXISTS course_materials (
                id INT PRIMARY KEY AUTO_INCREMENT,
                course_id VARCHAR(50) NOT NULL,
                week_no INT NOT NULL,
                title VARCHAR(150) NOT NULL,
                file_name VARCHAR(255) NOT NULL,
                pdf_file LONGBLOB NOT NULL,
                uploaded_by VARCHAR(100) NOT NULL,
                uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        """;

        String announcements = """
            CREATE TABLE IF NOT EXISTS course_announcements (
                id INT PRIMARY KEY AUTO_INCREMENT,
                course_id VARCHAR(50) NOT NULL,
                week_no INT NOT NULL,
                announcement_text TEXT NOT NULL,
                created_by VARCHAR(100) NOT NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        """;

        try (Connection con = DatabaseInitializer.getConnection();
             Statement st = con.createStatement()) {
            st.execute(materials);
            st.execute(announcements);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean uploadPdf(String courseId, int weekNo, String title, File pdfFile, String lecturerId) {
        String sql = """
            INSERT INTO course_materials
            (course_id, week_no, title, file_name, pdf_file, uploaded_by)
            VALUES (?, ?, ?, ?, ?, ?)
        """;

        try (Connection con = DatabaseInitializer.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             FileInputStream fis = new FileInputStream(pdfFile)) {

            ps.setString(1, courseId);
            ps.setInt(2, weekNo);
            ps.setString(3, title);
            ps.setString(4, pdfFile.getName());
            ps.setBinaryStream(5, fis, pdfFile.length());
            ps.setString(6, lecturerId);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean addAnnouncement(String courseId, int weekNo, String text, String lecturerId) {
        String sql = """
            INSERT INTO course_announcements
            (course_id, week_no, announcement_text, created_by)
            VALUES (?, ?, ?, ?)
        """;

        try (Connection con = DatabaseInitializer.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, courseId);
            ps.setInt(2, weekNo);
            ps.setString(3, text);
            ps.setString(4, lecturerId);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<CourseMaterial> getMaterials(String courseId, int weekNo) {
        List<CourseMaterial> list = new ArrayList<>();

        String sql = """
            SELECT id, course_id, week_no, title, file_name, uploaded_by,
                   DATE_FORMAT(uploaded_at, '%Y-%m-%d %H:%i') AS uploaded_at
            FROM course_materials
            WHERE course_id = ? AND week_no = ?
            ORDER BY uploaded_at DESC
        """;

        try (Connection con = DatabaseInitializer.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, courseId);
            ps.setInt(2, weekNo);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new CourseMaterial(
                        rs.getInt("id"),
                        rs.getString("course_id"),
                        rs.getInt("week_no"),
                        rs.getString("title"),
                        rs.getString("file_name"),
                        rs.getString("uploaded_by"),
                        rs.getString("uploaded_at")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<CourseAnnouncement> getAnnouncements(String courseId, int weekNo) {
        List<CourseAnnouncement> list = new ArrayList<>();

        String sql = """
            SELECT id, course_id, week_no, announcement_text, created_by,
                   DATE_FORMAT(created_at, '%Y-%m-%d %H:%i') AS created_at
            FROM course_announcements
            WHERE course_id = ? AND week_no = ?
            ORDER BY created_at DESC
        """;

        try (Connection con = DatabaseInitializer.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, courseId);
            ps.setInt(2, weekNo);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new CourseAnnouncement(
                        rs.getInt("id"),
                        rs.getString("course_id"),
                        rs.getInt("week_no"),
                        rs.getString("announcement_text"),
                        rs.getString("created_by"),
                        rs.getString("created_at")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public byte[] getPdfFile(int materialId) {
        String sql = "SELECT pdf_file FROM course_materials WHERE id = ?";

        try (Connection con = DatabaseInitializer.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, materialId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getBytes("pdf_file");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public String getPdfFileName(int materialId) {
        String sql = "SELECT file_name FROM course_materials WHERE id = ?";

        try (Connection con = DatabaseInitializer.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, materialId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getString("file_name");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "lecture_note.pdf";
    }

    public boolean deleteMaterial(int id, String lecturerId) {
        String sql = "DELETE FROM course_materials WHERE id = ? AND uploaded_by = ?";

        try (Connection con = DatabaseInitializer.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.setString(2, lecturerId);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteAnnouncement(int id, String lecturerId) {
        String sql = "DELETE FROM course_announcements WHERE id = ? AND created_by = ?";

        try (Connection con = DatabaseInitializer.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.setString(2, lecturerId);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Course> getStudentRegisteredCourses(String regNo) {
        List<Course> courses = new ArrayList<>();

        String sql = """
                SELECT c.id, c.department, c.year, c.semester, c.course_id, c.course_name,
                       c.coordinator, c.credits, c.image_path, c.status
                FROM course_registration cr
                JOIN courses c ON cr.course_id = c.course_id
                WHERE cr.reg_no = ?
                ORDER BY c.year, c.semester, c.course_name
                """;

        try (Connection con = DatabaseInitializer.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, regNo);

            ResultSet rs = ps.executeQuery();

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