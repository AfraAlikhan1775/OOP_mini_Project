package com.dao.notes;

import com.database.DatabaseInitializer;
import com.model.notes.CourseNote;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CourseNoteDAO {

    public CourseNoteDAO() {
        createTableIfNotExists();
    }

    public void createTableIfNotExists() {
        String sql = """
                CREATE TABLE IF NOT EXISTS course_notes (
                    id INT PRIMARY KEY AUTO_INCREMENT,
                    course_id VARCHAR(50) NOT NULL,
                    lecturer_emp_id VARCHAR(100) NOT NULL,
                    title VARCHAR(150) NOT NULL,
                    note_text TEXT NOT NULL,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
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

    public boolean addNote(String courseId, String lecturerEmpId, String title, String noteText) {
        String sql = """
                INSERT INTO course_notes (course_id, lecturer_emp_id, title, note_text)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, courseId);
            pst.setString(2, lecturerEmpId);
            pst.setString(3, title);
            pst.setString(4, noteText);

            return pst.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteNote(int noteId, String lecturerEmpId) {
        String sql = "DELETE FROM course_notes WHERE id = ? AND lecturer_emp_id = ?";

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, noteId);
            pst.setString(2, lecturerEmpId);

            return pst.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<CourseNote> getNotesByCourse(String courseId) {
        List<CourseNote> notes = new ArrayList<>();

        String sql = """
                SELECT n.id, n.course_id, c.course_name, n.lecturer_emp_id,
                       n.title, n.note_text,
                       DATE_FORMAT(n.created_at, '%Y-%m-%d %H:%i') AS created_at
                FROM course_notes n
                JOIN courses c ON n.course_id = c.course_id
                WHERE n.course_id = ?
                ORDER BY n.created_at DESC
                """;

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, courseId);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                notes.add(mapNote(rs));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return notes;
    }

    public List<CourseNote> getRegisteredCoursesForStudent(String regNo) {
        List<CourseNote> courses = new ArrayList<>();

        String sql = """
                SELECT c.course_id, c.course_name, c.coordinator
                FROM course_registration cr
                JOIN courses c ON cr.course_id = c.course_id
                WHERE cr.reg_no = ?
                ORDER BY c.year, c.semester, c.course_name
                """;

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, regNo);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                CourseNote course = new CourseNote();
                course.setCourseId(rs.getString("course_id"));
                course.setCourseName(rs.getString("course_name"));
                course.setLecturerEmpId(rs.getString("coordinator"));
                courses.add(course);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return courses;
    }

    private CourseNote mapNote(ResultSet rs) throws SQLException {
        return new CourseNote(
                rs.getInt("id"),
                rs.getString("course_id"),
                rs.getString("course_name"),
                rs.getString("lecturer_emp_id"),
                rs.getString("title"),
                rs.getString("note_text"),
                rs.getString("created_at")
        );
    }
}