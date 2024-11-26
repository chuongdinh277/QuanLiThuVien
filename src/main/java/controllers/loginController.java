package controllers;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.event.ActionEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Random;
import java.util.ResourceBundle;
import User.Admin;
import User.User;
import User.currentUser;
import User.Member;
import javafx.util.Duration;

public class loginController implements Initializable {
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordHiddenlogin;
    @FXML
    private TextField usernameFieldlogin;

    @FXML
    private PasswordField PasswordHiddenRL;
    @FXML
    private PasswordField passwordHidden;

    @FXML
    private Label welcomeLabel;
    @FXML
    private Button signUpButton;

    @FXML
    private Button loginButton;
    @FXML
    private Button registerButton;
    @FXML
    private ChoiceBox<String> myChoiceBox;
    @FXML
    private AnchorPane starPane;
    @FXML
    private Button loginReturn;
    @FXML
    private TextField fullNameField;
    @FXML
    private TextField numberField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField MSSVTextfield;
    @FXML
    private ImageView showImage;
    @FXML
    private TextField passwordTextField;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        myChoiceBox.getItems().add("admin");
        myChoiceBox.getItems().add("user");

        if (starPane != null) {
            Timeline snowflakeGenerator = new Timeline(
                    new KeyFrame(Duration.millis(100), e -> createBlinkingStar())
            );
            snowflakeGenerator.setCycleCount(Timeline.INDEFINITE);
            snowflakeGenerator.play();
        }

        HBox hbox = new HBox(16); // Hộp chứa các chữ cái, khoảng cách giữa các chữ cái
        String text = "Welcome to Library";
        // Tạo Label cho mỗi ký tự và thêm vào HBox
        for (char c : text.toCharArray()) {
            Label letter = new Label(String.valueOf(c));
            letter.setFont(Font.font(30)); // Thiết lập kích thước chữ
            hbox.getChildren().add(letter);

            // Tạo hiệu ứng di chuyển cho mỗi chữ cái
            TranslateTransition transition = new TranslateTransition();
            transition.setNode(letter);
            transition.setFromY(500); // Vị trí ban đầu (dưới)
            transition.setToY(-50);   // Vị trí kết thúc (trên)
            transition.setCycleCount(1); // Chạy một lần
            transition.setInterpolator(javafx.animation.Interpolator.LINEAR);
            transition.setDuration(Duration.seconds(1)); // Thời gian chạy của chữ

            // Bắt đầu hiệu ứng di chuyển
            transition.play();
        }
    }
    @FXML
    public void handleShowPassword(MouseEvent event) {
        // Kiểm tra xem hiện tại mật khẩu có đang được ẩn không
        if (passwordHiddenlogin.isVisible()) {
            passwordTextField.setText(passwordHiddenlogin.getText());
            passwordTextField.setVisible(true);
            passwordHiddenlogin.setVisible(false);
        } else {
            passwordHiddenlogin.setText(passwordTextField.getText());
            passwordHiddenlogin.setVisible(true);
            passwordTextField.setVisible(false);
        }
    }

    // Xử lí đăng kí tài khoản
    @FXML
    public void handleRegister(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordHidden.getText();
        String passwordConfirm = PasswordHiddenRL.getText();
        String number = numberField.getText();
        String email = emailField.getText();
        String fullName = fullNameField.getText();
        String role = myChoiceBox.getValue();
        String mssv = MSSVTextfield.getText();  // Lấy giá trị MSSV từ TextField

        // Kiểm tra xem các trường bắt buộc có được điền đầy đủ không
        if(username.isEmpty() || password.isEmpty() || passwordConfirm.isEmpty() || role.isEmpty() || number.isEmpty()
                || email.isEmpty() || fullName.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lỗi đăng kí");
            alert.setHeaderText("Vui lòng điền đầy đủ thông tin");
            alert.showAndWait();
            return;
        }

        // Kiểm tra mật khẩu nhập lại có khớp không
        if(!passwordConfirm.equals(password)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lỗi đăng kí");
            alert.setHeaderText("Mật khẩu nhập lại không đúng");
            alert.showAndWait();
            return;
        }

        // Kiểm tra nếu là user thì MSSV phải được điền đầy đủ
        if ("user".equals(role) && (mssv == null || mssv.isEmpty())) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lỗi đăng kí");
            alert.setHeaderText("MSSV là bắt buộc đối với người dùng");
            alert.showAndWait();
            return;
        }
        User newUser;

        if("user".equals(role)) {newUser = new User(Integer.parseInt(mssv), username, password, role, fullName, email, number);}
        else {newUser = new User(0,username,password,role,fullName,email,number);}
        try {

            if (newUser.isUsernameTaken(username)) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Lỗi");
                alert.setHeaderText("Tên tài khoản đã tồn tại");
                alert.setContentText("Vui lòng chọn tên tài khoản khác");
                alert.showAndWait();
            } else {
                newUser.register();
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/login.fxml"));
                    Parent root = loader.load();
                    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    stage.setScene(new Scene(root));
                    stage.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lỗi");
            alert.setHeaderText("Đăng ký thất bại");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    //xử lí đăng nhập tài khoản

    public void handleLogin(ActionEvent event) {
        String username = usernameFieldlogin.getText();
        String password = passwordHiddenlogin.getText();

        try {
            // Gọi phương thức login từ lớp User
            String role = User.getRoleUser(username, password); // Gọi phương thức login và nhận lại vai trò
            String rolecheck = myChoiceBox.getValue();
            // Kiểm tra vai trò
            if(rolecheck.equals(role)) {
                if ("admin".equals(role)) {
                    currentUser.setUsername(username);
                    currentUser.setRole(role);
                    // Chuyển đến trang Admin
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/hello-view.fxml"));
                    Parent root = loader.load();
                    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    stage.setScene(new Scene(root, 1300, 800));
                    stage.show();
                    stage.centerOnScreen();
                } else if ("user".equals(role)) {
                    int mssv = User.getStudentIdByusername(username);
                    User newuser = User.loadStudentDetailsByID(String.valueOf(mssv));

                    currentUser.setUsername(username);
                    currentUser.setRole(role);
                    currentUser.setId(mssv);
                    currentUser.setEmail(newuser.getEmail());
                    currentUser.setPassword(newuser.getPassword());
                    currentUser.setFullName(newuser.getFullName());
                    // Chuyển đến trang Member
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/userView.fxml"));
                    Parent root = loader.load();
                    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    stage.setScene(new Scene(root, 1300, 800));
                    stage.show();
                } else {
                    // Thông báo lỗi nếu không phải Admin hoặc Member
                    showAlert("Error", "Đăng nhập thất bại: " + role);
                }
            }
            else {
                showAlert("Error", "Đăng nhập thất bại: " + role);
            }
        } catch (SQLException e) {
            showAlert("Error", "Đăng nhập thất bại: " + e.getMessage());
        } catch (IOException e) {
            showAlert("Error", "Lỗi" + e.getMessage());
        }
    }

    private void createBlinkingStar() {
        Random random = new Random();

        // Tạo một bông tuyết (Circle) với kích thước ngẫu nhiên
        Circle star = new Circle(random.nextInt(5) + 1, Color.WHITE); // Kích thước bông tuyết ngẫu nhiên từ 5-10px

        // Đặt vị trí ngẫu nhiên cho bông tuyết ở trên đầu màn hình
        double startX = random.nextDouble() * starPane.getWidth(); // Vị trí ngẫu nhiên trên trục X
        double startY = random.nextDouble() * 400; // Vị trí ngẫu nhiên trên trục Y (ở ngoài màn hình trên cùng)

        star.setLayoutX(startX);
        star.setLayoutY(startY);

        // Thêm bông tuyết vào AnchorPane
        starPane.getChildren().add(star);

        // Tạo Timeline để di chuyển bông tuyết từ trên xuống dưới
        Timeline fallTimeline = new Timeline(
                new KeyFrame(Duration.ZERO, e -> {
                    // Di chuyển bông tuyết theo trục Y, tạo hiệu ứng rơi
                    star.setLayoutY(star.getLayoutY() + 1); // Di chuyển xuống mỗi frame

                    // Kiểm tra nếu bông tuyết rơi khỏi màn hình thì xóa nó
                    if (star.getLayoutY() > starPane.getHeight()) {
                        starPane.getChildren().remove(star); // Xóa bông tuyết khi rơi ra ngoài
                    }
                }),
                new KeyFrame(Duration.millis(10)) // Cập nhật mỗi 10ms
        );

        fallTimeline.setCycleCount(Timeline.INDEFINITE); // Lặp vô hạn
        fallTimeline.play();
    }


    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    public void signUp(ActionEvent event) {
        // Logic cho việc chuyển sang trang đăng ký
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/signUp.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void loginReturn(ActionEvent event) {
        // Logic cho việc chuyển sang trang đăng nhập
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Object getclass() {
        return getClass();
    }
}
