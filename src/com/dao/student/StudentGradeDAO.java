package com.dao.student;

import com.database.DatabaseInitializer;
import com.model.student.StudentResultSheetRow;

import java.sql.*;
import java.util.*;

public class StudentGradeDAO {

    public List<String> getSemestersForStudent(String regNo) {
        List<String> semesters = new ArrayList<>();

        String sql = """
                SELECT DISTINCT mg.semester
                FROM student_marks sm
                JOIN marks_group mg ON sm.course_id = mg.course_id
                WHERE sm.reg_no = ?
                ORDER BY mg.semester
                """;

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, regNo);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                semesters.add(rs.getString("semester"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return semesters;
    }

    public List<String> getStudentSubjectCodes(String regNo, String semester) {
        List<String> subjects = new ArrayList<>();

        String sql = """
                SELECT DISTINCT sm.course_id
                FROM student_marks sm
                JOIN marks_group mg ON sm.course_id = mg.course_id
                WHERE sm.reg_no = ?
                  AND mg.semester = ?
                ORDER BY sm.course_id
                """;

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, regNo);
            ps.setString(2, semester);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                subjects.add(rs.getString("course_id"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return subjects;
    }

    public StudentResultSheetRow getStudentResult(String regNo, String semester) {
        StudentResultSheetRow row = new StudentResultSheetRow(regNo);

        List<String> subjects = getStudentSubjectCodes(regNo, semester);

        double totalPoints = 0;
        int totalCredits = 0;

        try (Connection conn = DatabaseInitializer.getConnection()) {

            for (String courseId : subjects) {
                CourseResult result = getCourseResult(conn, regNo, courseId);

                row.setCourseGrade(courseId, result.grade);

                if (result.countForGpa && result.credit > 0) {
                    totalPoints += result.point * result.credit;
                    totalCredits += result.credit;
                }
            }

            row.setSgpa(totalCredits == 0 ? "N/A" : String.format("%.2f", totalPoints / totalCredits));
            row.setCgpa(row.getSgpa());

        } catch (Exception e) {
            e.printStackTrace();
        }

        return row;
    }

    public List<StudentResultSheetRow> getBatchResult(String regNo, String semester) {
        List<StudentResultSheetRow> rows = new ArrayList<>();

        String batchPrefix = getBatchPrefix(regNo);

        String sql = """
                SELECT DISTINCT sm.reg_no
                FROM student_marks sm
                JOIN marks_group mg ON sm.course_id = mg.course_id
                WHERE sm.reg_no LIKE ?
                  AND mg.semester = ?
                ORDER BY sm.reg_no
                """;

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, batchPrefix + "%");
            ps.setString(2, semester);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                rows.add(getStudentResult(rs.getString("reg_no"), semester));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return rows;
    }

    private CourseResult getCourseResult(Connection conn, String regNo, String courseId) throws SQLException {
        String sql = """
                SELECT final_mark, final_theory, final_practical
                FROM student_marks
                WHERE reg_no = ?
                  AND course_id = ?
                LIMIT 1
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, regNo);
            ps.setString(2, courseId);

            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                return new CourseResult("-", 0, getCredit(conn, courseId), false);
            }

            double finalMark = rs.getDouble("final_mark");
            double theory = rs.getDouble("final_theory");
            double practical = rs.getDouble("final_practical");

            if (finalMark <= 0) {
                finalMark = calculateFinalMark(conn, regNo, courseId);
            }

            if (finalMark <= 0 && theory <= 0 && practical <= 0) {
                return new CourseResult("AB", 0, getCredit(conn, courseId), false);
            }

            GradeInfo grade = convertToGrade(finalMark);

            return new CourseResult(
                    grade.grade,
                    grade.point,
                    getCredit(conn, courseId),
                    true
            );
        }
    }

    private double calculateFinalMark(Connection conn, String regNo, String courseId) throws SQLException {
        String sql = """
                SELECT quiz1, quiz2, quiz3, assignment_mark, mid_exam, final_theory, final_practical
                FROM student_marks
                WHERE reg_no = ?
                  AND course_id = ?
                LIMIT 1
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, regNo);
            ps.setString(2, courseId);

            ResultSet rs = ps.executeQuery();

            if (!rs.next()) return 0;

            double quiz1 = rs.getDouble("quiz1");
            double quiz2 = rs.getDouble("quiz2");
            double quiz3 = rs.getDouble("quiz3");
            double assignment = rs.getDouble("assignment_mark");
            double mid = rs.getDouble("mid_exam");
            double theory = rs.getDouble("final_theory");
            double practical = rs.getDouble("final_practical");

            double quizAverage = (quiz1 + quiz2 + quiz3) / 3.0;

            double ca =
                    quizAverage * 0.10 +
                            assignment * 0.10 +
                            mid * 0.20;

            double finalExam;

            if (theory > 0 && practical > 0) {
                finalExam = theory * 0.30 + practical * 0.30;
            } else if (theory > 0) {
                finalExam = theory * 0.60;
            } else {
                finalExam = practical * 0.60;
            }

            double finalMark = ca + finalExam;

            String updateSql = """
                    UPDATE student_marks
                    SET ca_mark = ?, final_mark = ?
                    WHERE reg_no = ?
                      AND course_id = ?
                    """;

            try (PreparedStatement update = conn.prepareStatement(updateSql)) {
                update.setDouble(1, ca);
                update.setDouble(2, finalMark);
                update.setString(3, regNo);
                update.setString(4, courseId);
                update.executeUpdate();
            }

            return finalMark;
        }
    }

    private int getCredit(Connection conn, String courseId) {
        String sql = "SELECT credits FROM courses WHERE course_id = ? LIMIT 1";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, courseId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) return rs.getInt("credits");

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 1;
    }

    private GradeInfo convertToGrade(double mark) {
        if (mark >= 85) return new GradeInfo("A+", 4.00);
        if (mark >= 70) return new GradeInfo("A", 4.00);
        if (mark >= 65) return new GradeInfo("A-", 3.70);
        if (mark >= 60) return new GradeInfo("B+", 3.30);
        if (mark >= 55) return new GradeInfo("B", 3.00);
        if (mark >= 50) return new GradeInfo("B-", 2.70);
        if (mark >= 45) return new GradeInfo("C+", 2.30);
        if (mark >= 40) return new GradeInfo("C", 2.00);
        if (mark >= 35) return new GradeInfo("C-", 1.70);
        if (mark >= 30) return new GradeInfo("D+", 1.30);
        if (mark >= 25) return new GradeInfo("D", 1.00);
        return new GradeInfo("E", 0.00);
    }

    private String getBatchPrefix(String regNo) {
        int index = regNo.lastIndexOf("/");
        if (index == -1) return regNo;
        return regNo.substring(0, index + 1);
    }

    private static class CourseResult {
        String grade;
        double point;
        int credit;
        boolean countForGpa;

        CourseResult(String grade, double point, int credit, boolean countForGpa) {
            this.grade = grade;
            this.point = point;
            this.credit = credit;
            this.countForGpa = countForGpa;
        }
    }

    private static class GradeInfo {
        String grade;
        double point;

        GradeInfo(String grade, double point) {
            this.grade = grade;
            this.point = point;
        }
    }
}