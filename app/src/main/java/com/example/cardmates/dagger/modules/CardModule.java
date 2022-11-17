package com.example.cardmates.dagger.modules;

import android.app.Application;

import com.example.cardmates.activities.Register;
import com.example.cardmates.activities.UserProfile;
import com.example.cardmates.firebase.FirebaseMethods;
import com.example.cardmates.interfaces.FirebaseInterface;
import com.example.cardmates.interfaces.RegisterInterface;
import com.example.cardmates.interfaces.UserProfileInterface;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class CardModule {


    @Provides
    @Singleton
    RegisterInterface registerInterface() {
        return new Register();
    }


    @Provides
    @Singleton
    FirebaseInterface firebaseInterface(Application applicationContext) {
        return new FirebaseMethods(applicationContext);
    }

    @Provides
    @Singleton
    UserProfileInterface userProfileInterface() {
        return new UserProfile();
    }

}
