package Controllers;

import Document.Book;
import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

public class CardController {
    @FXML
    private AnchorPane cardPane;
    @FXML
    private ImageView imageView_User;
    @FXML
    private Label titleLabel;
    @FXML
    private Label authorLabel;
    @FXML
    private Label quantityLabel;
    @FXML
    private Button seeButton;
    @FXML
    private Button borrowButton;

    @FXML
    private Book currentBook;

    /**
     * Initializes the card pane with mouse event handlers for animations.
     */
    @FXML
    private void initialize() {
        // Set up mouse event handlers for the card pane
        cardPane.setOnMouseEntered(this::handleMouseEnter);
        cardPane.setOnMouseExited(this::handleMouseExit);
    }

    /**
     * Handles the mouse enter event on the card pane.
     * Animates the card pane by scaling it up slightly.
     *
     * @param event the mouse event triggered when the mouse enters the card pane
     */
    @FXML
    private void handleMouseEnter(MouseEvent event) {
        ScaleTransition st = new ScaleTransition(Duration.millis(200), cardPane);
        st.setToX(1.1);
        st.setToY(1.1);
        st.play();
    }

    /**
     * Handles the mouse exit event on the card pane.
     * Animates the card pane by scaling it back to its original size.
     *
     * @param event the mouse event triggered when the mouse exits the card pane
     */
    @FXML
    private void handleMouseExit(MouseEvent event) {
        ScaleTransition st = new ScaleTransition(Duration.millis(200), cardPane);
        st.setToX(1.0);
        st.setToY(1.0);
        st.play();
    }

    /**
     * Sets the book details to be displayed in the card pane.
     * If the book is not cached, it is added to the cache. The book's title, author,
     * and image are displayed, with a default image used if none is provided.
     *
     * @param book the book whose details are to be displayed; if null, no action is taken
     */
    public void setBook(Book book) {
        if (book == null) {
            return;
        }
        this.currentBook = book;

        if (!Cache.BookCache.isCached(book.getISBN())) {
            Cache.BookCache.putBook(book);
        }

        titleLabel.setText(book.getTitle());
        authorLabel.setText(book.getAuthor());

        String imagePath = book.getImagePath();
        if (imagePath != null && !imagePath.isEmpty()) {
            imageView_User.setImage(Cache.ImageCache.getImage(imagePath));
        } else {
            String path = "/image/biasachmacdinh.png";
            Image image = new Image(getClass().getResourceAsStream(path));
            if (!image.isError()) {
                imageView_User.setImage(image);
            }
        }
    }

}