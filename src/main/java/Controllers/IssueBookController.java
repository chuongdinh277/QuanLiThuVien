package Controllers;

import Document.Book;
import Document.BookDAO;
import Document.TransactionDAO;
import User.User;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;

import java.sql.SQLException;

/**
 * Lớp IssueBookController điều khiển giao diện và logic cho chức năng mượn sách.
 * Quản lý thông tin sách, sinh viên và xử lý mượn sách.
 */
public class IssueBookController {

    @FXML
    private TextField borrowDay; // Trường nhập số ngày mượn sách
    @FXML
    private TextField authorTextField; // Hiển thị tên tác giả của sách
    @FXML
    private TextField quantityTextField; // Hiển thị số lượng sách còn lại
    @FXML
    private BorderPane borderPane_book; // Bố cục BorderPane của giao diện
    @FXML
    private TextField categoryTextField; // Hiển thị thể loại của sách
    @FXML
    private TextField titleTextField; // Hiển thị tiêu đề của sách
    @FXML
    private TextField ISBNsearchBook; // Trường nhập ISBN để tìm sách
    @FXML
    private TextField IDsearchStudent; // Trường nhập ID sinh viên để tìm sinh viên
    @FXML
    private TextField nameStudentTextfield; // Hiển thị tên sinh viên
    @FXML
    private TextField AvailableTextField; // Hiển thị trạng thái sách (Có sẵn/Không có sẵn)
    @FXML
    private TextField studentNumberTextfield; // Hiển thị số điện thoại của sinh viên
    @FXML
    private TextField studentEmailTextfield; // Hiển thị email của sinh viên

    /**
     * Phương thức khởi tạo khi giao diện được tải.
     * Thiết lập hành động cho các trường tìm kiếm ISBN và ID sinh viên.
     */
    @FXML
    public void initialize() {
        ISBNsearchBook.setOnAction(event -> loadBookDetails());
        IDsearchStudent.setOnAction(event -> loadStudentDetails());
    }

    /**
     * Phương thức tải thông tin sách dựa trên ISBN nhập vào.
     */
    private void loadBookDetails() {
        String isbn = ISBNsearchBook.getText();

        if (isbn != null && !isbn.isEmpty()) {
            try {
                Book book = BookDAO.getBookByISBN(isbn);
                if (book != null) {
                    titleTextField.setText(book.getTitle());
                    authorTextField.setText(book.getAuthor());
                    categoryTextField.setText(book.getCategory());
                    if (book.getRemainingBook() > 0) {
                        AvailableTextField.setText("Available");
                    } else {
                        AvailableTextField.setText("Not Available");
                    }
                } else {
                    showAlbertDialog("Không tìm thấy sách với ISBN này.");
                }
            } catch (Exception e) {
                showErrorDialog("Error", "Lỗi khi tìm kiếm sách: " + e.getMessage());
            }
        }
    }

    /**
     * Phương thức tải thông tin sinh viên dựa trên ID nhập vào.
     */
    private void loadStudentDetails() {
        String studentID = IDsearchStudent.getText();

        if (studentID != null && !studentID.isEmpty()) {
            try {
                User student = User.loadStudentDetailsByID(studentID);
                if (student != null) {
                    nameStudentTextfield.setText(student.getFullName());
                    studentNumberTextfield.setText(student.getNumber());
                    studentEmailTextfield.setText(student.getEmail());
                } else {
                    showAlbertDialog("Không tìm thấy sinh viên với ID này.");
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Phương thức xử lý khi người dùng nhấn nút mượn sách.
     * Kiểm tra thông tin và thực hiện mượn sách nếu hợp lệ.
     */
    @FXML
    private void IssueBookButton() {
        String title = titleTextField.getText();
        String author = authorTextField.getText();
        String isbn = ISBNsearchBook.getText(); // Đảm bảo rằng ISBN là duy nhất để xác định sách
        String studentID = IDsearchStudent.getText();
        int numberofdays = Integer.parseInt(borrowDay.getText());

        if (title != null && !title.isEmpty() && author != null && !author.isEmpty() && studentID != null && !studentID.isEmpty()) {
            try {
                Book book = BookDAO.getBookByISBN(isbn);
                if (book != null && book.getRemainingBook() > 0) {
                    User user = User.loadStudentDetailsByID(studentID);
                    if (user != null) {
                        boolean success = TransactionDAO.borrowBook(user, book, 1, numberofdays);
                        if (success) {
                            showAlbertDialog("Sách đã được mượn thành công.");
                        } else {
                            showAlbertDialog("Không thể mượn sách, sách có thể đã được mượn trước đó.");
                        }
                    } else {
                        showAlbertDialog("Không tìm thấy sinh viên với ID này.");
                    }
                } else {
                    showAlbertDialog("Sách không có sẵn để mượn.");
                }
            } catch (SQLException e) {
                showErrorDialog("Error", "Lỗi khi thực hiện mượn sách: " + e.getMessage());
            }
        } else {
            showAlbertDialog("Thông tin sách hoặc sinh viên chưa đầy đủ.");
        }
    }

    /**
     * Hiển thị hộp thoại thông báo cho người dùng.
     *
     * @param message Nội dung thông báo.
     */
    private void showAlbertDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Hiển thị hộp thoại lỗi cho người dùng.
     *
     * @param title Tiêu đề hộp thoại lỗi.
     * @param message Nội dung lỗi.
     */
    private void showErrorDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
