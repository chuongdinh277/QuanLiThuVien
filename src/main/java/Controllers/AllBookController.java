package Controllers;

import Document.Book;
import User.Admin;
import User.currentUser;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.util.List;

import javafx.scene.chart.BarChart;

public class AllBookController {

    @FXML
    private BorderPane borderPane_book;
    @FXML
    private TableView<Book> bookTable;
    @FXML
    private TableColumn<Book, Integer> bookID;
    @FXML
    private TableColumn<Book, String> bookTitle;
    @FXML
    private TableColumn<Book, String> bookAuthor;
    @FXML
    private TableColumn<Book, String> bookPublisher;
    @FXML
    private TableColumn<Book, Integer> bookQuantity;
    @FXML
    private TableColumn<Book, HBox> bookAction;
    @FXML
    private TableColumn<Book, String> bookCategory;
    @FXML
    private TableColumn<Book, Integer> remainingBook;
    @FXML
    private TableColumn<Book, String> bookAvailability;
    @FXML
    private TableColumn<Book, String> ISBNbook;
    @FXML
    private Button addBook_Admin;
    @FXML
    private BarChart<String, Number> categoryBarChart;
    private static MenuAdminController menuController;

    /**
     * Sets the menu controller for this controller.
     * @param menuController The MenuController_Admin to set.
     */
    public void setMenuController(MenuAdminController menuController) {
        this.menuController = menuController;
    }

    /**
     * Initializes the controller by setting up the TableView and loading books.
     */
    @FXML
    private void initialize() {
        ISBNbook.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getISBN()));
        bookTitle.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTitle()));
        bookAuthor.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAuthor()));
        bookPublisher.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPublisher()));
        bookQuantity.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getQuantity()).asObject());
        bookCategory.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCategory()));
        remainingBook.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getRemainingBook()).asObject());
        bookAvailability.setCellValueFactory(cellData -> {
            int remaining = cellData.getValue().getRemainingBook();
            String availability = (remaining == 0) ? "Not available" : "Available";
            return new SimpleStringProperty(availability);
        });
        loadBooks();

        bookTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Book selectedBook = bookTable.getSelectionModel().getSelectedItem();
                if (selectedBook != null) {
                    openBookDetails(selectedBook);
                }
            }
        });
    }

    /**
     * Loads the list of books into the TableView.
     */
    public void loadBooks() {
        ObservableList<Book> booksList = FXCollections.observableArrayList();

        try {
            Admin admin = new Admin(currentUser.getUsername(), currentUser.getRole());
            List<Book> bookList = admin.viewAllBooks();
            if (bookList != null) {
                booksList.addAll(bookList);
            } else {
                showInfoDialog("No books found in the database.");
            }
        } catch (Exception e) {
            showErrorDialog("Error loading books: " + e.getMessage());
        }
        bookTable.setItems(booksList);
    }

    /**
     * Opens the AddBook view when the add book button is clicked.
     */
    @FXML
    private void addBook() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/timsach.fxml"));
            Parent root = fxmlLoader.load();

            AddBookController controller = fxmlLoader.getController();
            controller.setHomeController(this);
            borderPane_book.setCenter(root);
        } catch (IOException e) {
            showErrorDialog("Error opening Add Book view: " + e.getMessage());
        }
    }

    /**
     * Opens the Book Details view when a book is double-clicked in the table.
     * @param book The selected book to view details.
     */
    private void openBookDetails(Book book) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/bookDetails.fxml"));
            Parent bookDetailsRoot = fxmlLoader.load();

            BookDetailController controller = fxmlLoader.getController();
            controller.setMenuController(menuController);
            controller.setBook(book);

            borderPane_book.setCenter(bookDetailsRoot);
        } catch (IOException e) {
            showErrorDialog("Error displaying book details: " + e.getMessage());
        }
    }

    /**
     * Displays an error dialog with the provided message.
     * @param message The message to display in the error dialog.
     */
    private void showErrorDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Displays an informational dialog with the provided message.
     * @param message The message to display in the informational dialog.
     */
    private void showInfoDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
