package controllers;

import Document.Book;
import Document.BookDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;


import javafx.scene.input.MouseEvent;

import java.sql.SQLException;

public class bookDetailCotroller {

    @FXML
    private ImageView ImageView_book;

    @FXML
    private TextField authorTextField;

    @FXML
    private TextField bookIDtextField;

    @FXML
    private TextField categoryTextField;

    @FXML
    private Button commentBook;

    @FXML
    private Pane commentPane;

    @FXML
    private TextField publisherTextField;

    @FXML
    private TextField quantityTextField;

    @FXML
    private ImageView returnHome;

    @FXML
    private Button viewBook;

    @FXML
    private Pane viewBookPane;

    private Book currentBook;
    private MenuController_Admin menuControllerAdmin;
    @FXML
    private TextField sectionTextField;

    @FXML
    private void showViewBook(ActionEvent event) {
        viewBookPane.setVisible(true);
        commentPane.setVisible(false);
    }
    @FXML
    private void showCommentBook(ActionEvent event) {
        viewBookPane.setVisible(false);
        commentPane.setVisible(true);
    }
    @FXML
    private void handleClick(MouseEvent event) {
        if (menuControllerAdmin != null) {
            menuControllerAdmin.showHome();
        }
        else {
            System.out.println("null");
        }
    }
    public void setMenuController(MenuController_Admin menuController) {
        this.menuControllerAdmin = menuController;
    }

    public void setBook (Book book) {
        if (book != null) {
            currentBook = book;
            bookIDtextField.setText(book.getISBN());
            authorTextField.setText(book.getAuthor());
            categoryTextField.setText(book.getCategory());
            publisherTextField.setText(book.getPublisher());
            quantityTextField.setText(String.valueOf(book.getQuantity()));
            sectionTextField.setText(book.getSection());
        }
        if (book.getImagePath() != null) {
            ImageView_book.setImage(new Image(book.getImagePath()));
        }
    }
    @FXML
    private void updateBook(ActionEvent event) {
        try {
            // Get the updated quantity from the input
            int newQuantity = Integer.parseInt(quantityTextField.getText());
            //System.out.println(newQuantity);
            int additionalQuantity = currentBook.getQuantity() + newQuantity;
            System.out.println(additionalQuantity);
            // Update other fields
            currentBook.setAuthor(authorTextField.getText());
            currentBook.setISBN(bookIDtextField.getText());
            currentBook.setCategory(categoryTextField.getText());
            currentBook.setPublisher(publisherTextField.getText());
            currentBook.setSection(sectionTextField.getText());

            // Update book in the database
            boolean isUpdate = BookDAO.updateBook(currentBook);

            // Update quantity separately
            boolean updateQuantity = BookDAO.updateQuantity(currentBook, additionalQuantity);

            if (isUpdate && updateQuantity) {
                System.out.println("Cập nhật sách thành công");
            } else {
                System.out.println("Cập nhật thất bại");
            }
        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
            System.out.println("Lỗi khi cập nhật sách: " + e.getMessage());
        }
    }


    @FXML
    private void deleteBook(ActionEvent event) {
        try {
            boolean isDelete = BookDAO.deleteBook(currentBook);
            if (isDelete) {
                System.out.println("Xóa sách thành công");
                //menuControllerAdmin.loadBookList();
            } else {
                System.out.println("Xóa thất bại");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("L��i khi xóa sách: " + e.getMessage());
        }
    }

}
