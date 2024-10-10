package Review;

import java.util.Date;

public class Reviews {
    private int id;
    private String username;
    private String bookTitle;
    private String author;
    private String comment;
    private int rating;
    private Date reviewDate;
    public Reviews(String username, String bookTitle, String author, String comment, int rating) {
        this.username = username;
        this.bookTitle = bookTitle;
        this.author = author;
        this.comment = comment;
        this.rating = rating;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public String getAuthor() {
        return author;
    }

    public String getComment() {
        return comment;
    }

    public int getRating() {
        return rating;
    }

    public Date getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(Date reviewDate) {
        this.reviewDate = reviewDate;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Reviews{" +
                "username='" + username + '\'' +
                ", bookTitle='" + bookTitle + '\'' +
                ", author='" + author + '\'' +
                ", comment='" + comment + '\'' +
                ", rating=" + rating +
                ", reviewDate=" + reviewDate +
                '}';
    }
}
