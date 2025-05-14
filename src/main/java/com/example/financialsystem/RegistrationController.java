package com.example.financialsystem;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RegistrationController {
    @FXML
    private TextField nameField;
    @FXML private TextField surnameField;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
    @FXML private Label successLabel;
    @FXML
    private void handleRegistration() {
        String name = nameField.getText().trim();
        String surname = surnameField.getText().trim();
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        String role = "USER";

        if (name.isEmpty() || surname.isEmpty() || username.isEmpty() || password.isEmpty()) {
            showError("All fields must be filled in");
            return;
        }
        if (!username.matches("^[a-zA-Z0-9_]{3,20}$")) {
            showError("Username: 3-20 characters, letters, numbers or underscores only");
            return;
        }
        if (password.length() < 6) {
            showError("Password must be at least 6 characters long");
            return;
        }

        try {
            if (DatabaseManager.usernameExists(username)) {
                showError("Username is already taken");
                return;
            }
            String sql = "INSERT INTO users (username, password, name, surname, role) VALUES (?, ?, ?, ?, ?) RETURNING id";
            try (Connection conn = DatabaseManager.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, username);
                stmt.setString(2, password);
                stmt.setString(3, name);
                stmt.setString(4, surname);
                stmt.setString(5, role);

                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    showSuccess("Registration was successful!");
                    closeWindow();
                } else {
                    showError("Failed to register");
                }
            }
        } catch (SQLException e) {
            showError("Database error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        successLabel.setVisible(false);
    }

    private void showSuccess(String message) {
        successLabel.setText(message);
        successLabel.setVisible(true);
        errorLabel.setVisible(false);
    }

    private void closeWindow() {
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }
}
