package Controllers;

import Document.Book;
import User.Member;
import User.User;
import User.currentUser;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ViewBookInLibrary extends ViewBook {

    /**
     * Tải danh sách sách từ cơ sở dữ liệu và hiển thị chúng theo trang, mỗi trang chứa tối đa 7 sách.
     * Phương thức này sử dụng đa luồng để tải sách và tránh chặn giao diện người dùng.
     *
     * @throws SQLException Nếu có lỗi trong quá trình truy vấn cơ sở dữ liệu.
     */
    @Override
    public void loadBooks() {
        if (isLoading) return; // Kiểm tra xem sách đã được tải hay chưa.

        isLoading = true; // Đánh dấu bắt đầu quá trình tải sách.
        executorService.submit(() -> { // Sử dụng ExecutorService để chạy phương thức trong một luồng riêng biệt.
            try {
                int studentID = User.getStudentIdByusername(currentUser.getUsername()); // Lấy ID sinh viên từ tên người dùng hiện tại.
                Member member = new Member(studentID, currentUser.getUsername(), currentUser.getRole()); // Tạo đối tượng Member.
                List<Book> bookList = member.viewBooksPaginated(booksLoaded, PAGE_SIZE); // Lấy danh sách sách đã tải theo phân trang.

                if (bookList != null && !bookList.isEmpty()) { // Kiểm tra nếu danh sách sách không rỗng.
                    int row = booksLoaded / 7; // Tính toán hàng.
                    int col = booksLoaded % 7; // Tính toán cột.

                    for (Book book : bookList) { // Duyệt qua từng sách trong danh sách.
                        AnchorPane card = createCard(book); // Tạo thẻ sách.
                        if (card != null) { // Nếu thẻ sách được tạo thành công.
                            int finalRow = row;
                            int finalCol = col;
                            javafx.application.Platform.runLater(() -> bookGrid.add(card, finalCol, finalRow)); // Thêm thẻ sách vào lưới trong giao diện người dùng.
                        }

                        col++; // Tăng cột.
                        if (col >= 7) { // Nếu cột đạt đến giới hạn, chuyển sang hàng tiếp theo.
                            col = 0;
                            row++;
                        }
                    }
                    booksLoaded += bookList.size(); // Cập nhật số lượng sách đã tải.
                }
            } catch (SQLException e) { // Xử lý lỗi SQL.
                e.printStackTrace();
            } finally {
                isLoading = false; // Đánh dấu kết thúc quá trình tải sách.
            }
        });
    }
}
