package application.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class User {
    private Integer id;
    private String username;
    private String password;
    private ObservableList<FavouriteBook> favorites = FXCollections.observableArrayList();

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean checkPassword(String password) {
        return this.password.equals(password);
    }

    public ObservableList<FavouriteBook> getFavorites() {
        return favorites;
    }

    public void setFavorites(ObservableList<FavouriteBook> favorites) {
        this.favorites = favorites;
    }
}
