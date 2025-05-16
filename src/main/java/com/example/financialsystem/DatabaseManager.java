package com.example.financialsystem;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.sql.DriverManager.getConnection;

public class DatabaseManager {
    private static final String URL = "jdbc:postgresql://localhost:5432/finance_manager";
    private static final String USER = "postgres";
    private static final String PASSWORD = "nurchik04";

    static {
        try {
            createDefaultAdmin();
        } catch (SQLException e) {
            System.err.println("Error creating admin user: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    private static void createDefaultAdmin() throws SQLException {
        String adminUsername = "admin";
        String adminPassword = "qwerty12";
        String adminRole = "ANALYST";
        String adminName = "Nurdoolot";
        String adminSurname = "Zhoodarov";

        if (usernameExists(adminUsername)) {
            System.out.println("Admin user already exists");
            return;
        }

        String sql = "INSERT INTO users (username, password, name, surname, role) VALUES (?, ?, ?, ?, ?) RETURNING id";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, adminUsername);
            stmt.setString(2, adminPassword);
            stmt.setString(3, adminName);
            stmt.setString(4, adminSurname);
            stmt.setString(5, adminRole);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                System.out.println("Admin user created successfully with id: " + rs.getInt("id"));
            } else {
                System.err.println("Failed to create admin user");
            }
        }
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
    }

    public static int getUserId(String username) throws SQLException {
        String sql = "SELECT id FROM users WHERE username = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        }
        throw new SQLException("User not found: " + username);
    }

    public static void addTransaction(int userId, double amount, String type, String description, LocalDate date) throws SQLException {
        String sql = "INSERT INTO transactions (user_id, amount, type, description, date) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setDouble(2, amount);
            stmt.setString(3, type);
            stmt.setString(4, description);
            stmt.setDate(5, Date.valueOf(date));
            stmt.executeUpdate();
        }
    }

    public static void updateTransaction(int transactionId, double amount, String description, LocalDate date) throws SQLException {
        String sql = "UPDATE transactions SET amount = ?, description = ?, date = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, amount);
            stmt.setString(2, description);
            stmt.setDate(3, Date.valueOf(date));
            stmt.setInt(4, transactionId);
            stmt.executeUpdate();
        }
    }

    public static void deleteTransaction(int transactionId) throws SQLException {
        String sql = "DELETE FROM transactions WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, transactionId);
            stmt.executeUpdate();
        }
    }

    public static List<Income> getIncomes(int userId) throws SQLException {
        List<Income> incomes = new ArrayList<>();
        String sql = "SELECT id, amount, description, date FROM transactions WHERE user_id = ? AND type = 'INCOME'";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                incomes.add(new Income(
                        rs.getInt("id"),
                        rs.getDouble("amount"),
                        rs.getString("description"),
                        rs.getDate("date").toLocalDate()
                ));
            }
        }
        return incomes;
    }

    public static List<Expense> getExpenses(int userId) throws SQLException {
        List<Expense> expenses = new ArrayList<>();
        String sql = "SELECT id, amount, description, date FROM transactions WHERE user_id = ? AND type = 'EXPENSE'";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                expenses.add(new Expense(
                        rs.getInt("id"),
                        rs.getDouble("amount"),
                        rs.getString("description"),
                        rs.getDate("date").toLocalDate()
                ));
            }
        }
        return expenses;
    }

    public static void addGoal(int userId, String description, double targetAmount) throws SQLException {
        String sql = "INSERT INTO goals (user_id, goal_description, target_amount) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setString(2, description);
            stmt.setDouble(3, targetAmount);
            stmt.executeUpdate();
        }
    }

    public static void updateGoal(int goalId, String description, double targetAmount) throws SQLException {
        String sql = "UPDATE goals SET goal_description = ?, target_amount = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, description);
            stmt.setDouble(2, targetAmount);
            stmt.setInt(3, goalId);
            stmt.executeUpdate();
        }
    }

    public static void deleteGoal(int goalId) throws SQLException {
        String sql = "DELETE FROM goals WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, goalId);
            stmt.executeUpdate();
        }
    }

    public static List<Goal> getGoals(int userId) throws SQLException {
        List<Goal> goals = new ArrayList<>();
        String sql = "SELECT id, goal_description, target_amount FROM goals WHERE user_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                goals.add(new Goal(
                        rs.getInt("id"),
                        rs.getString("goal_description"),
                        rs.getDouble("target_amount")
                ));
            }
        }
        return goals;
    }

    public static double getTotalIncome(int userId) throws SQLException {
        String sql = "SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE user_id = ? AND type = 'INCOME'";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble(1);
            }
            return 0;
        }
    }

    public static double getTotalExpense(int userId) throws SQLException {
        String sql = "SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE user_id = ? AND type = 'EXPENSE'";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble(1);
            }
            return 0;
        }
    }

    public static void addNote(int userId, int analystId, String text, LocalDate date) throws SQLException {
        String sql = "INSERT INTO notes (user_id, analyst_id, text, created_at) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, analystId);
            stmt.setString(3, text);
            stmt.setDate(4, Date.valueOf(date));
            stmt.executeUpdate();
        }
    }

    public static List<Note> getNotesForUser(int userId) throws SQLException {
        List<Note> notes = new ArrayList<>();
        String sql = "SELECT n.id, n.text, n.created_at, u.username AS analyst_username " +
                "FROM notes n JOIN users u ON n.analyst_id = u.id WHERE n.user_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                notes.add(new Note(
                        rs.getInt("id"),
                        rs.getString("text"),
                        rs.getDate("created_at").toLocalDate(),
                        rs.getString("analyst_username")
                ));
            }
        }
        return notes;
    }

    public static List<UserSummary> getUserSummaries(String period) throws SQLException {
        List<UserSummary> summaries = new ArrayList<>();
        String sql = "SELECT u.id, u.username, u.name, u.surname, " +
                "COALESCE(SUM(CASE WHEN t.type = 'INCOME' THEN t.amount ELSE 0 END), 0) AS total_income, " +
                "COALESCE(SUM(CASE WHEN t.type = 'EXPENSE' THEN t.amount ELSE 0 END), 0) AS total_expense " +
                "FROM users u LEFT JOIN transactions t ON u.id = t.user_id ";
        if ("Last Month".equalsIgnoreCase(period)) {
            sql += "WHERE t.date >= CURRENT_DATE - INTERVAL '1 month' ";
        } else if ("Last Year".equalsIgnoreCase(period)) {
            sql += "WHERE t.date >= CURRENT_DATE - INTERVAL '1 year' ";
        }
        sql += "GROUP BY u.id, u.username, u.name, u.surname";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                summaries.add(new UserSummary(
                        rs.getString("username"),
                        rs.getString("name"),
                        rs.getString("surname"),
                        rs.getDouble("total_income"),
                        rs.getDouble("total_expense")
                ));
            }
        }
        return summaries;
    }
}
