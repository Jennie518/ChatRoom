import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;

public class ConnectionHandler implements Runnable {
    private final Socket client;
    ConnectionHandler(Socket client){
        this.client = client;
    }

    @Override
    public void run() {
        try {
            // 获取客户端的输入输出流
            InputStream clientRequestStream = client.getInputStream();
            OutputStream clientResponseStream = client.getOutputStream();

            // 创建HTTPRequest对象，传入输入流进行解析
            HTTPRequest httpRequest = new HTTPRequest(clientRequestStream);
//            httpRequest.parseHeaders();  // 解析HTTP头部

            HTTPResponse httpResponse = new HTTPResponse(clientResponseStream,clientRequestStream);
            // 根据是否为WebSocket升级请求来执行不同的逻辑
            if (httpRequest.isWebSocketUpgrade()) {
                System.out.println("This is a web socket request!");
                // 如果是WebSocket升级请求，则执行WebSocket握手
                httpResponse.handleWebSocketHandshake(httpRequest);
                while(!Thread.currentThread().isInterrupted()) {
                    if (clientRequestStream.available() > 0) {
                        WebSocketFrame frame = WebSocketFrame.readFromStream(clientRequestStream);
                        if (frame.isTextFrame()) {
                            String receivedText = frame.getPayloadText();
                            System.out.println("Received text frame: " + receivedText);
                            String responseText = "Server received: " + receivedText;
                            WebSocketFrame responseFrame = WebSocketFrame.createTextFrame(responseText);
                            responseFrame.writeToStream(clientResponseStream);
                        } else {
                            WebSocketFrame.sendFrame(frame, clientResponseStream);
                        }
                    } else {
                        Thread.sleep(50);  // Pause for a short duration before checking again
                    }
                }
            } else {
                System.out.println("This is a HTTP request: " + httpRequest.filePath);
                httpResponse.sendTheExistingFile(httpRequest.filePath);
            }
        } catch (IOException | NoSuchAlgorithmException | InterruptedException e) {
            System.err.println("Error handling connection: " + e.getMessage());
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                System.err.println("Error closing socket: " + e.getMessage());
            }
        }
    }
}