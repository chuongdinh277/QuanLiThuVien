package GoogleAPI;

import APIGoogle.GoogleBooksAPI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class GoogleBooksAPITest {

    private HttpClient httpClient;

    @BeforeEach
    public void setUp() {
        httpClient = mock(HttpClient.class);
    }

    @Test
    void testSearchBookByTitleAndAuthor() throws Exception {
        String title = "The Great Gatsby";
        String author = "F. Scott Fitzgerald";
        String expectedResponse = "{ \"items\": [{ \"volumeInfo\": { \"title\": \"The Great Gatsby\", \"authors\": [\"F. Scott Fitzgerald\"], \"imageLinks\": { \"thumbnail\": \"http://example.com/image.jpg\" } } }] }";

        // Mock the HttpResponse
        HttpResponse<InputStream> mockResponse = mock(HttpResponse.class);
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn(new ByteArrayInputStream(expectedResponse.getBytes()));

        // Mock the HttpClient
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockResponse);

        // Spy on GoogleBooksAPI
        GoogleBooksAPI api = spy(new GoogleBooksAPI());
        doReturn(httpClient).when(api).getHttpClient();  // Stubbing the getHttpClient method

        // Act
        String result = api.searchBookByTitleAndAuthor(title, author);

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("The Great Gatsby"));
    }
}
