package com.dao.admin;

import com.database.DatabaseInitializer;
import com.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;


public class UserDAO {
    public UserDAO() {
        createTable();
    }

    public void createTable() {
        String sql = """
                CREATE TABLE IF NOT EXISTS Users (
                    user_id INT PRIMARY KEY AUTO_INCREMENT,
                    username VARCHAR(50) UNIQUE NOT NULL,
                    password_hash VARCHAR(255) NOT NULL,
                    role ENUM('Admin', 'Lecturer', 'Student', 'Technical Officer') NOT NULL,
                    profile_pic VARCHAR(255)
                
                )
                """;
        try (Connection conn = DatabaseInitializer.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean saveUser(User u){
        String sql = """
                INSERT INTO Users(user_name,password_hash,role,profile_pic)
                VALUES (?,?,?,?)
                """;

        try(Connection conn = DatabaseInitializer.getConnection();
            PreparedStatement pst = conn.prepareStatement(sql)){

            pst.setString(1,u.getUserName());
            pst.setString(2,"12345");
            pst.setString(3,u.getRole());
            pst.setString(4,u.getProfPic());

            pst.executeUpdate();
            return true;

        }
        catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }


}


