package application;

import application.model.ReviewData;
import javafx.scene.control.Alert;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ReviewService {
    private final Connection connection;

    public ReviewService(Connection connection) {
        this.connection = connection;
    }

    public void saveReview(ReviewData review) {
        try {
            String sql = "INSERT INTO books_reviews (user_id, book_id, is_read, rating, review, review_date) " +
                    "VALUES (?, ?, ?, ?, ?, now()) " +
                    "ON CONFLICT (user_id, book_id) DO UPDATE SET " +
                    "rating = EXCLUDED.rating, " +
                    "review = EXCLUDED.review";

            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, review.getUser().getId());
                pstmt.setInt(2, review.getBook().getId());
                pstmt.setBoolean(3, review.getRead());
                pstmt.setInt(4, review.getRating());
                pstmt.setString(5, review.getText());
                pstmt.executeUpdate();
            }

            showAlert("Success", "Review saved successfully");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to save review");
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
