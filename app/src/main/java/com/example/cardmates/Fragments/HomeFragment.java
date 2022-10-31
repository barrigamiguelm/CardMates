package com.example.cardmates.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.cardmates.Activities.Login;
import com.example.cardmates.Dagger.CardMatesApp;
import com.example.cardmates.R;
import com.example.cardmates.interfaces.FirebaseInterface;

import javax.inject.Inject;

public class HomeFragment extends Fragment {

    @Inject
    FirebaseInterface firebaseInterface;

    private Button logOut;


    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((CardMatesApp) getActivity().getApplicationContext()).getCardComponent().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_home,
                container, false);

        logOut = (Button) view.findViewById(R.id.logOut);


        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseInterface.logOut();
                startActivity(new Intent(getActivity(), Login.class));
            }
        });

        return view;

    }

}