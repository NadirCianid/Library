package application;

import application.model.Book;
import application.model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookService {
    private final Connection connection;

    public BookService(Connection connection) {
        this.connection = connection;
    }

    public void addBook(Book book, User user) {
        String sql = """
                WITH inserted_book AS (
                    INSERT INTO books (title, author, year, genre, url)
                    VALUES (?, ?, ?, ?, ?)
                    ON CONFLICT ON CONSTRAINT book_unique DO NOTHING
                    RETURNING id
                )
                INSERT INTO book_availability (book_id, is_available, updated_by)
                SELECT id, false, ?
                FROM inserted_book
                ON CONFLICT (book_id) DO NOTHING;
                """;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, book.getTitle());
            pstmt.setString(2, book.getAuthor());
            pstmt.setInt(3, book.getYear());
            pstmt.setString(4, book.getGenre());
            pstmt.setString(5, book.getUrl());
            pstmt.setInt(6, user.getId());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to add book", e);
        }
    }

    public void removeBook(Book book) {
        String sql = "DELETE FROM books WHERE title = ? AND author = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, book.getTitle());
            pstmt.setString(2, book.getAuthor());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to remove book", e);
        }
    }

    public ObservableList<Book> getAllBooks() {
        ObservableList<Book> books = FXCollections.observableArrayList();
        String sql = "SELECT b.*, is_available FROM books b JOIN book_availability ba ON b.id = ba.book_id";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                books.add(resultSetToBook(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get books", e);
        }

        return books;
    }

    public FilteredList<Book> searchBooks(String title, String author, String year, String genre) {
        List<Book> bookList = new ArrayList<>();
        StringBuilder sql = new StringBuilder("""
                SELECT b.*, ba.is_available, COALESCE(AVG(br.rating), 0.0) as rating
                            FROM books b
                            JOIN book_availability ba ON b.id = ba.book_id
                            LEFT JOIN books_reviews br ON b.id = br.book_id
                            WHERE ba.is_available = true
                """);

        if (title != null && !title.isEmpty()) {
            sql.append(" AND LOWER(title) LIKE LOWER(?)");
        }
        if (author != null && !author.isEmpty()) {
            sql.append(" AND LOWER(author) LIKE LOWER(?)");
        }
        if (year != null && !year.isEmpty()) {
            sql.append(" AND year = ?");
        }
        if (genre != null && !genre.isEmpty()) {
            sql.append(" AND LOWER(genre) LIKE LOWER(?)");
        }

        // Группируем по книге, чтобы получить средний рейтинг
        sql.append(" GROUP BY b.id, ba.is_available");

        try (PreparedStatement pstmt = connection.prepareStatement(sql.toString())) {
            int paramIndex = 1;

            if (title != null && !title.isEmpty()) {
                pstmt.setString(paramIndex++, "%" + title + "%");
            }
            if (author != null && !author.isEmpty()) {
                pstmt.setString(paramIndex++, "%" + author + "%");
            }
            if (year != null && !year.isEmpty()) {
                pstmt.setInt(paramIndex++, Integer.parseInt(year));
            }
            if (genre != null && !genre.isEmpty()) {
                pstmt.setString(paramIndex, "%" + genre + "%");
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Book book = resultSetToBook(rs);
                    book.setRating(String.format("%1.1f", rs.getDouble("rating")));
                    bookList.add(book);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to search books", e);
        }

        return new FilteredList<>(FXCollections.observableArrayList(bookList));
    }

    private Book resultSetToBook(ResultSet rs) throws SQLException {
        return new Book(
                rs.getObject("id", Integer.class),
                rs.getString("title"),
                rs.getString("author"),
                rs.getInt("year"),
                rs.getString("genre"),
                rs.getString("url"),
                rs.getBoolean("is_available")
        );
    }

    public void toggleBookAvailability(Book book) {
        if (book == null) {
            return;
        }

        String sql = "UPDATE book_availability SET is_available = ? WHERE book_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setBoolean(1, !book.isAvailable());
            pstmt.setInt(2, book.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to toggle book availability", e);
        }
    }
}
