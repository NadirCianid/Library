package application.model;

public class FavouriteBook {

    private final User user;

    private final Book book;

    private final BookReadingStatus status;


    public FavouriteBook(User user, Book book, String status) {
        this.user = user;
        this.book = book;
        this.status = BookReadingStatus.valueOf(status);
    }

    public User getUser() {
        return user;
    }

    public Book getBook() {
        return book;
    }

    public BookReadingStatus getStatus() {
        return status;
    }
}
