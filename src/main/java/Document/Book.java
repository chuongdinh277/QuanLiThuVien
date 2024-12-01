package Document;


public class Book {
    public Object Integer;
    private int id;
    private String title;// tên sách
    private String author;//tác giả
    private String category;//thể loại
    private int quantity;//số lượng
    private String description;
    private String imagePath;// miêu tả
    private double average_rating;
    private String publisher;
    private int remainingBook;
    private String ISBN;
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
    public String getISBN() {
        return ISBN;
    }
    public void setISBN(String ISBN) {
        this.ISBN = ISBN;
    }
    public int getRemainingBook() {
        return remainingBook;
    }
    public void setRemainingBook(int remainingBook) {
        this.remainingBook = remainingBook;
    }
    public String getTitle() {
        return title;
    }
    public String getPublisher() {
        return publisher;
    }
    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }
    public String getAuthor() {
        return author;
    }

    public String getCategory() {
        return category;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getDescription() {
        return description;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setCategory(String categoryl) {
        this.category = categoryl;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public void setId(int id) {
        this.id = id;
    }
    public int getId() {
        return id;
    }
    public String getImagePath() {
        return imagePath;
    }
    public double getRating() {
        return average_rating;
    }
    public void setRating(int rate) {
        this.average_rating = rate;
    }

//    @Override
//    public String toString() {
//        return "Document{" +
//                "id=" + id +
//                ", title='" + title + '\'' +
//                ", author='" + author + '\'' +
//                ", categoryl='" + category + '\'' +
//                ", quantity=" + quantity +
//                ", description='" + description + '\'' +
//                '}';
//    }

}
