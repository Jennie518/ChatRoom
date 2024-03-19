import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class HTTPRequest {
    private final InputStream clientRequest;
    private final Map<String, String> headers = new HashMap<>();
    public String fileName;
    public String filePath;
    // 构造函数，用于初始化 HTTPRequest 对象，接收一个 InputStream 参数
    public HTTPRequest(InputStream clientRequest_) {
        this.clientRequest = clientRequest_;
        parseHeaders();
    }

    void parseHeaders() {
        Scanner sc = new Scanner(clientRequest);
        String fileLine = sc.nextLine();

        if(!fileLine.isEmpty())
        {
            String[] lines = fileLine.split(" ");
            if (!lines[1].isEmpty()) { // 如果数组的第二个元素不为空
                fileName = lines[1]; // 将文件名设置为该元素的值
            }
            if (fileName.equals("/")) {
                fileName = "/index.html";
            }
            filePath = "resources" + fileName;

        }
        System.out.println(fileLine);
        System.out.println(filePath);
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            if (line.isEmpty()) {
                break;
            }
            int colonIndex = line.indexOf(':');
            if (colonIndex != -1) {
                String headerName = line.substring(0, colonIndex).trim();
                //.trim()：这个方法调用用于删除字符串两端的空白字符（包括空格、制表符等）。
                // 这是为了确保即使头部的格式为“名称 : 值”，也能正确提取出名称和值。
                String headerValue = line.substring(colonIndex + 1).trim();
                headers.put(headerName, headerValue);
            }
        }
    }

    public boolean isWebSocketUpgrade() {
        // 在这里实现检查HTTP头部的逻辑，确定这是否是一个WebSocket升级请求
        // 返回true如果是WebSocket升级请求，否则返回false
        // ...
        return "websocket".equalsIgnoreCase(headers.get("Upgrade")) &&
                headers.containsKey("Sec-WebSocket-Key") &&
                "Upgrade".equalsIgnoreCase(headers.get("Connection"));

    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getFileName() {
        return this.fileName;
    }
}
