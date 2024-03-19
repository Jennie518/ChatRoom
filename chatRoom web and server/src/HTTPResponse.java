import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;


public class HTTPResponse {
    private OutputStream clientStream; // 用于向客户端发送数据的输出流

    private InputStream clientRequestStream;


    public HTTPResponse(OutputStream clientStream, InputStream clientRequestStream) {
        this.clientStream = clientStream;
        this.clientRequestStream = clientRequestStream;
    }
//    public HTTPResponse( OutputStream clientStream) {
//        this.clientStream = clientStream;
//    }
    public void sendTheExistingFile(String filePath) throws IOException {

        try {
            FileInputStream fileToRead = new FileInputStream(filePath); // 创建一个用于读取文件的FileInputStream
            clientStream.write("HTTP/1.1 200\n".getBytes()); // 发送HTTP响应头部信息，表示成功
            clientStream.write("\n".getBytes()); // 响应头部和内容之间的空行
            fileToRead.transferTo(clientStream);

            clientStream.flush(); // 刷新输出流
            clientStream.close(); // 关闭输出流
        } catch (FileNotFoundException fe) {
            System.out.println(fe.getMessage());
            String errorFilePath = "/Users/zhanyijun/Desktop/CS6011/Week1/Day5/untitled/resources/index.html";
            sendErrorFile(errorFilePath); // if file not found return error page
        } catch (IOException io) {
            io.printStackTrace();
            System.out.println(io.getMessage());
        }
    }

    // 发送错误文件的方法
    public void sendErrorFile(String errorFilePath) throws IOException {
        try {
            FileInputStream fileToRead = new FileInputStream(errorFilePath); // 创建一个用于读取错误文件的FileInputStream
            clientStream.write("HTTP/1.1 404\n".getBytes()); // 发送HTTP响应头部信息，表示文件未找到
            clientStream.write("Content-Type: text/html\n".getBytes()); // 指定响应内容类型为文本/html
            clientStream.write("\n".getBytes()); // 响应头部和内容之间的空行
            fileToRead.transferTo(clientStream); // transfer error content to client
            clientStream.flush(); // flush output string
            clientStream.close(); // 关闭输出流
        } catch (FileNotFoundException fe) {
            System.out.println(fe.getMessage());
        }
    }
    public void handleWebSocketHandshake(HTTPRequest request) throws IOException, NoSuchAlgorithmException {
        if (request.isWebSocketUpgrade()) {
            // 获取客户端的 Sec-WebSocket-Key
            String webSocketKey = request.getHeaders().get("Sec-WebSocket-Key");

            // 拼接上特定的GUID
            String acceptKey = webSocketKey + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";

            // 进行SHA-1哈希运算
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] hash = md.digest(acceptKey.getBytes("UTF-8"));

            // 进行Base64编码
            String encodedHash = Base64.getEncoder().encodeToString(hash);

            // 发送响应
            clientStream.write("HTTP/1.1 101 Switching Protocols\r\n".getBytes());
            clientStream.write("Upgrade: websocket\r\n".getBytes());
            clientStream.write("Connection: Upgrade\r\n".getBytes());
            clientStream.write(("Sec-WebSocket-Accept: " + encodedHash + "\r\n").getBytes());
            clientStream.write("\r\n".getBytes()); // 响应头部和内容之间的空行
            clientStream.flush();
            // 此时WebSocket连接已经建立，可以继续进行数据帧的发送和接收
            handleWebSocketFrames();
        }
    }
    public void handleWebSocketFrames() throws IOException {
        Room room = new Room(""); // 初始化为默认房间或根据需要设置
        MessageHandler messageHandler = new MessageHandler(room,clientStream);
        while (true) { // listen from client side
            // 从InputStream中读取并解码帧
            WebSocketFrame receivedFrame = WebSocketFrame.readFromStream(clientRequestStream);

            // 基于帧的内容执行相应的操作
            // 例如：如果它是一个文本帧，你可能想回应相同的消息
            if (receivedFrame.isTextFrame()) {
                String message = receivedFrame.getPayloadText();
                messageHandler.handleMessage(message);// 使用MessageHandler处理消息
//                WebSocketFrame responseFrame = WebSocketFrame.createTextFrame(message);
//                room.broadcastMessage(message);

            }
        }
    }

}
