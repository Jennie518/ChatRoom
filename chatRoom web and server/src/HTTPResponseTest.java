//import org.junit.jupiter.api.Test;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//import static org.mockito.Mockito.*;
//
//import java.io.ByteArrayOutputStream;
//import java.io.IOException;
//import java.nio.charset.StandardCharsets;
//import java.security.MessageDigest;
//import java.security.NoSuchAlgorithmException;
//import java.util.Base64;
//
//class HTTPResponseTest {
//    @Test
//    public void testHandleWebSocketHandshake() throws IOException, NoSuchAlgorithmException, NoSuchAlgorithmException {
//        // 创建模拟的请求和输出流
//        HTTPRequest mockRequest = mock(HTTPRequest.class);
//        when(mockRequest.isWebSocketUpgrade()).thenReturn(true);
//        when(mockRequest.getHeaders()).thenReturn(Map.of("Sec-WebSocket-Key", "testKey"));
//
//        ByteArrayOutputStream mockOutputStream = new ByteArrayOutputStream();
//
//        // 创建 HTTPResponse 实例
//        HTTPResponse response = new HTTPResponse(mockOutputStream);
//
//        // 执行握手方法
//        response.handleWebSocketHandshake(mockRequest);
//
//        // 将响应转换为字符串以便于断言
//        String responseString = mockOutputStream.toString();
//
//        // 构建预期的Sec-WebSocket-Accept值
//        String acceptKey = "testKey" + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
//        MessageDigest md = MessageDigest.getInstance("SHA-1");
//        byte[] hash = md.digest(acceptKey.getBytes(StandardCharsets.UTF_8));
//        String encodedHash = Base64.getEncoder().encodeToString(hash);
//
//        // 检查输出流是否包含正确的握手响应
//        assertTrue(responseString.contains("HTTP/1.1 101 Switching Protocols\r\n"));
//        assertTrue(responseString.contains("Upgrade: websocket\r\n"));
//        assertTrue(responseString.contains("Connection: Upgrade\r\n"));
//        assertTrue(responseString.contains("Sec-WebSocket-Accept: " + encodedHash + "\r\n"));
//    }
//
////    @Test
////    void testSendTheExistingFile() throws IOException {
////        // 创建模拟的文件内容和相应的 ByteArrayInputStream
////        String fileContent = "<html><body>Test File</body></html>";
////        ByteArrayInputStream fileInputStream = new ByteArrayInputStream(fileContent.getBytes());
////
////        // 创建模拟的客户端输出流
////        ByteArrayOutputStream clientStream = new ByteArrayOutputStream();
////
////        // 替换 FileInputStream 的创建过程
////        HTTPResponse response = new HTTPResponse(clientStream, null) {
////            @Override
////            FileInputStream getFileInputStream(String filePath) {
////                return new FileInputStream(fileInputStream);
////            }
////        };
////
////        // 指定一个不存在的文件路径，因为它会被我们的 getFileInputStream 方法替代
////        response.sendTheExistingFile("does_not_exist.html");
////
////        // 将输出流转换为字符串，并验证是否包含正确的HTTP状态码和文件内容
////        String responseContent = clientStream.toString();
////        assertTrue(responseContent.contains("HTTP/1.1 200"));
////        assertTrue(responseContent.contains(fileContent));
////    }
////}
