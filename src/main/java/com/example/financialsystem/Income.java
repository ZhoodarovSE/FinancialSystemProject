package com.example.financialsystem;

import java.time.LocalDate;

public class Income extends Transaction {
    private final String source;

    public Income(int id, double amount, String source, LocalDate date) {
        super(id, amount, source, date);
        this.source = source;
    }

    public String getSource() {
        return source;
    }

    @Override
    public String getType() {
        return "INCOME";
    }
}