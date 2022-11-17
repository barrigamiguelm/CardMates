package com.example.cardmates.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Button;

import com.example.cardmates.Activities.Login;
import com.example.cardmates.Activities.model.Cards;
import com.example.cardmates.Activities.model.User;
import com.example.cardmates.Dagger.CardMatesApp;
import com.example.cardmates.R;
import com.example.cardmates.adapters.HomeRvAdapter;
import com.example.cardmates.interfaces.FirebaseInterface;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

import javax.inject.Inject;

public class HomeFragment extends Fragment {

    @Inject
    FirebaseInterface firebaseInterface;

    private Button logOut;
    private Context context;

    private FirebaseFirestore db;


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
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);

        db = firebaseInterface.getDatabase();

        Query query = db.collection("Users");
        FirestoreRecyclerOptions<User> options = new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(query, User.class)
                .build();

        HomeRvAdapter recyclerViewAdapter = new HomeRvAdapter(options);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(recyclerViewAdapter);

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