package com.example.cardmates.Dagger;

import com.example.cardmates.Activities.Login;
import com.example.cardmates.Activities.Register;
import com.example.cardmates.Activities.UserProfile;
import com.example.cardmates.Dagger.Modules.AppModule;
import com.example.cardmates.Dagger.Modules.CardModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class, CardModule.class})
public interface CardComponent {
    void inject(Login login);

    void inject(Register register);

    void inject(UserProfile userProfile);

}