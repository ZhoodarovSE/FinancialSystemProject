package com.example.financialsystem;

public class Goal {
    private final int id;
    private final String description;
    private final double targetAmount;

    public Goal(int id, String description, double targetAmount) {
        this.id = id;
        this.description = description;
        this.targetAmount = targetAmount;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public double getTargetAmount() {
        return targetAmount;
    }
}