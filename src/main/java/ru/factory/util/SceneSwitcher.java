package ru.factory.util;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.net.URL;
public class SceneSwitcher {
    public static void toMainDashboard(ActionEvent event) {
        try {
            URL fxmlLocation = SceneSwitcher.class.getResource("/dashboard.fxml");

            if (fxmlLocation == null) {
                System.err.println("ОШИБКА: Файл dashboard.fxml не найден!");
                return;
            }
            Parent root = FXMLLoader.load(fxmlLocation);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Панель управления заводом");
            stage.show();
        } catch (Exception e) {
            System.err.println("Ошибка при загрузке дашборда:");
            e.printStackTrace();
        }
    }
}