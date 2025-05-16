package com.example.financialsystem;

import java.time.LocalDate;

public class Note {
    private final int id;
    private final String content;
    private final LocalDate date;
    private final String analystUsername;

    public Note(int id, String content, LocalDate date, String analystUsername) {
        this.id = id;
        this.content = content;
        this.date = date;
        this.analystUsername = analystUsername;
    }

    public int getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getAnalystUsername() {
        return analystUsername;
    }

    @Override
    public String toString() {
        return String.format("[%s] Analyst: %s\n%s", date, analystUsername, content);
    }
}
