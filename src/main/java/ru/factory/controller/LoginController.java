package ru.factory.controller;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;
import ru.factory.dao.DatabaseHandler;
import ru.factory.model.CurrentUser;
import ru.factory.model.User;
import ru.factory.util.SceneSwitcher;
import java.sql.*;
public class LoginController {
    @FXML private TextField userField;
    @FXML private PasswordField passField;
    @FXML
    private void onLoginClick() {
        try (Connection conn = DatabaseHandler.getConnection()) {
            String sql = "SELECT * FROM users WHERE username = ?";
            PreparedStatement st = conn.prepareStatement(sql);
            st.setString(1, userField.getText());
            ResultSet rs = st.executeQuery();
            if (rs.next() && BCrypt.checkpw(passField.getText(), rs.getString("password_hash"))) {
                CurrentUser.setUser(new User(rs.getInt("id"), rs.getString("username"), rs.getString("role")));
                SceneSwitcher.toMainDashboard(new javafx.event.ActionEvent(userField, null));
            } else {
                new Alert(Alert.AlertType.ERROR, "Неверный логин или пароль").showAndWait();
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }
    @FXML
    private void guestLogin() {
        CurrentUser.setUser(null);
        SceneSwitcher.toMainDashboard(new javafx.event.ActionEvent(userField, null));
    }
    @FXML
    private void toRegister() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/register.fxml"));
            Stage stage = (Stage) userField.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) { e.printStackTrace(); }
    }

}