// UserService.java
package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;

import java.util.HashMap;
import java.util.Map;

public class UserService {
    private Map<String, User> users;

    public UserService() {
        users = new HashMap<>();
    }

    public void initializeSampleData() {
        users.put("admin", new Admin("admin", "admin123"));
    }

    public User authenticate(String username, String password) {
        if (users.containsKey(username)){
            User user = users.get(username);
            if (user.checkPassword(password)) {
                return user;
            }
        }
        return null;
    }

    public boolean registerUser(String username, String password, String confirmPassword) {
        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Registration Failed", "All fields must be filled.");
            return false;
        }

        if (!password.equals(confirmPassword)) {
            showAlert("Registration Failed", "Passwords do not match.");
            return false;
        }

        if (users.containsKey(username)) {
            showAlert("Registration Failed", "Username already exists.");
            return false;
        }

        users.put(username, new User(username, password));
        return true;
    }

    public void addToFavorites(User user, Book book) {
        if (user != null && book != null) {
            user.addToFavorites(book);
        }
    }

    public void removeFromFavorites(User user, Book book) {
        if (user != null && book != null) {
            user.removeFromFavorites(book);
        }
    }

    public ObservableList<Book> getFavorites(User user) {
        return user != null ? user.getFavorites() : FXCollections.emptyObservableList();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}