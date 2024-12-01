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

public class IssueBookController {

    @FXML
    private TextField borrowDay;
    @FXML
    private TextField authorTextField;
    @FXML
    private TextField quantityTextField;
    @FXML
    private BorderPane borderPane_book;

    @FXML
    private TextField sectionTextField;

    @FXML
    private TextField titleTextField;
    @FXML
    private TextField ISBNsearchBook;
    @FXML
    private TextField IDsearchStudent;
    @FXML
    private TextField nameStudentTextfield;
    @FXML
    private TextField AvailableTextField;
    @FXML
    private TextField studentNumberTextfield;
    @FXML
    private TextField studentEmailTextfield;
    @FXML
    public void initialize() {
        ISBNsearchBook.setOnAction(event -> loadBookDetails());
        IDsearchStudent.setOnAction(event -> loadStudentDetails());
    }
    private void loadBookDetails() {
        String isbn = ISBNsearchBook.getText();

        if (isbn != null && !isbn.isEmpty()) {
            try {
                Book book = BookDAO.getBookByISBN(isbn);
                if (book != null) {
                    titleTextField.setText(book.getTitle());
                    authorTextField.setText(book.getAuthor());
                    System.out.println(book.getRemainingBook());
                    if (book.getRemainingBook() > 0) AvailableTextField.setText("Available");
                    else AvailableTextField.setText("Not Available");
                } else {
                    showAlbertDialog("Không tìm thấy sách với ISBN này.");
                }
            } catch (Exception e) {
                showErrorDialog("Error", "Lỗi khi tìm kiếm sách: " + e.getMessage());
            }
        }
    }
    private void loadStudentDetails() {
        String studentID = IDsearchStudent.getText();

        if (studentID != null && !studentID.isEmpty()) {
           try {
               User student = User.loadStudentDetailsByID(studentID);
               if (student != null) {
                   // Hiển thị thông tin sinh viên vào các trường
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
    @FXML
    private void IssueBookButton() {
        String title = titleTextField.getText();
        String author = authorTextField.getText();
        String isbn = ISBNsearchBook.getText(); // Đảm bảo rằng ISBN là duy nhất để xác định sách
        String studentID = IDsearchStudent.getText();
        int quantity = Integer.parseInt(quantityTextField.getText());
        int numberofdays = Integer.parseInt(borrowDay.getText());
        if (title != null && !title.isEmpty() && author != null && !author.isEmpty() && studentID != null && !studentID.isEmpty()) {
            try {
                Book book = BookDAO.getBookByISBN(isbn);
                if (book != null && book.getRemainingBook() > 0) {
                    User user = User.loadStudentDetailsByID(studentID);
                    if (user != null) {
                        // Gọi phương thức borrowBook để mượn sách
                        boolean success = TransactionDAO.borrowBook(user, book, quantity,numberofdays);
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
                showErrorDialog("Error","Lỗi khi thực hiện mượn sách: " + e.getMessage());
            }
        } else {
            showAlbertDialog("Thông tin sách hoặc sinh viên chưa đầy đủ.");
        }
    }
    private void showAlbertDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    private void showErrorDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
