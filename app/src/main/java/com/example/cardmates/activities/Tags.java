package com.example.cardmates.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.cardmates.model.Cards;
import com.example.cardmates.dagger.CardMatesApp;
import com.example.cardmates.MainActivity;
import com.example.cardmates.R;
import com.example.cardmates.interfaces.FirebaseInterface;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;


public class Tags extends AppCompatActivity {

    @Inject
    FirebaseInterface firebaseInterface;


    private RecyclerView recyclerTags;
    private Context context;
    private Button checkList;
    private String userID;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private RecyclerView recyclerView;
    private FirestoreRecyclerAdapter adapter;


    ArrayList<String> userLikesArray = new ArrayList<>();
    Map<String, ArrayList> userLikes = new HashMap<>();
    Map<String, Map> cardUser = new HashMap<>();
    Map<String, Object> userTags = new HashMap<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ((CardMatesApp) getApplicationContext()).getCardComponent().inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tags);

        StringBuilder stringBuilder = new StringBuilder();

        recyclerView = (RecyclerView) findViewById(R.id.recyclerTags);
        checkList = (Button) findViewById(R.id.checkList);

        userID = firebaseInterface.getUserID();
        db = firebaseInterface.getDatabase();
        context = getApplicationContext();

        database = FirebaseDatabase.getInstance("https://cardmates-8e17e-default-rtdb.europe-west1.firebasedatabase.app/");
        myRef = database.getReference("Cards");

        DocumentReference documentReferenceUser = db.collection("Users").document(userID);


        Query query = db.collection("Cards");
        FirestoreRecyclerOptions<Cards> options = new FirestoreRecyclerOptions.Builder<Cards>()
                .setQuery(query, Cards.class)
                .build();

        checkList.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                for (Map.Entry<String, Map> entry : cardUser.entrySet()) {
                    String key = entry.getKey();
                    DatabaseReference updateRef = myRef.child(key);
                    updateRef.updateChildren(userTags);
                    userLikesArray.add(key);
                }

                userLikes.put("userLikes", userLikesArray);
                documentReferenceUser.set(userLikes, SetOptions.merge());
                startActivity(new Intent(Tags.this, MainActivity.class));
            }


        });

        adapter = new FirestoreRecyclerAdapter<Cards, CardsViewHolder>(options) {

            @NonNull
            @Override
            public CardsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_row, parent, false);
                return new CardsViewHolder(view);

            }

            @Override
            protected void onBindViewHolder(@NonNull CardsViewHolder holder, int position, @NonNull Cards model) {

                holder.title.setText(model.getTitle());

                Glide.with(context)
                        .load(model.getImage())
                        .into(holder.img);

                holder.img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (model.isChecked() == false) {
                            model.setChecked(true);
                            holder.tick.setVisibility(View.VISIBLE);
                            userTags.put(userID, true);
                            cardUser.put(model.getTagTitle(), userTags);

                        } else if (model.isChecked() == true) {
                            model.setChecked(false);
                            holder.tick.setVisibility(View.GONE);
                            userTags.remove(userID);
                            cardUser.remove(model.getTagTitle());
                        }
                    }
                });
            }

        };

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(adapter);


    }


    private class CardsViewHolder extends RecyclerView.ViewHolder {

        private TextView title;
        private ImageView img;
        private ImageView tick;

        public CardsViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.cardTxt);
            img = itemView.findViewById(R.id.cardImg);
            tick = itemView.findViewById(R.id.tick);

            tick.setVisibility(View.GONE);
        }

    }


    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}


