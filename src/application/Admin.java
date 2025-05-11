package application;

class Admin extends User {

    public Admin(String username, String password) {
        super(username, password);
    }

    // Добавление книги
    public void addBook(Library library, Book book) {
        library.addBook(book);
    }

    // Удаление книги
    public void removeBook(Library library, Book book) {
        library.removeBook(book);
    }

    // Изменение доступности книги
    public void setBookAvailability(Book book, boolean isAvailable) {
        book.setAvailable(isAvailable);
    }
}
