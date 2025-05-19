package com.example.financialsystem;

import java.time.LocalDate;

public class Expense extends Transaction {
    private final String forWhat;

    public Expense(int id, double amount, String forWhat, LocalDate date) {
        super(id, amount, forWhat, date);
        this.forWhat = forWhat;
    }

    public String getForWhat() {
        return forWhat;
    }

    @Override
    public String getType() {
        return "EXPENSE";
    }
}
