package com.example.cardmates.activities;

import android.app.Application;
import android.content.Context;

import com.example.cardmates.model.Cards;
import com.example.cardmates.R;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

public class CardRepository {

    private Gson gson;
    private Context applicationContext;

    public CardRepository(Application applicationContext, Gson gson) {
        this.gson = gson;
        this.applicationContext = applicationContext;
    }

    public List<Cards> getMovies() {
        String jsonString = null;

        try {
            InputStream inputStream = applicationContext.getResources().openRawResource(R.raw.movies);
            byte[] b = new byte[inputStream.available()];
            inputStream.read(b);

            jsonString = new String(b);
        } catch (IOException e) {
            //TODO: fix io exception
        }

        Cards[] movies = gson.fromJson(jsonString, Cards[].class);


        return Arrays.asList(movies);
    }
}
