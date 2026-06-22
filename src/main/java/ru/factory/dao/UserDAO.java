package ru.factory.dao;
import org.mindrot.jbcrypt.BCrypt;
import ru.factory.model.User;

import java.sql.*;
public class UserDAO {
    public User signIn(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = DatabaseHandler.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
            st.setString(1, username);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                if (BCrypt.checkpw(password, rs.getString("password_hash"))) {
                    return new User(rs.getInt("id"), rs.getString("username"), rs.getString("role"));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }
}