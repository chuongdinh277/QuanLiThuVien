package Document;


import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
// Thay <package_name> bằng tên gói thực tế

import static Database.DatabaseConnection.getConnection;


public class BookDAO {
    public static boolean addBook(Book book) throws SQLException {
        String checkSql = "SELECT 1 FROM books WHERE title = ? AND author = ? AND category = ? AND description = ? AND publisher = ? AND section = ?";
        String updateSql = "UPDATE books SET quantity = quantity + ?, isbn = ? WHERE title = ? AND author = ? AND category = ? AND description = ? AND imagePath = ? AND publisher = ? AND section = ?";
        String insertSql = "INSERT INTO books (title, author, category, quantity, remaining_book, description, imagePath, publisher, section, isbn) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = getConnection()) {
            connection.setAutoCommit(false);

            try (PreparedStatement checkStatement = connection.prepareStatement(checkSql);
                 PreparedStatement updateStatement = connection.prepareStatement(updateSql);
                 PreparedStatement insertStatement = connection.prepareStatement(insertSql)) {

                checkStatement.setString(1, book.getTitle());
                checkStatement.setString(2, book.getAuthor());
                checkStatement.setString(3, book.getCategory());
                checkStatement.setString(4, book.getDescription());
                checkStatement.setString(5, book.getPublisher());
                checkStatement.setString(6, book.getSection());

                ResultSet resultSet = checkStatement.executeQuery();
                if (resultSet.next()) {
                    // Nếu sách đã có trong cơ sở dữ liệu, cập nhật số lượng và ISBN
                    updateStatement.setInt(1, book.getRemainingBook());
                    updateStatement.setString(2, book.getISBN()); // Cập nhật ISBN
                    updateStatement.setString(3, book.getTitle());
                    updateStatement.setString(4, book.getAuthor());
                    updateStatement.setString(5, book.getCategory());
                    updateStatement.setString(6, book.getDescription());
                    updateStatement.setString(7, book.getImagePath());
                    updateStatement.setString(8, book.getPublisher());
                    updateStatement.setString(9, book.getSection());

                    int rowsAffected = updateStatement.executeUpdate();
                    connection.commit();
                    return rowsAffected > 0;
                } else {
                    // Nếu sách chưa có, thêm mới vào cơ sở dữ liệu
                    insertStatement.setString(1, book.getTitle());
                    insertStatement.setString(2, book.getAuthor());
                    insertStatement.setString(3, book.getCategory());
                    insertStatement.setInt(4, book.getQuantity());
                    insertStatement.setInt(5, book.getQuantity()); // Giả sử remaining_book bằng quantity
                    insertStatement.setString(6, book.getDescription());
                    insertStatement.setString(7, book.getImagePath());
                    insertStatement.setString(8, book.getPublisher());
                    insertStatement.setString(9, book.getSection());
                    insertStatement.setString(10, book.getISBN()); // Thêm ISBN vào câu lệnh insert

                    int rowsAffected = insertStatement.executeUpdate();
                    connection.commit();
                    return rowsAffected > 0;
                }
            } catch (SQLException e) {
                connection.rollback();
                System.err.println("Error during database operation: " + e.getMessage());
                throw e;
            }
        } catch (SQLException e) {
            System.err.println("Connection error: " + e.getMessage());
            throw e;
        }
    }

    public static boolean deleteBookByTitle(String title) throws SQLException {
        String sql = "DELETE FROM books WHERE title =?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, title);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw e;
        }
    }
    public static boolean deleteBook(Book book) throws SQLException {
        // Câu lệnh DELETE chỉ cần ISBN hoặc ID để xác định cuốn sách
        String sql = "DELETE FROM books WHERE isbn = ?"; // Hoặc sử dụng `id` nếu bạn có trường ID
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            // Sử dụng ISBN (hoặc ID) để xác định cuốn sách cần xóa
            statement.setString(1, book.getISBN()); // Hoặc dùng `book.getId()` nếu xóa theo ID
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0; // Nếu có ít nhất một hàng bị ảnh hưởng, trả về true
        } catch (SQLException e) {
            throw e; // Ném lại SQLException nếu có lỗi
        }
    }
    public static boolean updateBook(Book book) throws SQLException {
        String sql = "UPDATE books SET title=?, author=?, category=?, description=?, publisher=?, section=?  ,imagePath=?, isbn=? WHERE imagePath=?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, book.getTitle());
            statement.setString(2, book.getAuthor());
            statement.setString(3, book.getCategory());
           // statement.setInt(4, book.getQuantity());
            //statement.setInt(4,book.getRemainingBook());
            statement.setString(4, book.getDescription());
            statement.setString(5, book.getPublisher());
            statement.setString(6, book.getSection());
            statement.setString(7, book.getImagePath());
            statement.setString(8, book.getISBN());
            statement.setString(9,book.getImagePath());

            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw e;
        }
    }
    public static boolean updateQuantity(Book book, int additionalQuantity) throws SQLException {
        String sql = "UPDATE books SET quantity = ?, remaining_book =? WHERE isbn = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            Book current = book;
            int newQuantity =  additionalQuantity - book.getQuantity();
            int newRemainingBook = Math.min(additionalQuantity, book.getRemainingBook() + newQuantity);
            System.out.println(newRemainingBook);
            statement.setInt(1, additionalQuantity);
            statement.setInt(2, newRemainingBook);
            statement.setString(3, book.getISBN());

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                // Cập nhật lại giá trị quantity và remaining_book trong đối tượng Book
                book.setQuantity(additionalQuantity);
                book.setRemainingBook(newRemainingBook);
            }
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw e;
        }
    }



    public static Book searchBooksExact(String title, String author) throws SQLException {
        String query = "SELECT * FROM books WHERE title = ? AND author = ?";
        Book books = null; ;
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, title);
            preparedStatement.setString(2, author);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                books = new Book(resultSet.getString("title"),
                        resultSet.getString("author"),
                        resultSet.getString("category"),
                        resultSet.getInt("quantity"),
                        resultSet.getInt("remaining_book"),
                        resultSet.getString("description"),
                        resultSet.getString("publisher"),
                        resultSet.getString("section"),
                        resultSet.getString("imagePath"),
                        resultSet.getString("ISBN"));
            }
        } catch (SQLException e) {
            throw e;
        }
        return books;
    }
    public static List<Book> getBooksByKeyword(String keyword) throws SQLException {
        String query = "SELECT * FROM books WHERE title LIKE ? OR author LIKE ?";
        List<Book> books = new ArrayList<>();
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            // Thêm dấu '%' vào từ khóa tìm kiếm
            String searchKeyword = "%" + keyword + "%"; // Tìm kiếm chứa keyword
            preparedStatement.setString(1, searchKeyword);
            preparedStatement.setString(2, searchKeyword);

            ResultSet resultSet = preparedStatement.executeQuery();
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
                books.add(book);
            }
        } catch (SQLException e) {
            throw e;
        }
        return books;
    }

    public static List<Book> getBooksByTitle(String title) throws SQLException {
        String query = "SELECT * FROM books WHERE title =?";
        List<Book> books = new ArrayList<>();
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, title);

            ResultSet resultSet = preparedStatement.executeQuery();
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
                books.add(book);
            }
        } catch (SQLException e) {
            throw e;
        }
        return books;
    }

    public static List<Book> getBooksByAuthor(String author) throws SQLException {
        String query = "SELECT * FROM books WHERE author =?";
        List<Book> books = new ArrayList<>();
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, author);

            ResultSet resultSet = preparedStatement.executeQuery();
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
                books.add(book);
            }
        } catch (SQLException e) {
            throw e;
        }
        return books;
    }

    public static List<Book> getBooksByCategory(String category) throws SQLException {
        String query = "SELECT * FROM books WHERE category =?";
        List<Book> books = new ArrayList<>();
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, category);

            ResultSet resultSet = preparedStatement.executeQuery();
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
                books.add(book);
            }
        } catch (SQLException e) {
            throw e;
        }
        return books;
    }
    public static List<Book> getAvailableBooks() throws SQLException {
        List<Book> books = new ArrayList<Book>();
        String sql = "SELECT * FROM books WHERE quantity > 0";
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                String title = resultSet.getString("title");
                String author = resultSet.getString("author");
                String categoryl = resultSet.getString("category");
                int quantity = resultSet.getInt("quantity");
                int remaining_book = resultSet.getInt("remaining_book");
                String description = resultSet.getString("description");
                String imagePath = resultSet.getString("imagePath");
                String publisher = resultSet.getString("publisher");
                String section = resultSet.getString("section");
                String ISBN = resultSet.getString("ISBN");
                books.add(new Book(title, author, categoryl, quantity,remaining_book, description, publisher,section,imagePath,ISBN));
            }
        } catch (Exception e) {
            throw e;
        }
        return books;
    }
    public static boolean validateBookData(Book book) {
        if (book.getTitle() == null || book.getTitle().isEmpty()) {
            return false;
        }
        if (book.getAuthor() == null || book.getAuthor().isEmpty()) {
            return false;
        }
        if (book.getQuantity() < 0) {
            return false;
        }
        return true;
    }
    public static Book getBookByISBN(String isbn) throws SQLException {
        String query = "SELECT * FROM books WHERE isbn = ?";
        Book book = null;

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, isbn);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                book = new Book(
                        resultSet.getString("title"),
                        resultSet.getString("author"),
                        resultSet.getString("category"),
                        resultSet.getInt("quantity"),
                        resultSet.getInt("remaining_book"),
                        resultSet.getString("description"),
                        resultSet.getString("publisher"),
                        resultSet.getString("section"),
                        resultSet.getString("imagePath"),
                        resultSet.getString("ISBN")
                );
            }
        } catch (SQLException e) {
            throw e;
        }
        return book;
    }

    public static boolean updateRemainingByISBN(String isbn, int additionalBooks) {
        // Đầu tiên, lấy số lượng remaining_books hiện tại từ cơ sở dữ liệu theo ISBN
        String selectSql = "SELECT remaining_book FROM books WHERE isbn = ?";

        // Cập nhật số lượng remaining_books
        String updateSql = "UPDATE books SET remaining_book = remaining_book + ? WHERE isbn = ?";

        try (Connection conn = getConnection();
             PreparedStatement selectStmt = conn.prepareStatement(selectSql);
             PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {

            // Lấy số lượng remaining_books hiện tại theo ISBN
            selectStmt.setString(1, isbn);
            ResultSet rs = selectStmt.executeQuery();
            Book book = BookDAO.getBookByISBN(isbn);
            if (rs.next()) {
                int currentRemainingBooks = rs.getInt("remaining_book");

                updateStmt.setInt(1, additionalBooks);  // Cộng thêm số sách vào remaining_books
                updateStmt.setString(2, isbn);  // ISBN của sách

                int rowsAffected = updateStmt.executeUpdate();
                if (rowsAffected > 0) {
                    // Cập nhật lại giá trị quantity và remaining_book trong đối tượng Book
                   // book.setQuantity(additionalQuantity);
                    book.setRemainingBook(book.getRemainingBook() + additionalBooks);
                    return true;
                }
            }

        } catch (SQLException e) {
            System.out.println("Error updating remaining books: " + e.getMessage());
        }
        return false;
    }
    public static Map<String, Integer> getCategoryStatistics() throws SQLException {
        String query = "SELECT category, COUNT(*) AS book_count FROM books GROUP BY category";
        Map<String, Integer> categoryStats = new HashMap<>();

        try (Connection connection = getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                String category = resultSet.getString("category");
                int count = resultSet.getInt("book_count");
                categoryStats.put(category, count);
            }
        } catch (SQLException e) {
            throw e; // Xử lý lỗi nếu có
        }

        return categoryStats; // Trả về thống kê
    }
    public static List<Book> getAllBooks() throws SQLException {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books";  // Lấy tất cả sách
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                // Tạo đối tượng Book từ dữ liệu trong ResultSet
                Book book = new Book(
                        resultSet.getString("title"),
                        resultSet.getString("author"),
                        resultSet.getString("category"),
                        resultSet.getInt("quantity"),
                        resultSet.getInt("remaining_book"),
                        resultSet.getString("description"),
                        resultSet.getString("publisher"),
                        resultSet.getString("section"),
                        resultSet.getString("imagePath"),
                        resultSet.getString("ISBN")
                );
                books.add(book);
            }
        } catch (SQLException e) {
            throw e;
        }
        return books;
    }


}