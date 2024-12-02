package Controllers;

import Document.Book;
import Document.BookDAO;
import Document.Transaction;
import Document.TransactionDAO;
import User.User;
import User.Admin;
import User.currentUser;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static APIGoogle.GoogleBooksAPI.showErrorDialog;

public class DashboardController {

    private BorderPane mainBorderPane;
    @FXML
    private Label percentageQuantityLabel;
    @FXML
    private Label percentageBorrowLabel;
    @FXML
    private AnchorPane pane1;
    @FXML
    private AnchorPane pane2;
    @FXML
    private Label label1;
    @FXML
    private Label label2;
    @FXML
    private StackedBarChart categoriesBarchart;
    @FXML
    private Label timeLabel;
    @FXML
    private Label categoryLabel;
    @FXML
    private Label usernameLabel;
    @FXML
    private AnchorPane circlePane;
    @FXML
    private Circle outerCircle;
    @FXML
    private ListView<User> listMember;
    @FXML
    private ListView<Book> listViewBook;
    private int booksCount = 0;
    @FXML
    private Label booksCountLabel;
    @FXML
    private Label membersCountLabel;
    @FXML
    private Label bookQuantityLabel;
    private int membersCount = 0;
    private int bookQuantity = 0;
    private int bookQuantityBorrowed = 0;
    private int categories = 0;

    /**
     * Sets the main BorderPane for the controller.
     * This allows the controller to reference and manipulate the application's main layout.
     *
     * @param mainBorderPane the main BorderPane of the application
     */
    public void setMainBorderPane(BorderPane mainBorderPane) {
        this.mainBorderPane = mainBorderPane;
    }

    /**
     * Initializes the controller.
     * This method sets up the username display, a real-time clock, and loads various data including
     * student, book, and category statistics. Additionally, it calculates and updates a progress circle
     * to reflect the borrowing statistics.
     */
    public void initialize() {
        usernameLabel.setText(currentUser.getUsername());

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(0), e -> {
                    String currentTime = sdf.format(new Date());
                    timeLabel.setText(currentTime);
                }),
                new KeyFrame(Duration.minutes(1))
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        loadAllStudents();
        loadAllBooks();

        membersCountLabel.setText(String.valueOf(membersCount));
        booksCountLabel.setText(String.valueOf(booksCount));
        bookQuantityLabel.setText(String.valueOf(bookQuantity));

        loadCategoryStatistics();
        categoryLabel.setText(String.valueOf(categories));

        loadBookborrowed();
        double result = (double) (bookQuantityBorrowed) / bookQuantity * 100;
        drawProgressCircle(result);
    }

    /**
     * Tải danh sách tất cả học sinh và cập nhật số lượng thành viên.
     * Lấy danh sách người dùng từ lớp Admin, lọc ra các mục không hợp lệ
     * và hiển thị những người dùng hợp lệ trong ListView listMember.
     */
    private void loadAllStudents() {
        List<User> memberList = Admin.getAllUsers();

        if (memberList != null) {
            membersCount = memberList.size();

            List<User> validUsers = memberList.stream()
                    .filter(user -> user != null && user.getId() != 0 && user.getFullName() != null)
                    .toList();

            ObservableList<User> observableList = FXCollections.observableArrayList(validUsers);
            listMember.setItems(observableList);

            listMember.setCellFactory(listView -> new ListCell<>() {
                @Override
                protected void updateItem(User user, boolean empty) {
                    super.updateItem(user, empty);
                    if (empty || user == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        setText(user.getId() + "    " + user.getFullName());
                        setStyle("-fx-padding: 10px;");
                    }
                }
            });
        }
    }

    /**
     * Tải danh sách tất cả sách và cập nhật số lượng sách và tổng số lượng.
     * Lấy danh sách sách từ lớp BookDAO, lọc ra các mục không hợp lệ,
     * và hiển thị các sách hợp lệ trong ListView listViewBook với định dạng ô tùy chỉnh.
     */
    private void loadAllBooks() {
        try {
            List<Book> bookList = BookDAO.getAllBooks();

            if (bookList != null) {
                booksCount = bookList.size();
                for (Book book : bookList) {bookQuantity += book.getQuantity();}
                List<Book> validBooks = bookList.stream()
                        .filter(book -> book != null)
                        .toList();
                ObservableList<Book> observableBooks = FXCollections.observableArrayList(validBooks);

                listViewBook.setItems(observableBooks);

                listViewBook.setCellFactory(listView -> new ListCell<Book>() {
                    @Override
                    protected void updateItem(Book book, boolean empty) {
                        super.updateItem(book, empty);
                        if (empty || book == null) {
                            setText(null);
                            setGraphic(null);
                        } else {

                            HBox hBox = new HBox(10);
                            hBox.setStyle("-fx-padding: 10px;");

                            Label isbnLabel = new Label(book.getISBN());
                            Label titleLabel = new Label(book.getAuthor());

                            hBox.getChildren().addAll(isbnLabel, titleLabel);

                            setGraphic(hBox);
                        }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Tải thống kê danh mục sách và hiển thị dưới dạng biểu đồ cột.
     * Phương thức này lấy dữ liệu thống kê từ cơ sở dữ liệu thông qua BookDAO,
     * sau đó cập nhật biểu đồ cột với số lượng sách của từng danh mục.
     */
    private void loadCategoryStatistics() {
        try {
            Map<String, Integer> categoryStats = BookDAO.getCategoryStatistics();
            categoriesBarchart.getData().clear();

            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Số lượng sách");

            for (Map.Entry<String, Integer> entry : categoryStats.entrySet()) {
                String category = entry.getKey();
                categories += 1;
                int count = entry.getValue();
                series.getData().add(new XYChart.Data<>(category, count));
            }

            categoriesBarchart.getData().add(series);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Tải danh sách giao dịch mượn sách và cập nhật số lượng sách đã mượn.
     * Lấy dữ liệu giao dịch từ TransactionDAO và đếm số lượng sách đã được mượn.
     */
    private void loadBookborrowed() {
        try {
            List<Transaction> transactions = TransactionDAO.getAllTransaction();
            bookQuantityBorrowed = transactions.size();
        } catch (Exception e) {
            showErrorDialog("Lỗi khi tải giao dịch: " + e.getMessage());
        }
    }

    /**
     * Vẽ biểu đồ tròn hiển thị tỷ lệ phần trăm sách đã được mượn.
     * Tạo các thành phần vòng tròn, vòng cung hiển thị tiến độ, và nhãn phần trăm,
     * sau đó thêm chúng vào giao diện trong CirclePane.
     *
     * @param percentage Tỷ lệ phần trăm sách đã mượn.
     */
    private void drawProgressCircle(double percentage) {

        double radius = 100.0;
        double strokeWidth = 30.0;

        Circle outerCircle = new Circle(radius);
        outerCircle.setStrokeWidth(strokeWidth);
        outerCircle.setStroke(Color.GRAY);
        outerCircle.setFill(Color.TRANSPARENT);

        Arc progressArc = new Arc();
        progressArc.setRadiusX(radius);
        progressArc.setRadiusY(radius);
        progressArc.setStartAngle(90);
        progressArc.setLength(-percentage * 360 / 100);
        progressArc.setType(ArcType.OPEN);
        progressArc.setStroke(Color.web("#02d7f7"));
        progressArc.setStrokeWidth(strokeWidth);
        progressArc.setFill(Color.TRANSPARENT);

        Circle innerCircle = new Circle(radius - strokeWidth);
        innerCircle.setStrokeWidth(0);
        innerCircle.setFill(Color.TRANSPARENT);

        Label percentageLabel = new Label(String.format("%.0f%%", percentage));
        percentageLabel.setStyle("-fx-font-size: 40px; -fx-font-weight: bold; -fx-text-fill: #f2aaf1;");
        percentageBorrowLabel.setText(String.valueOf(bookQuantityBorrowed));
        percentageQuantityLabel.setText(String.valueOf(bookQuantity));

        circlePane.getChildren().clear();

        double paneCenterX = circlePane.getWidth() + 165;
        double paneCenterY = circlePane.getHeight() + 180;
        outerCircle.setCenterX(paneCenterX);
        outerCircle.setCenterY(paneCenterY);
        progressArc.setCenterX(paneCenterX);
        progressArc.setCenterY(paneCenterY);
        innerCircle.setCenterX(paneCenterX);
        innerCircle.setCenterY(paneCenterY);

        percentageLabel.setLayoutX(paneCenterX - percentageLabel.getWidth() / 2 - 35);
        percentageLabel.setLayoutY(paneCenterY - percentageLabel.getHeight() / 2 - 30);
        circlePane.getChildren().addAll(outerCircle, progressArc, innerCircle, percentageLabel, pane2, pane1, label1, label2, percentageBorrowLabel, percentageQuantityLabel);
    }

    /**
     * Xử lý sự kiện nhấp chuột vào nhãn xem tất cả sinh viên.
     * Tải giao diện danh sách sinh viên từ tệp FXML và đặt nó vào trung tâm của BorderPane.
     *
     * @param event Sự kiện chuột.
     */
    @FXML
    private void seeallstudentsLabel(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/students.fxml"));
            Pane homeRoot = loader.load();
            mainBorderPane.setCenter(homeRoot);
        } catch (IOException e) {
            e.printStackTrace();
            showErrorDialog("Lỗi khi tải giao diện students.fxml: " + e.getMessage());
        }
    }

    /**
     * Xử lý sự kiện nhấp chuột vào nhãn xem tất cả sách.
     * Tải giao diện danh sách sách từ tệp FXML và đặt nó vào trung tâm của BorderPane.
     *
     * @param event Sự kiện chuột.
     */
    @FXML
    private void seeallbooksLabel(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/allBook.fxml"));
            ScrollPane homeRoot = loader.load();
            mainBorderPane.setCenter(homeRoot);
        } catch (IOException e) {
            e.printStackTrace();
            showErrorDialog("Lỗi khi tải giao diện allBook.fxml: " + e.getMessage());
        }
    }


}
