package com.example.financialsystem;

// Абстрактный класс для представления пользователя
public abstract class AbstractUser {
    // Поля для хранения ID и имени пользователя
    protected final int id;
    protected final String username;

    // Конструктор для инициализации пользователя
    public AbstractUser(String username, int id) {
        this.username = username;
        this.id = id;
    }

    // Получаем имя пользователя
    public String getUsername() {
        return username;
    }

    // Получаем ID пользователя
    public int getId() {
        return id;
    }
}