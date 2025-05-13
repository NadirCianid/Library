package application;

import application.model.Book;
import application.model.BookReadingStatus;
import application.model.FavouriteBook;
import application.model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FavouritesService {
    private final Connection connection;

    public FavouritesService(Connection connection) {
        this.connection = connection;
    }

    public void addToFavorites(User user, Book book) {
        if (user == null || book == null) return;

        String sql = """
                INSERT INTO user_favorites (user_id, book_id) VALUES (?, ?)
                ON CONFLICT (user_id, book_id) DO NOTHING""";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, user.getId());
            pstmt.setInt(2, book.getId());
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
                SELECT b.*, uf.status, ba.is_available FROM books b
                JOIN user_favorites uf ON b.id = uf.book_id
                LEFT JOIN book_availability ba ON  ba.book_id = b.id
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
                favorites.add(new FavouriteBook(user, book, rs.getString("status")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load favorites");
        }

        return favorites;
    }

    public void updateReadingStatus(FavouriteBook favouriteBook, BookReadingStatus newStatus) {
        if (favouriteBook == null || favouriteBook.getUser() == null || favouriteBook.getBook() == null) {
            showAlert("Error", "FavouriteBook, user or book cannot be null");
        }

        String sql = "UPDATE user_favorites SET status = ? WHERE user_id = ? AND book_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, newStatus.name());
            pstmt.setInt(2, favouriteBook.getUser().getId());
            pstmt.setInt(3, favouriteBook.getBook().getId());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update reading status", e);
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
