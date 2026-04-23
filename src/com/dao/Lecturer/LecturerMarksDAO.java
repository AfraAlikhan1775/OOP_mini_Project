package com.dao.Lecturer;

import com.database.DatabaseInitializer;
import com.model.CourseAssessmentScheme;
import com.model.StudentMark;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LecturerMarksDAO {

    public LecturerMarksDAO() {
        createTablesIfNotExists();
    }

    public void createTablesIfNotExists() {
        String schemeSql = """
                CREATE TABLE IF NOT EXISTS course_assessment_scheme (
                    course_id VARCHAR(50) PRIMARY KEY,
                    has_theory BOOLEAN NOT NULL DEFAULT TRUE,
                    has_practical BOOLEAN NOT NULL DEFAULT FALSE,
                    FOREIGN KEY (course_id) REFERENCES courses(course_id)
                        ON UPDATE CASCADE
                        ON DELETE CASCADE
                )
                """;

        String marksSql = """
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
                    PRIMARY KEY (course_id, reg_no),
                    FOREIGN KEY (course_id) REFERENCES courses(course_id)
                        ON UPDATE CASCADE
                        ON DELETE CASCADE,
                    FOREIGN KEY (reg_no) REFERENCES student(reg_no)
                        ON UPDATE CASCADE
                        ON DELETE CASCADE
                )
                """;

        try (Connection conn = DatabaseInitializer.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(schemeSql);
            stmt.execute(marksSql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean saveOrUpdateAssessmentScheme(CourseAssessmentScheme scheme) {
        String sql = """
                INSERT INTO course_assessment_scheme (course_id, has_theory, has_practical)
                VALUES (?, ?, ?)
                ON DUPLICATE KEY UPDATE
                    has_theory = VALUES(has_theory),
                    has_practical = VALUES(has_practical)
                """;

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, scheme.getCourseId());
            pst.setBoolean(2, scheme.isHasTheory());
            pst.setBoolean(3, scheme.isHasPractical());

            return pst.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public CourseAssessmentScheme getAssessmentScheme(String courseId) {
        String sql = "SELECT * FROM course_assessment_scheme WHERE course_id = ?";

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, courseId);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return new CourseAssessmentScheme(
                            rs.getString("course_id"),
                            rs.getBoolean("has_theory"),
                            rs.getBoolean("has_practical")
                    );
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<StudentMark> getRegisteredStudentsForMarks(String courseId, String semester, String academicYear) {
        List<StudentMark> list = new ArrayList<>();

        String sql = """
                SELECT s.reg_no, CONCAT(s.first_name, ' ', s.last_name) AS full_name,
                       sm.quiz1, sm.quiz2, sm.quiz3,
                       sm.assignment_mark, sm.mid_exam,
                       sm.final_theory, sm.final_practical,
                       sm.ca_mark, sm.final_mark
                FROM course_registration cr
                JOIN student s ON cr.reg_no = s.reg_no
                LEFT JOIN student_marks sm
                    ON sm.course_id = cr.course_id AND sm.reg_no = cr.reg_no
                WHERE cr.course_id = ? AND cr.semester = ? AND cr.academic_year = ?
                ORDER BY s.reg_no
                """;

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, courseId);
            pst.setString(2, semester);
            pst.setString(3, academicYear);

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    StudentMark mark = new StudentMark(courseId,
                            rs.getString("reg_no"),
                            rs.getString("full_name"));

                    mark.setQuiz1(rs.getDouble("quiz1"));
                    mark.setQuiz2(rs.getDouble("quiz2"));
                    mark.setQuiz3(rs.getDouble("quiz3"));
                    mark.setAssignment(rs.getDouble("assignment_mark"));
                    mark.setMidExam(rs.getDouble("mid_exam"));
                    mark.setFinalTheory(rs.getDouble("final_theory"));
                    mark.setFinalPractical(rs.getDouble("final_practical"));
                    mark.setCaMark(rs.getDouble("ca_mark"));
                    mark.setFinalMark(rs.getDouble("final_mark"));

                    list.add(mark);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public boolean saveStudentMarksBulk(List<StudentMark> marks) {
        String sql = """
                INSERT INTO student_marks (
                    course_id, reg_no, quiz1, quiz2, quiz3,
                    assignment_mark, mid_exam, final_theory, final_practical,
                    ca_mark, final_mark
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                ON DUPLICATE KEY UPDATE
                    quiz1 = VALUES(quiz1),
                    quiz2 = VALUES(quiz2),
                    quiz3 = VALUES(quiz3),
                    assignment_mark = VALUES(assignment_mark),
                    mid_exam = VALUES(mid_exam),
                    final_theory = VALUES(final_theory),
                    final_practical = VALUES(final_practical),
                    ca_mark = VALUES(ca_mark),
                    final_mark = VALUES(final_mark)
                """;

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            conn.setAutoCommit(false);

            for (StudentMark mark : marks) {
                pst.setString(1, mark.getCourseId());
                pst.setString(2, mark.getRegNo());
                pst.setDouble(3, mark.getQuiz1());
                pst.setDouble(4, mark.getQuiz2());
                pst.setDouble(5, mark.getQuiz3());
                pst.setDouble(6, mark.getAssignment());
                pst.setDouble(7, mark.getMidExam());
                pst.setDouble(8, mark.getFinalTheory());
                pst.setDouble(9, mark.getFinalPractical());
                pst.setDouble(10, mark.getCaMark());
                pst.setDouble(11, mark.getFinalMark());
                pst.addBatch();
            }

            pst.executeBatch();
            conn.commit();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}