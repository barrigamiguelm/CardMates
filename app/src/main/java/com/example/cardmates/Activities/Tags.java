package com.example.cardmates.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.cardmates.Activities.model.Cards;
import com.example.cardmates.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

import java.net.URL;


public class Tags extends AppCompatActivity {

    private RecyclerView recyclerTags;
    private Context context;

    private FirebaseFirestore db;
    private RecyclerView recyclerView;
    private FirestoreRecyclerAdapter adapter;


    //TODO: mirar si usar json para guardarlo cuando el usuario seleccione y poder mirar de ahi los gustos de un usuario.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tags);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerTags);

        context = getApplicationContext();
        db = FirebaseFirestore.getInstance();

        Query query = db.collection("Cards");
        FirestoreRecyclerOptions<Cards> options = new FirestoreRecyclerOptions.Builder<Cards>()
                .setQuery(query,Cards.class)
                .build();


        adapter = new FirestoreRecyclerAdapter<Cards, CardsViewHolder>(options) {

            @NonNull
            @Override
            public CardsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_row,parent,false);
                return new CardsViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull CardsViewHolder holder, int position, @NonNull Cards model) {
                holder.title.setText(model.getTitle());
                Glide.with(context)
                        .load(model.getImage())
                        .into(holder.img);
            }
        };

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));
        recyclerView.setAdapter(adapter);


    }


    private class CardsViewHolder extends RecyclerView.ViewHolder{

        private TextView title;
        private ImageView img;

        public CardsViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.cardTxt);
            img = itemView.findViewById(R.id.cardImg);
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


