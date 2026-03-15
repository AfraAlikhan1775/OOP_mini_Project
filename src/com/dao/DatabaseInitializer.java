package com.dao;

import java.sql.*;

public class DatabaseInitializer {
    public static void databaseInitializer() throws SQLException {
        String url = "jdbc:mysql://localhost:33061/fms_db";
        String user = "root";
        String password = "Umes0820@@";

        Connection conn = DriverManager.getConnection(url,user,password);



    }
}
