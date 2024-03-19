import org.json.JSONObject;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Room {
    static ArrayList<Room> rooms = new ArrayList<>();
private Map<String, OutputStream> clientMap = new HashMap<>(); // 存储用户名及其对应的OutputStream
    private String roomName;


    public Room(String roomName) {
        this.roomName = roomName;
        this.clientMap = new HashMap<>();
    }

    public String getRoomName() {
        return roomName;
    }

    public static Room joinRoom(String roomName) {
        for (Room room : rooms) {
            if (room.getRoomName().equals(roomName)) {
                // 如果找到匹配的房间名，返回该房间
                return room;
            }
        }
        // 如果没有找到匹配的房间，创建一个新的并添加到rooms列表
        Room newRoom = new Room(roomName);
        rooms.add(newRoom);
        return newRoom;
    }

    public synchronized void addClient(String clientName, OutputStream clientStream) {
        clientMap.put(clientName, clientStream);
        // Send the list of current users in the room to the new client
        broadcastUpdatedUserList();
    }

    public ArrayList<String> getClients() {
        return new ArrayList<>(clientMap.keySet());
    }
    public synchronized void removeClient(String clientName,OutputStream clientStream) {
        clientMap.remove(clientName);
        broadcastUpdatedUserList();
    }
    public synchronized void broadcastMessage(String type, String user, String room, String message) {
        JSONObject jsonMessageObject = new JSONObject();

        jsonMessageObject.put("type", type);
        jsonMessageObject.put("user", user);
        jsonMessageObject.put("room", room);

        if ("message".equals(type)) {
            jsonMessageObject.put("message", message);
        }

        String jsonMessage = jsonMessageObject.toString();

        // Create a WebSocketFrame for the message
        WebSocketFrame frame = WebSocketFrame.createTextFrame(jsonMessage);

        // Send the frame to all clients in the current room
        for (OutputStream clientStream : clientMap.values()) {
            try {
                frame.writeToStream(clientStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void broadcastUpdatedUserList() {
        ArrayList<String> currentUsers = getClients();
        JSONObject userListObject = new JSONObject();
        userListObject.put("type", "userList");
        userListObject.put("users", currentUsers);
        String userListMessage = userListObject.toString();
        WebSocketFrame frame = WebSocketFrame.createTextFrame(userListMessage);
        for (OutputStream clientStream : clientMap.values()) {
            try {
                frame.writeToStream(clientStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }




}
