package Controllers;
import User.*;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static Database.DatabaseConnection.getConnection;

public class UserProfileController {

    @FXML
    private AnchorPane informationPane; // Giao diện chứa các thông tin người dùng
    @FXML
    private Label fullNameLabel; // Nhãn hiển thị tên đầy đủ
    @FXML
    private Label fullNameLabel1; // Nhãn hiển thị tên đầy đủ (một phiên bản khác)
    @FXML
    private Label idLabel; // Nhãn hiển thị ID người dùng
    @FXML
    private Label idLabel1; // Nhãn hiển thị ID người dùng (một phiên bản khác)
    @FXML
    private TextField emailTextField; // Trường văn bản để nhập email
    @FXML
    private Label usernameLabel; // Nhãn hiển thị tên người dùng
    @FXML
    private TextField passwordTextField; // Trường văn bản để nhập mật khẩu
    @FXML
    private Label numberLabel; // Nhãn hiển thị số điện thoại

    @FXML
    private Button editButton; // Nút chỉnh sửa thông tin người dùng
    @FXML
    private Button updateButton; // Nút cập nhật thông tin người dùng
    @FXML
    private Button cancelButton; // Nút hủy bỏ thay đổi

    /**
     * Phương thức khởi tạo, thiết lập các thông tin ban đầu cho giao diện người dùng.
     */
    @FXML
    private void initialize() {
        //System.out.println("Full name : " + currentUser.getFullName());
        emailTextField.setText(currentUser.getEmail()); // Thiết lập email người dùng
        usernameLabel.setText(currentUser.getUsername()); // Thiết lập tên người dùng
        passwordTextField.setText(currentUser.getPassword()); // Thiết lập mật khẩu người dùng
        fullNameLabel.setText(currentUser.getFullName()); // Thiết lập tên đầy đủ
        fullNameLabel1.setText(currentUser.getFullName()); // Thiết lập tên đầy đủ (phiên bản khác)
        idLabel.setText(String.valueOf(currentUser.getId())); // Thiết lập ID người dùng
        idLabel1.setText(String.valueOf(currentUser.getId())); // Thiết lập ID người dùng (phiên bản khác)
        numberLabel.setText(currentUser.getNumber()); // Thiết lập số điện thoại người dùng
       // System.out.println("number = " + currentUser.getNumber());

        // Cài đặt trường văn bản không thể chỉnh sửa và ẩn các nút cập nhật, hủy
        emailTextField.setEditable(false);
        passwordTextField.setEditable(false);
        updateButton.setVisible(false);
        cancelButton.setVisible(false);

        // Thêm hiệu ứng hover cho các nút
        addHoverEffect(editButton);
        addHoverEffect(cancelButton);
        addHoverEffect(updateButton);
    }

    /**
     * Phương thức xử lý khi nhấn nút chỉnh sửa.
     * Cho phép người dùng chỉnh sửa thông tin email và mật khẩu.
     */
    @FXML
    private void handleEditButton() {
        //System.out.println("clicked edit button");
        updateButton.setVisible(true);
        cancelButton.setVisible(true);
        // Kích hoạt trường văn bản để chỉnh sửa
        emailTextField.setEditable(true);
        passwordTextField.setEditable(true);
    }

    /**
     * Phương thức xử lý khi nhấn nút cập nhật thông tin.
     * Cập nhật email và mật khẩu người dùng vào cơ sở dữ liệu.
     */
    @FXML
    private void handleUpdateButton() {
        //System.out.println("clicked update button");
        String newEmail = emailTextField.getText();
        String newPassword = passwordTextField.getText();

        // Kiểm tra dữ liệu đầu vào
        if (newEmail.isEmpty() || newPassword.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Dữ liệu không hợp lệ");
            alert.setHeaderText(null);
            alert.setContentText("Email và mật khẩu không được để trống.");
            alert.showAndWait();
            return;
        }

        // Cập nhật dữ liệu người dùng
        currentUser.setEmail(newEmail);
        currentUser.setPassword(newPassword);

        // Vô hiệu hóa các trường chỉnh sửa
        emailTextField.setEditable(false);
        passwordTextField.setEditable(false);
        updateButton.setVisible(false);
        cancelButton.setVisible(false);

        // Cập nhật dữ liệu trong cơ sở dữ liệu
        String sql = "UPDATE users SET email = ?, password = ? WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, currentUser.getEmail());
            pstmt.setString(2, currentUser.getPassword()); // Không mã hóa mật khẩu
            pstmt.setInt(3, currentUser.getId());

            pstmt.executeUpdate();
            // Hiển thị thông báo thành công
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Cập nhật thành công");
            alert.setHeaderText(null);
            alert.setContentText("Thông tin người dùng đã được cập nhật thành công.");
            alert.showAndWait();

        } catch (SQLException e) {

            // Hiển thị thông báo lỗi
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lỗi cập nhật");
            alert.setHeaderText("Không thể cập nhật thông tin");
            alert.setContentText("Đã xảy ra lỗi khi cập nhật thông tin người dùng: " + e.getMessage());
            alert.showAndWait();
        }
    }

    /**
     * Phương thức xử lý khi nhấn nút hủy.
     * Đặt lại các trường văn bản và ẩn các nút cập nhật, hủy.
     */
    @FXML
    private void handleCancelButton() {
        emailTextField.setText(currentUser.getEmail());
        passwordTextField.setText(currentUser.getPassword());
        // Vô hiệu hóa các trường chỉnh sửa
        emailTextField.setEditable(false);
        passwordTextField.setEditable(false);
        updateButton.setVisible(false);
        cancelButton.setVisible(false);
    }

    /**
     * Thêm hiệu ứng hover cho các nút.
     * Khi di chuột vào nút, kích thước nút sẽ thay đổi.
     *
     * @param node Nút cần thêm hiệu ứng hover.
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
}
