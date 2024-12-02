/**
 * Lớp ProfileController chịu trách nhiệm quản lý giao diện hồ sơ người dùng.
 * Cung cấp chức năng chỉnh sửa thông tin cá nhân như tên đầy đủ, số điện thoại và mật khẩu.
 */
package Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import User.currentUser;
import User.User;
import javafx.scene.layout.AnchorPane;

import java.sql.SQLException;

public class ProfileController {

    @FXML
    private ImageView editPassword; // Icon chỉnh sửa mật khẩu.

    @FXML
    private ImageView editPhoneNumber; // Icon chỉnh sửa số điện thoại.

    @FXML
    private ImageView editfullName; // Icon chỉnh sửa tên đầy đủ.

    @FXML
    private MenuItem editProfileButton; // MenuItem để mở giao diện chỉnh sửa hồ sơ.

    @FXML
    private MenuButton logoutAndEditProfile; // MenuButton bao gồm các lựa chọn chỉnh sửa hồ sơ và đăng xuất.

    @FXML
    private MenuItem logoutButton; // MenuItem để đăng xuất.

    @FXML
    private TextField passwordTextField; // TextField để nhập mật khẩu mới.

    @FXML
    private TextField phoneTextField; // TextField để nhập số điện thoại mới.

    @FXML
    private TextField repasswordTextField; // TextField để nhập lại mật khẩu (không sử dụng trong mã hiện tại).

    @FXML
    private Label role; // Nhãn hiển thị vai trò người dùng.

    @FXML
    private Label fullNameLabelAdmin; // Nhãn hiển thị tên đầy đủ của người dùng ở giao diện admin.

    @FXML
    private TextField fullnameTextfield; // TextField để nhập tên đầy đủ mới.

    @FXML
    private Label fullnameLabel; // Nhãn hiển thị tên đầy đủ của người dùng.

    @FXML
    private Label phoneLabel; // Nhãn hiển thị số điện thoại của người dùng.

    @FXML
    private Label emailLabel; // Nhãn hiển thị email của người dùng.

    @FXML
    private Label usernameLabel; // Nhãn hiển thị tên đăng nhập của người dùng.

    @FXML
    private Label passwordLabel; // Nhãn hiển thị mật khẩu (ẩn).

    @FXML
    private Label roleLabel; // Nhãn hiển thị vai trò người dùng.

    @FXML
    private Button saveButton; // Nút lưu thay đổi.

    @FXML
    private Label emailLabel1; // Nhãn hiển thị email (bản sao).

    @FXML
    private Label phoneLabel1; // Nhãn hiển thị số điện thoại (bản sao).

    @FXML
    private AnchorPane anchorPane; // Thành phần chính chứa giao diện hồ sơ.

    private boolean isEditingFullName = false; // Biến trạng thái chỉnh sửa tên đầy đủ.
    private boolean isEditingPhone = false; // Biến trạng thái chỉnh sửa số điện thoại.
    private boolean isEditingPassword = false; // Biến trạng thái chỉnh sửa mật khẩu.

    User newUser;

    /**
     * Phương thức khởi tạo, được gọi khi giao diện hồ sơ được tải.
     * Nạp dữ liệu người dùng và hiển thị thông tin trên các nhãn tương ứng.
     */
    public void initialize() {
        try {
            newUser = User.loadUserDetailsByUsername(currentUser.getUsername());
            fullnameLabel.setText(newUser.getFullName());
            fullNameLabelAdmin.setText(newUser.getFullName());
            phoneLabel.setText(newUser.getNumber());
            phoneLabel1.setText(newUser.getNumber());
            emailLabel.setText(newUser.getEmail());
            emailLabel1.setText(newUser.getEmail());
            usernameLabel.setText(newUser.getUserName());
            passwordLabel.setText(newUser.getPassword());
            roleLabel.setText(newUser.getRole());

            fullnameTextfield.setVisible(false);
            phoneTextField.setVisible(false);
            passwordTextField.setVisible(false);
        } catch (SQLException e) {
            // Xử lý lỗi SQL khi tải thông tin người dùng.
        }
    }
    /**
     * Handles the click event on a star in the rating section. Based on the clicked star, it sets the selected rating.
     * The method also updates the appearance of the stars according to the selected rating.
     *
     * @param event The mouse click event on the star.
     */
    @FXML
    private void handleStarclick(MouseEvent event) {
    }
    /**
     * Phương thức xử lý sự kiện click vào biểu tượng chỉnh sửa tên đầy đủ.
     * Hiển thị TextField để người dùng nhập tên mới.
     */
    @FXML
    private void onEditFullNameClick() {
        isEditingFullName = true;
        fullnameLabel.setVisible(false);
        fullnameTextfield.setVisible(true);
    }

    /**
     * Phương thức xử lý sự kiện click vào biểu tượng chỉnh sửa số điện thoại.
     * Hiển thị TextField để người dùng nhập số điện thoại mới.
     */
    @FXML
    private void onEditPhoneNumberClick() {
        isEditingPhone = true;
        phoneLabel.setVisible(false);
        phoneTextField.setVisible(true);
    }

    /**
     * Phương thức xử lý sự kiện click vào biểu tượng chỉnh sửa mật khẩu.
     * Hiển thị TextField để người dùng nhập mật khẩu mới.
     */
    @FXML
    private void onEditPasswordClick() {
        isEditingPassword = true;
        passwordLabel.setVisible(false);
        passwordTextField.setVisible(true);
        passwordTextField.setText(""); // Để trống để nhập mật khẩu mới.
    }

    /**
     * Phương thức xử lý sự kiện click vào nút lưu.
     * Lưu thay đổi thông tin người dùng và cập nhật cơ sở dữ liệu.
     */
    @FXML
    private void onSaveButtonClick() {
        if (isEditingFullName) {
            String newFullName = fullnameTextfield.getText();
            newUser.setFullName(newFullName);
            fullnameLabel.setText(newFullName);
            fullnameLabel.setVisible(true);
            fullnameTextfield.setVisible(false);
            isEditingFullName = false;
        }

        if (isEditingPhone) {
            String newPhoneNumber = phoneTextField.getText();
            newUser.setNumber(newPhoneNumber);
            phoneLabel.setText(newPhoneNumber);
            phoneLabel.setVisible(true);
            phoneTextField.setVisible(false);
            isEditingPhone = false;
        }

        if (isEditingPassword) {
            String newPassword = passwordTextField.getText();
            if (!newPassword.isEmpty()) { // Chỉ cập nhật nếu nhập mật khẩu mới.
                newUser.setPassword(newPassword);
            }
            passwordLabel.setText("********"); // Hiển thị mật khẩu ẩn.
            passwordLabel.setVisible(true);
            passwordTextField.setVisible(false);
            isEditingPassword = false;
        }

        // Cập nhật thông tin hiện tại của người dùng.
        currentUser.setUsername(newUser.getUserName());
        currentUser.setFullName(newUser.getFullName());
        currentUser.setPassword(newUser.getPassword());
        currentUser.setEmail(newUser.getEmail());
        currentUser.setNumber(newUser.getNumber());

        // Ghi thông tin vào cơ sở dữ liệu.
        User.updateDatabase(newUser.getFullName(), newUser.getNumber(), newUser.getPassword(), newUser.getUserName());
    }
}
