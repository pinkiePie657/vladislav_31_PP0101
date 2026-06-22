package ru.factory.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;
import ru.factory.dao.DatabaseHandler;

import java.sql.*;
public class RegisterController {
    @FXML private TextField regUserField;
    @FXML private PasswordField regPassField;
    @FXML
    private void onRegisterClick() {
        String user = regUserField.getText();
        String pass = regPassField.getCharacters().toString();
        if (user.isEmpty() || pass.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Заполните все поля!").showAndWait();
            return;
        }
        String hashedPass = BCrypt.hashpw(pass, BCrypt.gensalt());
        String role = "WORKER";
        try (Connection conn = DatabaseHandler.getConnection()) {
            String sql = "INSERT INTO users (username, password_hash, role) VALUES (?, ?, ?)";
            PreparedStatement st = conn.prepareStatement(sql);
            st.setString(1, user);
            st.setString(2, hashedPass);
            st.setString(3, role);
            st.executeUpdate();
            new Alert(Alert.AlertType.INFORMATION, "Регистрация успешна!").showAndWait();
            toLogin();
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Ошибка: Логин уже занят!").showAndWait();
        }
    }
    @FXML
    private void toLogin() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/login.fxml"));
            Stage stage = (Stage) regUserField.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) { e.printStackTrace(); }
    }
}