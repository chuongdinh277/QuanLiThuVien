package controllers;

import Document.Book;
import User.Member;
import User.currentUser;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.sql.SQLException;
import java.util.List;

public class borrowBook {

    @FXML
    private TableView<Book> bookLibraryList;
    @FXML
    private TableColumn<Book, Integer> bookSTT; // Cột ID
    @FXML
    private TableColumn<Book, String> nameOfbook_User; // Cột tên sách
    @FXML
    private TableColumn<Book, String> authorOfbook_User; // Cột tác giả
    @FXML
    private TableColumn<Book, Integer> quantityOfbook_User; // Cột số lượng
    @FXML
    private TableColumn<Book, Button> seeBookButton_User; // Cột hành động

    @FXML
    private ImageView imageView_User;
    @FXML
    private Label TitleShow_User;
    @FXML
    private Label authorShow_User;
    @FXML
    private Label QuantityShow_User;
    @FXML
    private Label categoryShow_User;
    @FXML
    private void initialize() {
        // Thiết lập cột ID
        bookSTT.setCellValueFactory(cellData -> new SimpleIntegerProperty(bookLibraryList.getItems().indexOf(cellData.getValue()) + 1).asObject());

        // Thiết lập cột tên sách
        nameOfbook_User.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTitle()));

        // Thiết lập cột tác giả
        authorOfbook_User.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAuthor()));

        // Thiết lập cột số lượng
        quantityOfbook_User.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getQuantity()).asObject());

        // Thiết lập cột nút "Xem"
        seeBookButton_User.setCellValueFactory(cellData -> {
            Button button = new Button("Xem");
            button.setOnAction(event -> {
                // Lấy thông tin sách đang chọn
                Book book = cellData.getValue();
                //Hiển thị thông tin sách đang chọn
                TitleShow_User.setText(book.getTitle());
                authorShow_User.setText(book.getAuthor());
                QuantityShow_User.setText(String.valueOf(book.getQuantity()));
                categoryShow_User.setText(String.valueOf(book.getCategory()));

                Image image = new Image(book.getImagePath());
                if(image != null) {
                    imageView_User.setImage(image);
                }
                // Xử lý hành động khi nhấn nút
                System.out.println("Đã nhấn nút xem cho sách: " + cellData.getValue().getTitle());


            });
            return new SimpleObjectProperty<>(button);
        });

        // Tải dữ liệu sách vào bảng
        loadBook();
    }

    private void loadBook() {
        ObservableList<Book> bookList = FXCollections.observableArrayList();

        try {
            Member member = new Member(currentUser.getUsername(), currentUser.getRole());
            List<Book> bookLists = member.viewAllBooks();
            if (bookLists != null) {
                bookList.addAll(bookLists);
                bookLibraryList.setItems(bookList); // Gán danh sách sách vào bảng
            } else {
                System.out.println("Không tìm thấy sách nào");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Lỗi không tìm thấy sách");
        }
    }
}
