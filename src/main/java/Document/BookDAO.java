package Document;


import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
// Thay <package_name> bằng tên gói thực tế

import static Database.DatabaseConnection.getConnection;


public class BookDAO {
    /**
     * Thêm một cuốn sách vào cơ sở dữ liệu hoặc cập nhật thông tin của sách nếu sách đã tồn tại.
     * Phương thức này sẽ kiểm tra xem cuốn sách đã tồn tại trong cơ sở dữ liệu hay chưa bằng cách so sánh các thuộc tính như tên sách, tác giả, thể loại, mô tả, và nhà xuất bản.
     * Nếu sách đã có, số lượng sách sẽ được cập nhật và ISBN sẽ được thêm vào; nếu sách chưa có, cuốn sách mới sẽ được thêm vào cơ sở dữ liệu.
     *
     * @param book Đối tượng Book chứa thông tin của sách cần thêm hoặc cập nhật.
     * @return true nếu việc thêm hoặc cập nhật sách thành công, false nếu thất bại.
     * @throws SQLException Nếu có lỗi xảy ra khi tương tác với cơ sở dữ liệu.
     */
    public static boolean addBook(Book book) throws SQLException {
        String checkSql = "SELECT 1 FROM books WHERE title = ? AND author = ? AND category = ? AND description = ? AND publisher = ?";
        String updateSql = "UPDATE books SET quantity = quantity + ?, isbn = ? WHERE title = ? AND author = ? AND category = ? AND description = ? AND imagePath = ? AND publisher = ?";
        String insertSql = "INSERT INTO books (title, author, category, quantity, remaining_book, description, imagePath, publisher, isbn) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

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
                    insertStatement.setString(9, book.getISBN()); // Thêm ISBN vào câu lệnh insert

                    int rowsAffected = insertStatement.executeUpdate();
                    connection.commit();
                    return rowsAffected > 0;
                }
            } catch (SQLException e) {
                connection.rollback();
               // System.err.println("Error during database operation: " + e.getMessage());
                throw e;
            }
        } catch (SQLException e) {
          //  System.err.println("Connection error: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Xóa cuốn sách trong cơ sở dữ liệu theo tiêu đề.
     * Phương thức này sử dụng tiêu đề của cuốn sách để tìm và xóa sách trong bảng `books`.
     *
     * @param title Tiêu đề của cuốn sách cần xóa.
     * @return true nếu việc xóa sách thành công, false nếu thất bại.
     * @throws SQLException Nếu có lỗi xảy ra khi tương tác với cơ sở dữ liệu.
     */
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

    /**
     * Xóa cuốn sách trong cơ sở dữ liệu theo đối tượng Book.
     * Phương thức này sử dụng ISBN của cuốn sách để tìm và xóa sách trong bảng `books`.
     *
     * @param book Đối tượng Book chứa thông tin của cuốn sách cần xóa.
     * @return true nếu việc xóa sách thành công, false nếu thất bại.
     * @throws SQLException Nếu có lỗi xảy ra khi tương tác với cơ sở dữ liệu.
     */
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

    /**
     * Cập nhật thông tin của cuốn sách trong cơ sở dữ liệu.
     * Phương thức này sử dụng ISBN để xác định cuốn sách cần cập nhật và thay đổi các thông tin như tiêu đề, tác giả, thể loại, mô tả, nhà xuất bản, và đường dẫn hình ảnh.
     *
     * @param book Đối tượng Book chứa thông tin mới của cuốn sách.
     * @return true nếu việc cập nhật sách thành công, false nếu thất bại.
     * @throws SQLException Nếu có lỗi xảy ra khi tương tác với cơ sở dữ liệu.
     */
    public static boolean updateBook(Book book) throws SQLException {
        String sql = "UPDATE books SET title=?, author=?, category=?, description=?, publisher=?, imagePath=?, isbn=? WHERE isbn=?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, book.getTitle());
            statement.setString(2, book.getAuthor());
            statement.setString(3, book.getCategory());
            statement.setString(4, book.getDescription());
            statement.setString(5, book.getPublisher());
            statement.setString(6, book.getImagePath());
            statement.setString(7, book.getISBN());
            statement.setString(8, book.getISBN());

            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw e;
        }
    }

    /**
     * Cập nhật số lượng sách và số lượng sách còn lại trong kho.
     *
     * @param book              Đối tượng sách cần cập nhật.
     * @param additionalQuantity Số lượng sách mới để cập nhật.
     * @return                  true nếu cập nhật thành công, ngược lại false.
     * @throws SQLException     Nếu xảy ra lỗi trong quá trình thực thi câu lệnh SQL.
     */
    public static boolean updateQuantity(Book book, int additionalQuantity) throws SQLException {
        String sql = "UPDATE books SET quantity = ?, remaining_book =? WHERE isbn = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            Book current = book;
            int newQuantity =  additionalQuantity - book.getQuantity();
            int newRemainingBook = Math.min(additionalQuantity, book.getRemainingBook() + newQuantity);
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

    /**
     * Tìm kiếm sách với tiêu đề và tác giả khớp chính xác.
     *
     * @param title     Tiêu đề của sách cần tìm.
     * @param author    Tác giả của sách cần tìm.
     * @return          Đối tượng Book nếu tìm thấy, ngược lại null.
     * @throws SQLException Nếu xảy ra lỗi trong quá trình thực thi câu lệnh SQL.
     */
    public static Book searchBooksExact(String title, String author) throws SQLException {
        String query = "SELECT * FROM books WHERE title = ? AND author = ?";
        Book books = null;
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
                        resultSet.getString("imagePath"),
                        resultSet.getString("ISBN"));
            }
        } catch (SQLException e) {
            throw e;
        }
        return books;
    }

    /**
     * Tìm kiếm sách với từ khóa trong tiêu đề hoặc tác giả.
     *
     * @param keyword   Từ khóa tìm kiếm (không phân biệt chữ hoa/thường).
     * @return          Danh sách các sách khớp với từ khóa tìm kiếm.
     * @throws SQLException Nếu xảy ra lỗi trong quá trình thực thi câu lệnh SQL.
     */
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
                        resultSet.getString("imagePath"),
                        resultSet.getString("ISBN"));
                books.add(book);
            }
        } catch (SQLException e) {
            throw e;
        }
        return books;
    }


    /**
     * Tìm kiếm sách theo tiêu đề.
     *
     * @param title     Tiêu đề sách (không phân biệt chữ hoa/thường).
     * @return          Danh sách các sách có tiêu đề chứa từ khóa tìm kiếm.
     * @throws SQLException Nếu xảy ra lỗi trong quá trình thực thi câu lệnh SQL.
     */
    public static List<Book> getBooksByTitle(String title) throws SQLException {
        String query = "SELECT * FROM books WHERE LOWER(title) LIKE ?";
        List<Book> books = new ArrayList<>();
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            // Sử dụng '%' để tìm kiếm các tiêu đề chứa từ khóa
            preparedStatement.setString(1, "%" + title.toLowerCase() + "%");

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Book book = new Book(resultSet.getString("title"),
                        resultSet.getString("author"),
                        resultSet.getString("category"),
                        resultSet.getInt("quantity"),
                        resultSet.getInt("remaining_book"),
                        resultSet.getString("description"),
                        resultSet.getString("publisher"),
                        resultSet.getString("imagePath"),
                        resultSet.getString("ISBN"));
                books.add(book);
            }
        } catch (SQLException e) {
            throw e;
        }
        return books;
    }

    /**
     * Tìm kiếm sách theo tác giả.
     *
     * @param author    Tên tác giả của sách.
     * @return          Danh sách các sách của tác giả được chỉ định.
     * @throws SQLException Nếu xảy ra lỗi trong quá trình thực thi câu lệnh SQL.
     */
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
                        resultSet.getString("imagePath"),
                        resultSet.getString("ISBN"));
                books.add(book);
            }
        } catch (SQLException e) {
            throw e;
        }
        return books;
    }

    /**
     * Tìm kiếm sách theo thể loại.
     *
     * @param category  Thể loại của sách.
     * @return          Danh sách các sách thuộc thể loại được chỉ định.
     * @throws SQLException Nếu xảy ra lỗi trong quá trình thực thi câu lệnh SQL.
     */
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
                        resultSet.getString("imagePath"),
                        resultSet.getString("ISBN"));
                books.add(book);
            }
        } catch (SQLException e) {
            throw e;
        }
        return books;
    }

    /**
     * Lấy danh sách tất cả các sách còn tồn tại (quantity > 0) từ cơ sở dữ liệu.
     *
     * @return Danh sách các sách có số lượng lớn hơn 0.
     * @throws SQLException Nếu xảy ra lỗi khi truy vấn cơ sở dữ liệu.
     */
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
                String ISBN = resultSet.getString("ISBN");
                books.add(new Book(title, author, categoryl, quantity,remaining_book, description, publisher,imagePath,ISBN));
            }
        } catch (Exception e) {
            throw e;
        }
        return books;
    }
    /**
     * Lấy thông tin chi tiết của một cuốn sách dựa trên ISBN.
     *
     * @param isbn Mã ISBN của sách cần tìm.
     * @return Đối tượng Book chứa thông tin sách hoặc null nếu không tìm thấy.
     * @throws SQLException Nếu xảy ra lỗi khi truy vấn cơ sở dữ liệu.
     */
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
                        resultSet.getString("imagePath"),
                        resultSet.getString("ISBN")
                );
            }
        } catch (SQLException e) {
            throw e;
        }
        return book;
    }

    /**
     * Cập nhật số lượng sách còn lại (remaining_book) trong cơ sở dữ liệu theo ISBN.
     *
     * @param isbn Mã ISBN của sách cần cập nhật.
     * @param additionalBooks Số lượng sách cần thêm (có thể là số âm để giảm).
     * @return true nếu cập nhật thành công, ngược lại false.
     */
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
           // System.out.println("Error updating remaining books: " + e.getMessage());
        }
        return false;
    }

    /**
     * Lấy thống kê số lượng sách theo từng thể loại.
     *
     * @return Bản đồ (Map) chứa thể loại sách và số lượng sách tương ứng.
     * @throws SQLException Nếu xảy ra lỗi khi truy vấn cơ sở dữ liệu.
     */
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

    /**
     * Lấy danh sách tất cả các sách trong cơ sở dữ liệu.
     *
     * @return Danh sách tất cả các đối tượng Book.
     * @throws SQLException Nếu xảy ra lỗi khi truy vấn cơ sở dữ liệu.
     */
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

    /**
     * Lấy danh sách các sách có đánh giá trung bình cao nhất.
     * Sách không có đánh giá sẽ bị loại bỏ.
     *
     * @return Danh sách các sách được sắp xếp theo đánh giá trung bình giảm dần.
     * @throws SQLException Nếu xảy ra lỗi khi truy vấn cơ sở dữ liệu.
     */
    public static List<Book> getBooksByAverageRating() throws SQLException {
        List<Book> books = getAllBooks();
        List<Book> filteredBooks = new ArrayList<>();
        Map<String, Double> ratingsMap = new HashMap<>();

        // Lấy đánh giá trung bình cho từng sách
        for (Book book : books) {
            double averageRating = ReviewDAO.getAverageRating(book.getISBN());
            if (averageRating > 0) {
                filteredBooks.add(book);
                ratingsMap.put(book.getISBN(), averageRating);
            //    System.out.println(book.getTitle() + " " + averageRating);
            }
        }

        // Sắp xếp filteredBooks theo đánh giá trung bình
        filteredBooks.sort((book1, book2) -> {
            double rating1 = ratingsMap.get(book1.getISBN());
            double rating2 = ratingsMap.get(book2.getISBN());
            return Double.compare(rating2, rating1); // Sắp xếp giảm dần
        });

        return filteredBooks;
    }

    /**
     * Lấy danh sách 10 sách được thêm gần đây nhất trong cơ sở dữ liệu.
     *
     * @return Danh sách 10 sách mới nhất.
     * @throws SQLException Nếu xảy ra lỗi khi truy vấn cơ sở dữ liệu.
     */
    public static List<Book> getRecentlyAddedBooks() throws SQLException {
        List<Book> books = getAllBooks();
        List<Book> recentlyAddedBooks = new ArrayList<>();

        // Iterate through the list in reverse order
        for (int i = books.size() - 1; i >= 0 && recentlyAddedBooks.size() < 10; i--) {
           // System.out.println(books.get(i).getTitle());
            recentlyAddedBooks.add(books.get(i));
        }
        return recentlyAddedBooks;
    }
}