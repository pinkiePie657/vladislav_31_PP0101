package ru.factory.dao;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ru.factory.model.Brick;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler {
    private static final String URL = "jdbc:postgresql://localhost:5432/brick_factory";
    private static final String USER = "brick_app";
    private static final String PASS = "user12";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    public static void saveMessage(int senderId, String message) {
        String sql = "CALL sp_send_message(?, ?)";
        try (Connection conn = getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {
            cs.setInt(1, senderId);
            cs.setString(2, message);
            cs.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<String> getMessages() {
        List<String> messages = new ArrayList<>();
        DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HH:mm");
        String sql = "SELECT COALESCE(u.username, 'Гость') AS username, m.message, m.sent_at " +
                "FROM chat_messages m LEFT JOIN users u ON m.sender_id = u.id " +
                "ORDER BY m.sent_at DESC LIMIT 50";
        try (Connection conn = getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Timestamp ts = rs.getTimestamp("sent_at");
                String timeStr = ts != null ? "[" + ts.toLocalDateTime().format(timeFmt) + "] " : "";
                messages.add(timeStr + rs.getString("username") + ": " + rs.getString("message"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }

    public static ObservableList<Brick> getAllBricks() {
        ObservableList<Brick> list = FXCollections.observableArrayList();
        String sql = "SELECT id, name, quantity, status FROM bricks ORDER BY id";
        try (Connection conn = getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Brick brick = new Brick(
                        rs.getString("name"),
                        rs.getInt("quantity"),
                        rs.getString("status")
                );
                list.add(brick);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static void addBrick(String name, int quantity) {
        String status = (quantity == 0) ? "Нет на складе" : (quantity < 1000) ? "Мало" : "В наличии";
        String sql = "INSERT INTO bricks (name, quantity, status) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setInt(2, quantity);
            ps.setString(3, status);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateBrickQuantity(String name, int newQuantity) {
        String status = (newQuantity == 0) ? "Нет на складе" : (newQuantity < 1000) ? "Мало" : "В наличии";
        String sql = "UPDATE bricks SET quantity = ?, status = ? WHERE name = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, newQuantity);
            ps.setString(2, status);
            ps.setString(3, name);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteBrick(String name) {
        String sql = "DELETE FROM bricks WHERE name = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}