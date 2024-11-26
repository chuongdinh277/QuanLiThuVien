package cache;

import Document.Book;
import java.util.HashMap;
import java.util.Map;

public class BookCache {
    private static final Map<Integer, Book> cache = new HashMap<>();

    public static Book getBook(int bookId) {
        return cache.get(bookId);
    }

    public static void putBook(Book book) {
        cache.put(book.getId(), book);
    }

    public static boolean isCached(int bookId) {
        return cache.containsKey(bookId);
    }
}