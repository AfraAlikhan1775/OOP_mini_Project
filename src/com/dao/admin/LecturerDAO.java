package com.dao.admin;

import com.database.DatabaseInitializer;
import com.model.Lecturer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class LecturerDAO {

    public LecturerDAO() {
        createTable();
    }

    public void createTable() {
        String sql = """
                CREATE TABLE IF NOT EXISTS lecturer (
                    id INT PRIMARY KEY AUTO_INCREMENT,
                    first_name VARCHAR(100),
                    last_name VARCHAR(100),
                    emp_id VARCHAR(100) UNIQUE,
                    nic VARCHAR(50),
                    dob DATE,
                    gender VARCHAR(10),
                    image_path VARCHAR(255),
                    district VARCHAR(50),

                    email VARCHAR(100),
                    phone VARCHAR(20),
                    address TEXT,

                    department VARCHAR(50),
                    specialization VARCHAR(100),
                    designation VARCHAR(100),
                    qualification VARCHAR(100)
                )
                """;

        try (Connection conn = DatabaseInitializer.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean existsByEmpId(String empId) {
        String sql = "SELECT COUNT(*) FROM lecturer WHERE emp_id = ?";

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, empId);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean saveLecturer(Lecturer lecturer) {
        String sql = """
                INSERT INTO lecturer (
                    first_name, last_name, emp_id, nic, dob, gender, image_path, district,
                    email, phone, address,
                    department, specialization, designation, qualification
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, lecturer.getFirstName());
            pst.setString(2, lecturer.getLastName());
            pst.setString(3, lecturer.getEmpId());
            pst.setString(4, lecturer.getNic());

            if (lecturer.getDob() != null) {
                pst.setDate(5, java.sql.Date.valueOf(lecturer.getDob()));
            } else {
                pst.setDate(5, null);
            }

            pst.setString(6, lecturer.getGender());
            pst.setString(7, lecturer.getImagePath());
            pst.setString(8, lecturer.getDistrict());

            pst.setString(9, lecturer.getEmail());
            pst.setString(10, lecturer.getPhone());
            pst.setString(11, lecturer.getAddress());

            pst.setString(12, lecturer.getDepartment());
            pst.setString(13, lecturer.getSpecialization());
            pst.setString(14, lecturer.getDesignation());
            pst.setString(15, lecturer.getQualification());

            pst.executeUpdate();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Lecturer> getAllLecturers() {
        List<Lecturer> lecturers = new ArrayList<>();
        String sql = "SELECT * FROM lecturer ORDER BY id DESC";

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                lecturers.add(extractLecturer(rs));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return lecturers;
    }

    public List<Lecturer> searchLecturers(String keyword) {
        List<Lecturer> lecturers = new ArrayList<>();

        String sql = """
                SELECT * FROM lecturer
                WHERE first_name LIKE ?
                   OR last_name LIKE ?
                   OR emp_id LIKE ?
                   OR department LIKE ?
                   OR specialization LIKE ?
                ORDER BY id DESC
                """;

        String pattern = "%" + keyword + "%";

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, pattern);
            pst.setString(2, pattern);
            pst.setString(3, pattern);
            pst.setString(4, pattern);
            pst.setString(5, pattern);

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    lecturers.add(extractLecturer(rs));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return lecturers;
    }

    public List<Lecturer> filterByDepartment(String department) {
        List<Lecturer> lecturers = new ArrayList<>();
        String sql = "SELECT * FROM lecturer WHERE department = ? ORDER BY id DESC";

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, department);

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    lecturers.add(extractLecturer(rs));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return lecturers;
    }

    private Lecturer extractLecturer(ResultSet rs) throws Exception {
        return new Lecturer(
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("emp_id"),
                rs.getString("nic"),
                rs.getDate("dob") != null ? rs.getDate("dob").toLocalDate() : null,
                rs.getString("gender"),
                rs.getString("image_path"),
                rs.getString("district"),
                rs.getString("email"),
                rs.getString("phone"),
                rs.getString("address"),
                rs.getString("department"),
                rs.getString("specialization"),
                rs.getString("designation"),
                rs.getString("qualification")
        );
    }
}