package com.dao.admin;

import com.database.DatabaseInitializer;
import com.model.TechnicalOfficer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TechnicalOfficerDAO {

    public TechnicalOfficerDAO() {
        createTable();
    }

    public void createTable() {
        String sql = """
                CREATE TABLE IF NOT EXISTS technical_officer (
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
                    position VARCHAR(100),
                    shift_type VARCHAR(50),
                    assigned_lab VARCHAR(100)
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
        String sql = "SELECT COUNT(*) FROM technical_officer WHERE emp_id=?";

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, empId);

            ResultSet rs = pst.executeQuery();
            return rs.next() && rs.getInt(1) > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean saveTechnicalOfficer(TechnicalOfficer t) {
        String sql = """
                INSERT INTO technical_officer
                (first_name,last_name,emp_id,nic,dob,gender,image_path,district,
                 email,phone,address,department,position,shift_type,assigned_lab)
                VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)
                """;

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            fillStatement(pst, t);
            return pst.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateTechnicalOfficer(TechnicalOfficer t) {
        String sql = """
                UPDATE technical_officer SET
                    first_name = ?,
                    last_name = ?,
                    nic = ?,
                    dob = ?,
                    gender = ?,
                    image_path = ?,
                    district = ?,
                    email = ?,
                    phone = ?,
                    address = ?,
                    department = ?,
                    position = ?,
                    shift_type = ?,
                    assigned_lab = ?
                WHERE emp_id = ?
                """;

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, t.getFirstName());
            pst.setString(2, t.getLastName());
            pst.setString(3, t.getNic());

            if (t.getDob() != null) {
                pst.setDate(4, Date.valueOf(t.getDob()));
            } else {
                pst.setDate(4, null);
            }

            pst.setString(5, t.getGender());
            pst.setString(6, t.getImagePath());
            pst.setString(7, t.getDistrict());
            pst.setString(8, t.getEmail());
            pst.setString(9, t.getPhone());
            pst.setString(10, t.getAddress());
            pst.setString(11, t.getDepartment());
            pst.setString(12, t.getPosition());
            pst.setString(13, t.getShiftType());
            pst.setString(14, t.getAssignedLab());
            pst.setString(15, t.getEmpId());

            return pst.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<TechnicalOfficer> getAllTechnicalOfficers() {
        List<TechnicalOfficer> list = new ArrayList<>();
        String sql = "SELECT * FROM technical_officer ORDER BY id DESC";

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSet(rs));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<TechnicalOfficer> searchTechnicalOfficers(String keyword) {
        List<TechnicalOfficer> list = new ArrayList<>();

        String sql = """
                SELECT * FROM technical_officer
                WHERE first_name LIKE ?
                   OR last_name LIKE ?
                   OR emp_id LIKE ?
                   OR department LIKE ?
                   OR email LIKE ?
                   OR phone LIKE ?
                ORDER BY id DESC
                """;

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            String value = "%" + keyword + "%";

            for (int i = 1; i <= 6; i++) {
                pst.setString(i, value);
            }

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                list.add(mapResultSet(rs));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<TechnicalOfficer> filterByDepartment(String department) {
        List<TechnicalOfficer> list = new ArrayList<>();

        String sql = "SELECT * FROM technical_officer WHERE department=? ORDER BY id DESC";

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, department);

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                list.add(mapResultSet(rs));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public boolean deleteByEmpId(String empId) {
        String sql1 = "DELETE FROM technical_officer WHERE emp_id=?";
        String sql2 = "DELETE FROM users WHERE username=?";

        try (Connection conn = DatabaseInitializer.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement pst1 = conn.prepareStatement(sql1);
                 PreparedStatement pst2 = conn.prepareStatement(sql2)) {

                pst1.setString(1, empId);
                pst1.executeUpdate();

                pst2.setString(1, empId);
                pst2.executeUpdate();

                conn.commit();
                return true;

            } catch (Exception e) {
                conn.rollback();
                e.printStackTrace();
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public TechnicalOfficer getTOByEmpId(String empId) {
        String sql = "SELECT * FROM technical_officer WHERE emp_id=?";

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, empId);

            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                return mapResultSet(rs);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private void fillStatement(PreparedStatement pst, TechnicalOfficer t) throws SQLException {
        pst.setString(1, t.getFirstName());
        pst.setString(2, t.getLastName());
        pst.setString(3, t.getEmpId());
        pst.setString(4, t.getNic());

        if (t.getDob() != null) {
            pst.setDate(5, Date.valueOf(t.getDob()));
        } else {
            pst.setDate(5, null);
        }

        pst.setString(6, t.getGender());
        pst.setString(7, t.getImagePath());
        pst.setString(8, t.getDistrict());
        pst.setString(9, t.getEmail());
        pst.setString(10, t.getPhone());
        pst.setString(11, t.getAddress());
        pst.setString(12, t.getDepartment());
        pst.setString(13, t.getPosition());
        pst.setString(14, t.getShiftType());
        pst.setString(15, t.getAssignedLab());
    }

    private TechnicalOfficer mapResultSet(ResultSet rs) throws SQLException {
        TechnicalOfficer t = new TechnicalOfficer();

        t.setFirstName(rs.getString("first_name"));
        t.setLastName(rs.getString("last_name"));
        t.setEmpId(rs.getString("emp_id"));
        t.setNic(rs.getString("nic"));

        Date dob = rs.getDate("dob");
        if (dob != null) {
            t.setDob(dob.toLocalDate());
        }

        t.setGender(rs.getString("gender"));
        t.setImagePath(rs.getString("image_path"));
        t.setDistrict(rs.getString("district"));
        t.setEmail(rs.getString("email"));
        t.setPhone(rs.getString("phone"));
        t.setAddress(rs.getString("address"));
        t.setDepartment(rs.getString("department"));
        t.setPosition(rs.getString("position"));
        t.setShiftType(rs.getString("shift_type"));
        t.setAssignedLab(rs.getString("assigned_lab"));

        return t;
    }
}