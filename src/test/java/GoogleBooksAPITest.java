import APIGoogle.GoogleBooksAPI;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GoogleBooksAPITest {

    @Test
    void testSearchBooksByTitle() throws Exception {
        String title = "The Great Gatsby";
        String expectedResponse = "{ \"items\": [{ \"volumeInfo\": { \"title\": \"The Great Gatsby\", \"authors\": [\"F. Scott Fitzgerald\"], \"imageLinks\": { \"thumbnail\": \"http://example.com/image.jpg\" } } }] } }";

        // Giả lập HttpClient và HttpResponse
        HttpResponse<InputStream> mockResponse = mock(HttpResponse.class);
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn(new ByteArrayInputStream(expectedResponse.getBytes()));

        // Giả lập việc gọi API
        GoogleBooksAPI api = Mockito.spy(new GoogleBooksAPI());
        doReturn(mockResponse).when(api).getHttpClient().send(any(), any());

        String result = api.searchBooksByTitle(title);
        assertNotNull(result);
        assertTrue(result.contains("The Great Gatsby"));
    }

    @Test
    void testSearchBooksByTitle_Error() throws Exception {
        String title = "Unknown Book";

        // Giả lập HttpResponse với mã lỗi
        HttpResponse<InputStream> mockResponse = mock(HttpResponse.class);
        when(mockResponse.statusCode()).thenReturn(404);

        // Giả lập việc gọi API
        GoogleBooksAPI api = Mockito.spy(new GoogleBooksAPI());
        doReturn(mockResponse).when(api).getHttpClient().send(any(), any());

        String result = api.searchBooksByTitle(title);
        assertNull(result); // Kết quả phải là null khi có lỗi
    }

    @Test
    void testSearchBookByTitleAndAuthor() throws Exception {
        String title = "The Great Gatsby";
        String author = "F. Scott Fitzgerald";
        String expectedResponse = "{ \"items\": [{ \"volumeInfo\": { \"title\": \"The Great Gatsby\", \"authors\": [\"F. Scott Fitzgerald\"], \"imageLinks\": { \"thumbnail\": \"http://example.com/image.jpg\" } } }] } }";

        // Giả lập HttpResponse
        HttpResponse<InputStream> mockResponse = mock(HttpResponse.class);
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn(new ByteArrayInputStream(expectedResponse.getBytes()));

        // Giả lập việc gọi API
        GoogleBooksAPI api = Mockito.spy(new GoogleBooksAPI());
        doReturn(mockResponse).when(api).getHttpClient().send(any(), any());

        String result = api.searchBookByTitleAndAuthor(title, author);
        assertNotNull(result);
        assertTrue(result.contains("The Great Gatsby"));
    }

    @Test
    void testSearchBookByTitleAndAuthor_Error() throws Exception {
        String title = "Unknown Book";
        String author = "Unknown Author";

        // Giả lập HttpResponse với mã lỗi
        HttpResponse<InputStream> mockResponse = mock(HttpResponse.class);
        when(mockResponse.statusCode()).thenReturn(404);

        // Giả lập việc gọi API
        GoogleBooksAPI api = Mockito.spy(new GoogleBooksAPI());
        doReturn(mockResponse).when(api).getHttpClient().send(any(), any());

        String result = api.searchBookByTitleAndAuthor(title, author);
        assertNull(result); // Kết quả phải là null khi có lỗi
    }
}