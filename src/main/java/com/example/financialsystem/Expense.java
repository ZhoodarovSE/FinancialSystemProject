package com.example.financialsystem;

import java.time.LocalDate;

public class Expense {
    private final int id;
    private final double amount;
    private final String forWhat;
    private final LocalDate date;

    public Expense(int id, double amount, String forWhat, LocalDate date) {
        this.id = id;
        this.amount = amount;
        this.forWhat = forWhat;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public double getAmount() {
        return amount;
    }

    public String getForWhat() {
        return forWhat;
    }

    public LocalDate getDate() {
        return date;
    }
}
