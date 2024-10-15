package APIGoogle;

import Document.Book;
import javafx.scene.control.Alert;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

public class GoogleBooksAPI {
    private static final String API_KEY = "AIzaSyAfMWFgDwsKdnNoqNIUMhjJH0kkoMpOQfI"; // Thay bằng API Key của bạn
    private static final String BASE_URL = "https://www.googleapis.com/books/v1/volumes";
    private static final Map<String, Book> cache = new HashMap<>();

    private static final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(1)) // Đặt thời gian chờ kết nối
            .version(HttpClient.Version.HTTP_2) // Sử dụng HTTP/2
            .build();

    public static HttpClient getHttpClient() {
        return httpClient;
    }
    public static String searchBooksByTitle(String title) {
        // Kiểm tra trong cache trước
        if (cache.containsKey(title)) {
            return cache.get(title).getImagePath();
        }

        try {
            // Mã hóa tiêu đề sách để sử dụng trong URL
            String query = URLEncoder.encode( title, StandardCharsets.UTF_8.toString());
            // Tạo URL hợp lệ với API Key
            String urlString = BASE_URL + "?q=" + query + "&maxResults=1&fields=items(volumeInfo(title,authors,categories,imageLinks))&key=" + API_KEY;

            // Tạo request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(urlString))
                    .GET()
                    .header("Accept", "application/json")
                    .header("Accept-Encoding", "gzip")
                    .build();

            // Gửi yêu cầu không đồng bộ và xử lý phản hồi
            HttpResponse<InputStream> response = httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream());

            // Kiểm tra mã trạng thái HTTP
            if (response.statusCode() != 200) {
                int responseCode = response.statusCode();
                handleError(responseCode);
                return null;
            }

            InputStream responseStream = response.body();
            InputStreamReader reader ;
            if("gzip".equals(response.headers().firstValue("Content-Encoding").orElse(""))) {
                reader = new InputStreamReader(new GZIPInputStream(responseStream),StandardCharsets.UTF_8);
            } else {
                reader = new InputStreamReader(responseStream, StandardCharsets.UTF_8);
            }
            // Chuẩn hóa văn bản sau khi nhận về
            StringBuilder stringBuilder = new StringBuilder();
            try (BufferedReader bufferedReader = new BufferedReader(reader)) {
                String line ;
                while((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);
                }
            }
            String normalizedOutput = Normalizer.normalize(stringBuilder.toString(), Normalizer.Form.NFC);

            // Xử lý dữ liệu JSON ở đây hoặc trả về chuỗi kết quả thô
            return normalizedOutput;

        } catch (Exception e) {
            e.printStackTrace();
            showErrorDialog("An error occurred while searching for the book: " + e.getMessage());
        }
        return null;
    }

    // Xử lý lỗi từ HTTP status code
    private static void handleError(int responseCode) {
        switch (responseCode) {
            case 403:
                showErrorDialog("Access forbidden: Check your API key and permissions.");
                break;
            case 404:
                showErrorDialog("Resource not found: Check the URL.");
                break;
            default:
                showErrorDialog("Unexpected error: HTTP code " + responseCode);
                break;
        }
    }

    public static String searchBookByTitleAndAuthor(String title, String author) {

        try {
            String encodedTitle = URLEncoder.encode(title, "UTF-8");
            String encodedAuthor = URLEncoder.encode(author, "UTF-8");
            String urlString = BASE_URL + "?q=" + encodedTitle + "+inauthor:" + encodedAuthor + "&maxResults=1&fields=items(volumeInfo(title,authors,categories,imageLinks))&key=" + API_KEY;
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(urlString))
                    .GET()
                    .header("Accept", "application/json")
                    .header("Accept-Encoding", "gzip") // Thêm tiêu đề nén
                    .build();

            // Gửi yêu cầu không đồng bộ và xử lý phản hồi
            HttpResponse<InputStream> response = httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream());

            // Kiểm tra mã trạng thái HTTP
            if (response.statusCode() != 200) {
                int responseCode = response.statusCode();
                handleError(responseCode);
                return null;
            }

            // Giải nén phản hồi nếu được nén
            InputStream responseStream = response.body();
            InputStreamReader reader;
            if ("gzip".equals(response.headers().firstValue("Content-Encoding").orElse(""))) {
                // Nếu phản hồi được nén gzip, giải nén
                reader = new InputStreamReader(new GZIPInputStream(responseStream), StandardCharsets.UTF_8);
            } else {
                // Nếu không nén, đọc trực tiếp
                reader = new InputStreamReader(responseStream, StandardCharsets.UTF_8);
            }

            // Đọc dữ liệu từ phản hồi
            StringBuilder stringBuilder = new StringBuilder();
            try (BufferedReader bufferedReader = new BufferedReader(reader)) {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);
                }
            }

            // Chuẩn hóa văn bản sau khi nhận về
            String normalizedOutput = Normalizer.normalize(stringBuilder.toString(), Normalizer.Form.NFC);
            return normalizedOutput;

        } catch (Exception e) {
            e.printStackTrace();
            showErrorDialog("An error occurred while searching for the book: " + e.getMessage());
        }
        return null;
    }
    // Hiển thị hộp thoại lỗi
    public static void showErrorDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
