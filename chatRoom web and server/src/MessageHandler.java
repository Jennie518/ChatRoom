import org.json.JSONObject;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class MessageHandler {

    private Room room;
    private OutputStream clientStream;
    public MessageHandler(Room room, OutputStream clientStream) {
        this.room = room;
        this.clientStream = clientStream;
    }

    // 解析消息并执行相应的操作
    public void handleMessage(String messageContent) {
        Map<String, String> messageMap = parseMessage(messageContent);

        // 根据消息类型执行操作
        if (messageMap.containsKey("type")) {
            String messageType = messageMap.get("type");
            if ("join".equalsIgnoreCase(messageType)) {
                handleJoin(messageMap);
            }
            if("leave".equalsIgnoreCase(messageType)){
                handleLeave(messageMap);
            }
            if ("message".equalsIgnoreCase(messageType)) {
                handleMessage(messageMap);
            }
        }
    }

    private Map<String, String> parseMessage(String messageContent) {
        JSONObject json = new JSONObject(messageContent);
        Map<String, String> messageMap = new HashMap<>();
        for (String key : json.keySet()) {
            messageMap.put(key, json.getString(key));
        }
        return messageMap;
    }

    // 处理"join"消息
    private void handleJoin(Map<String, String> messageMap) {
        if (messageMap.containsKey("user") && messageMap.containsKey("room")) {
            String user = messageMap.get("user");
            String roomName = messageMap.get("room");
            // 根据消息内容查找或创建房间
            Room targetRoom = Room.joinRoom(roomName);
            // 将用户及其对应的OutputStream添加到目标房间的客户端映射中
            targetRoom.addClient(user, clientStream);
            // Broadcast join message
            targetRoom.broadcastMessage("join", user, roomName, ""); // No message content for join.
            System.out.println("Print clients in targetRoom: " + targetRoom.getClients());
        }
    }

    // 如果需要，可以添加其他的处理方法，如 handleLeave, handleMessage 等
    // 处理"leave"消息
    private void handleLeave(Map<String, String> messageMap) {
        if (messageMap.containsKey("user") && messageMap.containsKey("room")) {
            String user = messageMap.get("user");
            String roomName = messageMap.get("room");
            Room targetRoom = Room.joinRoom(roomName); // 通过名称获取房间
            targetRoom.removeClient(user,clientStream); // 从房间中移除客户端
            targetRoom.broadcastMessage("leave", user, roomName, "");
            System.out.println("Print clients in when someone leave " + targetRoom.getClients());
        }

    }
    private void handleMessage(Map<String, String> messageMap) {
        if (messageMap.containsKey("user") && messageMap.containsKey("room") && messageMap.containsKey("message")) {
            String user = messageMap.get("user");
            String roomName = messageMap.get("room");
            String message = messageMap.get("message");
            Room targetRoom = Room.joinRoom(roomName);
            targetRoom.broadcastMessage("message", user, roomName, message); // Add the room parameter and the message content to broadcast
        }
    }

}
