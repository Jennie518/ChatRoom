package com.example.chatroomapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Button buttonLogin = findViewById(R.id.loginButton);
        final EditText editTextUsername = findViewById(R.id.userName);
        final EditText editTextRoomname = findViewById(R.id.roomName);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = editTextUsername.getText().toString();
                String roomname = editTextRoomname.getText().toString();
                if (!username.isEmpty() && !roomname.isEmpty()) {
                    Intent intent = new Intent(MainActivity.this, ChatroomActivity.class);
                    intent.putExtra("username", username);
                    intent.putExtra("roomname", roomname);
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "Please Enter userName and Room Name", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}