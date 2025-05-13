package application.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class FavouriteBook {

    private final User user;

    private final Book book;

    private final BooleanProperty isRead;

    public FavouriteBook(User user, Book book, boolean isRead) {
        this.user = user;
        this.book = book;
        this.isRead = new SimpleBooleanProperty(isRead);
    }

    public User getUser() {
        return user;
    }

    public Book getBook() {
        return book;
    }

    public boolean isRead() {
        return isRead.get();
    }

    public BooleanProperty isReadProperty() {
        return isRead;
    }

    public void setIsRead(boolean isRead) {
        this.isRead.set(isRead);
    }
}
