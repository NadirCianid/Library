// UserService.java
package application;

import application.model.Admin;
import application.model.User;
import javafx.scene.control.Alert;

import java.sql.*;

public class UserService {
    private final Connection connection;

    public UserService(Connection connection) {
        this.connection = connection;
    }

    public User authenticate(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String storedPassword = rs.getString("password");
                if (storedPassword.equals(password)) {
                    boolean isAdmin = rs.getBoolean("is_admin");
                    User user = isAdmin ?
                            new Admin(username, password) :
                            new User(username, password);
                    user.setId(rs.getInt("id"));
                    return user;
                }
            }
        } catch (SQLException e) {
            showAlert("Error", "Database error during authentication");
        }
        return null;
    }

    public boolean registerUser(String username, String password, String confirmPassword) {
        if (validateUserData(username, password, confirmPassword)) return false;

        String sql = "INSERT INTO users (username, password) VALUES (?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.executeUpdate();

            // Получаем сгенерированный ID
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    User user = new User(username, password);
                    user.setId(rs.getInt(1));
                }
            }
            return true;
        } catch (SQLException e) {
            showAlert("Error", "Failed to register user: " + e.getMessage());
            return false;
        }
    }

    private boolean validateUserData(String username, String password, String confirmPassword) {
        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Registration Failed", "All fields must be filled.");
            return true;
        }

        if (!password.equals(confirmPassword)) {
            showAlert("Registration Failed", "Passwords do not match.");
            return true;
        }

        if (userExists(username)) {
            showAlert("Registration Failed", "Username already exists.");
            return true;
        }
        return false;
    }

    private boolean userExists(String username) {
        String sql = "SELECT 1 FROM users WHERE username = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            return pstmt.executeQuery().next();
        } catch (SQLException e) {
            return false;
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}