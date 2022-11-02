package com.example.cardmates.Dagger;

import com.example.cardmates.Activities.Login;
import com.example.cardmates.Activities.Register;
import com.example.cardmates.Activities.Tags;
import com.example.cardmates.Activities.UserProfile;
import com.example.cardmates.Dagger.Modules.AppModule;
import com.example.cardmates.Dagger.Modules.CardModule;
import com.example.cardmates.Fragments.HomeFragment;
import com.example.cardmates.Fragments.ProfileFragment;
import com.example.cardmates.MainActivity;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class, CardModule.class})
public interface CardComponent {
    void inject(Login login);

    void inject(Register register);

    void inject(UserProfile userProfile);

    void inject(HomeFragment homeFragment);

    void inject(Tags tags);

    void inject(ProfileFragment profileFragment);

}