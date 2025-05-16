package com.example.financialsystem;

public class Analyst {
    private int id;
    private String username;

    public Analyst(int id, String username) {
        this.id = id;
        this.username = username;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }
}