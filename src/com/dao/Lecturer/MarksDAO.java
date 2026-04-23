package com.dao.Lecturer;

import com.database.DatabaseInitializer;
import com.model.Lecturerr.MarksGroup;
import com.controller.Lecturer.MarkRow;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MarksDAO {

    public MarksDAO() {
        createTables();
    }

    private void createTables() {
        try (Connection conn = DatabaseInitializer.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS marks_group (
                    group_id INT AUTO_INCREMENT PRIMARY KEY,
                    course_id VARCHAR(50) NOT NULL,
                    year VARCHAR(20) NOT NULL,
                    semester VARCHAR(20) NOT NULL,
                    academic_year VARCHAR(20) NOT NULL,
                    exam_type VARCHAR(50) NOT NULL,
                    created_by VARCHAR(50) NOT NULL,
                    UNIQUE KEY uq_marks_group (course_id, year, semester, academic_year, exam_type)
                )
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS student_mark_entry (
                    group_id INT NOT NULL,
                    reg_no VARCHAR(50) NOT NULL,
                    raw_mark DOUBLE NOT NULL,
                    PRIMARY KEY (group_id, reg_no)
                )
            """);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int createGroupAndReturnId(MarksGroup g) {
        String sql = """
            INSERT INTO marks_group(course_id, year, semester, academic_year, exam_type, created_by)
            VALUES (?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pst.setString(1, g.getCourseId());
            pst.setString(2, g.getYear());
            pst.setString(3, g.getSemester());
            pst.setString(4, g.getAcademicYear());
            pst.setString(5, g.getExamType());
            pst.setString(6, g.getCreatedBy());

            int affected = pst.executeUpdate();

            if (affected > 0) {
                ResultSet rs = pst.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

            return -1;

        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("Duplicate marks group: " + e.getMessage());
            return -1;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public boolean addMark(int groupId, String regNo, double mark) {
        String sql = """
            INSERT INTO student_mark_entry(group_id, reg_no, raw_mark)
            VALUES (?, ?, ?)
            ON DUPLICATE KEY UPDATE raw_mark = VALUES(raw_mark)
        """;

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, groupId);
            pst.setString(2, regNo);
            pst.setDouble(3, mark);

            return pst.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<MarksGroup> getGroups(String lecturerId) {
        List<MarksGroup> list = new ArrayList<>();

        String sql = "SELECT * FROM marks_group WHERE created_by = ? ORDER BY group_id DESC";

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, lecturerId);

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                list.add(new MarksGroup(
                        rs.getInt("group_id"),
                        rs.getString("course_id"),
                        rs.getString("year"),
                        rs.getString("semester"),
                        rs.getString("academic_year"),
                        rs.getString("exam_type"),
                        rs.getString("created_by")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public void deleteGroup(int groupId) {
        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst1 = conn.prepareStatement("DELETE FROM student_mark_entry WHERE group_id = ?");
             PreparedStatement pst2 = conn.prepareStatement("DELETE FROM marks_group WHERE group_id = ?")) {

            pst1.setInt(1, groupId);
            pst1.executeUpdate();

            pst2.setInt(1, groupId);
            pst2.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean courseExists(String courseId) {
        String sql = "SELECT 1 FROM courses WHERE course_id = ?";

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, courseId);
            ResultSet rs = pst.executeQuery();
            return rs.next();

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean studentExists(String regNo) {
        String sql = "SELECT 1 FROM student WHERE reg_no = ?";

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, regNo);
            ResultSet rs = pst.executeQuery();
            return rs.next();

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isCourseCoordinator(String courseId, String lecturerId) {
        String sql = "SELECT 1 FROM courses WHERE course_id = ? AND coordinator = ?";

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, courseId);
            pst.setString(2, lecturerId);
            ResultSet rs = pst.executeQuery();
            return rs.next();

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<MarkRow> getMarksByGroup(int groupId) {
        List<MarkRow> list = new ArrayList<>();
        String sql = "SELECT reg_no, raw_mark FROM student_mark_entry WHERE group_id = ? ORDER BY reg_no";

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, groupId);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                list.add(new MarkRow(
                        rs.getString("reg_no"),
                        String.valueOf(rs.getDouble("raw_mark"))
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}