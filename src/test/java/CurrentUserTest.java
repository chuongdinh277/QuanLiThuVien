import User.currentUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CurrentUserTest {

    @BeforeEach
    void setUp() {
        // Thiết lập các giá trị ban đầu cho currentUser trước mỗi bài kiểm tra
        currentUser.setId(1);
        currentUser.setUsername("testUser");
        currentUser.setPassword("password123");
        currentUser.setEmail("test@example.com");
        currentUser.setFullName("Test User");
        currentUser.setNumber("1234567890");
        currentUser.setRole("admin");
    }

    @Test
    void testGetId() {
        assertEquals(1, currentUser.getId());
    }

    @Test
    void testGetUsername() {
        assertEquals("testUser", currentUser.getUsername());
    }

    @Test
    void testGetEmail() {
        assertEquals("test@example.com", currentUser.getEmail());
    }

    @Test
    void testGetFullName() {
        assertEquals("Test User", currentUser.getFullName());
    }

    @Test
    void testGetNumber() {
        assertEquals("1234567890", currentUser.getNumber());
    }

    @Test
    void testGetRole() {
        assertEquals("admin", currentUser.getRole());
    }

    @Test
    void testSetUsername() {
        currentUser.setUsername("newUser");
        assertEquals("newUser", currentUser.getUsername());
    }

    @Test
    void testSetEmail() {
        currentUser.setEmail("new@example.com");
        assertEquals("new@example.com", currentUser.getEmail());
    }

    @Test
    void testLogout() {
        currentUser.logout();
        assertNull(currentUser.getUsername());
        assertNull(currentUser.getRole());
        assertNull(currentUser.getPassword());
    }
}