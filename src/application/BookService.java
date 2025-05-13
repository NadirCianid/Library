package application;

import application.model.Book;
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

    public void addBook(Book book)  {
        String sql = """
                INSERT INTO books (title, author, year, genre, url, is_available)
                VALUES (?, ?, ?, ?, ?, ?)
                ON CONFLICT ON CONSTRAINT book_unique DO NOTHING""";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, book.getTitle());
            pstmt.setString(2, book.getAuthor());
            pstmt.setInt(3, book.getYear());
            pstmt.setString(4, book.getGenre());
            pstmt.setString(5, book.getUrl());
            pstmt.setBoolean(6, book.isAvailable());

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
        String sql = "SELECT * FROM books";

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

    public FilteredList<Book> searchBooks(String title, String author, Integer year, String genre) {
        List<Book> bookList = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM books WHERE is_available = true");

        if (title != null && !title.isEmpty()) {
            sql.append(" AND LOWER(title) LIKE LOWER(?)");
        }
        if (author != null && !author.isEmpty()) {
            sql.append(" AND LOWER(author) LIKE LOWER(?)");
        }
        if (year != null) {
            sql.append(" AND year = ?");
        }
        if (genre != null && !genre.isEmpty()) {
            sql.append(" AND LOWER(genre) LIKE LOWER(?)");
        }

        try (PreparedStatement pstmt = connection.prepareStatement(sql.toString())) {
            int paramIndex = 1;

            if (title != null && !title.isEmpty()) {
                pstmt.setString(paramIndex++, "%" + title + "%");
            }
            if (author != null && !author.isEmpty()) {
                pstmt.setString(paramIndex++, "%" + author + "%");
            }
            if (year != null) {
                pstmt.setInt(paramIndex++, year);
            }
            if (genre != null && !genre.isEmpty()) {
                pstmt.setString(paramIndex, "%" + genre + "%");
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    bookList.add(resultSetToBook(rs));
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
        if(book == null) {
            return;
        }

        String sql = "UPDATE books SET is_available = ? WHERE title = ? AND author = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setBoolean(1, !book.isAvailable());
            pstmt.setString(2, book.getTitle());
            pstmt.setString(3, book.getAuthor());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to toggle book availability", e);
        }
    }
}
