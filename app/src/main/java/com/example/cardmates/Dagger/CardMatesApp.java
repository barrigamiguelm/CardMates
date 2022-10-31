package com.example.cardmates.Dagger;

import android.app.Application;

import com.example.cardmates.Dagger.Modules.AppModule;
import com.example.cardmates.Dagger.Modules.CardModule;

public class CardMatesApp extends Application {

    private CardComponent cardComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        cardComponent = DaggerCardComponent.builder()
                .appModule(new AppModule(this)).cardModule(new CardModule()).build();
    }

    public CardComponent getCardComponent(){
        return cardComponent;
    }
}
