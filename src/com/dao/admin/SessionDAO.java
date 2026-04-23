package com.dao.admin;

import com.database.DatabaseInitializer;
import com.model.admin.SessionItem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SessionDAO {

    public SessionDAO() {
        createSessionTableIfNeeded();
    }

    public void createSessionTableIfNeeded() {
        String sql = """
                CREATE TABLE IF NOT EXISTS session (
                    id INT PRIMARY KEY AUTO_INCREMENT,
                    session_id VARCHAR(100) NOT NULL,
                    session_name VARCHAR(255),
                    type VARCHAR(20) NOT NULL,
                    course_id VARCHAR(100) NOT NULL,
                    year_no INT NOT NULL,
                    semester INT NOT NULL
                )
                """;

        try (Connection conn = DatabaseInitializer.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<String> getCourseIdsByYearAndSemester(int year, int semester) {
        List<String> list = new ArrayList<>();

        String sql = """
                SELECT course_id
                FROM courses
                WHERE year = ? AND semester = ?
                ORDER BY course_id
                """;

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, year);
            pst.setInt(2, semester);

            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                list.add(rs.getString("course_id"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public boolean existsSession(String courseId, String sessionId, String type) {
        String sql = """
                SELECT COUNT(*)
                FROM session
                WHERE course_id = ? AND session_id = ? AND type = ?
                """;

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, courseId);
            pst.setString(2, sessionId);
            pst.setString(3, type);

            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean addSession(SessionItem item) {
        String sql = """
                INSERT INTO session (session_id, session_name, type, course_id, year_no, semester)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, item.getSessionId());
            pst.setString(2, item.getSessionName());
            pst.setString(3, item.getType());
            pst.setString(4, item.getCourseId());
            pst.setInt(5, item.getYear());
            pst.setInt(6, item.getSemester());

            return pst.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public List<SessionItem> getAllSessions() {
        List<SessionItem> list = new ArrayList<>();

        String sql = """
                SELECT course_id, session_id, type, session_name, year_no, semester
                FROM session
                ORDER BY year_no, semester, course_id, session_id
                """;

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                SessionItem item = new SessionItem();
                item.setCourseId(rs.getString("course_id"));
                item.setSessionId(rs.getString("session_id"));
                item.setType(rs.getString("type"));
                item.setSessionName(rs.getString("session_name"));
                item.setYear(rs.getInt("year_no"));
                item.setSemester(rs.getInt("semester"));
                list.add(item);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<String> getSessionIdsByCourseAndType(String courseId, String type) {
        List<String> list = new ArrayList<>();

        String sql = """
                SELECT session_id
                FROM session
                WHERE course_id = ? AND type = ?
                ORDER BY session_id
                """;

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, courseId);
            pst.setString(2, type);

            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                list.add(rs.getString("session_id"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}