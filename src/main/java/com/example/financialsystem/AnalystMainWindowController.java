package com.example.financialsystem;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class AnalystMainWindowController {
    @FXML private TableView<UserSummary> usersTable;
    @FXML private ComboBox<String> periodComboBox;
    @FXML private AnchorPane usersPane;
    @FXML private AnchorPane userProfilePane;
    @FXML private TextField searchUsernameField;
    @FXML private Label userInfoLabel;
    @FXML private TableView<Transaction> transactionsTable;
    @FXML private TableView<Goal> goalsTable;
    @FXML private TextArea noteTextArea;
    @FXML private Label warningLabel;
    @FXML private Label analystLabel;

    private Analyst analyst;
    private RegularUser currentUser;

    public void setAnalyst(String username, int analystId) {
        this.analyst = new Analyst(analystId, username);
        analystLabel.setText(analyst.getUsername());
        initialize();
    }

    @FXML
    private void initialize() {
        setupUsersTable();
        setupTransactionsTable();
        setupGoalsTable();
        periodComboBox.getItems().addAll("All Time", "Last Month", "Last Year");
        periodComboBox.setValue("All Time");
        periodComboBox.setOnAction(e -> refresh());
        showAllUsersPane();
        refresh();
    }

    private void setupUsersTable() {
        usersTable.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("username"));
        usersTable.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("name"));
        usersTable.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("surname"));
        usersTable.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("totalIncome"));
        usersTable.getColumns().get(4).setCellValueFactory(new PropertyValueFactory<>("totalExpense"));
    }

    private void setupTransactionsTable() {
        transactionsTable.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("type"));
        transactionsTable.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("amount"));
        transactionsTable.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("description"));
        transactionsTable.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("date"));
    }

    private void setupGoalsTable() {
        goalsTable.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("description"));
        goalsTable.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("targetAmount"));
    }

    @FXML
    private void refresh() {
        try {
            usersTable.setItems(FXCollections.observableArrayList(DatabaseManager.getUserSummaries(periodComboBox.getValue())));
        } catch (SQLException e) {
            showAlert("Error", "Failed to refresh users: " + e.getMessage());
        }
    }

    @FXML
    private void exportAll() {
        try {
            List<UserSummary> users = DatabaseManager.getUserSummaries(periodComboBox.getValue());
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Users Report");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
            File file = fileChooser.showSaveDialog(usersTable.getScene().getWindow());
            if (file != null) {
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                    writer.write("Username,Name,Surname,Total Income,Total Expense,Balance\n");
                    for (UserSummary user : users) {
                        writer.write(String.format("%s,%s,%s,%.2f,%.2f,%.2f\n",
                                user.usernameProperty().get(), user.nameProperty().get(), user.surnameProperty().get(),
                                user.totalIncomeProperty().get(), user.totalExpenseProperty().get(), user.getBalance()));
                    }
                    showAlert("Success", "Users report exported successfully");
                }
            }
        } catch (SQLException | IOException e) {
            showAlert("Error", "Failed to export users: " + e.getMessage());
        }
    }

    @FXML
    private void search() {
        String username = searchUsernameField.getText().trim();
        if (username.isEmpty()) {
            showAlert("Error", "Please enter a username");
            return;
        }
        try {
            int userId = DatabaseManager.getUserId(username);
            currentUser = new RegularUser(username, userId);
            userInfoLabel.setText(String.format("Username: %s, Total Income: %.2f, Total Expense: %.2f, Balance: %.2f",
                    username, currentUser.getTotalIncome(), currentUser.getTotalExpense(),
                    currentUser.getTotalIncome() - currentUser.getTotalExpense()));
            loadTransactions();
            loadGoals();
            checkWarnings();
        } catch (SQLException e) {
            showAlert("Error", "User not found or database error: " + e.getMessage());
        }
    }

    private void loadTransactions() throws SQLException {
        List<Transaction> transactions = new ArrayList<>();
        transactions.addAll(currentUser.getIncomes());
        transactions.addAll(currentUser.getExpenses());
        transactionsTable.setItems(FXCollections.observableArrayList(transactions));
    }

    private void loadGoals() throws SQLException {
        goalsTable.setItems(FXCollections.observableArrayList(currentUser.getGoals()));
    }

    private void checkWarnings() throws SQLException {
        double income = currentUser.getTotalIncome();
        double expense = currentUser.getTotalExpense();
        if (expense > income * 0.8) {
            warningLabel.setText("Warning: High expenses (>80% of income)");
        } else if (income == 0 && expense == 0) {
            warningLabel.setText("Warning: No transactions recorded");
        } else if (income - expense < 0) {
            warningLabel.setText("Warning: Negative balance");
        } else {
            warningLabel.setText("");
        }
    }

    @FXML
    private void addNote() {
        if (currentUser == null) {
            showAlert("Error", "Please select a user first");
            return;
        }
        String note = noteTextArea.getText().trim();
        if (note.isEmpty()) {
            showAlert("Error", "Note cannot be empty");
            return;
        }
        try {
            DatabaseManager.addNote(currentUser.getId(), analyst.getId(), note, LocalDate.now());
            noteTextArea.clear();
            showAlert("Success", "Note saved successfully");
        } catch (SQLException e) {
            showAlert("Error", "Failed to save note: " + e.getMessage());
        }
    }

    @FXML
    private void exportProfile() {
        if (currentUser == null) {
            showAlert("Error", "Please select a user first");
            return;
        }
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save User Profile");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
            File file = fileChooser.showSaveDialog(transactionsTable.getScene().getWindow());
            if (file != null) {
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                    writer.write("Type,Amount,Description,Date\n");
                    for (Transaction t : transactionsTable.getItems()) {
                        writer.write(String.format("%s,%.2f,%s,%s\n",
                                t.getType(), t.getAmount(), t.getDescription(), t.getDate()));
                    }
                    writer.write("\nGoals:\nGoal,Target Amount\n");
                    for (Goal g : goalsTable.getItems()) {
                        writer.write(String.format("%s,%.2f\n", g.getDescription(), g.getTargetAmount()));
                    }
                    showAlert("Success", "User profile exported successfully");
                }
            }
        } catch (IOException e) {
            showAlert("Error", "Failed to export user profile: " + e.getMessage());
        }
    }

    @FXML
    private void logout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/financialsystem/Login.fxml"));
            Scene scene = new Scene(loader.load(), 600, 400);
            Stage stage = new Stage();
            stage.setTitle("Financial Management System");
            stage.setScene(scene);
            stage.show();

            Stage currentStage = (Stage) usersPane.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            showAlert("Error", "Failed to logout: " + e.getMessage());
        }
    }

    @FXML
    private void showAllUsersPane() {
        usersPane.setVisible(true);
        userProfilePane.setVisible(false);
    }

    @FXML
    private void userProfilePane() {
        usersPane.setVisible(false);
        userProfilePane.setVisible(true);
        clearProfile();
    }

    private void clearProfile() {
        searchUsernameField.clear();
        userInfoLabel.setText("Enter a username to view profile");
        transactionsTable.getItems().clear();
        goalsTable.getItems().clear();
        noteTextArea.clear();
        warningLabel.setText("");
        currentUser = null;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}