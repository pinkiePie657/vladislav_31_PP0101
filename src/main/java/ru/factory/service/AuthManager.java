package ru.factory.service;
import org.mindrot.jbcrypt.BCrypt;
import ru.factory.dao.DatabaseHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
public class AuthManager {
    public static boolean registerUser(String username, String password, String fullName, String role) {
        String insertQuery = "INSERT INTO users (username, password_hash, full_name, role) VALUES (?, ?, ?, ?)";
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        try (Connection conn = DatabaseHandler.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertQuery)) {
            pstmt.setString(1, username);
            pstmt.setString(2, hashedPassword);
            pstmt.setString(3, fullName);
            pstmt.setString(4, role);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}