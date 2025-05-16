package com.example.financialsystem;

import java.time.LocalDate;

public class Income {
    private final int id;
    private final double amount;
    private final String source;
    private final LocalDate date;

    public Income(int id, double amount, String source, LocalDate date) {
        this.id = id;
        this.amount = amount;
        this.source = source;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public double getAmount() {
        return amount;
    }

    public String getSource() {
        return source;
    }

    public LocalDate getDate() {
        return date;
    }
}