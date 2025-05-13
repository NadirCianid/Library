// UserService.java
package application;

import application.model.Admin;
import application.model.Book;
import application.model.FavouriteBook;
import application.model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

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

    public void addToFavorites(User user, Book book) {
        if (user == null || book == null) return;

        String sql = """
                INSERT INTO user_favorites (user_id, book_id, is_read) VALUES (?, ?, ?)
                ON CONFLICT (user_id, book_id) DO UPDATE SET is_read = EXCLUDED.is_read""";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, user.getId());
            pstmt.setInt(2, book.getId());
            pstmt.setBoolean(3, false);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            showAlert("Error", "Failed to add book to favorites");
        }
    }

    public void removeFromFavorites(FavouriteBook favouriteBook) {
        if (favouriteBook == null || favouriteBook.getBook() == null || favouriteBook.getUser() == null) return;

        String sql = "DELETE FROM user_favorites WHERE user_id = ? AND book_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, favouriteBook.getUser().getId());
            pstmt.setInt(2, favouriteBook.getBook().getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            showAlert("Error", "Failed to remove book from favorites");
        }
    }

    public ObservableList<FavouriteBook> getFavorites(User user) {
        if (user == null) return FXCollections.emptyObservableList();

        ObservableList<FavouriteBook> favorites = FXCollections.observableArrayList();
        String sql = """
                SELECT b.*, uf.is_read FROM books b
                JOIN user_favorites uf ON b.id = uf.book_id
                WHERE uf.user_id = ?""";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, user.getId());
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Book book = new Book(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getInt("year"),
                        rs.getString("genre"),
                        rs.getString("url"),
                        rs.getBoolean("is_available")
                );
                favorites.add(new FavouriteBook(user, book, rs.getBoolean("is_read")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load favorites");
        }

        return favorites;
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