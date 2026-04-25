package com.dao.Lecturer;

import com.database.DatabaseInitializer;
import com.model.Lecturerr.LecturerStudentResultRow;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LecturerStudentResultsDAO {

    private static final double ATTENDANCE_MIN = 80.0;
    private static final double FINAL_EXAM_MIN = 21.0;

    public List<String> getDepartments() {
        List<String> list = new ArrayList<>();

        String sql = """
                SELECT DISTINCT department
                FROM courses
                WHERE department IS NOT NULL AND department <> ''
                ORDER BY department
                """;

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(rs.getString("department"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<String> getYears(String department) {
        List<String> list = new ArrayList<>();

        String sql = """
                SELECT DISTINCT year
                FROM courses
                WHERE department = ?
                ORDER BY CAST(year AS UNSIGNED)
                """;

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, department);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(rs.getString("year"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<String> getSemesters(String department, String year) {
        List<String> list = new ArrayList<>();

        String sql = """
                SELECT DISTINCT semester
                FROM courses
                WHERE department = ?
                  AND year = ?
                ORDER BY CAST(semester AS UNSIGNED)
                """;

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, department);
            ps.setString(2, year);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(rs.getString("semester"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<String> getCourseIdsByFilter(String department, String year, String semester) {
        List<String> list = new ArrayList<>();

        String sql = """
                SELECT course_id
                FROM courses
                WHERE department = ?
                  AND year = ?
                  AND semester = ?
                ORDER BY course_id
                """;

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, department);
            ps.setString(2, year);
            ps.setString(3, semester);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(rs.getString("course_id"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<LecturerStudentResultRow> getStudentSummaryResults(
            String department,
            String year,
            String semester,
            String viewType
    ) {
        List<LecturerStudentResultRow> rows = new ArrayList<>();
        List<String> courseIds = getCourseIdsByFilter(department, year, semester);

        if (courseIds.isEmpty()) return rows;

        String sql = """
                SELECT DISTINCT
                    x.reg_no,
                    TRIM(CONCAT(COALESCE(s.first_name,''), ' ', COALESCE(s.last_name,''))) AS student_name
                FROM (
                    SELECT s.reg_no
                    FROM student s
                    WHERE s.department = ?
                      AND s.year_no = ?

                    UNION

                    SELECT sm.reg_no
                    FROM student_marks sm
                    JOIN courses c ON c.course_id = sm.course_id
                    WHERE c.department = ?
                      AND c.year = ?
                      AND c.semester = ?

                    UNION

                    SELECT ar.reg_no
                    FROM attendance_record ar
                    JOIN attendance_group ag ON ag.id = ar.group_id
                    JOIN courses c2 ON c2.course_id = ag.course_id
                    WHERE c2.department = ?
                      AND c2.year = ?
                      AND c2.semester = ?

                    UNION

                    SELECT m.reg_no
                    FROM medical m
                    JOIN medical_selected_session mss ON m.medical_id = mss.medical_id
                    JOIN courses c3 ON c3.course_id = mss.course_id
                    WHERE c3.department = ?
                      AND c3.year = ?
                      AND c3.semester = ?
                ) x
                LEFT JOIN student s ON s.reg_no = x.reg_no
                ORDER BY x.reg_no
                """;

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, department);
            ps.setString(2, year);

            ps.setString(3, department);
            ps.setString(4, year);
            ps.setString(5, semester);

            ps.setString(6, department);
            ps.setString(7, year);
            ps.setString(8, semester);

            ps.setString(9, department);
            ps.setString(10, year);
            ps.setString(11, semester);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String regNo = rs.getString("reg_no");
                String studentName = rs.getString("student_name");

                if (studentName == null || studentName.isBlank()) {
                    studentName = "-";
                }

                LecturerStudentResultRow row =
                        new LecturerStudentResultRow(regNo, studentName);

                double totalPoints = 0;
                int totalCredits = 0;
                boolean hasMC = false;

                for (String courseId : courseIds) {

                    if ("Eligibility".equalsIgnoreCase(viewType)) {
                        double attendance = getAttendancePercentage(conn, regNo, courseId);

                        row.setCourseValue(
                                courseId,
                                attendance >= ATTENDANCE_MIN
                                        ? "Eligible (" + format(attendance) + "%)"
                                        : "Not Eligible (" + format(attendance) + "%)"
                        );

                        row.setSgpa("");
                        row.setCgpa("");
                        continue;
                    }

                    CourseResult result = calculateOneCourse(conn, regNo, courseId);

                    if ("Marks".equalsIgnoreCase(viewType)) {
                        row.setCourseValue(
                                courseId,
                                "CA " + format(result.caMark)
                                        + " | Exam " + format(result.examMark)
                                        + " | Total " + format(result.totalMark)
                        );
                    } else {
                        row.setCourseValue(courseId, result.grade);
                    }

                    if ("MC".equalsIgnoreCase(result.grade)) {
                        hasMC = true;
                    }

                    if (result.countForGpa) {
                        int credit = getCredit(conn, courseId);
                        totalPoints += result.gradePoint * credit;
                        totalCredits += credit;
                    }
                }

                if (!"Eligibility".equalsIgnoreCase(viewType)) {
                    if (hasMC || totalCredits == 0) {
                        row.setSgpa("N/A");
                        row.setCgpa("N/A");
                    } else {
                        String gpa = String.format("%.2f", totalPoints / totalCredits);
                        row.setSgpa(gpa);
                        row.setCgpa(gpa);
                    }
                }

                rows.add(row);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return rows;
    }

    private CourseResult calculateOneCourse(Connection conn, String regNo, String courseId) throws SQLException {
        if (hasApprovedExamMedical(conn, regNo, courseId)) {
            return new CourseResult(0, 0, 0, "MC", false, 0);
        }

        String sql = """
                SELECT
                    COALESCE(quiz1,0) AS quiz1,
                    COALESCE(quiz2,0) AS quiz2,
                    COALESCE(quiz3,0) AS quiz3,
                    COALESCE(assignment_mark,0) AS assignment_mark,
                    COALESCE(mid_exam,0) AS mid_exam,
                    COALESCE(final_theory,0) AS final_theory,
                    COALESCE(final_practical,0) AS final_practical,
                    COALESCE(ca_mark,0) AS ca_mark,
                    COALESCE(final_mark,0) AS final_mark
                FROM student_marks
                WHERE reg_no = ?
                  AND course_id = ?
                LIMIT 1
                """;

        double quiz1 = 0, quiz2 = 0, quiz3 = 0;
        double assignment = 0, mid = 0;
        double theory = 0, practical = 0;
        double ca = 0, total = 0;

        boolean hasMarkRow = false;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, regNo);
            ps.setString(2, courseId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                hasMarkRow = true;

                quiz1 = rs.getDouble("quiz1");
                quiz2 = rs.getDouble("quiz2");
                quiz3 = rs.getDouble("quiz3");
                assignment = rs.getDouble("assignment_mark");
                mid = rs.getDouble("mid_exam");
                theory = rs.getDouble("final_theory");
                practical = rs.getDouble("final_practical");
                ca = rs.getDouble("ca_mark");
                total = rs.getDouble("final_mark");
            }
        }

        if (!hasMarkRow) {
            return new CourseResult(0, 0, 0, "AB", false, 0);
        }

        if (ca <= 0) {
            double quizAverage = (quiz1 + quiz2 + quiz3) / 3.0;
            ca = (quizAverage * 0.10) + (assignment * 0.10) + (mid * 0.20);
        }

        double exam = calculateExamContribution(theory, practical);

        if (exam <= 0) {
            return new CourseResult(ca, 0, ca, "AB", false, 0);
        }

        if (total <= 0) {
            total = ca + exam;
        }

        if (exam < FINAL_EXAM_MIN) {
            return new CourseResult(ca, exam, total, "E", true, 0);
        }

        GradeInfo gradeInfo = getGradeInfo(total);

        return new CourseResult(
                ca,
                exam,
                total,
                gradeInfo.grade,
                true,
                gradeInfo.gradePoint
        );
    }

    private double calculateExamContribution(double theory, double practical) {
        if (theory > 0 && practical > 0) return (theory * 0.30) + (practical * 0.30);
        if (theory > 0) return theory * 0.60;
        if (practical > 0) return practical * 0.60;
        return 0;
    }

    private double getAttendancePercentage(Connection conn, String regNo, String courseId) throws SQLException {
        String sql = """
                SELECT
                    COUNT(*) AS total_count,
                    SUM(
                        CASE
                            WHEN UPPER(ar.status) = 'PRESENT' THEN 1
                            WHEN UPPER(ar.status) = 'MEDICAL' THEN 1
                            WHEN UPPER(COALESCE(ar.medical_status,'')) = 'APPROVED' THEN 1
                            ELSE 0
                        END
                    ) AS attended_count
                FROM attendance_record ar
                JOIN attendance_group ag ON ag.id = ar.group_id
                WHERE ar.reg_no = ?
                  AND ag.course_id = ?
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, regNo);
            ps.setString(2, courseId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int total = rs.getInt("total_count");
                int attended = rs.getInt("attended_count");

                if (total == 0) return 0;

                return attended * 100.0 / total;
            }
        }

        return 0;
    }

    private boolean hasApprovedExamMedical(Connection conn, String regNo, String courseId) throws SQLException {
        String sql = """
                SELECT 1
                FROM medical m
                JOIN medical_selected_session mss ON m.medical_id = mss.medical_id
                WHERE m.reg_no = ?
                  AND mss.course_id = ?
                  AND UPPER(COALESCE(m.status,'')) = 'APPROVED'
                  AND UPPER(COALESCE(mss.status,'')) = 'APPROVED'
                  AND UPPER(COALESCE(mss.medical_for,'')) = 'EXAM'
                LIMIT 1
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, regNo);
            ps.setString(2, courseId);

            ResultSet rs = ps.executeQuery();
            return rs.next();
        }
    }

    private int getCredit(Connection conn, String courseId) throws SQLException {
        String sql = "SELECT credits FROM courses WHERE course_id = ? LIMIT 1";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, courseId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int credit = rs.getInt("credits");
                return credit <= 0 ? 1 : credit;
            }
        }

        return 1;
    }

    private GradeInfo getGradeInfo(double mark) {
        if (mark >= 85) return new GradeInfo("A+", 4.0);
        if (mark >= 70) return new GradeInfo("A", 4.0);
        if (mark >= 65) return new GradeInfo("A-", 3.7);
        if (mark >= 60) return new GradeInfo("B+", 3.3);
        if (mark >= 55) return new GradeInfo("B", 3.0);
        if (mark >= 50) return new GradeInfo("B-", 2.7);
        if (mark >= 45) return new GradeInfo("C+", 2.3);
        if (mark >= 40) return new GradeInfo("C", 2.0);
        if (mark >= 35) return new GradeInfo("C-", 1.7);
        if (mark >= 30) return new GradeInfo("D+", 1.3);
        if (mark >= 25) return new GradeInfo("D", 1.0);
        return new GradeInfo("E", 0.0);
    }

    private String format(double value) {
        return String.format("%.2f", value);
    }

    private static class CourseResult {
        double caMark;
        double examMark;
        double totalMark;
        String grade;
        boolean countForGpa;
        double gradePoint;

        CourseResult(double caMark, double examMark, double totalMark,
                     String grade, boolean countForGpa, double gradePoint) {
            this.caMark = caMark;
            this.examMark = examMark;
            this.totalMark = totalMark;
            this.grade = grade;
            this.countForGpa = countForGpa;
            this.gradePoint = gradePoint;
        }
    }

    private static class GradeInfo {
        String grade;
        double gradePoint;

        GradeInfo(String grade, double gradePoint) {
            this.grade = grade;
            this.gradePoint = gradePoint;
        }
    }
}