package com.example.cardmates.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cardmates.ConversionListener;
import com.example.cardmates.databinding.ItemContainerRecentConversationBinding;
import com.example.cardmates.model.ChatMessage;

import java.util.List;

public class RecentConversationsAdapter extends RecyclerView.Adapter<RecentConversationsAdapter.ConversationViewHolder> {

    private final List<ChatMessage> chatMessages;
    Context context;
    ConversionListener conversionListener;

    //TODO: sale id en vez de nombre
    //todo: salen las conversaciones 2 veces
    public RecentConversationsAdapter(List<ChatMessage> chatMessages, Context context, ConversionListener conversionListener) {
        this.chatMessages = chatMessages;
        this.context = context;
        this.conversionListener = conversionListener;
    }

    @NonNull
    @Override
    public ConversationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ConversationViewHolder(
                ItemContainerRecentConversationBinding.inflate(
                        LayoutInflater.from(parent.getContext()),
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ConversationViewHolder holder, int position) {
        holder.setData(chatMessages.get(position));
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    class ConversationViewHolder extends RecyclerView.ViewHolder {

        ItemContainerRecentConversationBinding binding;

        ConversationViewHolder(ItemContainerRecentConversationBinding itemContainerRecentConversationBinding) {
            super(itemContainerRecentConversationBinding.getRoot());
            binding = itemContainerRecentConversationBinding;

        }


        void setData(ChatMessage chatMessage) {
            binding.textRecentMessage.setText("ultimo mensaje: "+chatMessage.message);
            binding.textName.setText(chatMessage.conversionName);
        }

    }

}
