package Document;

/**
 * Lớp đại diện cho sách trong thư viện.
 * Lưu trữ các thông tin liên quan đến sách bao gồm tên, tác giả, thể loại, số lượng, mô tả, hình ảnh, và xếp hạng trung bình.
 */
public class Book {
    public Object Integer;
    private int id; // Mã sách
    private String title; // Tên sách
    private String author; // Tác giả sách
    private String category; // Thể loại sách
    private int quantity; // Số lượng sách
    private String description; // Mô tả sách
    private String imagePath; // Đường dẫn đến hình ảnh sách
    private double average_rating; // Xếp hạng trung bình của sách
    private String publisher; // Nhà xuất bản
    private int remainingBook; // Số lượng sách còn lại trong kho
    private String ISBN; // Mã số sách (ISBN)

    /**
     * Khởi tạo đối tượng Book với các thông tin cần thiết.
     * @param title Tên sách
     * @param author Tác giả sách
     * @param category Thể loại sách
     * @param quantity Tổng số lượng sách
     * @param remainingBook Số lượng sách còn lại
     * @param description Mô tả sách
     * @param publisher Nhà xuất bản sách
     * @param imagePath Đường dẫn đến hình ảnh của sách
     * @param ISBN Mã số sách (ISBN)
     */
    public Book(String title, String author, String category, int quantity, int remainingBook, String description, String publisher, String imagePath, String ISBN) {
        this.title = title;
        this.author = author;
        this.category = category;
        this.quantity = quantity;
        this.remainingBook = remainingBook;
        this.description = description;
        this.imagePath = imagePath;
        this.publisher = publisher;
        this.ISBN = ISBN;
    }

    /**
     * Lấy mã số ISBN của sách.
     * @return ISBN của sách
     */
    public String getISBN() {
        return ISBN;
    }

    /**
     * Thiết lập mã số ISBN cho sách.
     * @param ISBN Mã số ISBN
     */
    public void setISBN(String ISBN) {
        this.ISBN = ISBN;
    }

    /**
     * Lấy số lượng sách còn lại trong kho.
     * @return Số lượng sách còn lại
     */
    public int getRemainingBook() {
        return remainingBook;
    }

    /**
     * Thiết lập số lượng sách còn lại trong kho.
     * @param remainingBook Số lượng sách còn lại
     */
    public void setRemainingBook(int remainingBook) {
        this.remainingBook = remainingBook;
    }

    /**
     * Lấy tên sách.
     * @return Tên sách
     */
    public String getTitle() {
        return title;
    }

    /**
     * Lấy tên nhà xuất bản của sách.
     * @return Nhà xuất bản
     */
    public String getPublisher() {
        return publisher;
    }

    /**
     * Thiết lập tên nhà xuất bản cho sách.
     * @param publisher Tên nhà xuất bản
     */
    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    /**
     * Lấy tên tác giả của sách.
     * @return Tên tác giả
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Lấy thể loại sách.
     * @return Thể loại sách
     */
    public String getCategory() {
        return category;
    }

    /**
     * Lấy số lượng sách.
     * @return Số lượng sách
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Lấy mô tả sách.
     * @return Mô tả sách
     */
    public String getDescription() {
        return description;
    }

    /**
     * Thiết lập tên sách.
     * @param title Tên sách
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Thiết lập tên tác giả cho sách.
     * @param author Tên tác giả
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * Thiết lập thể loại sách.
     * @param category Thể loại sách
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * Thiết lập số lượng sách.
     * @param quantity Số lượng sách
     */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    /**
     * Thiết lập mô tả sách.
     * @param description Mô tả sách
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Thiết lập đường dẫn đến hình ảnh của sách.
     * @param imagePath Đường dẫn đến hình ảnh
     */
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    /**
     * Thiết lập mã sách.
     * @param id Mã sách
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Lấy mã sách.
     * @return Mã sách
     */
    public int getId() {
        return id;
    }

    /**
     * Lấy đường dẫn đến hình ảnh của sách.
     * @return Đường dẫn hình ảnh
     */
    public String getImagePath() {
        return imagePath;
    }

    /**
     * Lấy xếp hạng trung bình của sách.
     * @return Xếp hạng trung bình
     */
    public double getRating() {
        return average_rating;
    }

    /**
     * Thiết lập xếp hạng trung bình của sách.
     * @param rate Xếp hạng trung bình
     */
    public void setRating(int rate) {
        this.average_rating = rate;
    }
}
