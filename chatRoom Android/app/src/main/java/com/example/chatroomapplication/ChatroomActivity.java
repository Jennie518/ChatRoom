package com.example.chatroomapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ChatroomActivity extends AppCompatActivity {
    private ArrayList<String> messages;
    private ArrayAdapter<String> adapter;
    private WebSocket ws;
    private String username;
    private String roomname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom);

        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        roomname = intent.getStringExtra("roomname");

        TextView roomNameTextView = findViewById(R.id.roomName);
        roomNameTextView.setText(roomname);
        EditText inputMessage = findViewById(R.id.inputMessage);
        ListView messagesListView = findViewById(R.id.messageList);
        Button sendButton = findViewById(R.id.sendButton);
        Button leaveButton = findViewById(R.id.leaveRoomButton);
        TextView timeText = findViewById(R.id.timeTextView);


        messages = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, messages);
        messagesListView.setAdapter(adapter);

        try {
            ws = new WebSocketFactory().createSocket("ws://10.0.2.2:8080", 5000);
            ws.addListener(new WebSocketAdapter() {
                @Override
                public void onTextMessage(WebSocket websocket, String message) throws Exception {
                    runOnUiThread(() -> {
                        try {
                            JSONObject data = new JSONObject(message);
                            String type = data.optString("type");
                            String user = data.optString("user");
                            String room = data.optString("room");
                            String msg = data.optString("message");
                            String time = getCurrentTime();

                            switch (type) {
                                case "join":
                                    messages.add(user + " joined the room " + room);
                                    break;
                                case "message":
                                    messages.add(user + ": " + msg);
                                    break;
                                case "leave":
                                    messages.add(user + " left the room " + room);
                                    break;
                            }
                            timeText.setText(time);

                            adapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    });
                }

                public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception {
                    Message joinMessage = new Message("join", username, roomname, null);
                    Gson gson = new Gson();
                    String jsonJoinMessage = gson.toJson(joinMessage);
                    ws.sendText(jsonJoinMessage);
                }

            });
            ws.connectAsynchronously();
        } catch (IOException e) {
            e.printStackTrace();
        }

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String messageText = inputMessage.getText().toString();
                if (!messageText.isEmpty()) {
                    Message message = new Message("message", username, roomname, messageText);
                    Gson gson = new Gson();
                    String jsonMessage = gson.toJson(message);

                    if (ws != null && ws.isOpen()) {
//                        Toast.makeText(ChatroomActivity.this, "msg sent", Toast.LENGTH_SHORT).show();

                        ws.sendText(jsonMessage);
                        inputMessage.setText("");  // 清空输入框
                    } else {
                        // 这里你可以处理WebSocket未连接的情况
                        Toast.makeText(ChatroomActivity.this, "Not connected", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        leaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String messageText = inputMessage.getText().toString();
                Message message = new Message("leave", username, roomname, messageText);
                Gson gson = new Gson();
                String jsonMessage = gson.toJson(message);
                if (ws != null && ws.isOpen()) {
//                        Toast.makeText(ChatroomActivity.this, "msg sent", Toast.LENGTH_SHORT).show();
                    ws.sendText(jsonMessage);
                    Intent intent = new Intent(ChatroomActivity.this,MainActivity.class);
                    startActivity(intent);
                } else {
                    // 这里你可以处理WebSocket未连接的情况
                    Toast.makeText(ChatroomActivity.this, "Not connected", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public static String getCurrentTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return dateFormat.format(new Date());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (ws != null && ws.isOpen()) {
            ws.disconnect();
        }
    }
}
