package com.example.financialsystem;

import java.time.LocalDate;

public abstract class Transaction {
    private final int id;
    private final double amount;
    private final LocalDate date;
    private final String description;

    public Transaction(int id, double amount, String description, LocalDate date) {
        this.id = id;
        this.amount = amount;
        this.description = description;
        this.date = date;
    }

    public int getId() {
        return id;
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

    // Абстрактный метод для получения типа транзакции
    public abstract String getType();
}
