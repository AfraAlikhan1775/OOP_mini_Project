package com.dao.admin;

import com.database.DatabaseInitializer;
import com.model.admin.TimetableSessionInput;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TimetableDAO {

    public TimetableDAO() {
        createTablesIfNotExists();
    }

    public void createTablesIfNotExists() {
        String createGroupTable = """
                CREATE TABLE IF NOT EXISTS timetable_group (
                    id INT PRIMARY KEY AUTO_INCREMENT,
                    department VARCHAR(50) NOT NULL,
                    level_no INT NOT NULL,
                    semester INT NOT NULL,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
                """;

        String createSessionTable = """
                CREATE TABLE IF NOT EXISTS timetable_session (
                    id INT PRIMARY KEY AUTO_INCREMENT,
                    timetable_group_id INT NOT NULL,
                    subject VARCHAR(100) NOT NULL,
                    day_name VARCHAR(20) NOT NULL,
                    start_time VARCHAR(20) NOT NULL,
                    end_time VARCHAR(20) NOT NULL,
                    lecturer VARCHAR(100) NOT NULL,
                    room VARCHAR(50) NOT NULL,
                    session_type VARCHAR(50) NOT NULL,
                    FOREIGN KEY (timetable_group_id) REFERENCES timetable_group(id) ON DELETE CASCADE
                )
                """;

        try (Connection conn = DatabaseInitializer.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(createGroupTable);
            stmt.execute(createSessionTable);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean saveTimetable(String department, int level, int semester, List<TimetableSessionInput> sessions) {
        if (sessions == null || sessions.isEmpty()) {
            return false;
        }

        String insertGroupSql = """
                INSERT INTO timetable_group (department, level_no, semester)
                VALUES (?, ?, ?)
                """;

        String insertSessionSql = """
                INSERT INTO timetable_session (
                    timetable_group_id,
                    subject,
                    day_name,
                    start_time,
                    end_time,
                    lecturer,
                    room,
                    session_type
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = DatabaseInitializer.getConnection()) {
            conn.setAutoCommit(false);

            int groupId;

            try (PreparedStatement groupStmt = conn.prepareStatement(insertGroupSql, Statement.RETURN_GENERATED_KEYS)) {
                groupStmt.setString(1, department);
                groupStmt.setInt(2, level);
                groupStmt.setInt(3, semester);
                groupStmt.executeUpdate();

                try (ResultSet rs = groupStmt.getGeneratedKeys()) {
                    if (!rs.next()) {
                        conn.rollback();
                        return false;
                    }
                    groupId = rs.getInt(1);
                }
            }

            try (PreparedStatement sessionStmt = conn.prepareStatement(insertSessionSql)) {
                for (TimetableSessionInput session : sessions) {
                    sessionStmt.setInt(1, groupId);
                    sessionStmt.setString(2, session.getSubject());
                    sessionStmt.setString(3, session.getDayName());
                    sessionStmt.setString(4, session.getStartTime());
                    sessionStmt.setString(5, session.getEndTime());
                    sessionStmt.setString(6, session.getLecturer());
                    sessionStmt.setString(7, session.getRoom());
                    sessionStmt.setString(8, session.getSessionType());
                    sessionStmt.addBatch();
                }
                sessionStmt.executeBatch();
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<TimetableSummary> getAllTimetableSummaries() {
        String sql = """
                SELECT
                    tg.id,
                    tg.department,
                    tg.level_no,
                    tg.semester,
                    COUNT(ts.id) AS session_count
                FROM timetable_group tg
                LEFT JOIN timetable_session ts ON tg.id = ts.timetable_group_id
                GROUP BY tg.id, tg.department, tg.level_no, tg.semester
                ORDER BY tg.id DESC
                """;

        List<TimetableSummary> list = new ArrayList<>();

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                list.add(new TimetableSummary(
                        rs.getInt("id"),
                        rs.getString("department"),
                        rs.getInt("level_no"),
                        rs.getInt("semester"),
                        rs.getInt("session_count")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<TimetableSummary> searchTimetableSummaries(String keyword) {
        String sql = """
                SELECT
                    tg.id,
                    tg.department,
                    tg.level_no,
                    tg.semester,
                    COUNT(ts.id) AS session_count
                FROM timetable_group tg
                LEFT JOIN timetable_session ts ON tg.id = ts.timetable_group_id
                WHERE tg.department LIKE ?
                   OR CAST(tg.level_no AS CHAR) LIKE ?
                   OR CAST(tg.semester AS CHAR) LIKE ?
                GROUP BY tg.id, tg.department, tg.level_no, tg.semester
                ORDER BY tg.id DESC
                """;

        List<TimetableSummary> list = new ArrayList<>();
        String pattern = "%" + keyword + "%";

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, pattern);
            pst.setString(2, pattern);
            pst.setString(3, pattern);

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    list.add(new TimetableSummary(
                            rs.getInt("id"),
                            rs.getString("department"),
                            rs.getInt("level_no"),
                            rs.getInt("semester"),
                            rs.getInt("session_count")
                    ));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<TimetableSummary> filterByDepartment(String department) {
        String sql = """
                SELECT
                    tg.id,
                    tg.department,
                    tg.level_no,
                    tg.semester,
                    COUNT(ts.id) AS session_count
                FROM timetable_group tg
                LEFT JOIN timetable_session ts ON tg.id = ts.timetable_group_id
                WHERE tg.department = ?
                GROUP BY tg.id, tg.department, tg.level_no, tg.semester
                ORDER BY tg.id DESC
                """;

        List<TimetableSummary> list = new ArrayList<>();

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, department);

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    list.add(new TimetableSummary(
                            rs.getInt("id"),
                            rs.getString("department"),
                            rs.getInt("level_no"),
                            rs.getInt("semester"),
                            rs.getInt("session_count")
                    ));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<TimetableSessionInput> getSessionsByGroupId(int groupId) {
        String sql = """
                SELECT
                    subject,
                    day_name,
                    start_time,
                    end_time,
                    lecturer,
                    room,
                    session_type
                FROM timetable_session
                WHERE timetable_group_id = ?
                ORDER BY
                    CASE day_name
                        WHEN 'Monday' THEN 1
                        WHEN 'Tuesday' THEN 2
                        WHEN 'Wednesday' THEN 3
                        WHEN 'Thursday' THEN 4
                        WHEN 'Friday' THEN 5
                        ELSE 6
                    END,
                    start_time
                """;

        List<TimetableSessionInput> list = new ArrayList<>();

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, groupId);

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    list.add(new TimetableSessionInput(
                            rs.getString("subject"),
                            rs.getString("day_name"),
                            rs.getString("start_time"),
                            rs.getString("end_time"),
                            rs.getString("lecturer"),
                            rs.getString("room"),
                            rs.getString("session_type")
                    ));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public static class TimetableSummary {
        private final int id;
        private final String department;
        private final int level;
        private final int semester;
        private final int sessionCount;

        public TimetableSummary(int id, String department, int level, int semester, int sessionCount) {
            this.id = id;
            this.department = department;
            this.level = level;
            this.semester = semester;
            this.sessionCount = sessionCount;
        }

        public int getId() {
            return id;
        }

        public String getDepartment() {
            return department;
        }

        public int getLevel() {
            return level;
        }

        public int getSemester() {
            return semester;
        }

        public int getSessionCount() {
            return sessionCount;
        }
    }
}