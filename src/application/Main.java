// UserInterface.java
package application;

import application.bd.DBInitException;
import application.bd.DataSource;
import application.model.*;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.util.Optional;

public class Main extends Application {
    private UserService userService;
    private BookService bookService;
    private ReviewService reviewService;
    private FavouritesService favouritesService;
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
        reviewService = new ReviewService(DataSource.getConn());
        favouritesService = new FavouritesService(DataSource.getConn());
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
        root.setPrefSize(1000, 500);

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
                    // Label с информацией о книге
                    Label label = new Label(book.toString());
                    label.setPrefWidth(500);

                    // Кнопка Open
                    Button openButton = new Button("Open");
                    openButton.setOnAction(e -> {
                        if (hostServices != null && book.getUrl() != null && !book.getUrl().isEmpty()) {
                            hostServices.showDocument(book.getUrl());
                        }
                    });

                    // Кнопка Review
                    Button reviewButton = new Button("Review");
                    reviewButton.setOnAction(e -> {
                        showReviewDialog(book, user);
                        bookListView.setItems(bookService.searchBooks("", "", null, ""));
                        bookListView.refresh();
                    });

                    //Label с рейтингом книги
                    Label rating = new Label(book.getRating());

                    hbox.getChildren().addAll(label, openButton, reviewButton, rating);
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
                favouritesService.addToFavorites(user, selected);
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
            FilteredList<Book> filteredList = bookService.searchBooks(
                    titleField.getText().toLowerCase(),
                    authorField.getText().toLowerCase(),
                    yearField.getText(),
                    genreField.getText().toLowerCase()
            );
            bookListView.setItems(filteredList);
        });

        VBox centerBox = new VBox(10, new Label("Available Books"), bookListView, buttonBox);
        root.setLeft(searchBox);
        root.setCenter(centerBox);

        userStage.setScene(new Scene(root));
        userStage.setTitle("User Mode - " + user.getUsername());
        userStage.show();
        primaryStage.hide();
    }

    private void showReviewDialog(Book book, User user) {
        Dialog<ReviewData> dialog = new Dialog<>();
        dialog.setTitle("Review Book");
        dialog.setHeaderText("Review for: " + book.toString());

        // Установка кнопок
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Создание содержимого
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // Элементы формы
        Spinner<Integer> ratingSpinner = new Spinner<>(1, 5, 3);
        TextArea reviewText = new TextArea();
        reviewText.setPromptText("Enter your review here...");

        CheckBox isReadCheckBox = new CheckBox();

        // Добавляем элементы в grid
        grid.add(new Label("Rating (1-5):"), 0, 0);
        grid.add(ratingSpinner, 1, 0);
        grid.add(new Label("Read:"), 0, 1);
        grid.add(isReadCheckBox, 1, 1);
        grid.add(new Label("Review:"), 0, 2);
        grid.add(reviewText, 1, 2);

        dialog.getDialogPane().setContent(grid);

        // Преобразование результата
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                return new ReviewData(
                        user,
                        book,
                        ratingSpinner.getValue(),
                        reviewText.getText(),
                        isReadCheckBox.isSelected()
                );
            }
            return null;
        });

        // Обработка результата
        Optional<ReviewData> result = dialog.showAndWait();

        result.ifPresent(review -> {
            // Сохранение отзыва в базу данных
            reviewService.saveReview(review);
        });
    }

    private void showFavoritesWindow(User user) {
        Stage stage = new Stage();
        VBox root = new VBox(10);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-padding: 15;");

        ListView<FavouriteBook> favoritesList = new ListView<>(favouritesService.getFavorites(user));
        favoritesList.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(FavouriteBook favouriteBook, boolean empty) {
                super.updateItem(favouriteBook, empty);
                if (empty || favouriteBook == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    HBox hbox = new HBox(10);
                    hbox.setAlignment(Pos.CENTER_LEFT);

                    // Создаем выпадающий список для статуса чтения
                    ComboBox<BookReadingStatus> statusCombo = new ComboBox<>();
                    statusCombo.getItems().addAll(BookReadingStatus.values());
                    statusCombo.setValue(favouriteBook.getStatus());
                    statusCombo.setPrefWidth(120);

                    // Обработчик изменения статуса
                    statusCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
                        favouritesService.updateReadingStatus(favouriteBook, newVal);
                        favoritesList.setItems(favouritesService.getFavorites(user));
                        favoritesList.refresh();
                    });

                    Button removeBtn = new Button("Remove");
                    removeBtn.setOnAction(e -> {
                        favouritesService.removeFromFavorites(favouriteBook);
                        favoritesList.setItems(favouritesService.getFavorites(user));
                        favoritesList.refresh();
                    });
                    hbox.getChildren().addAll(new Label(favouriteBook.getBook().getTitle()), statusCombo, removeBtn);
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