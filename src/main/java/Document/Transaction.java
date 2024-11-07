package Document;

import java.sql.Date;

public class Transaction {
    private int id;
    private String username;
    private String title;
    private String author;
    private String imagePath;
    private Date borrow_Date;
    private Date return_Date;
    private String isbn;
    private int quantity;
    public Transaction( int id, String isbn, String username, String title, String author,
                        String imagePath, Date borrow_Date, Date return_Date, int quantity) {
        this.id = id;
        this.isbn = isbn;
        this.username = username;
        this.title = title;
        this.author = author;
        this.imagePath = imagePath;
        this.borrow_Date = borrow_Date;
        this.return_Date = return_Date;
        this.quantity = quantity;
    }
    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    public int getId() {
        return id;
    }
    public String getIsbn() {
        return isbn;
    }
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getUsername() {
        return username;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getImagePath() {
        return imagePath;
    }

    public Date getBorrow_Date() {
        return borrow_Date;
    }

    public Date getReturn_Date() {
        return return_Date;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public void setBorrow_Date(Date borrow_Date) {
        this.borrow_Date = borrow_Date;
    }

    public void setReturn_Date(Date return_Date) {
        this.return_Date = return_Date;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", imagePath='" + imagePath + '\'' +
                ", borrow_Date=" + borrow_Date +
                ", return_Date=" + return_Date +
                '}';
    }
}