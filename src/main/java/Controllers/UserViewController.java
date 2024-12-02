package Controllers;

import User.currentUser;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;

public class UserViewController {

    // Các thành phần giao diện
    @FXML
    private Label timeLabel;
    @FXML
    private Label dayOfWeekLabel;
    @FXML
    private Label dayLabel;
    @FXML
    private Label monthLabel;
    @FXML
    private Label username;
    @FXML
    private Label role;
    @FXML
    private Button personInformation;
    @FXML
    private Button bookLibrary;
    @FXML
    private Button bookBorrowed;
    @FXML
    private Button homeButton;
    @FXML
    private MenuButton logoutAndEditProfile;
    @FXML
    private Button searchButton;
    @FXML
    private ImageView libraryImageView;
    @FXML
    private ImageView borrowedBookImageView;
    @FXML
    private ImageView profileImageView;
    @FXML
    private ImageView homeImageView;
    @FXML
    private ImageView avatar;
    @FXML
    private MenuItem logoutButton;
    @FXML
    private MenuItem editProfileButton;
    @FXML
    private AnchorPane infoPane;
    @FXML
    private HBox exit;
    @FXML
    private TextField searchBook;
    @FXML
    private BorderPane borderPane_User;
    @FXML
    private ScrollPane scrollPane;

    private final Color defaultColor = Color.valueOf("#000000");
    private final Color activeColor = Color.valueOf("#FFFFFF");

    /**
     * Phương thức khởi tạo, thiết lập các giá trị ban đầu cho giao diện người dùng.
     * Cập nhật thời gian, ngày tháng và các sự kiện hover cho các nút.
     */
    @FXML
    private void initialize() {
        removeFocusFromAllNodes(scrollPane);
        loadFXML("/views/userHome.fxml");
        updateTime();
        updateDate();
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> updateTime()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
        username.setText(currentUser.getUsername());
        role.setText(currentUser.getRole());
        addHoverEffect(exit);
        addHoverEffect(searchButton);
        addHoverEffect(avatar);
        addHoverEffect(bookLibrary);
        addHoverEffect(bookBorrowed);
        addHoverEffect(logoutAndEditProfile);
        addHoverEffect(homeButton);
        setButtonTextColor(bookLibrary, defaultColor);
        setButtonTextColor(bookBorrowed, defaultColor);
        searchBook.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                search_Button(null);
            }
        });
    }

    /**
     * Cập nhật thời gian hiện tại trên giao diện.
     */
    private void updateTime() {
        LocalTime now = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        timeLabel.setText(now.format(formatter));
    }

    /**
     * Cập nhật ngày tháng và tên ngày trong tuần trên giao diện.
     */
    private void updateDate() {
        LocalDate today = LocalDate.now();
        String dayOfWeek = today.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault());
        dayOfWeekLabel.setText(dayOfWeek);
        dayLabel.setText(String.valueOf(today.getDayOfMonth()));
        String month = today.getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault());
        monthLabel.setText(month);
    }

    /**
     * Xử lý khi người dùng nhấn nút "Library" để xem thư viện sách.
     */
    @FXML
    void handleLibraryClick() {
        handleButtonClickWithFXML(libraryImageView, "/image/libraryColor.png", bookLibrary, "/views/viewBookInLibrary.fxml");
    }

    /**
     * Xử lý khi người dùng nhấn nút "Borrowed Books" để xem sách đã mượn.
     */
    @FXML
    void handleBorrowedClick() {
        handleButtonClickWithFXML(borrowedBookImageView, "/image/bookColor.png", bookBorrowed, "/views/viewBookBorrowed.fxml");
    }

    /**
     * Xử lý khi người dùng nhấn nút "Profile" để xem thông tin cá nhân.
     */
    @FXML
    void handlePersonClick() {
        handleButtonClickWithFXML(profileImageView, "/image/rabbit.png", personInformation, "/views/userProfile.fxml");
    }

    /**
     * Xử lý khi người dùng nhấn nút "Home" để quay lại trang chủ.
     */
    @FXML
    void handleHomeClick() {
        handleButtonClickWithFXML(homeImageView, "/image/homeColor.png", homeButton, "/views/userHome.fxml");
    }

    /**
     * Loại bỏ khả năng nhận focus cho tất cả các node trong một parent.
     *
     * @param parent Node cha chứa các thành phần con.
     */
    private void removeFocusFromAllNodes(Parent parent) {
        for (Node node : parent.getChildrenUnmodifiable()) {
            node.setFocusTraversable(false); // Không cho phép đối tượng nhận focus
            if (node instanceof Parent) {
                removeFocusFromAllNodes((Parent) node); // Đệ quy cho các node con
            }
        }
    }

    /**
     * Thay đổi hình ảnh của một ImageView.
     *
     * @param imageView ImageView muốn thay đổi.
     * @param imagePath Đường dẫn tới hình ảnh mới.
     */
    private void changeImage(ImageView imageView, String imagePath) {
        try {
            Image newImage = new Image(getClass().getResourceAsStream(imagePath));
            imageView.setImage(newImage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Thay đổi kiểu dáng của một nút bấm.
     *
     * @param button Nút bấm cần thay đổi kiểu dáng.
     */
    private void changeButtonStyle(Button button) {
        button.setTextFill(activeColor);

        String backgroundColor;
        if (button.equals(personInformation)) {
            backgroundColor = "rgba(0, 0, 0, 0)";
        } else {
            backgroundColor = "#AAAAAA";
        }

        button.setStyle("-fx-background-color: " + backgroundColor + ";");
    }

    /**
     * Đặt lại kiểu dáng và hình ảnh cho tất cả các nút.
     */
    private void resetButtonStyles() {
        setButtonTextColor(bookLibrary, defaultColor);
        setButtonTextColor(bookBorrowed, defaultColor);
        setButtonTextColor(personInformation, defaultColor);
        setButtonTextColor(homeButton, defaultColor);

        // Đặt lại hình ảnh cho tất cả ImageView
        changeImage(libraryImageView, "/image/libraryNoColor.png");
        changeImage(borrowedBookImageView, "/image/bookNoColor.png");
        changeImage(homeImageView, "/image/homeNoColor.png");
    }

    /**
     * Đặt màu sắc cho văn bản của một nút.
     *
     * @param button Nút bấm cần thay đổi màu văn bản.
     * @param color Màu sắc văn bản.
     */
    private void setButtonTextColor(Button button, Color color) {
        button.setTextFill(color);
        button.setStyle("-fx-background-color: transparent;");
    }

    /**
     * Thêm hiệu ứng hover (phóng to) cho một node khi chuột di chuyển vào và thu nhỏ khi chuột di chuyển ra.
     *
     * @param node Node cần thêm hiệu ứng hover.
     */
    private void addHoverEffect(Node node) {
        if (node != null) {
            node.setOnMouseEntered(event -> {
                node.setScaleX(1.1);
                node.setScaleY(1.1);
            });

            node.setOnMouseExited(event -> {
                node.setScaleX(1.0);
                node.setScaleY(1.0);
            });
        }
    }

    /**
     * Xử lý khi người dùng nhấn nút tìm kiếm.
     *
     * @param event Sự kiện khi người dùng nhấn nút tìm kiếm.
     */
    @FXML
    private void search_Button(ActionEvent event) {
        resetButtonStyles();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/viewBooksSearchResult.fxml"));
            Parent root = loader.load();
            ViewBooksSearchResult controller = loader.getController();
            String searchQuery = searchBook.getText();
            controller.setSearchQuery(searchQuery);
            borderPane_User.setCenter(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Tải một FXML vào BorderPane.
     *
     * @param fxmlPath Đường dẫn đến file FXML.
     */
    private void loadFXML(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            borderPane_User.setCenter(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Xử lý khi người dùng nhấn nút để chuyển sang một trang mới.
     *
     * @param imageView ImageView của nút.
     * @param imagePath Đường dẫn đến hình ảnh.
     * @param button Nút bấm.
     * @param fxmlPath Đường dẫn đến file FXML cần tải.
     */
    private void handleButtonClickWithFXML(ImageView imageView, String imagePath, Button button, String fxmlPath) {
        changeImage(imageView, imagePath);
        setButtonTextColor(button, activeColor);
        loadFXML(fxmlPath);
    }
    /**
     * Đăng xuất người dùng hiện tại và chuyển về giao diện đăng nhập.
     */
    @FXML
    private void logout_Button(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/login.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) borderPane_User.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
