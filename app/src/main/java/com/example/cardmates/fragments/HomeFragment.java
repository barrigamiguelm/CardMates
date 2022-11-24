package com.example.cardmates.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.cardmates.model.User;
import com.example.cardmates.dagger.CardMatesApp;
import com.example.cardmates.R;
import com.example.cardmates.adapters.HomeRvAdapter;
import com.example.cardmates.interfaces.FirebaseInterface;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import javax.inject.Inject;

public class HomeFragment extends Fragment implements SearchView.OnQueryTextListener{

    @Inject
    FirebaseInterface firebaseInterface;

    private Button logOut;
    private Context context;
    private SearchView txtSearch;

    private Query query;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference ref = db.collection("Users");
    private RecyclerView recyclerView;
    private HomeRvAdapter recyclerViewAdapter;

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

        //logOut = (Button) view.findViewById(R.id.logOut);

        initialize(view);
        listUsers();

        setRecyclerViewAdapter(query);
        txtSearch.setOnQueryTextListener(this);

        /*logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseInterface.logOut();
                startActivity(new Intent(getActivity(), Login.class));
            }
        });*/


        return view;

    }

    private void initialize(View view){
        recyclerView = view.findViewById(R.id.recyclerView);
        txtSearch = view.findViewById(R.id.txtSearch);
    }

    private void listUsers(){
        query = ref;
    }

    private void setRecyclerViewAdapter(Query query) {
        FirestoreRecyclerOptions<User> options = new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(query, User.class)
                .build();

        recyclerViewAdapter = new HomeRvAdapter(options);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerViewAdapter.startListening();
    }

    private void search(String searchText){
        if (!searchText.isEmpty()){
            query = ref.whereEqualTo("Localidad", searchText);
        } else {
            query = ref;
        }

        setRecyclerViewAdapter(query);
    }

    @Override
    public void onStart(){
        super.onStart();
        recyclerViewAdapter.startListening();
    }

    @Override
    public void onStop(){
        super.onStop();
        recyclerViewAdapter.stopListening();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        Log.e("Search", newText);
        search(newText);
        return true;
    }

}