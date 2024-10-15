package controllers;

import User.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import Document.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import APIGoogle.*;
import Document.BookDAO;
import java.sql.Connection;
import static Document.BookDAO.addBook;

public class addController {

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
    private void apiSearch_Button() {


        apiSearch.setOnAction(e ->{
            String title = titleSearch.getText();
            String author = authorSearch.getText();

            String bookInfor = GoogleBooksAPI.searchBookByTitleAndAuthor(title, author);

            if(bookInfor != null) {

                Book book = BookParser.parseBookInfo(bookInfor);

                titleSearch_1.setText(book.getTitle());
                authorSearch_1.setText(book.getAuthor());
                categorySearch_1.setText(book.getCategory());

                if (book.getImagePath() != null) {
                    Image image = new Image(book.getImagePath());
                    showImage.setImage(image);
                }

            }
        });


    }

    @FXML
    private void addBook_Button() {
        addBook_search.setOnAction(e -> {

            String title = titleSearch_1.getText();
            String author = authorSearch_1.getText();
            String category = categorySearch_1.getText();
            String quantity = quantity_book.getText();
            int quantity_Book = Integer.parseInt(quantity);

            String imagePath;
            if (showImage.getImage()!= null) {
                imagePath = showImage.getImage().getUrl().toString();
            } else {
                imagePath = null;
            }
            Book book = new Book(title, author, category, quantity_Book ,"description", imagePath);

            try {
                BookDAO.addBook(book);
                System.out.println("thêm sách thành công");
            }
            catch (SQLException ex) {
                System.out.println("thêm sách không thành công");
            }

        });
    }




}
