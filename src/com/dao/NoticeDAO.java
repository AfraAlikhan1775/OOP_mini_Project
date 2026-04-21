package com.dao;

import com.database.DatabaseInitializer;
import com.model.Notice;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NoticeDAO {

    public NoticeDAO() {
        createTable();
    }

    public void createTable() {
        String sql = """
                CREATE TABLE IF NOT EXISTS notice (
                    notice_id    INT PRIMARY KEY AUTO_INCREMENT,
                    title        VARCHAR(200) NOT NULL,
                    content      TEXT NOT NULL,
                    category     ENUM('General', 'Academic', 'Exam', 'Event', 'Urgent') DEFAULT 'General',
                    target_role  ENUM('All', 'Student', 'Lecturer', 'Technical Officer') DEFAULT 'All',
                    posted_by    VARCHAR(50),
                    posted_date  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
                """;

        try (Connection conn = DatabaseInitializer.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean addNotice(Notice n) {
        String sql = """
                INSERT INTO notice (title, content, category, target_role, posted_by)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, n.getTitle());
            pst.setString(2, n.getContent());
            pst.setString(3, n.getCategory());
            pst.setString(4, n.getTargetRole());
            pst.setString(5, n.getPostedBy());

            pst.executeUpdate();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Notice> getAllNotices() {
        List<Notice> list = new ArrayList<>();
        String sql = "SELECT * FROM notice ORDER BY posted_date DESC";

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                list.add(extractNotice(rs));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<Notice> getNoticesForRole(String role) {
        List<Notice> list = new ArrayList<>();
        String sql = "SELECT * FROM notice WHERE target_role = 'All' OR target_role = ? ORDER BY posted_date DESC";

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, role);

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    list.add(extractNotice(rs));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<Notice> searchNotices(String keyword, String role) {
        List<Notice> list = new ArrayList<>();
        String sql = """
                SELECT * FROM notice
                WHERE (target_role = 'All' OR target_role = ?)
                  AND (title LIKE ? OR content LIKE ? OR category LIKE ?)
                ORDER BY posted_date DESC
                """;

        String pattern = "%" + keyword + "%";

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, role);
            pst.setString(2, pattern);
            pst.setString(3, pattern);
            pst.setString(4, pattern);

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    list.add(extractNotice(rs));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<Notice> filterByCategory(String category, String role) {
        List<Notice> list = new ArrayList<>();
        String sql = """
                SELECT * FROM notice
                WHERE (target_role = 'All' OR target_role = ?)
                  AND category = ?
                ORDER BY posted_date DESC
                """;

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, role);
            pst.setString(2, category);

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    list.add(extractNotice(rs));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    private Notice extractNotice(ResultSet rs) throws SQLException {
        return new Notice(
                rs.getInt("notice_id"),
                rs.getString("title"),
                rs.getString("content"),
                rs.getString("category"),
                rs.getString("target_role"),
                rs.getString("posted_by"),
                rs.getString("posted_date")
        );
    }
}
