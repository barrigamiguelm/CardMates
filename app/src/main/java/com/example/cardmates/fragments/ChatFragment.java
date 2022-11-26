package com.example.cardmates.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.cardmates.ChatActivity2;
import com.example.cardmates.ConversionListener;
import com.example.cardmates.R;
import com.example.cardmates.adapters.RecentConversationsAdapter;
import com.example.cardmates.databinding.FragmentChatBinding;
import com.example.cardmates.model.ChatMessage;
import com.example.cardmates.model.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.units.qual.C;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChatFragment extends Fragment implements ConversionListener {

    private FragmentChatBinding binding;
    private List<ChatMessage> conversations;
    private RecentConversationsAdapter conversationsAdapter;
    private FirebaseFirestore database;
    private String user_id;


    public ChatFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentChatBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = FragmentChatBinding.bind(view);

        conversations = new ArrayList<>();
        conversationsAdapter = new RecentConversationsAdapter(conversations,getActivity(),this);
        binding.recyclerRecentChats.setHasFixedSize(true);
        binding.recyclerRecentChats.setAdapter(conversationsAdapter);
        binding.recyclerRecentChats.setLayoutManager(new LinearLayoutManager(getActivity()));

        database = FirebaseFirestore.getInstance();
        user_id = FirebaseAuth.getInstance().getUid();

        listenConversations();

    }


    private void listenConversations() {
        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .whereEqualTo(Constants.KEY_SENDER_ID, user_id)
                .addSnapshotListener(eventListener);
        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .whereEqualTo(Constants.KEY_RECIEVER_ID, user_id)
                .addSnapshotListener(eventListener);
    }

    private final EventListener<QuerySnapshot> eventListener = (value, error) -> {
        if (error != null) {
            return;
        }
        if (value != null) {
            for (DocumentChange documentChange : value.getDocumentChanges()) {
                if (documentChange.getType() == DocumentChange.Type.ADDED) {
                    String sender_Id = documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                    String redeiver_Id = documentChange.getDocument().getString(Constants.KEY_RECIEVER_ID);
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.sender_id = sender_Id;
                    chatMessage.reciever_id = redeiver_Id;
                    if (user_id.equals(sender_Id)) {
                        chatMessage.conversionName = documentChange.getDocument().getString(Constants.KEY_RECEIVER_NAME);
                        chatMessage.conversionId = documentChange.getDocument().getString(Constants.KEY_RECIEVER_ID);
                    } else {
                        chatMessage.conversionName = documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                        chatMessage.conversionId = documentChange.getDocument().getString(Constants.KEY_SENDER_NAME);
                    }
                    chatMessage.message = documentChange.getDocument().getString(Constants.KEY_LAST_MESSAGE);
                    chatMessage.dateObject = documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP);
                    conversations.add(chatMessage);
                } else if (documentChange.getType() == DocumentChange.Type.MODIFIED) {
                    for (int i = 0; i < conversations.size(); i++) {
                        String sender_id = documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                        String receiver_id = documentChange.getDocument().getString(Constants.KEY_RECIEVER_ID);
                        if (conversations.get(i).sender_id.equals(sender_id) && conversations.get(i).reciever_id.equals(receiver_id)) {
                            conversations.get(i).message = documentChange.getDocument().getString(Constants.KEY_LAST_MESSAGE);
                            conversations.get(i).dateObject = documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP);
                            break;
                        }
                    }
                }
            }
            conversations.sort((obj1, obj2) -> obj2.dateObject.compareTo(obj1.dateObject));
            conversationsAdapter.notifyDataSetChanged();
            binding.recyclerRecentChats.setAdapter(conversationsAdapter);
            binding.recyclerRecentChats.setVisibility(View.VISIBLE);
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onConversionClicked(String sender_id, String sender_name) {
        Intent intent =new Intent(getActivity(), ChatActivity2.class);
        intent.putExtra("sender_id", sender_id);

        startActivity(intent);
    }
}