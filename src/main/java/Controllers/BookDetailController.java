package Controllers;

import Document.*;
import User.currentUser;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;


import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

public class BookDetailController {

    @FXML
    private TextField ISBNTextField;

    @FXML
    private ImageView ImageView_book;

    @FXML
    private TextField authorTextField;

    @FXML
    private Label availableLabel;

    @FXML
    private TableColumn<Transaction, Date> borrowDate;

    @FXML
    private Label categoryLabel;

    @FXML
    private Button commentButton;

    @FXML
    private Pane commentPane;

    @FXML
    private TextArea commentTextArea;

    @FXML
    private VBox commentVbox;

    @FXML
    private TextArea descriptionTextField;

    @FXML
    private Label publisherLabel;

    @FXML
    private TextField quantityTextField;

    @FXML
    private TableColumn<Transaction, Date> returnDate;

    @FXML
    private ImageView star1;

    @FXML
    private ImageView star2;

    @FXML
    private ImageView star3;

    @FXML
    private ImageView star4;

    @FXML
    private ImageView star5;

    @FXML
    private ImageView starbook1;

    @FXML
    private ImageView starbook2;

    @FXML
    private ImageView starbook3;

    @FXML
    private ImageView starbook4;

    @FXML
    private ImageView starbook5;

    @FXML
    private TableColumn<Transaction, String> studentID;

    @FXML
    private TableColumn<Transaction, String> studentName;

    @FXML
    private TableView<Transaction> studentTableview;

    @FXML
    private TextField titleTextField;

    @FXML
    private Pane viewBookPane;
    @FXML
    private Label totalStudentLabel;
    private Book currentBook;

    private MenuAdminController menuControllerAdmin;
    private int selectedRating = 0;

    private int totalStudent = 0;

    /**
     * Displays the book view pane and hides the comment pane when triggered by an event.
     * This method makes the book details pane visible and the comment pane invisible.
     *
     * @param event The action event that triggers the method.
     */
    @FXML
    private void showViewBook(ActionEvent event) {
        viewBookPane.setVisible(true);
        commentPane.setVisible(false);
    }

    /**
     * Displays the comment section for the book and hides the book view pane when triggered by an event.
     * This method makes the comment pane visible, hides the book details pane,
     * and loads the reviews for the current book if available.
     *
     * @param event The action event that triggers the method.
     */
    @FXML
    private void showCommentBook(ActionEvent event) {
        viewBookPane.setVisible(false);
        commentPane.setVisible(true);
        if(currentBook != null ) System.out.println(currentBook.getISBN());
        loadReview();
        System.out.println("hello");
    }

    /**
     * Initializes the controller by setting up event handlers for UI components.
     * This method sets the actions for the comment button and binds table columns to the corresponding properties
     * of the student data. It also calls the method to load all student data (currently commented out).
     */
    @FXML
    private void initialize() {
        commentButton.setOnAction(even -> saveReview());
        studentID.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMssv()));
        studentName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUsername()));
        borrowDate.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getBorrow_Date()));
        returnDate.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getReturn_Date()));
        //loadallStudent();
    }

    /**
     * Handles the click event on the menu. If the menuControllerAdmin is not null, it shows the home view.
     * If the controller is null, a message is printed to the console.
     *
     * @param event The mouse click event.
     */
    @FXML
    private void handleClick(MouseEvent event) {
        if (menuControllerAdmin != null) {
            menuControllerAdmin.showHome();
        } else {
            System.out.println("null");
        }
    }

    /**
     * Handles the click event on a star in the rating section. Based on the clicked star, it sets the selected rating.
     * The method also updates the appearance of the stars according to the selected rating.
     *
     * @param event The mouse click event on the star.
     */
    @FXML
    private void handleStarclick(MouseEvent event) {
        Object source = event.getSource();
        if (source == star1) selectedRating = 1;
        else if (source == star2) selectedRating = 2;
        else if (source == star3) selectedRating = 3;
        else if (source == star4) selectedRating = 4;
        else if (source == star5) selectedRating = 5;
        updateStarColors(selectedRating);
    }

    /**
     * Updates the appearance of the stars based on the given rating. Stars are displayed as selected or unselected
     * based on the rating value.
     *
     * @param rating The rating value (1 to 5).
     */
    private void updateStarColors(int rating) {
        // Create list of selected and unselected star images
        Image selectedStar = new Image(getClass().getResourceAsStream("/image/star.png"));
        Image unselectedStar = new Image(getClass().getResourceAsStream("/image/starv.png"));
        // Update stars based on rating
        if (rating >= 1) star1.setImage(selectedStar);
        else star1.setImage(unselectedStar);

        if (rating >= 2) star2.setImage(selectedStar);
        else star2.setImage(unselectedStar);

        if (rating >= 3) star3.setImage(selectedStar);
        else star3.setImage(unselectedStar);

        if (rating >= 4) star4.setImage(selectedStar);
        else star4.setImage(unselectedStar);

        if (rating >= 5) star5.setImage(selectedStar);
        else star5.setImage(unselectedStar);
    }

    /**
     * Sets the MenuController_Admin instance for the controller, allowing interaction with the admin menu.
     *
     * @param menuController The MenuController_Admin instance to be set.
     */
    public void setMenuController(MenuAdminController menuController) {
        this.menuControllerAdmin = menuController;
    }

    /**
     * Sets the details of the provided book in the user interface elements, such as ISBN, title, author,
     * category, publisher, quantity, and description. It also displays the availability status of the book
     * and its average rating. If the book has an image path, it loads and displays the image as well.
     *
     * @param book The book object whose details will be displayed.
     */
    public void setBook(Book book) {
        if (book != null) {
            this.currentBook = book;
            ISBNTextField.setText(book.getISBN());
            titleTextField.setText(book.getTitle());
            authorTextField.setText(book.getAuthor());
            categoryLabel.setText(book.getCategory());
            publisherLabel.setText(book.getPublisher());
            quantityTextField.setText(String.valueOf(book.getQuantity()));
            descriptionTextField.setText(book.getDescription());
            if (book.getRemainingBook() > 0) {
                availableLabel.setText("Available");
            } else {
                availableLabel.setText("Not available");
            }
            try {
                double averageRating = ReviewDAO.getAverageRating(book.getISBN());
                System.out.println(averageRating);
                displayRating(averageRating);
            } catch (SQLException e) {
                showErrorDialog("Lỗi khi lấy đánh giá của sách: " + book.getTitle());
            }
        }
        if (book.getImagePath() != null) {
            ImageView_book.setImage(new Image(book.getImagePath()));
        }
        loadallStudent();
    }

    /**
     * Updates the current book's details in the database, including its author, ISBN, category, publisher,
     * and quantity. The method updates the quantity by adding the newly entered value to the existing quantity.
     * A success or failure message is displayed based on the outcome of the update operations.
     *
     * @param event The event that triggers the update action (typically a button click).
     */
    @FXML
    private void updateBook(ActionEvent event) {
        try {
            // Get the updated quantity from the input
            int newQuantity = Integer.parseInt(quantityTextField.getText());
            int additionalQuantity = currentBook.getQuantity() + newQuantity;
            System.out.println(additionalQuantity);
            // Update other fields
            currentBook.setAuthor(authorTextField.getText());
            currentBook.setISBN(ISBNTextField.getText());
            currentBook.setCategory(categoryLabel.getText());
            currentBook.setPublisher(publisherLabel.getText());
            // Update book in the database
            boolean isUpdate = BookDAO.updateBook(currentBook);
            // Update quantity separately
            boolean updateQuantity = BookDAO.updateQuantity(currentBook, additionalQuantity);

            if (isUpdate && updateQuantity) {
                showAlbertDialog("Cập nhật sách thành công");
            } else {
                showAlbertDialog("Cập nhật thất bại");
            }
        } catch (SQLException | NumberFormatException e) {
            showErrorDialog("Lỗi khi cập nhật sách: " + e.getMessage());
        }
    }


    /**
     * Deletes the current book from the database if there are no students currently borrowing the book.
     * If any students are still borrowing the book, a dialog will be shown to inform the user.
     * A success or failure message is displayed based on the outcome of the delete operation.
     *
     * @param event The event that triggers the deletion action (typically a button click).
     */
    @FXML
    private void deleteBook(ActionEvent event) {
        if (totalStudent != 0) {
            showAlbertDialog("Vẫn còn sinh viên đang mượn quyển sách này");
            return;
        }
        try {
            boolean isDelete = BookDAO.deleteBook(currentBook);
            if (isDelete) {
                showAlbertDialog("Xóa sách thành công");
            } else {
                showAlbertDialog("Xóa thất bại");
            }
        } catch (SQLException e) {
            showErrorDialog("Lỗi khi xóa sách: " + e.getMessage());
        }
    }

    /**
     * Saves the review for the current book, including the rating and comment, to the database.
     * If the user has selected a rating and written a comment, the review will be saved.
     * If no rating is selected or no book is set, a dialog will inform the user to complete the review.
     *
     * @throws SQLException If an error occurs while saving the review to the database.
     */
    private void saveReview() {
        if (selectedRating > 0 && currentBook != null) {
            String isbn = currentBook.getISBN();
            String username = currentUser.getUsername();
            String comment = commentTextArea.getText().trim(); // Optionally fetch from a comment field if available

            try {
                ReviewDAO.saveReview(username, isbn, selectedRating, comment);
                showAlbertDialog("Đã lưu đánh giá " + selectedRating + " sao cho sách: " + currentBook.getTitle());
            } catch (SQLException e) {
                showErrorDialog("Lỗi khi lưu đánh giá cho sách: " + currentBook.getTitle());
            }
        } else {
            showAlbertDialog("Chưa chọn đánh giá hoặc sách để lưu.");
        }
    }

    /**
     * Displays the rating of a book by updating the star images based on the average rating.
     * The rating is rounded to the nearest integer, and the stars are visually updated to reflect the rating.
     *
     * @param averageRating The average rating of the book, represented as a double value.
     *                      This value will be rounded to the nearest integer for display purposes.
     */
    private void displayRating(double averageRating) {
        int roundedRating = (int) averageRating;
        Image selectedStar = new Image(getClass().getResourceAsStream("/image/star.png"));
        Image unselectedStar = new Image(getClass().getResourceAsStream("/image/starv.png"));
        Image unselectedStar1 = new Image(getClass().getResourceAsStream("/image/rating.png"));
        int i = 1;
        while (i <= roundedRating) {
            if (i == 1) starbook1.setImage(selectedStar);
            if (i == 2) starbook2.setImage(selectedStar);
            if (i == 3) starbook3.setImage(selectedStar);
            if (i == 4) starbook4.setImage(selectedStar);
            if (i == 5) starbook5.setImage(selectedStar);
            i++;
        }
        int j = roundedRating + 1;

        if (j <= 5 && (double )(roundedRating) < averageRating) {
            if (i == 1) starbook1.setImage(unselectedStar1);
            if (i == 2) starbook2.setImage(unselectedStar1);
            if (i == 3) starbook3.setImage(unselectedStar1);
            if (i == 4) starbook4.setImage(unselectedStar1);
            if (i == 5) starbook5.setImage(unselectedStar1);
        }
        j++;
        while (j <= 5) {
            if (j == 1) starbook1.setImage(unselectedStar);
            if (j == 2) starbook2.setImage(unselectedStar);
            if (j == 3) starbook3.setImage(unselectedStar);
            if (j == 4) starbook4.setImage(unselectedStar);
            if (j == 5) starbook5.setImage(unselectedStar);
            j++;
        }
    }

    /**
     * Loads and displays the reviews of the current book by fetching them from the database using its ISBN.
     * If the book is valid and reviews are found, they will be displayed. If an error occurs during the process,
     * an error dialog will be shown.
     */
    private void loadReview() {
        if (currentBook != null) {
            String isbn = currentBook.getISBN();
            System.out.println(isbn);
            try {
                List<Review> reviews = ReviewDAO.getReviewsByISBN(isbn);
                displayReviews(reviews);
            } catch (SQLException e) {
                showErrorDialog("Lỗi khi lấy đánh giá của sách: " + currentBook.getTitle());
            }
        }
    }


    /**
     * Displays a list of reviews for the current book.
     * Each review includes the username, star rating, and the comment.
     * The reviews are displayed in a VBox, with the username in bold,
     * stars representing the rating, and the comment below the stars.
     *
     * @param reviews The list of reviews to be displayed.
     */
    private void displayReviews(List<Review> reviews) {
        commentVbox.getChildren().clear();

        for (Review review : reviews) {
            VBox commentBox = new VBox(5);
            commentBox.setStyle("-fx-padding: 10;");

            Label usernameLabel = new Label(review.getUsername());
            usernameLabel.setStyle("-fx-font-weight: bold;");

            HBox starsBox = new HBox(5);
            for (int i = 1; i <= 5; i++) {
                ImageView star = new ImageView();
                if (i <= review.getRating()) {
                    star.setImage(new Image(getClass().getResourceAsStream("/image/star.png")));
                } else {
                    star.setImage(new Image(getClass().getResourceAsStream("/image/starv.png")));
                }
                star.setFitWidth(15);
                star.setFitHeight(15);

                starsBox.getChildren().add(star);
            }

            Label commentLabel = new Label(review.getComment());
            commentLabel.setWrapText(true);
            commentLabel.setStyle("-fx-padding: 5px;");

            commentBox.getChildren().addAll(usernameLabel, starsBox, commentLabel);

            commentVbox.getChildren().add(commentBox);
        }
    }
    /**
     * Loads all students who have borrowed the current book and displays the transaction details in a table.
     * The number of students is shown in the totalStudentLabel.
     * If there are no transactions, a dialog is shown indicating no transactions are available.
     *
     * @throws Exception If an error occurs while fetching the transactions from the database.
     */
    private void loadallStudent() {
        String isbn = currentBook.getISBN();
        ObservableList<Transaction> transactions = FXCollections.observableArrayList();
        try {
            List<Transaction> issueBook = TransactionDAO.getTransactionsByISBN(isbn);
            totalStudent = issueBook.size();
            totalStudentLabel.setText(String.valueOf(issueBook.size()));
            if (issueBook != null) {
                transactions.addAll(issueBook);
            } else {
                showAlbertDialog("Không có giao dịch trong cơ sở dữ liệu.");
            }
        } catch (Exception e) {
            showErrorDialog("Lỗi khi tải giao dịch: " + e.getMessage());
        }
        studentTableview.setItems(transactions);
    }
    /**
     * Displays an informational dialog with a given message.
     *
     * @param message The message to be displayed in the dialog.
     */
    private void showAlbertDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Displays an error dialog with a given message.
     *
     * @param message The error message to be displayed in the dialog.
     */
    private void showErrorDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


}
