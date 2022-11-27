package com.example.cardmates.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cardmates.ChatActivity2;
import com.example.cardmates.model.Constants;
import com.example.cardmates.model.User;
import com.example.cardmates.R;
import com.example.cardmates.model.UserChat;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class HomeRvAdapter
        extends FirestoreRecyclerAdapter<User, HomeRvAdapter.MyViewHolder> {

    Context context;
    //TODO: no hacer que se actualice todo el rato porque si se registra un usuario mientas esta abierto en otro telefono, peta

    public HomeRvAdapter(@NonNull FirestoreRecyclerOptions<User> options, Context context) {
        super(options);
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull User model) {
        holder.name.setText(model.getName());
        holder.description.setText(model.getDescription());
        holder.imageView.setImageBitmap(getUserImage(model.getImage()));
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ChatActivity2.class);
            intent.putExtra("userId", model.getUser_id());
            intent.putExtra("userName", model.getName());
            intent.putExtra("image", model.getImage());
            context.startActivity(intent);
        });
    }

    private Bitmap getUserImage(String encodedImage) {
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView name, description;
        private ImageView imageView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imgUserCard);
            name = itemView.findViewById(R.id.tvName);
            description = itemView.findViewById(R.id.tvDescription);
        }
    }


}
