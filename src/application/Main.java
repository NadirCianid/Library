// UserInterface.java
package application;

import application.bd.DBInitException;
import application.bd.DataSource;
import application.model.Admin;
import application.model.Book;
import application.model.FavouriteBook;
import application.model.User;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {
    private UserService userService;
    private BookService bookService;
    private Stage primaryStage;
    private HostServices hostServices;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void init() {
        try {
            DataSource.initializeDataBase();
        } catch (DBInitException e) {
            Platform.runLater(() -> {
                showAlert("Data source is unavailable", "Unable to connect to database. Please contact the admin.");
                Platform.exit();
            });
        }

        bookService = new BookService(DataSource.getConn());
        userService = new UserService(DataSource.getConn());
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.hostServices = getHostServices();
        showLoginScreen();
    }

    private void showLoginScreen() {
        VBox root = new VBox(15);
        root.setAlignment(Pos.CENTER);
        root.setPrefSize(400, 300);

        Label welcomeLabel = new Label("Welcome to Library App");
        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        Button loginButton = new Button("Login");
        loginButton.setOnAction(e -> {
            User user = userService.authenticate(usernameField.getText(), passwordField.getText());
            if (user != null) {
                if (user instanceof Admin) {
                    showAdminMode();
                } else {
                    showUserMode(user);
                }
            } else {
                showAlert("Login Failed", "Invalid username or password.");
            }
        });

        Button registerButton = new Button("Register");
        registerButton.setOnAction(e -> showRegisterScreen());

        root.getChildren().addAll(welcomeLabel, usernameField, passwordField, loginButton, registerButton);

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Library App - Login");
        primaryStage.show();
    }

    private void showAdminMode() {
        VBox root = new VBox(15);
        root.setAlignment(Pos.CENTER);
        root.setPrefSize(400, 300);

        Label adminLabel = new Label("Welcome, Admin!");

        ListView<Book> bookListView = new ListView<>();
        bookListView.setItems(bookService.getAllBooks());

        bookListView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Book book, boolean empty) {
                super.updateItem(book, empty);
                if (empty || book == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    HBox hbox = new HBox(10);
                    Label label = new Label(book.getTitle() + " - " + (book.isAvailable() ? "Available" : "Not Available"));
                    Button toggleAvailabilityButton = new Button(book.isAvailable() ? "Mark as Unavailable" : "Mark as Available");

                    toggleAvailabilityButton.setOnAction(e -> {
                        bookService.toggleBookAvailability(book);
                        bookListView.setItems(bookService.getAllBooks());
                    });

                    hbox.getChildren().addAll(label, toggleAvailabilityButton);
                    setGraphic(hbox);
                }
            }
        });

        Button addBookButton = new Button("Add Book");
        addBookButton.setOnAction(e -> showAddBookScreen());

        Button removeBookButton = new Button("Remove Book");
        removeBookButton.setOnAction(e -> {
            Book selectedBook = bookListView.getSelectionModel().getSelectedItem();
            if (selectedBook != null) {
                bookService.removeBook(selectedBook);
                bookListView.setItems(bookService.getAllBooks());
                showAlert("Book Removed", "The book has been removed from the library.");
            } else {
                showAlert("Error", "Please select a book to remove.");
            }
        });

        Button logoutButton = new Button("Logout");
        logoutButton.setOnAction(e -> showLoginScreen());

        root.getChildren().addAll(adminLabel, bookListView, addBookButton, removeBookButton, logoutButton);

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Library App - Admin Panel");
        primaryStage.show();
    }

    private void showAddBookScreen() {
        VBox root = new VBox(15);
        root.setAlignment(Pos.CENTER);
        root.setPrefSize(400, 300);

        Label addBookLabel = new Label("Add a New Book");

        TextField titleField = new TextField();
        titleField.setPromptText("Title");
        TextField authorField = new TextField();
        authorField.setPromptText("Author");
        TextField yearField = new TextField();
        yearField.setPromptText("Year");
        TextField genreField = new TextField();
        genreField.setPromptText("Genre");
        TextField urlField = new TextField();
        urlField.setPromptText("URL");

        Button addButton = new Button("Add Book");
        addButton.setOnAction(e -> {
            try {
                int year = Integer.parseInt(yearField.getText());
                Book newBook = new Book(
                        null,
                        titleField.getText(),
                        authorField.getText(),
                        year,
                        genreField.getText(),
                        urlField.getText(),
                        true
                );
                bookService.addBook(newBook);
                showAlert("Book Added", "The book has been added to the library.");
            } catch (NumberFormatException ex) {
                showAlert("Error", "Please enter a valid year.");
            }
        });

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> showAdminMode());

        root.getChildren().addAll(addBookLabel, titleField, authorField, yearField, genreField, urlField, addButton, backButton);

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Library App - Add Book");
        primaryStage.show();
    }

    private void showRegisterScreen() {
        VBox root = new VBox(15);
        root.setAlignment(Pos.CENTER);
        root.setPrefSize(400, 300);

        Label registerLabel = new Label("Register a New Account");
        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirm Password");

        Button registerButton = new Button("Register");
        registerButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
            String confirmPassword = confirmPasswordField.getText();

            if (userService.registerUser(username, password, confirmPassword)) {
                showAlert("Registration Successful", "Account created successfully.");
                showLoginScreen();
            }
        });

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> showLoginScreen());

        root.getChildren().addAll(registerLabel, usernameField, passwordField, confirmPasswordField, registerButton, backButton);

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Library App - Register");
        primaryStage.show();
    }

    private void showUserMode(User user) {
        Stage userStage = new Stage();
        BorderPane root = new BorderPane();
        root.setPrefSize(800, 500);

        VBox searchBox = new VBox(10);
        searchBox.setPrefWidth(300);
        searchBox.setAlignment(Pos.TOP_LEFT);
        searchBox.setStyle("-fx-padding: 15;");

        TextField titleField = new TextField();
        titleField.setPromptText("Search by Title");
        TextField authorField = new TextField();
        authorField.setPromptText("Search by Author");
        TextField yearField = new TextField();
        yearField.setPromptText("Search by Year");
        TextField genreField = new TextField();
        genreField.setPromptText("Search by Genre");

        Button searchButton = new Button("Search");

        searchBox.getChildren().addAll(new Label("Search Books"), titleField, authorField, yearField, genreField, searchButton);

        ListView<Book> bookListView = new ListView<>();
        FilteredList<Book> filteredBooks = bookService.searchBooks("", "", null, "");
        bookListView.setItems(filteredBooks);

        bookListView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Book book, boolean empty) {
                super.updateItem(book, empty);
                if (empty || book == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    HBox hbox = new HBox(10);
                    Label label = new Label(book.toString());
                    Button openButton = new Button("Open");
                    openButton.setOnAction(e -> {
                        if (hostServices != null && book.getUrl() != null && !book.getUrl().isEmpty()) {
                            hostServices.showDocument(book.getUrl());
                        }
                    });
                    hbox.getChildren().addAll(label, openButton);
                    setGraphic(hbox);
                }
            }
        });

        VBox buttonBox = new VBox(10);
        buttonBox.setAlignment(Pos.CENTER);

        Button addToFavoritesButton = new Button("Add to Favorites");
        addToFavoritesButton.setOnAction(e -> {
            Book selected = bookListView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                userService.addToFavorites(user, selected);
                showAlert("Success", "Book added to favorites");
            }
        });

        Button viewFavoritesButton = new Button("View Favorites");
        viewFavoritesButton.setOnAction(e -> showFavoritesWindow(user));

        Button logoutButton = new Button("Logout");
        logoutButton.setOnAction(e -> {
            userStage.close();
            primaryStage.show();
        });

        buttonBox.getChildren().addAll(addToFavoritesButton, viewFavoritesButton, logoutButton);

        searchButton.setOnAction(e -> {
            filteredBooks.setPredicate(book -> {
                boolean matchesTitle = book.getTitle().toLowerCase().contains(titleField.getText().toLowerCase());
                boolean matchesAuthor = book.getAuthor().toLowerCase().contains(authorField.getText().toLowerCase());
                boolean matchesGenre = book.getGenre().toLowerCase().contains(genreField.getText().toLowerCase());

                boolean matchesYear = true;
                if (!yearField.getText().isEmpty()) {
                    try {
                        int yearValue = Integer.parseInt(yearField.getText());

                    } catch (NumberFormatException ex) {
                        matchesYear = false;
                    }
                }

                boolean matchesAvailability = book.isAvailable();

                return matchesTitle && matchesAuthor && matchesYear && matchesGenre && matchesAvailability;
            });
        });

        VBox centerBox = new VBox(10, new Label("Available Books"), bookListView, buttonBox);
        root.setLeft(searchBox);
        root.setCenter(centerBox);

        userStage.setScene(new Scene(root));
        userStage.setTitle("User Mode - " + user.getUsername());
        userStage.show();
        primaryStage.hide();
    }

    private void showFavoritesWindow(User user) {
        Stage stage = new Stage();
        VBox root = new VBox(10);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-padding: 15;");

        ListView<FavouriteBook> favoritesList = new ListView<>(userService.getFavorites(user));
        favoritesList.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(FavouriteBook favouriteBook, boolean empty) {
                super.updateItem(favouriteBook, empty);
                if (empty || favouriteBook == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    HBox hbox = new HBox(10);
                    CheckBox readCheck = new CheckBox("Read");
                    readCheck.setSelected(favouriteBook.isRead());
                    readCheck.selectedProperty().bindBidirectional(favouriteBook.isReadProperty());
                    Button removeBtn = new Button("Remove");
                    removeBtn.setOnAction(e -> {
                        userService.removeFromFavorites(favouriteBook);
                        favoritesList.refresh();
                    });
                    hbox.getChildren().addAll(new Label(favouriteBook.getBook().getTitle()), readCheck, removeBtn);
                    setGraphic(hbox);
                }
            }
        });

        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> stage.close());

        root.getChildren().addAll(new Label("Your Favorites"), favoritesList, closeButton);

        stage.setScene(new Scene(root, 400, 300));
        stage.setTitle("Favorites");
        stage.show();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}