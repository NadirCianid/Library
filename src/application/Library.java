package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;

class Library {
    private ObservableList<Book> books = FXCollections.observableArrayList();

    public void addBook(Book book) {
        books.add(book);
    }

    public void removeBook(Book book) {
        books.remove(book);
    }

    public ObservableList<Book> getAllBooks() {
        return books;
    }

    public FilteredList<Book> searchBooks(String title, String author, Integer year, String genre) {
        return new FilteredList<>(books, book ->
                (title == null || title.isEmpty() || book.getTitle().toLowerCase().contains(title.toLowerCase())) &&
                        (author == null || author.isEmpty() || book.getAuthor().toLowerCase().contains(author.toLowerCase())) &&
                        (year == null || book.getYear() == year) &&
                        (genre == null || genre.isEmpty() || book.getGenre().toLowerCase().contains(genre.toLowerCase())) &&
                        book.isAvailable()
        );
    }
}
