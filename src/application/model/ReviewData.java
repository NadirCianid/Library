package application.model;

public class ReviewData {
    private final User user;
    private final Book book;
    private final int rating;
    private final String text;
    private final boolean isRead;

    public ReviewData(User user, Book book, int rating, String text, boolean isRead) {
        this.user = user;
        this.book = book;
        this.rating = rating;
        this.text = text;
        this.isRead = isRead;
    }

    public User getUser() {
        return user;
    }

    public Book getBook() {
        return book;
    }

    public int getRating() {
        return rating;
    }

    public String getText() {
        return text;
    }

    public boolean getRead() {
        return isRead;
    }
}