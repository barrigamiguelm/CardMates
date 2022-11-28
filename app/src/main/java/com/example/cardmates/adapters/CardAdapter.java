package com.example.cardmates.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.cardmates.R;
import com.example.cardmates.model.Cards;

import java.util.List;

public class CardAdapter extends ArrayAdapter<Cards> {

    public CardAdapter(@NonNull Context context, int resource, List<Cards> cardsList) {
        super(context, resource);
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        Cards card = getItem(position);

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.card_row, parent, false);
        }
        TextView title = convertView.findViewById(R.id.cardTxt);
        ImageView image = convertView.findViewById(R.id.cardImg);

        title.setText(card.getTitle());

        Glide.with(getContext())
                .load(card.getImage())
                .into(image);

        return convertView;
    }
}