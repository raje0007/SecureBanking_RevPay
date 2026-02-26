package org.revpay.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DBConnection is a utility CLASS (not interface, not annotation, not exception).
 *
 * It is responsible for:
 * - Establishing MySQL database connections
 * - Centralizing database configuration
 * - Providing connection instances to DAO layer
 *
 * Designed for JDBC-based architecture.
 */
public final class DBConnection {

    // Private constructor to prevent instantiation
    private DBConnection() {
    }

    // Database configuration (move to properties file in production)
    private static final String URL = "jdbc:mysql://localhost:3306/revpay?useSSL=false&serverTimezone=UTC";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "12345";

    static {
        try {
            // Load MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Failed to load MySQL JDBC Driver", e);
        }
    }

    /**
     * Returns a new database connection.
     *
     * @return Connection object
     * @throws SQLException if connection fails
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }
}