package APIGoogle;

import Document.Book;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.text.Normalizer;
import java.util.HashMap;
import java.util.Map;

public class BookParser {

    private static final Map<String, Book> cache = new HashMap<>();

    public static Book parseBookInfo(String jsonResponse) {
        System.out.println(jsonResponse);
        // Kiểm tra cache
        if (cache.containsKey(jsonResponse)) {
            return cache.get(jsonResponse);
        }

        // Phân tích JSON
        JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();

        // Kiểm tra nếu có kết quả
        if (jsonObject.has("items") && jsonObject.getAsJsonArray("items").size() > 0) {
            JsonObject volumeInfo = jsonObject.getAsJsonArray("items").get(0).getAsJsonObject().getAsJsonObject("volumeInfo");

            // Chuẩn hóa thông tin cần thiết
            String title = Normalizer.normalize(volumeInfo.get("title").getAsString(), Normalizer.Form.NFC);
            String author = volumeInfo.has("authors") ?
                    Normalizer.normalize(volumeInfo.get("authors").getAsJsonArray().toString().replaceAll("[\\[\\]\"]", ""), Normalizer.Form.NFC) :
                    "Unknown";
            String category = volumeInfo.has("categories") ?
                    Normalizer.normalize(volumeInfo.get("categories").getAsJsonArray().toString().replaceAll("[\\[\\]\"]", ""), Normalizer.Form.NFC) :
                    "No category available";
            String description = volumeInfo.has("description") ?
                    Normalizer.normalize(volumeInfo.get("description").getAsString(), Normalizer.Form.NFC) :
                    "No description available";
            String imagePath = volumeInfo.has("imageLinks") && volumeInfo.getAsJsonObject("imageLinks").has("thumbnail") ?
                    volumeInfo.getAsJsonObject("imageLinks").get("thumbnail").getAsString() :
                    "No image available";
            String isbn = "No ISBN available"; // Default value nếu không tìm thấy ISBN
            if (volumeInfo.has("industryIdentifiers")) {
                for (var identifier : volumeInfo.getAsJsonArray("industryIdentifiers")) {
                    JsonObject idObject = identifier.getAsJsonObject();
                    String type = idObject.get("type").getAsString();
                    if ("ISBN_13".equals(type) || "ISBN_10".equals(type)) {
                        isbn = idObject.get("identifier").getAsString();
                        break;
                    }
                }
            }
            String publisher = volumeInfo.has("publisher") ?
                    Normalizer.normalize(volumeInfo.get("publisher").getAsString(), Normalizer.Form.NFC) :
                    "Unknown publisher";
            // Tạo đối tượng Book
            Book book = new Book(title, author, category, 1,1, description,publisher,imagePath,isbn);
            // Lưu vào cache

            cache.put(jsonResponse, book);
            return book; // Trả về đối tượng Book
        }

        return null; // Trả về null nếu không có kết quả
    }
}
