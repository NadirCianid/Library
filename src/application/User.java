package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

class User {
    protected String username;
    private String password;
    private ObservableList<Book> favorites = FXCollections.observableArrayList();

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public boolean checkPassword(String password) {
        return this.password.equals(password);
    }

    public void addToFavorites(Book book) {
        if (!favorites.contains(book)) {
            favorites.add(book);
        }
    }

    public void removeFromFavorites(Book book) {
        favorites.remove(book);
    }

    public ObservableList<Book> getFavorites() {
        return favorites;
    }
}
