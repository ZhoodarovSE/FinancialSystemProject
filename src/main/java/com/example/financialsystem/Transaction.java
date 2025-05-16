package com.example.financialsystem;

import java.time.LocalDate;

public class Transaction {
    private final int id;
    private final String type;
    private final double amount;
    private final String description;
    private final LocalDate date;

    public Transaction(int id, String type, double amount, String description, LocalDate date) {
        this.id = id;
        this.type = type;
        this.amount = amount;
        this.description = description;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getDate() {
        return date;
    }
}
