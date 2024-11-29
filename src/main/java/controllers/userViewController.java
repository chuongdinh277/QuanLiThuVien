package controllers;

import User.*;
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

public class userViewController {

    // Label

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


    // Button
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


    // ImageView

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


    // Hỗn hợp

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

    @FXML
    private void initialize() {
        removeFocusFromAllNodes(scrollPane);
        updateTime();
        updateDate(); // Cập nhật ngày tháng khi khởi tạo
        // Tạo Timeline để cập nhật thời gian mỗi giây
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> updateTime()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
        // Display username and role
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
    }

    private void updateTime() {
        // Lấy thời gian hiện tại và định dạng nó
        LocalTime now = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        timeLabel.setText(now.format(formatter)); // Cập nhật Label
    }

    private void updateDate() {
        // Lấy ngày hiện tại
        LocalDate today = LocalDate.now();

        // Cập nhật thứ
        String dayOfWeek = today.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault());
        dayOfWeekLabel.setText(dayOfWeek);

        // Cập nhật ngày
        dayLabel.setText(String.valueOf(today.getDayOfMonth()));

        // Cập nhật tháng
        String month = today.getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault());
        monthLabel.setText(month);
    }

    @FXML
    void handleLibraryClick() {
        handleButtonClickWithFXML(libraryImageView, "/image/libraryColor.png", bookLibrary, "/views/viewBookInLibrary.fxml");
    }

    @FXML
    void handleBorrowedClick() {
        handleButtonClickWithFXML(borrowedBookImageView, "/image/bookColor.png", bookBorrowed, "/views/viewBookBorrowed.fxml");
    }

    @FXML
    void handlePersonClick() {
        handleButtonClickWithFXML(profileImageView, "/image/rabbit.png", personInformation, "/views/userProfile.fxml");
    }

    @FXML
    void handleHomeClick() {
        handleButtonClickWithFXML(homeImageView, "/image/homeColor.png", homeButton, "/views/viewBookInLibrary.fxml");
    }


    private void removeFocusFromAllNodes(Parent parent) {
        for (Node node : parent.getChildrenUnmodifiable()) {
            node.setFocusTraversable(false); // Không cho phép đối tượng nhận focus
            if (node instanceof Parent) {
                removeFocusFromAllNodes((Parent) node); // Đệ quy cho các node con
            }
        }
    }

    private void changeImage(ImageView imageView, String imagePath) {
        try {
            Image newImage = new Image(getClass().getResourceAsStream(imagePath));
            imageView.setImage(newImage);
        } catch (Exception e) {
            System.err.println("Error loading image: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void changeButtonStyle(Button button) {
        button.setTextFill(activeColor);

        String backgroundColor;
        if (button.equals(personInformation)) {
            backgroundColor = "rgba(0, 0, 0, 0)"; // Màu nền trong suốt
        } else {
            backgroundColor = "#AAAAAA"; // Màu nền xám
        }

        button.setStyle("-fx-background-color: " + backgroundColor + ";");
    }

    private void resetButtonStyles() {
        setButtonTextColor(bookLibrary, defaultColor);
        setButtonTextColor(bookBorrowed, defaultColor);
        setButtonTextColor(personInformation, defaultColor);
        setButtonTextColor(homeButton, defaultColor);

        // Đặt lại hình ảnh cho tất cả ImageView
        changeImage(libraryImageView, "/image/libraryNoColor.png");
        changeImage(borrowedBookImageView, "/image/bookNoColor.png");
//        changeImage(profileImageView, "/image/userNoColor.png");
        changeImage(homeImageView, "/image/homeNoColor.png");
    }


    private void setButtonTextColor(Button button, Color color) {
        button.setTextFill(color);
        button.setStyle("-fx-background-color: transparent;");
    }

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
            System.out.println("Load successful");
        } catch (Exception e) {
            System.err.println("Error loading FXML: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadFXML(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            borderPane_User.setCenter(root);
            System.out.println("Load successful");
        } catch (Exception e) {
            System.err.println("Error loading FXML: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void logout_Button(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/login.fxml"));
            Parent root = loader.load();

            // Lấy Stage hiện tại từ bất kỳ Node nào
            Stage stage = (Stage) borderPane_User.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            System.err.println("Error loading FXML: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private void loadFXML(String fxmlPath, ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Node sourceNode = (Node) event.getSource();
            Stage stage = (Stage) sourceNode.getScene().getWindow();

            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            System.err.println("Error loading FXML: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleButtonClickWithFXML(ImageView imageView, String imagePath, Button button, String fxmlPath) {
        try {
            resetButtonStyles(); // Đặt lại kiểu dáng mặc định
            changeImage(imageView, imagePath); // Thay đổi hình ảnh của nút được nhấn
            changeButtonStyle(button); // Thay đổi kiểu dáng của nút được nhấn
            loadFXML(fxmlPath); // Load file FXML tương ứng
        } catch (Exception e) {
            System.err.println("Error in handleButtonClickWithFXML: " + e.getMessage());
            e.printStackTrace();
        }
    }

}