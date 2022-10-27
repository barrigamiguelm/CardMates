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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.cardmates.Activities.model.Cards;
import com.example.cardmates.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.ArrayList;


public class Tags extends AppCompatActivity {

    private RecyclerView recyclerTags;
    private Context context;
    private Button checkList;

    private FirebaseFirestore db;
    private RecyclerView recyclerView;
    private FirestoreRecyclerAdapter adapter;

    private ArrayList<String> selected = new ArrayList<>();


    //TODO:subir gustos ( ni idea de como) a firebase.


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tags);

        StringBuilder stringBuilder = new StringBuilder();

        recyclerView = (RecyclerView) findViewById(R.id.recyclerTags);
        checkList = (Button) findViewById(R.id.checkList);


        context = getApplicationContext();
        db = FirebaseFirestore.getInstance();

        Query query = db.collection("Cards");
        FirestoreRecyclerOptions<Cards> options = new FirestoreRecyclerOptions.Builder<Cards>()
                .setQuery(query, Cards.class)
                .build();

        checkList.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                for (int i = 0; i < selected.size(); i++) {
                    String next = selected.get(i);
                    stringBuilder.append(next);
                    stringBuilder.append("\n");
                }
                Toast.makeText(context, stringBuilder, Toast.LENGTH_SHORT).show();
                stringBuilder.setLength(0);
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
                            selected.add(model.getTagTitle());

                        } else if (model.isChecked() == true) {
                            model.setChecked(false);
                            holder.tick.setVisibility(View.GONE);
                            String borrar = model.getTagTitle();
                            selected.remove(borrar);
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


