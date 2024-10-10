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
    private final static String MEMBER = "member";
    public Member(String userName, String password) {
        super(userName, password,MEMBER);
    }
    public void borrowBook(Book borrowBook) throws SQLException {
        try {
            boolean isBorrow = TransactionDAO.borrowBook(this, borrowBook);
            if (isBorrow) {
                this.showAlberDialog("Mượn sách thành công");
            } else {
                this.showAlberDialog("Mượn sách thất bại");
            }
        } catch (SQLException e) {
            this.showErrorDialog("Database connection error",e.getMessage());
        }
    };
    public void returnBook(Book returnBook) throws SQLException {
        try {
            boolean isReturn = TransactionDAO.returnBook(this, returnBook);
            if (isReturn) {
                this.showAlberDialog("Trả sách thành công");
            } else {
                this.showAlberDialog("Trả sách thất bại");
            }
        } catch (Exception e) {
            this.showErrorDialog("Database connection error",e.getMessage());
        }
    };
    public Book searchBook(String title,String author) throws SQLException {
        try {
            Book foundBook = BookDAO.searchBooksExact(title,author);
            if (foundBook != null) {
                this.showAlberDialog("Tìm thấy sách");
                return foundBook ;
            } else {
                this.showAlberDialog("Không tìm thấy sách nào");
            }
        } catch (Exception e) {
            this.showErrorDialog("Error", e.getMessage());
        }
        return null;
    }
    public List<Book> searchBookByTitle(String title) {
        try {
            List<Book> foundBooks = BookDAO.getBooksByTitle(title);
            if (!foundBooks.isEmpty()) {
                this.showAlberDialog("Tìm thấy sách");
                return foundBooks;
            } else {
                this.showAlberDialog("Không tìm thấy sách nào");
            }
        } catch (Exception e) {
            this.showErrorDialog("Error", e.getMessage());
        }
        return null;
    }
    public List<Book> findBookByAuthor(String author) {
        try {
            List<Book> foundBooks = BookDAO.getBooksByAuthor(author);
            if (!foundBooks.isEmpty()) {
                this.showAlberDialog("Tìm thấy sách");
                return foundBooks;
            } else {
                this.showAlberDialog("Không tìm thấy sách nào");
            }
        } catch (Exception e) {
            this.showErrorDialog("Error", e.getMessage());
        }
        return null;
    }
    public List<Book> findBookByCategory(String category) {
        try {
            List<Book> foundBooks = BookDAO.getBooksByCategory(category);
            if (!foundBooks.isEmpty()) {
                this.showAlberDialog("Tìm thấy sách");
                return foundBooks;
            } else {
                this.showAlberDialog("Không tìm thấy sách nào");
            }
        } catch (Exception e) {
            this.showErrorDialog("Error", e.getMessage());
        }
        return null;
    }
    public List<Book> findBookByKeywords(String keywords) {
        try {
            List<Book> foundBooks = BookDAO.getBooksByKeyword(keywords);
            if (!foundBooks.isEmpty()) {
                this.showAlberDialog("Tìm thấy sách");
                return foundBooks;
            } else {
                this.showAlberDialog("Không tìm thấy sách nào");
            }
        } catch (Exception e) {
            this.showErrorDialog("Error", e.getMessage());
        }
        return null;
    }
    public List<Transaction> viewTransactionByMine() {
        try {
            List<Transaction> transactions = TransactionDAO.getTransactionByUserName(this);
            if (!transactions.isEmpty()) {
                this.showAlberDialog("Xem thông tin đơn hàng");
                return transactions;
            } else {
                this.showAlberDialog("Không tìm thấy đơn hàng nào");
            }
        } catch (Exception e) {
            this.showErrorDialog("Error", e.getMessage());
        }
        return null;
    }
    public HashMap<User,List<Transaction>> viewProfiles() {
        try {
            List<Transaction> transactions = new ArrayList<Transaction>();
            transactions = this.viewTransactionByMine();
            HashMap<User,List<Transaction>> hashMap = new HashMap<>();
            hashMap.put(this, transactions);
            return hashMap;
        } catch (Exception e) {
            this.showErrorDialog("Error", e.getMessage());
        }
        return null;
    }

    public List<BookRating> getRecommendBook() {
        try {
            List<BookRating> books = new ArrayList<BookRating>();
            books = Recommend.getRecommendBooks(this);
            return books;
        } catch (SQLException e) {
            this.showErrorDialog("Error",e.getMessage());
        }
        return null;
    }

    public void addReviewForBook(Book book, String comment, int rating) {
        Reviews review = new Reviews(this.getUserName(), book.getTitle(), book.getAuthor(), comment, rating);
        try {
            boolean check = ReviewDAO.addReview(this.getUserName(), book.getTitle(), book.getAuthor(), comment, rating);
            if (check) {
                this.showAlberDialog("Đã đánh giá sách thành công");
            } else {
                this.showAlberDialog("Đã xảy ra lỗi khi đánh giá sách");
            }
        } catch (Exception e) {
            this.showErrorDialog("Error", e.getMessage());
        }
    }

    public void addReviewForBook(Reviews review) {
        try {
            boolean check = ReviewDAO.addReview(this.getUserName(), review.getBookTitle(), review.getAuthor(), review.getComment(), review.getRating());
            if (check) {
                this.showAlberDialog("Đã đánh giá sách thành công");
            } else {
                this.showAlberDialog("Đã xảy ra lỗi khi đánh giá sách");
            }
        } catch (Exception e) {
            this.showErrorDialog("Error", e.getMessage());
        }
    }

    public List<Book> getAllBooks() {
        try {
            List<Book> allBooks = BookDAO.getAvailableBooks();
            if (!allBooks.isEmpty()) {
                this.showAlberDialog("Tìm thấy sách");
                return allBooks;
            } else {
                this.showAlberDialog("Không tìm thấy sách nào");
            }
        } catch (Exception e) {
            this.showErrorDialog("Error", e.getMessage());
        }
        return null;
    }


    private boolean registerUser(String userName, String password, String role) throws SQLException {
        String sql = "INSERT INTO users (username, password, role) VALUES (?,?,?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, userName);
            statement.setString(2, password);
            statement.setString(3, role);
            int rowsInserted = statement.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            throw e;
        }
    }

    @Override
    public void register() throws SQLException {

        try {
            boolean check = registerUser(this.getUserName(), this.getPassword(), this.getRole());
            if (check) {
                this.showAlberDialog(this.getRole() + " " + this.getUserName() + " " + "Đăng ký thành công");
            } else {
                this.showAlberDialog("Đăng ký thất bại");
            }
        } catch (Exception e) {
            this.showErrorDialog("Error", e.getMessage());
        }
    }

    private String login(String username, String password) throws SQLException {
        String checkUsernameSQL = "SELECT * FROM users WHERE username = ?";
        String checkPasswordSQL = "SELECT * FROM users WHERE username = ? AND password = ?";

        try (Connection connection = DatabaseConnection.getConnection()) {
            // Kiểm tra xem username có tồn tại không
            try (PreparedStatement checkUsernameStmt = connection.prepareStatement(checkUsernameSQL)) {
                checkUsernameStmt.setString(1, username);
                ResultSet usernameResult = checkUsernameStmt.executeQuery();
                if (!usernameResult.next()) {
                    return "Username not found"; // Tên đăng nhập không đúng
                }
            }

            // Kiểm tra xem mật khẩu có đúng không
            try (PreparedStatement checkPasswordStmt = connection.prepareStatement(checkPasswordSQL)) {
                checkPasswordStmt.setString(1, username);
                checkPasswordStmt.setString(2, password);
                ResultSet passwordResult = checkPasswordStmt.executeQuery();
                if (passwordResult.next()) {
                    return "Login successful"; // Đăng nhập thành công
                } else {
                    return "Incorrect password"; // Mật khẩu sai
                }
            }
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public void login() {
        try {
            String result = this.login(this.getUserName(), this.getPassword());
            if (result.equals("Login successful")) {
                this.showAlberDialog(this.getRole() + " " + this.getUserName() + " " + "Đăng nhập thành công");
            } else {
                this.showAlberDialog(result);
            }
        } catch (SQLException e) {
            this.showErrorDialog("Error", e.getMessage());
        }
    }




    // lấy sách có rating.
    public List<BookRating> getFullBooks() {
        try {

            List<BookRating> allBooks = BookRatingDAO.getAvailableBooks();
            if (!allBooks.isEmpty()) {
                this.showAlberDialog("Tìm thấy sách");
                return allBooks;
            }
        } catch (Exception e) {
            this.showErrorDialog("Error", e.getMessage());
        }
        return null;
    }

    public List<BookRating> findFullBookByCategory(String category) {
        try {
            List<BookRating> foundBooks = BookRatingDAO.getBooksByCategory(category);

            if (!foundBooks.isEmpty()) {
                this.showAlberDialog("Tìm thấy sách");
                return foundBooks;
            } else {
                this.showAlberDialog("Không tìm thấy sách nào");
            }
        } catch (Exception e) {
            this.showErrorDialog("Error", e.getMessage());
        }
        return null;
    }

    public List<BookRating> findFullBookByAuthor(String author) {
        try {
            List<BookRating> foundBooks = new ArrayList<BookRating>();
            foundBooks = BookRatingDAO.getBooksByAuthor(author);
            if (!foundBooks.isEmpty()) {
                this.showAlberDialog("Tìm thấy sách");
                return foundBooks;
            } else {
                this.showAlberDialog("Không tìm thấy sách nào");
            }
        } catch (Exception e) {
            this.showErrorDialog("Error", e.getMessage());
        }
        return null;
    }

    public List<BookRating> findFullBookByTitle(String title) {
        try {
            List<BookRating> bookList = new ArrayList<BookRating>();
            bookList = BookRatingDAO.getBooksByTitle(title);
            if (!bookList.isEmpty()) {
                this.showAlberDialog("Tìm thấy sách");
                return bookList;
            }
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
