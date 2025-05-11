// LibraryService.java
package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;

public class LibraryService {
    private Library library;

    public LibraryService() {
        library = new Library();
    }

    public void initializeSampleData() {
        library.addBook(new Book("1984", "George Orwell", 1949, "Dystopian", "https://www.planetebook.com/free-ebooks/1984.pdf"));
        library.addBook(new Book("The Idiot", "Fyodor Dostoevsky", 1869, "Fiction", "https://www.planetebook.com/free-ebooks/the-idiot.pdf"));
        library.addBook(new Book("To Kill a Mockingbird", "Harper Lee", 1960, "Fiction", "https://giove.isti.cnr.it/demo/eread/libri/angry/mockingbird.pdf"));
        library.addBook(new Book("Brave New World", "Aldous Huxley", 1932, "Dystopian", "https://gutenberg.ca/ebooks/huxleya-bravenewworld/huxleya-bravenewworld-00-e.html"));
    }

    public ObservableList<Book> getAllBooks() {
        return library.getAllBooks();
    }

    public void addBook(Book book) {
        library.addBook(book);
    }

    public void removeBook(Book book) {
        library.removeBook(book);
    }

    public void toggleBookAvailability(Book book) {
        if (book != null) {
            book.setAvailable(!book.isAvailable());
        }
    }

    public FilteredList<Book> searchBooks(String title, String author, Integer year, String genre) {
        return library.searchBooks(title, author, year, genre);
    }
}