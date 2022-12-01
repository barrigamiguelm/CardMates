package com.example.cardmates.dagger;

import com.example.cardmates.activities.ChatActivity2;
import com.example.cardmates.activities.ChatUserDetailActivity;
import com.example.cardmates.activities.Login;
import com.example.cardmates.activities.ProfileEdit;
import com.example.cardmates.activities.Register;
import com.example.cardmates.activities.Tags;
import com.example.cardmates.activities.UserProfile;
import com.example.cardmates.dagger.modules.AppModule;
import com.example.cardmates.dagger.modules.CardModule;
import com.example.cardmates.fragments.HomeFragment;
import com.example.cardmates.fragments.ProfileFragment;

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

    void inject(ProfileEdit profileEdit);

    void inject(ChatActivity2 chatActivity2);

    void inject(ChatUserDetailActivity chatUserDetailActivity);
}