package com.dao.Lecturer;

import com.controller.Lecturer.MarkRow;
import com.database.DatabaseInitializer;
import com.model.Lecturerr.MarksGroup;

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

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS student_marks (
                    course_id VARCHAR(50) NOT NULL,
                    reg_no VARCHAR(50) NOT NULL,
                    quiz1 DECIMAL(5,2) DEFAULT 0,
                    quiz2 DECIMAL(5,2) DEFAULT 0,
                    quiz3 DECIMAL(5,2) DEFAULT 0,
                    assignment_mark DECIMAL(5,2) DEFAULT 0,
                    mid_exam DECIMAL(5,2) DEFAULT 0,
                    final_theory DECIMAL(5,2) DEFAULT 0,
                    final_practical DECIMAL(5,2) DEFAULT 0,
                    ca_mark DECIMAL(6,2) DEFAULT 0,
                    final_mark DECIMAL(6,2) DEFAULT 0,
                    PRIMARY KEY (course_id, reg_no)
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
                if (rs.next()) return rs.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }

    public boolean addMark(int groupId, String regNo, double mark) {

        String insertEntrySql = """
        INSERT INTO student_mark_entry(group_id, reg_no, raw_mark)
        VALUES (?, ?, ?)
        ON DUPLICATE KEY UPDATE raw_mark = VALUES(raw_mark)
    """;

        String getGroupSql = """
        SELECT course_id, exam_type
        FROM marks_group
        WHERE group_id = ?
    """;

        try (Connection conn = DatabaseInitializer.getConnection()) {

            conn.setAutoCommit(false);

            String courseId = "";
            String examType = "";

            // 🔹 get course + exam type
            try (PreparedStatement ps = conn.prepareStatement(getGroupSql)) {
                ps.setInt(1, groupId);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    courseId = rs.getString("course_id");
                    examType = rs.getString("exam_type");
                } else {
                    conn.rollback();
                    return false;
                }
            }

            // 🔹 insert into student_mark_entry
            try (PreparedStatement ps = conn.prepareStatement(insertEntrySql)) {
                ps.setInt(1, groupId);
                ps.setString(2, regNo);
                ps.setDouble(3, mark);
                ps.executeUpdate();
            }

            // 🔥 FORCE INSERT INTO student_marks (THIS WAS MISSING)
            insertOrUpdateStudentMarks(conn, courseId, regNo, examType, mark);

            conn.commit();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    private void upsertStudentMarks(Connection conn,
                                    String courseId,
                                    String regNo,
                                    String examType,
                                    double mark) throws SQLException {

        String column = getColumnName(examType);

        if (column == null) {
            throw new SQLException("Invalid exam type: " + examType);
        }

        String sql = """
            INSERT INTO student_marks (
                course_id, reg_no, quiz1, quiz2, quiz3,
                assignment_mark, mid_exam, final_theory, final_practical,
                ca_mark, final_mark
            )
            VALUES (?, ?, 0, 0, 0, 0, 0, 0, 0, 0, 0)
            ON DUPLICATE KEY UPDATE reg_no = VALUES(reg_no)
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, courseId);
            ps.setString(2, regNo);
            ps.executeUpdate();
        }

        String updateSql = "UPDATE student_marks SET " + column + " = ? WHERE course_id = ? AND reg_no = ?";

        try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
            ps.setDouble(1, mark);
            ps.setString(2, courseId);
            ps.setString(3, regNo);
            ps.executeUpdate();
        }
    }

    private void recalculateStudentMarks(Connection conn,
                                         String courseId,
                                         String regNo) throws SQLException {

        String selectSql = """
            SELECT quiz1, quiz2, quiz3, assignment_mark, mid_exam,
                   final_theory, final_practical
            FROM student_marks
            WHERE course_id = ? AND reg_no = ?
        """;

        double quiz1 = 0;
        double quiz2 = 0;
        double quiz3 = 0;
        double assignment = 0;
        double mid = 0;
        double theory = 0;
        double practical = 0;

        try (PreparedStatement ps = conn.prepareStatement(selectSql)) {
            ps.setString(1, courseId);
            ps.setString(2, regNo);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                quiz1 = rs.getDouble("quiz1");
                quiz2 = rs.getDouble("quiz2");
                quiz3 = rs.getDouble("quiz3");
                assignment = rs.getDouble("assignment_mark");
                mid = rs.getDouble("mid_exam");
                theory = rs.getDouble("final_theory");
                practical = rs.getDouble("final_practical");
            }
        }

        double quizAverage = (quiz1 + quiz2 + quiz3) / 3.0;

        double caMark =
                quizAverage * 0.10 +
                        assignment * 0.10 +
                        mid * 0.20;

        boolean hasTheory = theory > 0;
        boolean hasPractical = practical > 0;

        double finalPart;

        if (hasTheory && hasPractical) {
            finalPart = theory * 0.30 + practical * 0.30;
        } else if (hasTheory) {
            finalPart = theory * 0.60;
        } else if (hasPractical) {
            finalPart = practical * 0.60;
        } else {
            finalPart = 0;
        }

        double finalMark = caMark + finalPart;

        String updateSql = """
            UPDATE student_marks
            SET ca_mark = ?, final_mark = ?
            WHERE course_id = ? AND reg_no = ?
        """;

        try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
            ps.setDouble(1, caMark);
            ps.setDouble(2, finalMark);
            ps.setString(3, courseId);
            ps.setString(4, regNo);
            ps.executeUpdate();
        }
    }

    private String getColumnName(String examType) {
        if (examType == null) return null;

        String value = examType.trim().toLowerCase();

        return switch (value) {
            case "quiz 1" -> "quiz1";
            case "quiz 2" -> "quiz2";
            case "quiz 3" -> "quiz3";
            case "assignment" -> "assignment_mark";
            case "mid exam" -> "mid_exam";
            case "final theory" -> "final_theory";
            case "final practical" -> "final_practical";
            default -> null;
        };
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

    private void insertOrUpdateStudentMarks(Connection conn,
                                            String courseId,
                                            String regNo,
                                            String examType,
                                            double mark) throws SQLException {

        // Step 1: create row if not exist
        String insertSql = """
        INSERT INTO student_marks (course_id, reg_no)
        VALUES (?, ?)
        ON DUPLICATE KEY UPDATE reg_no = VALUES(reg_no)
    """;

        try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
            ps.setString(1, courseId);
            ps.setString(2, regNo);
            ps.executeUpdate();
        }

        // Step 2: detect column
        String column = switch (examType.toLowerCase().trim()) {
            case "quiz 1" -> "quiz1";
            case "quiz 2" -> "quiz2";
            case "quiz 3" -> "quiz3";
            case "assignment" -> "assignment_mark";
            case "mid exam" -> "mid_exam";
            case "final theory" -> "final_theory";
            case "final practical" -> "final_practical";
            default -> null;
        };

        if (column == null) return;

        // Step 3: update correct column
        String updateSql = "UPDATE student_marks SET " + column + " = ? WHERE course_id=? AND reg_no=?";

        try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
            ps.setDouble(1, mark);
            ps.setString(2, courseId);
            ps.setString(3, regNo);
            ps.executeUpdate();
        }
    }
}