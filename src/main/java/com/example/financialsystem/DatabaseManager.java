package com.example.financialsystem;

import java.sql.*;

import static java.sql.DriverManager.getConnection;

public class DatabaseManager {
    private static final String URL = "jdbc:postgresql://localhost:5432/finance_manager";
    private static final String USER = "postgres";
    private static final String PASSWORD = "nurchik04";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static boolean authenticateUser(String username, String password) throws SQLException {
        String sql = "SELECT password FROM users WHERE username = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String storedPassword = rs.getString("password");
                return storedPassword.equals(password);
            }
        }
        return false;
    }

    public static String getUserRole(String username) throws SQLException {
        String sql = "SELECT role FROM users WHERE username = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("role");
            }
        }
        return null;
    }
    public static boolean usernameExists(String username) throws SQLException {
        String sql = "SELECT 1 FROM users WHERE username = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        }



}}
