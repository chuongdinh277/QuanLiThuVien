package Controllers;

import java.sql.SQLException;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import Document.*;
import APIGoogle.*;

/**
 * Controller for adding a new book to the system.
 */
public class AddBookController {

    @FXML
    private TextField titleSearch;
    @FXML
    private TextField authorSearch;
    @FXML
    private Button apiSearch;
    @FXML
    private TextField titleSearch_1;
    @FXML
    private TextField authorSearch_1;
    @FXML
    private TextField categorySearch_1;
    @FXML
    private Button addBook_search;
    @FXML
    private ImageView showImage;
    @FXML
    private TextField quantity_book;
    @FXML
    private TextField bookPublisher;
    @FXML
    private TextArea descriptionTextArea;
    @FXML
    private TextField ISBNtextField;

    private String description;
    private Book resultBook;
    private AllBookController homeController;

    /**
     * Sets the home controller for navigating book details.
     *
     * @param homeController The home controller instance.
     */
    public void setHomeController(AllBookController homeController) {
        this.homeController = homeController;
    }

    /**
     * Searches for a book using an external API based on the title and author provided.
     */
    @FXML
    private void apiSearch_Button() {
        apiSearch.setOnAction(e -> {
            String title = titleSearch.getText().trim();
            String author = authorSearch.getText().trim();

            if (!title.isEmpty() && !author.isEmpty()) {
                Task<Book> searchTask = new Task<>() {
                    @Override
                    protected Book call() {
                        String bookInfo = GoogleBooksAPI.searchBookByTitleAndAuthor(title, author);
                        return BookParser.parseBookInfo(bookInfo);
                    }

                    @Override
                    protected void succeeded() {
                        resultBook = getValue();
                        if (resultBook != null) {
                            Platform.runLater(() -> {
                                titleSearch_1.setText(resultBook.getTitle());
                                authorSearch_1.setText(resultBook.getAuthor());
                                categorySearch_1.setText(resultBook.getCategory());
                                bookPublisher.setText(resultBook.getPublisher());
                                ISBNtextField.setText(resultBook.getISBN());
                                description = resultBook.getDescription() != null ? resultBook.getDescription() : "No description available";
                                descriptionTextArea.setText(description);
                                if (resultBook.getImagePath() != null) {
                                    Image image = new Image(resultBook.getImagePath(), showImage.getFitWidth(), showImage.getFitHeight(), true, true);
                                    showImage.setImage(image);
                                    showImage.setSmooth(true);
                                } else {
                                    showImage.setImage(null);
                                }
                            });
                        } else {
                            showErrorDialog("No book found for the given title and author.");
                        }
                    }

                    @Override
                    protected void failed() {
                        showErrorDialog("Error occurred while searching for the book.");
                    }
                };

                new Thread(searchTask).start();
            } else {
                showErrorDialog("Please enter both title and author.");
            }
        });
    }

    /**
     * Adds a new book to the system after validating input.
     */
    @FXML
    private void addBook_Button() {
        addBook_search.setOnAction(e -> {
            String title = titleSearch_1.getText().trim();
            String author = authorSearch_1.getText().trim();
            String category = categorySearch_1.getText().trim();
            String quantityStr = quantity_book.getText().trim();
            String publisher = bookPublisher.getText().trim();
            String ISBN = ISBNtextField.getText().trim();

            int quantity;
            try {
                quantity = Integer.parseInt(quantityStr);
            } catch (NumberFormatException ex) {
                showErrorDialog("Invalid quantity input. Please enter a valid number.");
                return;
            }

            String imagePath = showImage.getImage() != null ? showImage.getImage().getUrl() : null;

            Book book = new Book(title, author, category, quantity, quantity, description, publisher, imagePath, ISBN);

            try {
                BookDAO.addBook(book);
                showSuccessDialog("Book added successfully!");
                clearInputFields();
            } catch (SQLException ex) {
                showErrorDialog("Failed to add book to the database: " + ex.getMessage());
            }
        });
    }

    /**
     * Clears all input fields on the form.
     */
    private void clearInputFields() {
        titleSearch_1.clear();
        authorSearch_1.clear();
        categorySearch_1.clear();
        quantity_book.clear();
        bookPublisher.clear();
        descriptionTextArea.clear();
        ISBNtextField.clear();
        showImage.setImage(null);
    }

    /**
     * Displays an error dialog with a given message.
     *
     * @param message The message to be displayed in the error dialog.
     */
    private void showErrorDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Displays a success dialog with a given message.
     *
     * @param message The message to be displayed in the success dialog.
     */
    private void showSuccessDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}