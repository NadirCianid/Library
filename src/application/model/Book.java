package application.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class Book {
    private Integer id;
    private String title;
    private String author;
    private int year;
    private String genre;
    private String url;
    private BooleanProperty isAvailable;
    private String rating;

    public Book(Integer id, String title, String author, int year, String genre, String url, boolean isAvailable) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.year = year;
        this.genre = genre;
        this.url = url;
        this.isAvailable = new SimpleBooleanProperty(isAvailable);
        this.rating = "-.-";
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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


    public void setAvailable(boolean available) {
        isAvailable.set(available);
    }


    public BooleanProperty isAvailableProperty() {
        return isAvailable;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    @Override
    public String toString() {
        return title + " by " + author + " (" + year + "), Genre: " + genre;
    }
}
