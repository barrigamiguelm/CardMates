package com.example.cardmates.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.example.cardmates.dagger.CardMatesApp;
import com.example.cardmates.databinding.ActivityChatDetail2Binding;
import com.example.cardmates.interfaces.FirebaseInterface;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

public class ChatUserDetailActivity extends AppCompatActivity {

    @Inject
    FirebaseInterface firebaseInterface;
    ActivityChatDetail2Binding binding;
    private String otherUserId;
    private Map<String, Object> userInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ((CardMatesApp) getApplicationContext()).getCardComponent().inject(this);
        super.onCreate(savedInstanceState);
        binding = ActivityChatDetail2Binding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        completar();
        setListeners();
    }

    private void setListeners() {
        binding.imageBack.setOnClickListener(view -> onBackPressed());
    }

    private void completar() {
        otherUserId = getIntent().getStringExtra("otherUserId");
        userInfo = firebaseInterface.getUserInfoWithId(otherUserId);
        binding.tvDesDetail.setText(String.valueOf(userInfo.get("Desc")));
        binding.tvEdadDetail.setText(String.valueOf(userInfo.get("Edad")));
        binding.tvNameDetail.setText(String.valueOf(userInfo.get("Nombre")));
        binding.tvLocalDetail.setText(String.valueOf(userInfo.get("Localidad")));
        binding.tvUserCardsDetail.setText(String.valueOf(userInfo.get("userLikes")));

        Glide.with(this)
                .load(userInfo.get("Imagen"))
                .into(binding.imgUserDetail);
    }

}