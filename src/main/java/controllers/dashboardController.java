package controllers;

import Document.Book;
import Document.BookDAO;
import Document.Transaction;
import Document.TransactionDAO;
import User.User;
import User.Admin;
import User.currentUser;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
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

public class dashboardController {
    @FXML
    private Label username;
    @FXML
    private Label role;
    @FXML
    private MenuButton logoutAndEditProfile;
    @FXML
    private MenuItem editProfileButton;
    @FXML
    private MenuItem logoutButton;

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

    public void setMainBorderPane(BorderPane mainBorderPane) {
        this.mainBorderPane = mainBorderPane;
    }

    public void initialize() {
        // Hiển thị thời gian hiện tại
        usernameLabel.setText(currentUser.getUsername());
        username.setText(currentUser.getUsername());
        role.setText(currentUser.getRole());
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(0), e -> {
                    String currentTime = sdf.format(new Date());
                    timeLabel.setText(currentTime);
                }),
                new KeyFrame(Duration.minutes(1)) // Cập nhật mỗi phút
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        // Load danh sách người dùng
        loadAllStudents();
        loadAllBooks();
        System.out.println(membersCount);
        membersCountLabel.setText(String.valueOf(membersCount));
        booksCountLabel.setText(String.valueOf(booksCount));
        bookQuantityLabel.setText(String.valueOf(bookQuantity));
        loadCategoryStatistics();
        categoryLabel.setText(String.valueOf(categories));
        loadBookborrowed();
        System.out.println(bookQuantityBorrowed);
        double result = (double)(bookQuantityBorrowed+1000) / bookQuantity * 100;
        System.out.println(result);
        drawProgressCircle(result);




    }

    private void loadAllStudents() {
        // Lấy danh sách từ Admin
        List<User> memberList = Admin.getAllUsers();

        if (memberList != null) {
            membersCount = memberList.size();
            System.out.println("Danh sách không rỗng.");

            // Lọc danh sách để chỉ lấy người dùng hợp lệ
            List<User> validUsers = memberList.stream()
                    .filter(user -> user != null && user.getId() != 0 && user.getFullName() != null)
                    .toList();

            ObservableList<User> observableList = FXCollections.observableArrayList(validUsers);
            listMember.setItems(observableList);

            // Sử dụng CellFactory để hiển thị dữ liệu
            listMember.setCellFactory(listView -> new ListCell<>() {
                @Override
                protected void updateItem(User user, boolean empty) {
                    super.updateItem(user, empty);
                    if (empty || user == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        // Hiển thị thông tin người dùng
                        setText(user.getId() + "    " + user.getFullName());
                        setStyle("-fx-padding: 10px;");
                    }
                }
            });
        } else {
            System.out.println("Danh sách rỗng.");
        }
    }

    private void loadAllBooks() {
        try {
            // Lấy danh sách sách từ BookDAO
            List<Book> bookList = BookDAO.getAllBooks();

            //System.out.println(bookList.size());
            if (bookList != null) {
                booksCount = bookList.size();
                System.out.println("Danh sách sách không rỗng.");
                for (Book book : bookList) {bookQuantity += book.getQuantity();}
                List<Book> validBooks = bookList.stream()
                        .filter(book -> book != null)
                        .toList();
                ObservableList<Book> observableBooks = FXCollections.observableArrayList(validBooks);

                // Hiển thị sách trong ListView
                listViewBook.setItems(observableBooks);

                // Sử dụng CellFactory để hiển thị thông tin sách
                listViewBook.setCellFactory(listView -> new ListCell<Book>() {
                    @Override
                    protected void updateItem(Book book, boolean empty) {
                        super.updateItem(book, empty);
                        if (empty || book == null) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            // Tạo một HBox để hiển thị các thông tin theo dạng hàng ngang
                            HBox hBox = new HBox(10);  // Khoảng cách giữa các phần tử
                            hBox.setStyle("-fx-padding: 10px;");

                            // Tạo các Label cho mỗi cột
                            Label isbnLabel = new Label(book.getISBN());
                            Label titleLabel = new Label(book.getAuthor());

                            // Thêm các Label vào HBox
                            hBox.getChildren().addAll(isbnLabel, titleLabel);

                            // Đặt HBox làm graphic của cell
                            setGraphic(hBox);
                        }
                    }
                });
            } else {
                System.out.println("Danh sách sách rỗng.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Lỗi khi lấy danh sách sách.");
        }
    }

    private void loadCategoryStatistics(){
        try {
                Map<String, Integer> categoryStats = BookDAO.getCategoryStatistics();

                // Xóa tất cả dữ liệu cũ trong biểu đồ (nếu có)
                categoriesBarchart.getData().clear();

                // Tạo một Series mới cho biểu đồ
                XYChart.Series<String, Number> series = new XYChart.Series<>();
                series.setName("Số lượng sách");

                // Thêm dữ liệu vào Series
                for (Map.Entry<String, Integer> entry : categoryStats.entrySet()) {
                    String category = entry.getKey();
                    categories+=1;
                    int count = entry.getValue();
                    series.getData().add(new XYChart.Data<>(category, count));
                }
                categoriesBarchart.getData().add(series);
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("Lỗi khi tải thống kê thể loại sách.");
            }
    }
    private void loadBookborrowed() {
        try {
            List<Transaction> transactions = TransactionDAO.getAllTransaction();
            bookQuantityBorrowed = transactions.size();

        } catch (Exception e) {
            showErrorDialog("Lỗi khi tải giao dịch: " + e.getMessage());
        }
    }
    private void drawProgressCircle(double percentage) {
        // Kích thước và màu sắc
        double radius = 100.0; // Bán kính hình tròn
        double strokeWidth = 30.0; // Độ dày viền

        // Hình tròn ngoài
        Circle outerCircle = new Circle(radius);
        outerCircle.setStrokeWidth(strokeWidth);
        outerCircle.setStroke(Color.GRAY); // Màu viền
        outerCircle.setFill(Color.TRANSPARENT); // Trong suốt

        // Tạo Arc để hiển thị phần trăm
        Arc progressArc = new Arc();
        progressArc.setRadiusX(radius);
        progressArc.setRadiusY(radius);
        progressArc.setStartAngle(90); // Bắt đầu từ vị trí 12 giờ
        progressArc.setLength(-percentage * 360 / 100); // Chiều dài theo %
        progressArc.setType(ArcType.OPEN);
        progressArc.setStroke(Color.web("#02d7f7"));
        progressArc.setStrokeWidth(strokeWidth);
        progressArc.setFill(Color.TRANSPARENT);

        // Hình tròn trong
        Circle innerCircle = new Circle(radius - strokeWidth);
        innerCircle.setStrokeWidth(0);
        innerCircle.setFill(Color.TRANSPARENT);

        Label percentageLabel = new Label(String.format("%.0f%%", percentage)); // Chuyển đổi phần trăm thành string
        percentageLabel.setStyle("-fx-font-size: 40px; -fx-font-weight: bold; -fx-text-fill: #f2aaf1;");
        percentageBorrowLabel.setText(String.valueOf(bookQuantityBorrowed));
        percentageQuantityLabel.setText(String.valueOf(bookQuantity));
        // Xóa các phần tử cũ trong `circlePane`
        circlePane.getChildren().clear();

        // Căn chỉnh phần tử trong `circlePane`
        double paneCenterX = circlePane.getWidth() + 165 ;
        double paneCenterY = circlePane.getHeight() + 180 ;
        System.out.println(paneCenterX);
        System.out.println(paneCenterY);
        outerCircle.setCenterX(paneCenterX);
        outerCircle.setCenterY(paneCenterY);
        progressArc.setCenterX(paneCenterX);
        progressArc.setCenterY(paneCenterY);
        innerCircle.setCenterX(paneCenterX);
        innerCircle.setCenterY(paneCenterY);

        percentageLabel.setLayoutX(paneCenterX - percentageLabel.getWidth() / 2 - 35);
        percentageLabel.setLayoutY(paneCenterY - percentageLabel.getHeight() / 2 - 30);
        // Thêm các phần tử vào `circlePane`
        circlePane.getChildren().addAll(outerCircle, progressArc, innerCircle, percentageLabel,pane2,pane1,label1,label2,percentageBorrowLabel,percentageQuantityLabel);
    }

    @FXML
    private void seeallstudentsLabel(MouseEvent event) {
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/students.fxml"));
            Pane homeRoot = loader.load();
            mainBorderPane.setCenter(homeRoot);
        } catch (IOException e) {
            e.printStackTrace();
            showErrorDialog("Lỗi khi tải giao diện home.fxml: " + e.getMessage());
        }
    }
    @FXML
    private void seeallbooksLabel(MouseEvent event) {
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/home.fxml"));
            ScrollPane homeRoot = loader.load();
            mainBorderPane.setCenter(homeRoot);
        } catch (IOException e) {
            e.printStackTrace();
            showErrorDialog("Lỗi khi tải giao diện home.fxml: " + e.getMessage());
        }
    }

    @FXML
    private void handlePersonClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/profile.fxml"));
            AnchorPane personRoot = loader.load();
            mainBorderPane.setCenter(personRoot);
        } catch (IOException e) {
            e.printStackTrace();
            showErrorDialog("L��i khi tải giao diện person.fxml: " + e.getMessage());
        }
    }

    @FXML
    private void logout_Button(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/login.fxml"));
            Parent root = loader.load();

            // Lấy Stage hiện tại từ bất kỳ Node nào
            Stage stage = (Stage) timeLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            System.err.println("Error loading FXML: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
