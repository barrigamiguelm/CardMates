package com.example.cardmates.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.paging.LoadState;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.cardmates.activities.LoadingDialog;
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

import java.util.Locale;

import javax.inject.Inject;

public class HomeFragment extends Fragment implements SearchView.OnQueryTextListener {

    @Inject
    FirebaseInterface firebaseInterface;


    private Context context;
    private SearchView txtSearch;
    private Spinner spinner;

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
        initialize(view);

        query = ref.whereNotEqualTo("Localidad" , "Sin localidad");

        setRecyclerViewAdapter(query);
        setSpinner();

        txtSearch.setOnQueryTextListener(this);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));

        setSpinnerListener();

        return view;
    }

    private void setSpinnerListener() {
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (spinner.getSelectedItemPosition() == 0) {
                    query = ref.whereNotEqualTo("Localidad", "Sin localidad");
                    setRecyclerViewAdapter(query);
                } else {
                    String spinnerText = spinner.getSelectedItem().toString();
                    query = ref.whereArrayContains("userLikes", spinnerText.toLowerCase());
                    setRecyclerViewAdapter(query);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }


    private void initialize(View view) {
        recyclerView = view.findViewById(R.id.recyclerView);
        txtSearch = view.findViewById(R.id.txtSearch);
        spinner = view.findViewById(R.id.spinner);
    }


    private void setRecyclerViewAdapter(Query query) {
        FirestoreRecyclerOptions<User> options = new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(query, User.class)
                .build();

        recyclerViewAdapter = new HomeRvAdapter(options, getActivity());
        recyclerView.setLayoutManager(new HomeRvAdapter.WrapContentLinearLayoutManager(getActivity()));
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerViewAdapter.startListening();
    }
    
    private void search(String searchText) {
        if (!searchText.isEmpty()) {
            query = ref.whereEqualTo("Localidad", searchText).whereNotEqualTo("Localidad", "Sin localidad");
        } else {
            query = ref.whereNotEqualTo("Localidad", "Sin localidad");
        }

        setRecyclerViewAdapter(query);
    }

    private void setSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.gustos, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinner.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        recyclerViewAdapter.startListening();
    }

    @Override
    public void onStop() {
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