package com.example.financialsystem;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class UserMainWindowController {
    @FXML private TableView<Income> incomeTable;
    @FXML private TableView<Expense> expenceTable;
    @FXML private TableView<Income> budgetIncomeTable;
    @FXML private TableView<Expense> budgetExpenceTable;
    @FXML private TableView<Goal> goalTable;
    @FXML private AnchorPane incomePane;
    @FXML private AnchorPane expencePane;
    @FXML private AnchorPane budgetPane;
    @FXML private AnchorPane goalPane;
    @FXML private AnchorPane advicePane;
    @FXML private AnchorPane reportPane;
    @FXML private TextField incomeAmountField;
    @FXML private TextField sourceField;
    @FXML private DatePicker dateIncomePicker;
    @FXML private TextField expenseAmountField;
    @FXML private TextField forWhatField;
    @FXML private DatePicker dateExpensePicker;
    @FXML private Label totalIncomeLabel;
    @FXML private Label totalExpenseLabel;
    @FXML private Label balanceLabel;
    @FXML private TextField goalField;
    @FXML private TextField goalAmountField;
    @FXML private Label reportIncomeLabel;
    @FXML private Label reportAmountLabel;
    @FXML private Label reportBalanceLabel;
    @FXML private TextArea reportTextArea;
    @FXML private TextArea adviceTextArea;
    @FXML private Label userLabel;

    private RegularUser user;

    @FXML
    private void initialize() {
        setupTableColumns();
    }

    public void setUser(RegularUser user) {
        this.user = user;
        userLabel.setText(user.getUsername());
        loadTables();
        showIncomePane();
    }

    private void setupTableColumns() {
        incomeTable.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("amount"));
        incomeTable.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("source"));
        incomeTable.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("date"));

        expenceTable.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("amount"));
        expenceTable.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("forWhat"));
        expenceTable.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("date"));

        budgetIncomeTable.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("amount"));
        budgetIncomeTable.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("source"));
        budgetIncomeTable.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("date"));

        budgetExpenceTable.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("amount"));
        budgetExpenceTable.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("forWhat"));
        budgetExpenceTable.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("date"));

        goalTable.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("description"));
        goalTable.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("targetAmount"));
    }

    private void loadTables() {
        try {
            incomeTable.setItems(FXCollections.observableArrayList(user.getIncomes()));
            expenceTable.setItems(FXCollections.observableArrayList(user.getExpenses()));
            budgetIncomeTable.setItems(FXCollections.observableArrayList(user.getIncomes()));
            budgetExpenceTable.setItems(FXCollections.observableArrayList(user.getExpenses()));
            goalTable.setItems(FXCollections.observableArrayList(user.getGoals()));
            updateBudgetLabels();
            updateReport();
        } catch (SQLException e) {
            showAlert("Error", "Failed to load data: " + e.getMessage());
        }
    }

    private void updateBudgetLabels() throws SQLException {
        double totalIncome = user.getTotalIncome();
        double totalExpense = user.getTotalExpense();
        totalIncomeLabel.setText(String.format("%.2f", totalIncome));
        totalExpenseLabel.setText(String.format("%.2f", totalExpense));
        balanceLabel.setText(String.format("%.2f", totalIncome - totalExpense));
    }

    @FXML
    private void addIncome() {
        try {
            double amount = Double.parseDouble(incomeAmountField.getText());
            String source = sourceField.getText().trim();
            LocalDate date = dateIncomePicker.getValue();
            if (amount <= 0) {
                showAlert("Error", "Amount must be greater than 0");
                return;
            }
            if (source.isEmpty()) {
                showAlert("Error", "Source cannot be empty");
                return;
            }
            if (date == null) {
                showAlert("Error", "Please select a date");
                return;
            }
            user.addIncome(amount, source, date);
            loadTables();
            clearIncome();
            showAlert("Success", "Income added successfully");
        } catch (NumberFormatException e) {
            showAlert("Error", "Invalid amount format");
        } catch (SQLException e) {
            showAlert("Error", "Failed to add income: " + e.getMessage());
        }
    }

    @FXML
    private void updateIncome() {
        Income selected = incomeTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Error", "Please select an income to update");
            return;
        }
        try {
            double amount = Double.parseDouble(incomeAmountField.getText());
            String source = sourceField.getText().trim();
            LocalDate date = dateIncomePicker.getValue();
            if (amount <= 0) {
                showAlert("Error", "Amount must be greater than 0");
                return;
            }
            if (source.isEmpty()) {
                showAlert("Error", "Source cannot be empty");
                return;
            }
            if (date == null) {
                showAlert("Error", "Please select a date");
                return;
            }
            user.updateIncome(selected.getId(), amount, source, date);
            loadTables();
            clearIncome();
            showAlert("Success", "Income updated successfully");
        } catch (NumberFormatException e) {
            showAlert("Error", "Invalid amount format");
        } catch (SQLException e) {
            showAlert("Error", "Failed to update income: " + e.getMessage());
        }
    }

    @FXML
    private void deleteIncome() {
        Income selected = incomeTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Error", "Please select an income to delete");
            return;
        }
        try {
            user.deleteIncome(selected.getId());
            loadTables();
            clearIncome();
            showAlert("Success", "Income deleted successfully");
        } catch (SQLException e) {
            showAlert("Error", "Failed to delete income: " + e.getMessage());
        }
    }

    @FXML
    private void clearIncome() {
        incomeAmountField.clear();
        sourceField.clear();
        dateIncomePicker.setValue(null);
    }

    @FXML
    private void showIncomePane() {
        incomePane.setVisible(true);
        expencePane.setVisible(false);
        budgetPane.setVisible(false);
        goalPane.setVisible(false);
        reportPane.setVisible(false);
        advicePane.setVisible(false);
    }

    @FXML
    private void addExpense() {
        try {
            double amount = Double.parseDouble(expenseAmountField.getText());
            String forWhat = forWhatField.getText().trim();
            LocalDate date = dateExpensePicker.getValue();
            if (amount <= 0) {
                showAlert("Error", "Amount must be greater than 0");
                return;
            }
            if (forWhat.isEmpty()) {
                showAlert("Error", "Description cannot be empty");
                return;
            }
            if (date == null) {
                showAlert("Error", "Please select a date");
                return;
            }
            user.addExpense(amount, forWhat, date);
            loadTables();
            clearExpense();
            showAlert("Success", "Expense added successfully");
        } catch (NumberFormatException e) {
            showAlert("Error", "Invalid amount format");
        } catch (SQLException e) {
            showAlert("Error", "Failed to add expense: " + e.getMessage());
        }
    }

    @FXML
    private void updateExpense() {
        Expense selected = expenceTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Error", "Please select an expense to update");
            return;
        }
        try {
            double amount = Double.parseDouble(expenseAmountField.getText());
            String forWhat = forWhatField.getText().trim();
            LocalDate date = dateExpensePicker.getValue();
            if (amount <= 0) {
                showAlert("Error", "Amount must be greater than 0");
                return;
            }
            if (forWhat.isEmpty()) {
                showAlert("Error", "Description cannot be empty");
                return;
            }
            if (date == null) {
                showAlert("Error", "Please select a date");
                return;
            }
            user.updateExpense(selected.getId(), amount, forWhat, date);
            loadTables();
            clearExpense();
            showAlert("Success", "Expense updated successfully");
        } catch (NumberFormatException e) {
            showAlert("Error", "Invalid amount format");
        } catch (SQLException e) {
            showAlert("Error", "Failed to update expense: " + e.getMessage());
        }
    }

    @FXML
    private void deleteExpense() {
        Expense selected = expenceTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Error", "Please select an expense to delete");
            return;
        }
        try {
            user.deleteExpense(selected.getId());
            loadTables();
            clearExpense();
            showAlert("Success", "Expense deleted successfully");
        } catch (SQLException e) {
            showAlert("Error", "Failed to delete expense: " + e.getMessage());
        }
    }

    @FXML
    private void clearExpense() {
        expenseAmountField.clear();
        forWhatField.clear();
        dateExpensePicker.setValue(null);
    }

    @FXML
    private void showExpensePane() {
        incomePane.setVisible(false);
        expencePane.setVisible(true);
        budgetPane.setVisible(false);
        goalPane.setVisible(false);
        reportPane.setVisible(false);
        advicePane.setVisible(false);
    }

    @FXML
    private void recalculate() {
        try {
            updateBudgetLabels();
            showAlert("Success", "Budget recalculated");
        } catch (SQLException e) {
            showAlert("Error", "Failed to recalculate budget: " + e.getMessage());
        }
    }

    @FXML
    private void showBudgetPane() {
        incomePane.setVisible(false);
        expencePane.setVisible(false);
        budgetPane.setVisible(true);
        goalPane.setVisible(false);
        reportPane.setVisible(false);
        advicePane.setVisible(false);
    }

    @FXML
    private void addGoal() {
        try {
            String description = goalField.getText().trim();
            double targetAmount = Double.parseDouble(goalAmountField.getText());
            if (description.isEmpty()) {
                showAlert("Error", "Goal description cannot be empty");
                return;
            }
            if (targetAmount <= 0) {
                showAlert("Error", "Goal amount must be greater than 0");
                return;
            }
            user.addGoal(description, targetAmount);
            loadTables();
            clearGoal();
            showAlert("Success", "Goal added successfully");
        } catch (NumberFormatException e) {
            showAlert("Error", "Invalid amount format");
        } catch (SQLException e) {
            showAlert("Error", "Failed to add goal: " + e.getMessage());
        }
    }

    @FXML
    private void updateGoal() {
        Goal selected = goalTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Error", "Please select a goal to update");
            return;
        }
        try {
            String description = goalField.getText().trim();
            double targetAmount = Double.parseDouble(goalAmountField.getText());
            if (description.isEmpty()) {
                showAlert("Error", "Goal description cannot be empty");
                return;
            }
            if (targetAmount <= 0) {
                showAlert("Error", "Goal amount must be greater than 0");
                return;
            }
            user.updateGoal(selected.getId(), description, targetAmount);
            loadTables();
            clearGoal();
            showAlert("Success", "Goal updated successfully");
        } catch (NumberFormatException e) {
            showAlert("Error", "Invalid amount format");
        } catch (SQLException e) {
            showAlert("Error", "Failed to update goal: " + e.getMessage());
        }
    }

    @FXML
    private void deleteGoal() {
        Goal selected = goalTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Error", "Please select a goal to delete");
            return;
        }
        try {
            user.deleteGoal(selected.getId());
            loadTables();
            clearGoal();
            showAlert("Success", "Goal deleted successfully");
        } catch (SQLException e) {
            showAlert("Error", "Failed to delete goal: " + e.getMessage());
        }
    }

    @FXML
    private void clearGoal() {
        goalField.clear();
        goalAmountField.clear();
    }

    @FXML
    private void showGoalPane() {
        incomePane.setVisible(false);
        expencePane.setVisible(false);
        budgetPane.setVisible(false);
        goalPane.setVisible(true);
        reportPane.setVisible(false);
        advicePane.setVisible(false);
    }

    @FXML
    private void updateReport() {
        try {
            double totalIncome = user.getTotalIncome();
            double totalExpense = user.getTotalExpense();
            reportIncomeLabel.setText(String.format("%.2f", totalIncome));
            reportAmountLabel.setText(String.format("%.2f", totalExpense));
            reportBalanceLabel.setText(String.format("%.2f", totalIncome - totalExpense));
            reportTextArea.setText(user.generateReport());
        } catch (SQLException e) {
            showAlert("Error", "Failed to update report: " + e.getMessage());
        }
    }

    @FXML
    private void showReportPane() {
        incomePane.setVisible(false);
        expencePane.setVisible(false);
        budgetPane.setVisible(false);
        goalPane.setVisible(false);
        reportPane.setVisible(true);
        advicePane.setVisible(false);
        updateReport();
    }

    @FXML
    private void showAdvicePane() {
        incomePane.setVisible(false);
        expencePane.setVisible(false);
        budgetPane.setVisible(false);
        goalPane.setVisible(false);
        reportPane.setVisible(false);
        advicePane.setVisible(true);
        try {
            List<Note> notes = user.getNotes();
            adviceTextArea.setText(String.join("\n\n", notes.stream().map(Note::toString).toArray(String[]::new)));
        } catch (SQLException e) {
            showAlert("Error", "Failed to load advice: " + e.getMessage());
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

            Stage currentStage = (Stage) incomePane.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            showAlert("Error", "Failed to logout: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}