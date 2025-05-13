package com.example.financialsystem;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private TextField passwordField;
    @FXML private ComboBox<String> roleComboBox;

    @FXML
    public void initialize() {
        if (roleComboBox != null) {
            roleComboBox.getItems().addAll("USER", "ANALYST");
            roleComboBox.getSelectionModel().selectFirst();
        }
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        String selectedRole = roleComboBox.getValue();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Please enter username and password");
            return;
        }
        if (selectedRole == null) {
            showAlert("Error", "Please select a role");
            return;
        }



    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

}