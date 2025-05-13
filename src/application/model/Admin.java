package application.model;

import application.BookService;

public class Admin extends User {

    public Admin(String username, String password) {
        super(username, password);
    }

    // Добавление книги
    public void addBook(BookService bookService, Book book) {
        bookService.addBook(book);
    }

    // Удаление книги
    public void removeBook(BookService bookService, Book book) {
        bookService.removeBook(book);
    }

    // Изменение доступности книги
    public void setBookAvailability(Book book, boolean isAvailable) {
        book.setAvailable(isAvailable);
    }
}
