package com.example.cardmates.interfaces;


public interface FirebaseInterface {
    void registerNewUser(String email, String password, String username);

    void initializeViewInterface(RegisterInterface registerInterface);

    void initializeUserProfileInterface(UserProfileInterface userProfileInterface);
}
