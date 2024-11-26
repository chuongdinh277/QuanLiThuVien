package APIGoogle;


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
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.net.HttpURLConnection;
import java.net.http.HttpClient;
import java.sql.SQLException;
import java.text.Normalizer;

public class BookSearchApp extends Application {
    private static final HttpClient httpClient = GoogleBooksAPI.getHttpClient();
    public static Book books;
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Tìm kiếm sách bằng ISBN");

        // Tạo form
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(8);
        grid.setHgap(10);

        // Tạo các thành phần trong form
        Label isbnLabel = new Label("ISBN:");
        GridPane.setConstraints(isbnLabel, 0, 0);

        TextField isbnInput = new TextField();
        GridPane.setConstraints(isbnInput, 1, 0);

        Button searchButton = new Button("Tìm kiếm");
        GridPane.setConstraints(searchButton, 1, 1);

        Label titleLabel = new Label("Tiêu đề:");
        GridPane.setConstraints(titleLabel, 0, 2);
        TextField titleInput = new TextField();
        GridPane.setConstraints(titleInput, 1, 2);
        titleInput.setEditable(false); // Không cho phép chỉnh sửa tiêu đề

        Label authorLabel = new Label("Tác giả:");
        GridPane.setConstraints(authorLabel, 0, 3);
        TextField authorInput = new TextField();
        GridPane.setConstraints(authorInput, 1, 3);
        authorInput.setEditable(false); // Không cho phép chỉnh sửa tác giả

        Label categoryLabel = new Label("Thể loại:");
        GridPane.setConstraints(categoryLabel, 0, 4);
        TextField categoryInput = new TextField();
        GridPane.setConstraints(categoryInput, 1, 4);
        categoryInput.setEditable(false); // Không cho phép chỉnh sửa thể loại

        Label descriptionLabel = new Label("Mô tả:");
        GridPane.setConstraints(descriptionLabel, 0, 5);
        TextField descriptionInput = new TextField();
        GridPane.setConstraints(descriptionInput, 1, 5);
        descriptionInput.setEditable(false); // Không cho phép chỉnh sửa mô tả

        Label quantityLabel = new Label("Số lượng:");
        GridPane.setConstraints(quantityLabel, 0, 6);
        TextField quantityInput = new TextField("1"); // Đặt giá trị mặc định là 1
        GridPane.setConstraints(quantityInput, 1, 6);

        Label imageLabel = new Label("Hình ảnh:");
        GridPane.setConstraints(imageLabel, 0, 7);
        ImageView imageView = new ImageView();
        imageView.setFitWidth(100);
        imageView.setFitHeight(150);
        GridPane.setConstraints(imageView, 1, 7);

        // Thiết lập phông chữ hỗ trợ tiếng Việt
        titleInput.setFont(Font.font("Times New Roman", FontWeight.NORMAL, 12));
        authorInput.setFont(Font.font("Times New Roman", FontWeight.NORMAL, 12));
        categoryInput.setFont(Font.font("Times New Roman", FontWeight.NORMAL, 12));
        descriptionInput.setFont(Font.font("Times New Roman", FontWeight.NORMAL, 12));

        // Thêm tất cả vào grid
        grid.getChildren().addAll(isbnLabel, isbnInput, searchButton, titleLabel, titleInput, authorLabel, authorInput,
                categoryLabel, categoryInput, descriptionLabel, descriptionInput, quantityLabel, quantityInput, imageLabel, imageView);

        // Khi bấm nút tìm kiếm
        searchButton.setOnAction(e -> {
            String isbn = isbnInput.getText().trim();
            if (!isbn.isEmpty()) {
                // Thực hiện tìm kiếm trong background thread
                Task<Book> task = new Task<>() {
                    @Override
                    protected Book call() {
                        String jsonResponse = GoogleBooksAPI.searchBooksByTitle(isbn);
                        return BookParser.parseBookInfo(jsonResponse);
                    }

                    @Override
                    protected void succeeded() {
                        Book book = getValue();

                        if (book != null) {
                            //books = book;
//                            try {BookDAO.addBook(book);}
//                            catch (Exception e){
//                                BookSearchApp.showAlberDialog("Thêm sách không thành công");
//                            }

                            Platform.runLater(() -> {
                                titleInput.setText(Normalizer.normalize(book.getTitle(), Normalizer.Form.NFC));
                                authorInput.setText(Normalizer.normalize(book.getAuthor(), Normalizer.Form.NFC));
                                categoryInput.setText(Normalizer.normalize(book.getCategory(), Normalizer.Form.NFC));
                                descriptionInput.setText(Normalizer.normalize(book.getDescription(), Normalizer.Form.NFC));
                                if (book.getImagePath().equals("No image available")) {
                                    BookSearchApp.showAlberDialog("Không tìm thấy ảnh của sách với ISBN: " + isbn);
                                    book.setImagePath(null);
                                    imageView.setImage(null);

                                } else {
                                    imageView.setImage(new Image(book.getImagePath()));
                                }
                                quantityInput.setText("1");
                            });
                        } else {
                            Platform.runLater(() -> {
                                BookSearchApp.showAlberDialog("Không tìm thấy sách.");
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
                            BookSearchApp.showAlberDialog("Lỗi khi tìm kiếm sách.");
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

        Scene scene = new Scene(grid, 400, 400);
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

    public static void main(String[] args) {
        // Cấu hình DNS cache nếu cần
        System.setProperty("networkaddress.cache.ttl", "10"); // Cache DNS cho 10 giây
        System.setProperty("networkaddress.cache.negative.ttl", "5");
        launch(args);
    }
}
