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
    private String section;
    public Book(String title, String author, String category, int quantity, String description, String publisher, String section, String imagePath) {
        this.title = title;
        this.author = author;
        this.category = category;
        this.quantity = quantity;
        this.description = description;
        this.imagePath = imagePath;
        this.publisher = publisher;
        this.section = section;
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
    public String getSection() {
        return section;
    }
    public void setSection(String section) {this.section = section;}
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