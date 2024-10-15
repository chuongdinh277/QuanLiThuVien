package APIGoogle;

import APIGoogle.BookParser;
import APIGoogle.GoogleBooksAPI;
import Document.Book;
import Document.BookDAO;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.net.http.HttpClient;
import java.sql.SQLException;
import java.text.Normalizer;

public class BookSearchApp2 extends Application {
    private static final HttpClient httpClient = GoogleBooksAPI.getHttpClient();
    private Book result ;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Tìm kiếm sách bằng ISBN Title AND Author");

        HBox hBox = new HBox(20);
        // Tạo form
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(9);
        grid.setHgap(11);

        // Tạo các thành phần trong form
        Label authorLabel = new Label("Tác giả:");
        GridPane.setConstraints(authorLabel, 0, 0);

        TextField authorInputSearch = new TextField();
        GridPane.setConstraints(authorInputSearch, 1, 0);

        Label titleLabelSearch = new Label("Tiêu đề:");
        GridPane.setConstraints(titleLabelSearch, 0, 1);

        TextField titleInputSearch = new TextField();
        GridPane.setConstraints(titleInputSearch, 1, 1);

        Button searchButton = new Button("Tìm kiếm");
        GridPane.setConstraints(searchButton, 1, 2);

        Label titleLabel = new Label("Tiêu đề:");
        GridPane.setConstraints(titleLabel, 0, 3);
        TextField titleInput = new TextField();
        GridPane.setConstraints(titleInput, 1, 3);
        titleInput.setEditable(false);

        Label authorLabelResult = new Label("Tác giả:");
        GridPane.setConstraints(authorLabelResult, 0, 4);
        TextField authorInput = new TextField();
        GridPane.setConstraints(authorInput, 1, 4);
        authorInput.setEditable(false);

        Label categoryLabel = new Label("Thể loại:");
        GridPane.setConstraints(categoryLabel, 0, 5);
        TextField categoryInput = new TextField();
        GridPane.setConstraints(categoryInput, 1, 5);
        categoryInput.setEditable(false);

        Label descriptionLabel = new Label("Mô tả:");
        GridPane.setConstraints(descriptionLabel, 0, 6);
        TextField descriptionInput = new TextField();
        GridPane.setConstraints(descriptionInput, 1, 6);
        descriptionInput.setEditable(false);

        Label quantityLabel = new Label("Số lượng:");
        GridPane.setConstraints(quantityLabel, 0, 7);
        TextField quantityInput = new TextField("1");
        GridPane.setConstraints(quantityInput, 1, 7);

        Label imageLabel = new Label("Hình ảnh:");
        GridPane.setConstraints(imageLabel, 0, 8);
        ImageView imageView = new ImageView();
        imageView.setFitWidth(100);
        imageView.setFitHeight(150);
        GridPane.setConstraints(imageView, 1, 8);

        // Thiết lập phông chữ hỗ trợ tiếng Việt
        titleInput.setFont(Font.font("Times New Roman", FontWeight.NORMAL, 12));
        authorInput.setFont(Font.font("Times New Roman", FontWeight.NORMAL, 12));
        categoryInput.setFont(Font.font("Times New Roman", FontWeight.NORMAL, 12));
        descriptionInput.setFont(Font.font("Times New Roman", FontWeight.NORMAL, 12));

        // Thêm tất cả vào grid
        grid.getChildren().addAll(authorLabel, authorInputSearch, titleLabelSearch, titleInputSearch, searchButton,
                titleLabel, titleInput, authorLabelResult, authorInput, categoryLabel, categoryInput,
                descriptionLabel, descriptionInput, quantityLabel, quantityInput, imageLabel, imageView);

        Button addBookButton = new Button("ADD BOOK");
        addBookButton.setDisable(true);
        hBox.getChildren().addAll(grid, addBookButton);
        // Khi bấm nút tìm kiếm
        searchButton.setOnAction(e -> {
            String title = titleInputSearch.getText().trim();
            String author = authorInputSearch.getText().trim();
            if (!title.isEmpty() && !author.isEmpty()) {
                // Thực hiện tìm kiếm trong background thread
                Task<Book> task = new Task<>() {
                    @Override
                    protected Book call() {
                        String jsonResponse = GoogleBooksAPI.searchBookByTitleAndAuthor(title, author);
                        return BookParser.parseBookInfo(jsonResponse);
                    }

                    @Override
                    protected void succeeded() {
                        Book book = getValue();
                        if (book != null) {
                            result = book;
                            Platform.runLater(() -> {
                                titleInput.setText(Normalizer.normalize(book.getTitle(), Normalizer.Form.NFC));
                                authorInput.setText(Normalizer.normalize(book.getAuthor(), Normalizer.Form.NFC));
                                categoryInput.setText(Normalizer.normalize(book.getCategory(), Normalizer.Form.NFC));
                                descriptionInput.setText(Normalizer.normalize(book.getDescription(), Normalizer.Form.NFC));
                                if (book.getImagePath().equals("No image available")) {
                                    APIGoogle.BookSearchApp.showAlberDialog("Không tìm thấy ảnh của sách . " );
                                    book.setImagePath(null);
                                    imageView.setImage(null);

                                } else {
                                    imageView.setImage(new Image(book.getImagePath()));
                                }
                                quantityInput.setText("1");// Gán giá trị mặc định cho số lượng
                                addBookButton.setDisable(false);
                            });
                        } else {
                            Platform.runLater(() -> {
                                APIGoogle.BookSearchApp.showAlberDialog("Không tìm thấy sách.");
                                // Xử lý khi không tìm thấy sách
                                titleInput.setText("Không tìm thấy sách.");
                                authorInput.clear();
                                categoryInput.clear();
                                descriptionInput.clear();
                                imageView.setImage(null);
                                quantityInput.setText("1");
                            });
                        }
                    }

                    @Override
                    protected void failed() {
                        Platform.runLater(() -> {
                            APIGoogle.BookSearchApp.showAlberDialog("Lỗi khi tìm kiếm sách.");
                            // Xử lý lỗi khi lấy dữ liệu từ API
                            titleInput.setText("Lỗi khi lấy dữ liệu.");
                            authorInput.clear();
                            categoryInput.clear();
                            descriptionInput.clear();
                            imageView.setImage(null);
                            quantityInput.setText("1");
                        });
                    }
                };

                new Thread(task).start();
            }
        });
        addBookButton.setOnAction(e -> {
            String title = titleInput.getText().trim();
            String author = authorInput.getText().trim();
            String category = categoryInput.getText().trim();
            String description = descriptionInput.getText().trim();
            int quantity = Integer.parseInt(quantityInput.getText());
            String imagePath = result.getImagePath();
            try {
                boolean check =  BookDAO.addBook(new Book(title, author, category, quantity,description, imagePath));
                if (check) {
                    showAlberDialog("Thêm thành công");
                } else {
                    showAlberDialog("Thêm thất bại");
                }
                addBookButton.setDisable(true);
            } catch (SQLException ex) {
                showErrorDialog("Error", ex.getMessage());
            }

        });
        Scene scene = new Scene(hBox, 400, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void showAlberDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    public static void showErrorDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        // Cấu hình DNS cache nếu cần
        System.setProperty("networkaddress.cache.ttl", "10"); // Cache DNS cho 10 giây
        System.setProperty("networkaddress.cache.negative.ttl", "5");
        launch(args);
    }
}
