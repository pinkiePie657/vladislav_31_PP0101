package ru.factory.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import ru.factory.model.CurrentUser;

import java.io.IOException;
import java.net.URL;

public class MainController {

    @FXML private StackPane contentArea;

    private void loadView(String fxmlPath) {
        try {
            URL resource = getClass().getResource(fxmlPath);
            if (resource == null) {
                resource = getClass().getClassLoader().getResource(fxmlPath.substring(1));
            }
            if (resource == null) {
                System.err.println("ОШИБКА: FXML не найден: " + fxmlPath);
                return;
            }
            Node node = FXMLLoader.load(resource);
            contentArea.getChildren().setAll(node);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML private void showInventory() { loadView("/inventory.fxml"); }
    @FXML private void showProduction() { loadView("/production.fxml"); }
    @FXML private void showChat() { loadView("/chat.fxml"); }
    @FXML private void showProfile() { loadView("/profile.fxml"); }
    @FXML
    private void showStatistics() {
        Alert statsAlert = new Alert(Alert.AlertType.INFORMATION);
        statsAlert.setTitle("📊 Статистика завода");
        statsAlert.setHeaderText("Ключевые показатели");

        String content = """
                🧱 Всего на складе: ~7 000 шт.
                ⚠️ Позиций с низким запасом: 2
                🔥 Активных партий в обжиге: 2
                💬 Сообщений в чате сегодня: 12
                👥 Активных пользователей: 4

                Система работает стабильно.
                """;
        statsAlert.setContentText(content);
        statsAlert.getDialogPane().setPrefWidth(420);
        statsAlert.showAndWait();
    }
    @FXML
    private void showLogin() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/login.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Вход в систему");
            stage.setScene(new Scene(root, 420, 380));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void handleLogout() {
        CurrentUser.setUser(null);
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/dashboard.fxml"));
            Stage stage = (Stage) contentArea.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Панель управления заводом");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void login() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/login.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Вход в систему");
            stage.setScene(new Scene(root, 420, 380));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}