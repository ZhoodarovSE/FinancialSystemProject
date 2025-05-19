package com.example.financialsystem;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class RegularUser extends AbstractUser {
    private final DatabaseManager dbManager;

    public RegularUser(String username, int userId) {
        super(username, userId);
        this.dbManager = new DatabaseManager();
    }

    public void addIncome(double amount, String source, LocalDate date) throws SQLException {
        dbManager.addTransaction(id, amount, "INCOME", source, date);
    }

    public void updateIncome(int incomeId, double amount, String source, LocalDate date) throws SQLException {
        dbManager.updateTransaction(incomeId, amount, source, date);
    }

    public void deleteIncome(int incomeId) throws SQLException {
        dbManager.deleteTransaction(incomeId);
    }

    public List<Income> getIncomes() throws SQLException {
        return dbManager.getIncomes(id);
    }

    public void addExpense(double amount, String forWhat, LocalDate date) throws SQLException {
        dbManager.addTransaction(id, amount, "EXPENSE", forWhat, date);
    }

    public void updateExpense(int expenseId, double amount, String forWhat, LocalDate date) throws SQLException {
        dbManager.updateTransaction(expenseId, amount, forWhat, date);
    }

    public void deleteExpense(int expenseId) throws SQLException {
        dbManager.deleteTransaction(expenseId);
    }

    public List<Expense> getExpenses() throws SQLException {
        return dbManager.getExpenses(id);
    }

    public void addGoal(String description, double targetAmount) throws SQLException {
        dbManager.addGoal(id, description, targetAmount);
    }

    public void updateGoal(int goalId, String description, double targetAmount) throws SQLException {
        dbManager.updateGoal(goalId, description, targetAmount);
    }

    public void deleteGoal(int goalId) throws SQLException {
        dbManager.deleteGoal(goalId);
    }

    public List<Goal> getGoals() throws SQLException {
        return dbManager.getGoals(id);
    }

    public double getTotalIncome() throws SQLException {
        return dbManager.getTotalIncome(id);
    }

    public double getTotalExpense() throws SQLException {
        return dbManager.getTotalExpense(id);
    }

    public String generateReport() throws SQLException {
        double totalIncome = getTotalIncome();
        double totalExpense = getTotalExpense();
        List<Goal> goals = getGoals();
        StringBuilder report = new StringBuilder();
        report.append(String.format("Total Income: %.2f\n", totalIncome));
        report.append(String.format("Total Expense: %.2f\n", totalExpense));
        report.append(String.format("Balance: %.2f\n", totalIncome - totalExpense));
        report.append("Goals:\n");
        for (Goal goal : goals) {
            report.append(String.format("Goal %d: %s, Target: %.2f\n",
                    goal.getId(), goal.getDescription(), goal.getTargetAmount()));
        }
        return report.toString();
    }

    public List<Note> getNotes() throws SQLException {
        return DatabaseManager.getNotesForUser(id);
    }
}