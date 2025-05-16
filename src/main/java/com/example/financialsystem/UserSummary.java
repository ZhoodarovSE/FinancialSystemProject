package com.example.financialsystem;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;

public class UserSummary {
    private final SimpleStringProperty username;
    private final SimpleStringProperty name;
    private final SimpleStringProperty surname;
    private final SimpleDoubleProperty totalIncome;
    private final SimpleDoubleProperty totalExpense;

    public UserSummary(String username, String name, String surname, double totalIncome, double totalExpense) {
        this.username = new SimpleStringProperty(username);
        this.name = new SimpleStringProperty(name);
        this.surname = new SimpleStringProperty(surname);
        this.totalIncome = new SimpleDoubleProperty(totalIncome);
        this.totalExpense = new SimpleDoubleProperty(totalExpense);
    }

    public SimpleStringProperty usernameProperty() {
        return username;
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public SimpleStringProperty surnameProperty() {
        return surname;
    }

    public SimpleDoubleProperty totalIncomeProperty() {
        return totalIncome;
    }

    public SimpleDoubleProperty totalExpenseProperty() {
        return totalExpense;
    }

    public double getBalance() {
        return totalIncome.get() - totalExpense.get();
    }
}
