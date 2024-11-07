package controllers;

import Document.Book;
import Document.BookDAO;
import Document.TransactionDAO;
import User.User;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;

import java.sql.SQLException;

public class issueBookController {

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
                // Lấy thông tin sách từ cơ sở dữ liệu dựa trên ISBN
                Book book = BookDAO.getBookByISBN(isbn);

                if (book != null) {
                    // Hiển thị thông tin sách vào các trường
                    titleTextField.setText(book.getTitle());
                    authorTextField.setText(book.getAuthor());
                    sectionTextField.setText(book.getSection());
                    System.out.println(book.getRemainingBook());
                    if (book.getRemainingBook() > 0) AvailableTextField.setText("Available");
                    else AvailableTextField.setText("Not Available");
                } else {
                    System.out.println("Không tìm thấy sách với ISBN này.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Lỗi khi tìm kiếm sách: " + e.getMessage());
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
                   System.out.println("Không tìm thấy sinh viên với ID này.");
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
                            System.out.println("Sách đã được mượn thành công.");
                        } else {
                            System.out.println("Không thể mượn sách, sách có thể đã được mượn trước đó.");
                        }
                    } else {
                        System.out.println("Không tìm thấy sinh viên với ID này.");
                    }
                } else {
                    System.out.println("Sách không có sẵn để mượn.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("Lỗi khi thực hiện mượn sách: " + e.getMessage());
            }
        } else {
            System.out.println("Thông tin sách hoặc sinh viên chưa đầy đủ.");
        }
    }

}
