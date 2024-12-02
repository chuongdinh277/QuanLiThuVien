package Controllers;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.event.ActionEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Random;
import java.util.ResourceBundle;
import User.User;
import User.currentUser;
import javafx.util.Duration;
/**
 * Controller for handling login and registration functionality.
 */
public class LoginController implements Initializable {

    /** TextField for username input during login. */
    @FXML
    private TextField usernameFieldlogin;

    /** PasswordField for hidden password input during login. */
    @FXML
    private PasswordField passwordHiddenlogin;

    /** TextField for username input during registration. */
    @FXML
    private TextField usernameField;

    /** PasswordField for hidden password input during registration. */
    @FXML
    private PasswordField passwordHidden;

    /** PasswordField for confirming the password during registration. */
    @FXML
    private PasswordField PasswordHiddenRL;

    /** TextField for full name input during registration. */
    @FXML
    private TextField fullNameField;

    /** TextField for phone number input during registration. */
    @FXML
    private TextField numberField;

    /** TextField for email input during registration. */
    @FXML
    private TextField emailField;

    /** TextField for student ID input during registration for 'user' role. */
    @FXML
    private TextField MSSVTextfield;

    /** ChoiceBox for selecting the user role (admin/user). */
    @FXML
    private ChoiceBox<String> myChoiceBox;

    /** AnchorPane for rendering animated stars. */
    @FXML
    private AnchorPane starPane;

    /** TextField for showing plain text password (visible password toggle). */
    @FXML
    private TextField passwordTextField;

    /**
     * Initializes the controller.
     * Adds role options to the ChoiceBox and starts the star animation.
     *
     * @param location  the location used to resolve relative paths for the root object, or null if not known.
     * @param resources the resources used to localize the root object, or null if not available.
     */
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
    }

    /**
     * Toggles between showing and hiding the password in plain text.
     *
     * @param event the MouseEvent triggered by clicking the toggle icon.
     */
    @FXML
    public void handleShowPassword(MouseEvent event) {
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

    /**
     * Handles the registration process.
     * Validates input fields, checks for username availability, and registers the user.
     *
     * @param event the ActionEvent triggered by clicking the Register button.
     */
    @FXML
    public void handleRegister(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordHidden.getText();
        String passwordConfirm = PasswordHiddenRL.getText();
        String number = numberField.getText();
        String email = emailField.getText();
        String fullName = fullNameField.getText();
        String role = myChoiceBox.getValue();
        String mssv = MSSVTextfield.getText();
        if(username.isEmpty() || password.isEmpty() || passwordConfirm.isEmpty() || role.isEmpty() || number.isEmpty()
                || email.isEmpty() || fullName.isEmpty()) {
            showErrorDialog("Vui lòng điền đầy đủ thông tin");
            return;
        }

        if(!passwordConfirm.equals(password)) {
            showErrorDialog("Mật khẩu nhập lại không đúng");
            return;
        }

        if ("user".equals(role) && (mssv == null || mssv.isEmpty())) {
            showErrorDialog("MSSV là bắt buộc đối với người dùng");
            return;
        }
        User newUser;

        if("user".equals(role)) {newUser = new User(Integer.parseInt(mssv), username, password, role, fullName, email, number);}
        else {newUser = new User(0,username,password,role,fullName,email,number);}
        try {

            if (newUser.isUsernameTaken(username)) {
                showErrorDialog("Tên tài khoản đã tồn tại. Vui lòng chọn tên tài khoản khác");
            } else {
                newUser.register();
                showSuccessDialog("Đăng ký thành công! Chuyển đến trang đăng nhập.");
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/login.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();
            }
        } catch (Exception e) {
            showErrorDialog("Đăng ký thất bại: " + e.getMessage());
        }
    }

    /**
     * Handles the login process.
     * Verifies the credentials and navigates to the appropriate user interface based on the role.
     *
     * @param event the ActionEvent triggered by clicking the Login button.
     */
    public void handleLogin(ActionEvent event) {
        String username = usernameFieldlogin.getText();
        String password = passwordHiddenlogin.getText();

        try {
            String role = User.getRoleUser(username, password);
            String rolecheck = myChoiceBox.getValue();
            if(rolecheck.equals(role)) {
                if ("admin".equals(role)) {
                    currentUser.setUsername(username);
                    currentUser.setRole(role);
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/menuAdmin.fxml"));
                    Parent root = loader.load();
                    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    stage.setScene(new Scene(root, 1300, 750));
                    stage.show();
                    stage.centerOnScreen();
                }
                else if ("user".equals(role)) {
                    int mssv = User.getStudentIdByusername(username);
                    User newuser = User.loadStudentDetailsByID(String.valueOf(mssv));
                    currentUser.setUsername(username);
                    currentUser.setRole(role);
                    currentUser.setId(mssv);
                    currentUser.setEmail(newuser.getEmail());
                    currentUser.setPassword(newuser.getPassword());
                    currentUser.setFullName(newuser.getFullName());
                    currentUser.setNumber(newuser.getNumber());
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/userView.fxml"));
                    Parent root = loader.load();
                    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    stage.setScene(new Scene(root, 1300, 800));
                    stage.show();
                    stage.centerOnScreen();
                }
                else {
                    showErrorDialog("Đăng nhập thất bại: " + role);
                }
            }
            else {
                showErrorDialog("Đăng nhập thất bại: " + role);
            }
        } catch (SQLException e) {
            showErrorDialog("Đăng nhập thất bại: " + e.getMessage());
        } catch (IOException e) {
            showErrorDialog("Lỗi: " + e.getMessage());
        }
    }

    /**
     * Creates and animates a blinking star on the interface.
     */
    private void createBlinkingStar() {
        Random random = new Random();

        Circle star = new Circle(random.nextInt(5) + 1, Color.WHITE);

        double startX = random.nextDouble() * starPane.getWidth();
        double startY = random.nextDouble() * 400;

        star.setLayoutX(startX);
        star.setLayoutY(startY);

        starPane.getChildren().add(star);

        Timeline fallTimeline = new Timeline(
                new KeyFrame(Duration.ZERO, e -> {
                    star.setLayoutY(star.getLayoutY() + 1);

                    if (star.getLayoutY() > starPane.getHeight()) {
                        starPane.getChildren().remove(star);
                    }
                }),
                new KeyFrame(Duration.millis(10))
        );

        fallTimeline.setCycleCount(Timeline.INDEFINITE);
        fallTimeline.play();
    }
    /**
     * Navigates the user to the sign-up page.
     *
     * This method loads the `signUp.fxml` file and updates the current stage
     * to display the sign-up interface.
     *
     * @param event the ActionEvent triggered by clicking the sign-up button.
     */
    @FXML
    public void signUp(ActionEvent event) {
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

    /**
     * Navigates the user back to the login page.
     *
     * This method loads the `login.fxml` file and updates the current stage
     * to display the login interface.
     *
     * @param event the ActionEvent triggered by clicking the return button.
     */
    @FXML
    public void loginReturn(ActionEvent event) {
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

    /**
     * Displays an error dialog with the provided message.
     *
     * @param message the error message to display.
     */
    private void showErrorDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    /**
     * Displays a success dialog with the provided message.
     *
     * @param message the success message to display.
     */
    private void showSuccessDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
