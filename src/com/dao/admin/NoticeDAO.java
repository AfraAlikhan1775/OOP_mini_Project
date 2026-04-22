package com.dao.admin;

import com.database.DatabaseInitializer;
import com.model.Notice;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NoticeDAO {

    public NoticeDAO() {
        createTableIfNotExists();
    }

    public void createTableIfNotExists() {
        String sql = """
                CREATE TABLE IF NOT EXISTS notices (
                    id INT PRIMARY KEY AUTO_INCREMENT,
                    title VARCHAR(200) NOT NULL,
                    description TEXT NOT NULL,
                    pdf_name VARCHAR(255) NOT NULL,
                    pdf_data LONGBLOB NOT NULL,
                    role_target VARCHAR(50) NOT NULL,
                    batch_target VARCHAR(50) NOT NULL,
                    department_target VARCHAR(50) NOT NULL,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
                """;

        try (Connection conn = DatabaseInitializer.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean saveNotice(Notice notice) {
        String sql = """
                INSERT INTO notices
                (title, description, pdf_name, pdf_data, role_target, batch_target, department_target)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, notice.getTitle());
            pst.setString(2, notice.getDescription());
            pst.setString(3, notice.getPdfName());
            pst.setBytes(4, notice.getPdfData());
            pst.setString(5, notice.getRoleTarget());
            pst.setString(6, notice.getBatchTarget());
            pst.setString(7, notice.getDepartmentTarget());

            return pst.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Notice> getAllNotices() {
        List<Notice> notices = new ArrayList<>();
        String sql = "SELECT * FROM notices ORDER BY created_at DESC";

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                Notice notice = new Notice();
                notice.setId(rs.getInt("id"));
                notice.setTitle(rs.getString("title"));
                notice.setDescription(rs.getString("description"));
                notice.setPdfName(rs.getString("pdf_name"));
                notice.setPdfData(rs.getBytes("pdf_data"));
                notice.setRoleTarget(rs.getString("role_target"));
                notice.setBatchTarget(rs.getString("batch_target"));
                notice.setDepartmentTarget(rs.getString("department_target"));
                notice.setCreatedAt(rs.getTimestamp("created_at"));
                notices.add(notice);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return notices;
    }

    public List<Notice> getVisibleNotices(String role, String batch, String department) {
        List<Notice> notices = new ArrayList<>();

        String sql = """
                SELECT * FROM notices
                WHERE (role_target = 'All' OR role_target = ?)
                  AND (batch_target = 'All' OR batch_target = ?)
                  AND (department_target = 'All' OR department_target = ?)
                ORDER BY created_at DESC
                """;

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, role);
            pst.setString(2, batch);
            pst.setString(3, department);

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                Notice notice = new Notice();
                notice.setId(rs.getInt("id"));
                notice.setTitle(rs.getString("title"));
                notice.setDescription(rs.getString("description"));
                notice.setPdfName(rs.getString("pdf_name"));
                notice.setPdfData(rs.getBytes("pdf_data"));
                notice.setRoleTarget(rs.getString("role_target"));
                notice.setBatchTarget(rs.getString("batch_target"));
                notice.setDepartmentTarget(rs.getString("department_target"));
                notice.setCreatedAt(rs.getTimestamp("created_at"));
                notices.add(notice);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return notices;
    }

    public boolean deleteNotice(int id) {
        String sql = "DELETE FROM notices WHERE id = ?";

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, id);
            return pst.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public File exportPdfToTempFile(int noticeId, String pdfName) {
        String sql = "SELECT pdf_data FROM notices WHERE id = ?";

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, noticeId);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                byte[] pdfBytes = rs.getBytes("pdf_data");
                File tempFile = File.createTempFile("notice_", "_" + pdfName);
                tempFile.deleteOnExit();

                try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                    fos.write(pdfBytes);
                }

                return tempFile;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}