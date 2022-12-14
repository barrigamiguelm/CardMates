package com.example.cardmates.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.cardmates.activities.ChatUserDetailActivity;
import com.example.cardmates.adapters.ChatAdapter2;
import com.example.cardmates.dagger.CardMatesApp;
import com.example.cardmates.databinding.ActivityChat2Binding;
import com.example.cardmates.interfaces.FirebaseInterface;
import com.example.cardmates.model.ChatMessage;
import com.example.cardmates.model.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;

public class ChatActivity2 extends AppCompatActivity {

    private ActivityChat2Binding binding;
    private String otherUserId, reciverName, userId, image_reciever;
    private ImageView imageView;
    private List<ChatMessage> chatMessages;
    private ChatAdapter2 chatAdapter2;
    private FirebaseFirestore database;
    private FirebaseAuth mAuth;
    private String conversionID = null;

    @Inject
    FirebaseInterface firebaseInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ((CardMatesApp) getApplicationContext()).getCardComponent().inject(this);
        super.onCreate(savedInstanceState);
        binding = ActivityChat2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListeners();
        loadRecieverDetails();
        init();
        listenMessages();
    }


    private void init() {
        userId = FirebaseAuth.getInstance().getUid();
        chatMessages = new ArrayList<>();
        chatAdapter2 = new ChatAdapter2(
                chatMessages,
                userId
        );
        binding.chatRecyclerView.setAdapter(chatAdapter2);
        database = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
    }


    private void sendMessage() {
        HashMap<String, Object> message = new HashMap<>();
        message.put(Constants.KEY_SENDER_ID, userId);
        message.put(Constants.KEY_RECIEVER_ID, otherUserId);
        message.put(Constants.KEY_MESSAGE, binding.inputMessage.getText().toString());
        message.put(Constants.KEY_TIMESTAMP, new Date());
        database.collection(Constants.KEY_COLLECION_CHAT).add(message);
       
        binding.inputMessage.setText(" ");
    }

    private void listenMessages() {
        database.collection(Constants.KEY_COLLECION_CHAT)
                .whereEqualTo(Constants.KEY_SENDER_ID, userId)
                .whereEqualTo(Constants.KEY_RECIEVER_ID, otherUserId)
                .addSnapshotListener(eventListener);

        database.collection(Constants.KEY_COLLECION_CHAT)
                .whereEqualTo(Constants.KEY_SENDER_ID, otherUserId)
                .whereEqualTo(Constants.KEY_RECIEVER_ID, userId)
                .addSnapshotListener(eventListener);
    }


    private final EventListener<QuerySnapshot> eventListener = (value, error) -> {
        if (error != null) {
            return;
        }
        if (value != null) {
            int count = chatMessages.size();
            for (DocumentChange documentChange : value.getDocumentChanges()) {
                if (documentChange.getType() == DocumentChange.Type.ADDED) {
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.sender_id = documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                    chatMessage.reciever_id = documentChange.getDocument().getString(Constants.KEY_RECIEVER_ID);
                    chatMessage.message = documentChange.getDocument().getString(Constants.KEY_MESSAGE);
                    chatMessage.dateObject = documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP);
                    chatMessages.add(chatMessage);
                }

            }
            chatMessages.sort(Comparator.comparing(obj -> obj.dateObject));

            if (count == 0) {
                chatAdapter2.notifyDataSetChanged();
            } else {
                chatAdapter2.notifyItemRangeInserted(chatMessages.size(), chatMessages.size());
                binding.chatRecyclerView.smoothScrollToPosition(chatMessages.size() - 1);
                binding.chatRecyclerView.setAdapter(chatAdapter2);
            }
            binding.chatRecyclerView.setVisibility(View.VISIBLE);
            if (conversionID == null) {
                checkForConversion();
            }
        }
    };

    private void loadRecieverDetails() {
        otherUserId = getIntent().getStringExtra(Constants.KEY_OTHER_USER_ID);
        if (otherUserId.equals(firebaseInterface.getUserID())){
            onBackPressed();
            Toast.makeText(this, "No te hables a ti mismo, pillin :)", Toast.LENGTH_SHORT).show();
        }
        binding.textName.setText(getIntent().getStringExtra(Constants.KEY_USER_NAME));
        binding.imageUser.setImageBitmap(getUserImage(getIntent().getStringExtra(Constants.KEY_RECIEVER_IMG)));
    }

    public void setListeners() {
        binding.layoutSend.setOnClickListener(v -> sendMessage());
        binding.imageBack.setOnClickListener(view -> onBackPressed());
        binding.imageUser.setOnClickListener(view -> openUserProfileDetail());
    }

    private void openUserProfileDetail() {
        Intent intent = new Intent(this, ChatUserDetailActivity.class);
        intent.putExtra("otherUserId", otherUserId);
        startActivity(intent);
    }

    private void addConversion(HashMap<String, Object> conversion) {
        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .add(conversion)
                .addOnSuccessListener(documentReference -> conversionID = documentReference.getId());
    }

    private void updateconversion(String message) {
        DocumentReference documentReference = database.collection(Constants.KEY_COLLECTION_CONVERSATIONS).document(conversionID);
        documentReference.update(
                Constants.KEY_LAST_MESSAGE, message,
                Constants.KEY_TIMESTAMP, new Date()
        );

    }


    private void checkForConversion() {
        if (chatMessages.size() != 0) {
            checkForConversionRemotely(
                    userId,
                    otherUserId
            );
            checkForConversionRemotely(
                    otherUserId,
                    userId
            );
        }
    }

    private Bitmap getUserImage(String encodedImage) {
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    private void checkForConversionRemotely(String senderId, String receiverId) {
        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .whereEqualTo(Constants.KEY_SENDER_ID, senderId)
                .whereEqualTo(Constants.KEY_RECIEVER_ID, receiverId)
                .get()
                .addOnCompleteListener(conversionCompleteListener);
    }


    private final OnCompleteListener<QuerySnapshot> conversionCompleteListener = task -> {
        if (task.isSuccessful() && task.getResult() != null && task.getResult().getDocuments().size() > 0) {
            DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
            conversionID = documentSnapshot.getId();
        }
    };
}


