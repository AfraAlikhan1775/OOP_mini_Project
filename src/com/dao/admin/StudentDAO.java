package com.dao.admin;

import com.database.DatabaseInitializer;
import com.model.Student;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {

    public StudentDAO() {
        createTable();
    }

    public void createTable() {

        String sql1 = """
                CREATE TABLE IF NOT EXISTS student (
                    id INT PRIMARY KEY AUTO_INCREMENT,
                    first_name VARCHAR(100),
                    last_name VARCHAR(100),
                    reg_no VARCHAR(100) UNIQUE,
                    nic VARCHAR(50),
                    dob DATE,
                    gender VARCHAR(10),
                    image_path VARCHAR(255),
                    district VARCHAR(50),

                    email VARCHAR(100),
                    phone VARCHAR(20),
                    address TEXT,

                    department VARCHAR(50),
                    course VARCHAR(50),
                    year_no VARCHAR(10),
                    mentor_id VARCHAR(50),

                    guardian_name VARCHAR(100),
                    guardian_phone VARCHAR(20),
                    guardian_relationship VARCHAR(50)
                )
                """;




        try (Connection conn = DatabaseInitializer.getConnection();
             Statement stmt = conn.createStatement()) {


            stmt.execute(sql1);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean saveStudent(Student s) {

        String sql = """
                INSERT INTO student (
                    first_name, last_name, reg_no, nic, dob, gender, image_path, district,
                    email, phone, address,
                    department, course, year_no, mentor_id,
                    guardian_name, guardian_phone, guardian_relationship
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, s.getFirstName());
            pst.setString(2, s.getLastName());
            pst.setString(3, s.getRegNo());
            pst.setString(4, s.getNic());

            if (s.getDob() != null) {
                pst.setDate(5, java.sql.Date.valueOf(s.getDob()));
            } else {
                pst.setDate(5, null);
            }

            pst.setString(6, s.getGender());
            pst.setString(7, s.getImagePath());
            pst.setString(8, s.getDistrict());

            pst.setString(9, s.getEmail());
            pst.setString(10, s.getPhone());
            pst.setString(11, s.getAddress());

            pst.setString(12, s.getDepartment());
            pst.setString(13, s.getCourse());
            pst.setString(14, s.getYear());
            pst.setString(15, s.getMentor());

            pst.setString(16, s.getGuardianName());
            pst.setString(17, s.getGuardianPhone());
            pst.setString(18, s.getGuardianRelationship());

            pst.executeUpdate();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Student> getAllStudents() {

        List<Student> students = new ArrayList<>();

        String sql = "SELECT * FROM student ORDER BY id DESC";

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                Student student = new Student(
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("reg_no"),
                        rs.getString("nic"),
                        rs.getDate("dob") != null ? rs.getDate("dob").toLocalDate() : null,
                        rs.getString("gender"),
                        rs.getString("image_path"),
                        rs.getString("district"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("address"),
                        rs.getString("department"),
                        rs.getString("course"),
                        rs.getString("year_no"),
                        rs.getString("mentor_id"),
                        rs.getString("guardian_name"),
                        rs.getString("guardian_phone"),
                        rs.getString("guardian_relationship")
                );

                students.add(student);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return students;
    }

    public List<Student> searchStudents(String keyword) {

        List<Student> students = new ArrayList<>();

        String sql = """
                SELECT * FROM student
                WHERE first_name LIKE ?
                   OR last_name LIKE ?
                   OR reg_no LIKE ?
                   OR department LIKE ?
                   OR course LIKE ?
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
                    Student student = new Student(
                            rs.getString("first_name"),
                            rs.getString("last_name"),
                            rs.getString("reg_no"),
                            rs.getString("nic"),
                            rs.getDate("dob") != null ? rs.getDate("dob").toLocalDate() : null,
                            rs.getString("gender"),
                            rs.getString("image_path"),
                            rs.getString("district"),
                            rs.getString("email"),
                            rs.getString("phone"),
                            rs.getString("address"),
                            rs.getString("department"),
                            rs.getString("course"),
                            rs.getString("year_no"),
                            rs.getString("mentor_id"),
                            rs.getString("guardian_name"),
                            rs.getString("guardian_phone"),
                            rs.getString("guardian_relationship")
                    );

                    students.add(student);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return students;
    }

    public List<Student> filterByDepartment(String department) {

        List<Student> students = new ArrayList<>();

        String sql = "SELECT * FROM student WHERE department = ? ORDER BY id DESC";

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, department);

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    Student student = new Student(
                            rs.getString("first_name"),
                            rs.getString("last_name"),
                            rs.getString("reg_no"),
                            rs.getString("nic"),
                            rs.getDate("dob") != null ? rs.getDate("dob").toLocalDate() : null,
                            rs.getString("gender"),
                            rs.getString("image_path"),
                            rs.getString("district"),
                            rs.getString("email"),
                            rs.getString("phone"),
                            rs.getString("address"),
                            rs.getString("department"),
                            rs.getString("course"),
                            rs.getString("year_no"),
                            rs.getString("mentor_id"),
                            rs.getString("guardian_name"),
                            rs.getString("guardian_phone"),
                            rs.getString("guardian_relationship")
                    );

                    students.add(student);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return students;
    }
}