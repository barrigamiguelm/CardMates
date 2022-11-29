package com.example.cardmates;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.cardmates.databinding.ActivityChatDetailBinding;
import com.example.cardmates.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class ChatDetailActivity extends AppCompatActivity {

    private User recieverUser;
    private ActivityChatDetailBinding binding;
    private FirebaseDatabase database;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityChatDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        final String senderId = auth.getUid();
        String recieveId = getIntent().getStringExtra("userId");
        String userName = getIntent().getStringExtra("userName");



    }

    private void loadReciverDetails(){
        recieverUser = (User) getIntent().getSerializableExtra("");
    }
}