package com.database;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.Properties;
import java.util.Scanner;

public class DatabaseInitializer {

    private static final String DB_NAME = "fms_db";

    private static final String SERVER_URL;
    private static final String DB_URL;
    private static final String USER;
    private static final String PASSWORD;

    static {
        Properties props = new Properties();

        try (InputStream is = DatabaseInitializer.class.getResourceAsStream("/db.properties")) {
            if (is != null) {
                props.load(is);
            }
        } catch (Exception e) {
            System.err.println("Could not load db.properties: " + e.getMessage());
        }

        USER = resolve(props, "db.user", "DB_USER", "root");
        PASSWORD = resolve(props, "db.password", "DB_PASSWORD", "Umes0820@@");

        String port = resolve(props, "db.port", "DB_PORT", "33061");

        SERVER_URL = "jdbc:mysql://localhost:" + port
                + "/?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";

        DB_URL = "jdbc:mysql://localhost:" + port + "/" + DB_NAME
                + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    }

    private static String resolve(Properties props, String propKey, String envKey, String fallback) {
        String env = System.getenv(envKey);
        if (env != null && !env.isBlank()) return env;

        String sys = System.getProperty(propKey);
        if (sys != null && !sys.isBlank()) return sys;

        return props.getProperty(propKey, fallback);
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASSWORD);
    }

    public static void initialize() {
        createDatabase();
        runSchemaFile();
    }

    private static void createDatabase() {
        String sql = "CREATE DATABASE IF NOT EXISTS " + DB_NAME;

        try (Connection conn = DriverManager.getConnection(SERVER_URL, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {

            stmt.execute(sql);

        } catch (SQLException e) {
            System.err.println("Database create error: " + e.getMessage());
        }
    }

    private static void runSchemaFile() {
        try (Connection conn = getConnection()) {

            InputStream is = DatabaseInitializer.class.getResourceAsStream("/com/Resources/schema.sql");

            if (is == null) {
                System.err.println("schema.sql not found: /com/Resources/schema.sql");
                return;
            }

            String sqlText;
            try (Scanner scanner = new Scanner(is, StandardCharsets.UTF_8)) {
                scanner.useDelimiter("\\A");
                sqlText = scanner.hasNext() ? scanner.next() : "";
            }

            String[] statements = sqlText.split(";");

            try (Statement stmt = conn.createStatement()) {
                for (String statement : statements) {
                    String sql = statement.trim();

                    if (sql.isEmpty()) continue;
                    if (sql.startsWith("--")) continue;

                    stmt.execute(sql);
                }
            }

            System.out.println("Database initialized successfully.");

        } catch (Exception e) {
            System.err.println("Schema run error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}