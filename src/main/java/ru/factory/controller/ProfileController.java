package ru.factory.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import ru.factory.model.CurrentUser;
import ru.factory.dao.DatabaseHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
    public class ProfileController {
    @FXML private TextField nameField;
    @FXML private DatePicker birthDatePicker;

    @FXML
    public void initialize() {
        if (CurrentUser.getUser() != null) {
            nameField.setText(CurrentUser.getUser().getUsername()); // можно расширить
        }
    }
    @FXML
    private void saveProfile() {
        if (CurrentUser.getUser() == null) {
            new Alert(Alert.AlertType.WARNING, "Авторизуйтесь для изменения профиля").showAndWait();
            return;
        }
        LocalDate date = birthDatePicker.getValue();
        if (date == null) {
            new Alert(Alert.AlertType.ERROR, "Выберите дату рождения").showAndWait();
            return;
        }
        String sql = "UPDATE users SET full_name = ?, birth_date = ? WHERE id = ?";
        try (Connection conn = DatabaseHandler.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nameField.getText().trim());
            ps.setDate(2, java.sql.Date.valueOf(date));
            ps.setInt(3, CurrentUser.getUser().getId()); // добавил getId() в User если нужно
            ps.executeUpdate();
            new Alert(Alert.AlertType.INFORMATION, "Профиль обновлён!").showAndWait();
        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Ошибка сохранения").showAndWait();
        }
    }
    @FXML
    private void backToDashboard() {
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }
}