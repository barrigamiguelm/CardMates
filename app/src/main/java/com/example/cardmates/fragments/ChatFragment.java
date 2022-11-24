package com.example.cardmates.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.cardmates.R;
import com.example.cardmates.databinding.FragmentChatBinding;
import com.example.cardmates.model.User;

public class ChatFragment extends Fragment {

    private FragmentChatBinding binding;
    private User reciverUser;

    public ChatFragment() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentChatBinding.inflate(getLayoutInflater());



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_chat, container, false);
    }
}