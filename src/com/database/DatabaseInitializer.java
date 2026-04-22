package com.database;

import java.sql.*;
import java.io.InputStream;
import java.util.Properties;
import java.util.Scanner;

import io.github.cdimascio.dotenv.Dotenv;

public class DatabaseInitializer {

    private static final String URL;
    private static final String USER;
    private static final String PASSWORD;

    static {
        // Load .env file (ignores if missing, so env vars / db.properties still work)
        Dotenv dotenv = Dotenv.configure()
                .ignoreIfMissing()
                .load();

        // Legacy: also try db.properties for backward compatibility
        Properties props = new Properties();
        try (InputStream is = DatabaseInitializer.class.getResourceAsStream("/db.properties")) {
            if (is != null) {
                props.load(is);
            }
        } catch (Exception e) {
            System.err.println("Could not load db.properties: " + e.getMessage());
        }

        URL      = resolve(dotenv, props, "DB_URL",      "db.url");
        USER     = resolve(dotenv, props, "DB_USER",     "db.user");
        PASSWORD = resolve(dotenv, props, "DB_PASSWORD", "db.password");
    }

    /**
     * Resolves a configuration value with the following priority:
     * 1. .env file (via Dotenv)
     * 2. OS environment variable
     * 3. db.properties file (legacy fallback)
     * Throws an error if the value is not found in any source.
     */
    private static String resolve(Dotenv dotenv, Properties props, String envKey, String propKey) {
        // 1. .env file
        String val = dotenv.get(envKey);
        if (val != null && !val.isBlank()) return val;

        // 2. OS environment variable
        val = System.getenv(envKey);
        if (val != null && !val.isBlank()) return val;

        // 3. db.properties
        val = props.getProperty(propKey);
        if (val != null && !val.isBlank()) return val;

        throw new RuntimeException("Missing required config: set " + envKey + " in .env, environment, or " + propKey + " in db.properties");
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void initialize() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            InputStream is = DatabaseInitializer.class.getResourceAsStream("/com/Resources/schema.sql");
            if (is == null) {
                System.err.println("Could not find schema.sql in Resources!");
                return;
            }

            try (Scanner scanner = new Scanner(is).useDelimiter(";")) {
                while (scanner.hasNext()) {
                    String query = scanner.next().trim();
                    if (!query.isEmpty()) {
                        stmt.execute(query);
                    }
                }
                System.out.println("Database initialized successfully.");
            }

        } catch (SQLException e) {
            System.err.println("Error during database initialization: " + e.getMessage());
        }
    }
}
