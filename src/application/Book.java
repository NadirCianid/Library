package application;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

class Book {
    private String title;
    private String author;
    private int year;
    private String genre;
    private String url;
    private BooleanProperty isRead;
    private BooleanProperty isAvailable;

    public Book(String title, String author, int year, String genre, String url) {
        this.title = title;
        this.author = author;
        this.year = year;
        this.genre = genre;
        this.url = url;
        this.isRead = new SimpleBooleanProperty(false);
        this.isAvailable = new SimpleBooleanProperty(true);
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public int getYear() {
        return year;
    }

    public String getGenre() {
        return genre;
    }

    public String getUrl() {
        return url;
    }

    public boolean isRead() {
        return isRead.get();
    }

    public boolean isAvailable() {
        return isAvailable.get();
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setRead(boolean read) {
        isRead.set(read);
    }

    public void setAvailable(boolean available) {
        isAvailable.set(available);
    }

    public BooleanProperty isReadProperty() {
        return isRead;
    }

    public BooleanProperty isAvailableProperty() {
        return isAvailable;
    }

    @Override
    public String toString() {
        return title + " by " + author + " (" + year + "), Genre: " + genre;
    }
}
