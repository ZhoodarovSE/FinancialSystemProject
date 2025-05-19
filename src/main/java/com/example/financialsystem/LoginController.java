package com.example.financialsystem;

// Импорты для работы с интерфейсом JavaFX и базой данных
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.SQLException;

// Класс управляет окном входа в систему
public class LoginController {
    @FXML private TextField usernameField;
    @FXML private TextField passwordField;
    @FXML private ComboBox<String> roleComboBox;

    // Настраиваем окно при запуске
    @FXML
    public void initialize() {
        if (roleComboBox != null) {
            roleComboBox.getItems().addAll("USER", "ANALYST");
            roleComboBox.getSelectionModel().selectFirst(); // Выбираем первую роль по умолчанию
        }
    }

    // Обрабатываем нажатие кнопки входа
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

        try {
            // Проверяем логин и пароль в базе
            if (DatabaseManager.authenticateUser(username, password)) {
                String roleFromDb = DatabaseManager.getUserRole(username);
                if (roleFromDb == null) {
                    showAlert("Error", "Failed to retrieve user role");
                    return;
                }

                // Проверяем, совпадает ли выбранная роль с ролью в базе
                if (!selectedRole.equalsIgnoreCase(roleFromDb)) {
                    showAlert("Error", "Selected role does not match your account role");
                    return;
                }

                openMainWindow(username, roleFromDb);
            } else {
                showAlert("Error", "Invalid username or password");
            }
        } catch (SQLException e) {
            showAlert("Database Error", "Authentication failed: " + e.getMessage());
        }
    }


    private void openMainWindow(String username, String role) {
        try {
            FXMLLoader loader;
            if (role.equalsIgnoreCase("ANALYST")) {

                loader = new FXMLLoader(getClass().getResource("/com/example/financialsystem/AnalystMainWindow.fxml"));
                Parent root = loader.load(); // Загружаем интерфейс
                AnalystMainWindowController controller = loader.getController(); // Получаем контроллер
                int analystId = DatabaseManager.getUserId(username); // Получаем ID аналитика
                controller.setAnalyst(username, analystId); // Устанавливаем аналитика
                Stage stage = new Stage(); // Создаём новое окно
                stage.setTitle("Finance Manager - " + username); // Задаём заголовок
                stage.setScene(new Scene(root, 800, 600)); // Устанавливаем сцену
                stage.show(); // Показываем окно
            } else {

                loader = new FXMLLoader(getClass().getResource("/com/example/financialsystem/UserMainWindow.fxml"));
                Parent root = loader.load(); // Загружаем интерфейс
                UserMainWindowController controller = loader.getController(); // Получаем контроллер
                int userId = DatabaseManager.getUserId(username); // Получаем ID пользователя
                controller.setUser(new RegularUser(username, userId)); // Устанавливаем пользователя
                Stage stage = new Stage(); // Создаём новое окно
                stage.setTitle("Finance Manager - " + username); // Задаём заголовок
                stage.setScene(new Scene(root, 800, 600)); // Устанавливаем сцену
                stage.show(); // Показываем окно
            }

            // Закрываем окно логина
            Stage loginStage = (Stage) usernameField.getScene().getWindow();
            loginStage.close();
        } catch (IOException | SQLException e) {
            showAlert("Error", "Failed to open main window: " + e.getMessage());
        }
    }


    @FXML
    private void showRegistrationWindow() {
        try {
            // Загружаем окно регистрации
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/financialsystem/Registration.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Registration");
            stage.setScene(new Scene(root, 300, 400));
            stage.show();
        } catch (IOException e) {
            showAlert("Error", "Cannot open registration window: " + e.getMessage()); // Показываем ошибку
        }
    }


    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}