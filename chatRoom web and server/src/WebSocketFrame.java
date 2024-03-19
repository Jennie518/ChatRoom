
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;


public class WebSocketFrame {
    private boolean fin;
    private byte opcode;
    private boolean maskFlag;
    private byte[] maskingKey;
    private byte[] payloadData;

//read in we messages in binary
    public static WebSocketFrame readFromStream(InputStream in) throws IOException {
        WebSocketFrame frame = new WebSocketFrame();
        int byte1 = in.read();
        frame.fin = (byte1 & 0x80) != 0; //check fin to judge whether this is the last frame or not
        frame.opcode = (byte) (byte1 & 0x0F);

        int byte2 = in.read();
        frame.maskFlag = (byte2 & 0x80) != 0; //check whether mask has been masked 80== 1000 0000


        long payloadLength = byte2 & 0x7F;// 获取7位的负载长度 7F==0111 1111
        if (payloadLength == 126) {
            // 如果负载长度为126，则表示接下来的2个字节定义实际的负载长度
            payloadLength = ((in.read() & 0xFF) << 8) | (in.read() & 0xFF);
        } else if (payloadLength == 127) {
            // For this example, we are not handling payloads of length more than 2^31
            throw new IOException("Payload too large");
        }

        if (frame.maskFlag) {
            frame.maskingKey = new byte[4];
            in.read(frame.maskingKey);
        }

        frame.payloadData = new byte[(int) payloadLength];
        in.read(frame.payloadData);
        if (frame.maskFlag) {
            for (int i = 0; i < frame.payloadData.length; i++) {
                frame.payloadData[i] = (byte) (frame.payloadData[i] ^ frame.maskingKey[i % 4]);
                //unmask message
            }
        }
        return frame;
    }

public static WebSocketFrame createTextFrame(String message) {
    WebSocketFrame frame = new WebSocketFrame();
    frame.fin = true;
    frame.opcode = 0x01; // 文本帧的opcode是0x01
    frame.payloadData = message.getBytes();
    return frame;
}

    public boolean isTextFrame() {
        return opcode == 0x01;
    }
    public String getPayloadText() {
        return new String(payloadData);
    }

    public static void sendFrame(WebSocketFrame frame, OutputStream outputStream) throws IOException {
        //确定FIN标志的值。如果frame.fin为true，则使用值0x80；否则，使用0x00。
        outputStream.write((frame.fin ? 0x80 : 0x00) | frame.opcode);
        byte[] data = frame.payloadData;
        if (data.length <= 125) {
            outputStream.write(data.length);
        } else if (data.length <= 65535) {
            outputStream.write(126);
            outputStream.write((data.length >> 8) & 0xFF);
            outputStream.write(data.length & 0xFF);
        } else {
            // 注意: 这只是一个简单的实现，没有处理大于 2^31 的数据长度
            outputStream.write(127);
            outputStream.write(ByteBuffer.allocate(8).putLong(data.length).array());
        }

        outputStream.write(data);
        outputStream.flush();
    }
    public void writeToStream(OutputStream clientStream) throws IOException {
        sendFrame(this, clientStream);
    }
}
