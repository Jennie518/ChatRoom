import org.junit.jupiter.api.Test;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
w
class HTTPRequestTest {

    @Test
    public void testGetTheFileName() throws IOException {
        String httpRequest = "GET /test.html HTTP/1.1\r\n" +
                "Host: localhost:8080\r\n" +
                "Connection: keep-alive\r\n\r\n";
        ByteArrayInputStream input = new ByteArrayInputStream(httpRequest.getBytes());
        HTTPRequest request = new HTTPRequest(input);
        assertEquals("/test.html", request.getFileName());
    }

    @Test
    public void testIsWebSocketUpgrade() throws IOException {
        String httpRequest = "GET / HTTP/1.1\r\n" +
                "Host: localhost:8080\r\n" +
                "Upgrade: websocket\r\n" +
                "Connection: Upgrade\r\n" +
                "Sec-WebSocket-Key: dGhlIHNhbXBsZSBub25jZQ==\r\n\r\n";
        ByteArrayInputStream input = new ByteArrayInputStream(httpRequest.getBytes());
        HTTPRequest request = new HTTPRequest(input);
        assertTrue(request.isWebSocketUpgrade());
    }
}
