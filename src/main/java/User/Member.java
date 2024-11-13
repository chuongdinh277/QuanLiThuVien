    package User;

    import BookRating.BookRating;
    import BookRating.BookRatingDAO;
    import Database.DatabaseConnection;
    import Document.Book;
    import Document.BookDAO;
    import Document.Transaction;
    import Document.TransactionDAO;
    import Recomend.Recommend;
    import Review.ReviewDAO;
    import Review.Reviews;

    import java.sql.Connection;
    import java.sql.PreparedStatement;
    import java.sql.ResultSet;
    import java.sql.SQLException;
    import java.util.ArrayList;
    import java.util.HashMap;
    import java.util.LinkedHashMap;
    import java.util.List;

    public class Member extends User{
        private final static String MEMBER = "User";
        public Member(int id, String userName, String password) {
            super(id, userName, password,MEMBER,"","","");
        }
        private boolean registerUser(String userName, String password, String role, String fullName, String email, String studentID) throws SQLException {
            String sql = "INSERT INTO users (username, password, role, fullName, email, number) VALUES (?, ?, ?, ?, ?, ?)";
            try (Connection connection = DatabaseConnection.getConnection();
                 PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
                statement.setString(1, userName);
                statement.setString(2, password);
                statement.setString(3, role);
                statement.setString(4, fullName);  // thêm tham số fullName
                statement.setString(5, email);      // thêm tham số email
                statement.setString(6, studentID);  // thêm tham số studentID
                int rowsInserted = statement.executeUpdate();
                return rowsInserted > 0;
            } catch (SQLException e) {
                throw e;
            }
        }


        @Override
        public void register() throws SQLException {
            try {
                boolean check = registerUser(this.getUserName(), this.getPassword(), this.getRole(), this.getFullName(), this.getEmail(), this.getNumber());
                if (check) {
                    this.showAlberDialog(this.getRole() + " " + this.getUserName() + " " + "Đăng ký thành công");
                } else {
                    this.showAlberDialog("Đăng ký thất bại");
                }
            } catch (Exception e) {
                this.showErrorDialog("Error", e.getMessage());
            }
        }



        public List<Book> viewBooksPaginated(int offset, int limit) throws SQLException {
            List<Book> books = new ArrayList<>();

            // Kết nối đến cơ sở dữ liệu và thực hiện câu lệnh SQL
            String query = "SELECT * FROM books LIMIT ? OFFSET ?";
            try (Connection connection = DatabaseConnection.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {

                statement.setInt(1, limit); // Số lượng sách trên mỗi trang
                statement.setInt(2, offset); // Số lượng sách đã được tải (tính từ đâu)

                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        Book book = new Book(resultSet.getString("title"),
                                resultSet.getString("author"),
                                resultSet.getString("category"),
                                resultSet.getInt("quantity"),
                                resultSet.getInt("remaining_book"),
                                resultSet.getString("description"),
                                resultSet.getString("publisher"),
                                resultSet.getString("section"),
                                resultSet.getString("imagePath"),
                                resultSet.getString("ISBN"));
                        // Cập nhật các thuộc tính khác nếu cần

                        books.add(book);
                    }
                }
            }
            return books;
        }


        public List<Book> viewAllBooks() throws SQLException {
            try {
                List<Book> books = BookDAO.getAvailableBooks();
                return books;
            } catch (Exception e) {
                this.showErrorDialog("Error", e.getMessage());
            }
            return null;
        }




        //public void showBook(Book book) {}
        public void showAlberDialog(String message) {
            super.showAlberDialog(message);
        }
        public void showErrorDialog(String title, String message) {
            super.showErrorDialog(title, message);
        }
    }
