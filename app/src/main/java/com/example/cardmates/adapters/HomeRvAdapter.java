package com.example.cardmates.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cardmates.Activities.model.User;
import com.example.cardmates.R;

import java.util.ArrayList;

public class HomeRvAdapter
        extends RecyclerView.Adapter<HomeRvAdapter.MyViewHolder> {

    private ArrayList<User> usersList;

    public HomeRvAdapter(ArrayList<User> usersList) {
        this.usersList = usersList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.name.setText(usersList.get(position).getName());
        holder.description.setText(usersList.get(position).getDescription());
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        private TextView name, description;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tvName);
            description = itemView.findViewById(R.id.tvDescription);
        }
    }
}
