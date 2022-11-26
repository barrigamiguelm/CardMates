package com.example.cardmates;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

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

import javax.inject.Inject;

public class ChatActivity2 extends AppCompatActivity {

    private ActivityChat2Binding binding;
    private String otherUserId, reciverName, userId, userName;
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


    //TODO: username es el mismo en los dos
    private void sendMessage() {
        HashMap<String, Object> message = new HashMap<>();
        message.put(Constants.KEY_SENDER_ID, userId);
        message.put(Constants.KEY_RECIEVER_ID, otherUserId);
        message.put(Constants.KEY_MESSAGE, binding.inputMessage.getText().toString());
        message.put(Constants.KEY_TIMESTAMP, new Date());
        database.collection(Constants.KEY_COLLECION_CHAT).add(message);
        if (conversionID != null) {
            updateconversion(binding.inputMessage.getText().toString());
        } else {
            HashMap<String, Object> conversion = new HashMap<>();
            conversion.put(Constants.KEY_SENDER_ID, userId);
            conversion.put(Constants.KEY_SENDER_NAME, firebaseInterface.provideUserInfo().get("Nombre").toString());
            conversion.put(Constants.KEY_RECIEVER_ID, otherUserId);
            conversion.put(Constants.KEY_RECEIVER_NAME, reciverName);
            conversion.put(Constants.KEY_LAST_MESSAGE, binding.inputMessage.getText().toString());
            conversion.put(Constants.KEY_TIMESTAMP, new Date());
            addConversion(conversion);
        }
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
                    chatMessage.datetime = getReadableDateTime(documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP));
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
        reciverName = getIntent().getStringExtra(Constants.KEY_USER_NAME);

        binding.textName.setText(reciverName);
    }

    public void setListeners() {
        binding.layoutSend.setOnClickListener(v -> sendMessage());
    }

    private String getReadableDateTime(Date date) {
        return new SimpleDateFormat("dd MMMM, yyyy - hh:mm a", Locale.getDefault()).format(date);
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


