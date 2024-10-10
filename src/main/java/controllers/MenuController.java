package controllers;

import APIGoogle.GoogleBooksAPI; // Đảm bảo bạn đã import GoogleBooksAPI
import BookRating.BookRating;
import BookRating.BookRatingDAO;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import User.currentUser; // Giả định rằng bạn đã có lớp User có biến currentUser

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;

public class MenuController {
    @FXML
    private Label name;
    @FXML
    private Label role;
    @FXML
    private HBox loveBooksHBox;

    public void initialize() {
        // Hiển thị tên người dùng và vai trò
        name.setText(currentUser.getUsername());
        role.setText(currentUser.getRole());

        // Gọi hàm để hiển thị sách theo thể loại
        try {
            displayBooksByCategory();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void displayBooksByCategory() throws SQLException {
        // Gọi phương thức tìm sách từ API và hiển thị trong loveBooksHBox
        searchAndDisplayBooks("Love Book");
    }

    private void searchAndDisplayBooks(String query) {
        loveBooksHBox.getChildren().clear(); // Xóa danh sách cũ trong HBox
        String jsonResponse = GoogleBooksAPI.searchBooksByTitle(query); // Gọi API để tìm sách

        if (jsonResponse != null) {
            JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();
            JsonArray items = jsonObject.getAsJsonArray("items");

            if (items != null && items.size() > 0) {
                for (int i = 0; i < items.size(); i++) {
                    JsonObject book = items.get(i).getAsJsonObject();
                    JsonObject volumeInfo = book.getAsJsonObject("volumeInfo");

                    String title = volumeInfo.get("title").getAsString();
                    String author = volumeInfo.has("authors") ? volumeInfo.getAsJsonArray("authors").get(0).getAsString() : "Unknown Author";
                    String imagePath = volumeInfo.has("imageLinks") ? volumeInfo.getAsJsonObject("imageLinks").get("thumbnail").getAsString() : "default_image_url"; // Đặt URL mặc định nếu không có

                    BookRating bookRating = new BookRating(title, author, "Love Book", 0, "", imagePath, 0.0);
                    VBox vBox = createBookVBox(bookRating);
                    loveBooksHBox.getChildren().add(vBox);
                }
            } else {
                System.out.println("No love books found.");
            }
        }
    }

    private VBox createBookVBox(BookRating book) {
        VBox vBox = new VBox();
        ImageView imageView = new ImageView();

        // Tạo đối tượng Image từ đường dẫn ảnh
        Image image = new Image(book.getImagePath());
        imageView.setImage(image);

        // Thiết lập kích thước cho ImageView
        imageView.setFitHeight(150);
        imageView.setFitWidth(100);

        // Thêm các thành phần vào VBox
        Text titleText = new Text(book.getTitle());
        Text authorText = new Text(book.getAuthor());

        vBox.getChildren().addAll(imageView, titleText, authorText);
        return vBox;
    }
}
