package com.example.cardmates.interfaces;

import android.net.Uri;

public interface FirebaseInterface {
    void registerNewUser(String email, String password, String username);

    void loginUser(String email, String password);

    void initializeRegisterView(RegisterInterface registerInterface);

    void initializeUserProfileInterface(UserProfileInterface userProfileInterface);

    void initializeLoginInterface(LoginInterface loginInterface);

    void checkUser();

    void addAditionalInfo(String desc, String datebirth);

    void uploadPhotoFirebase(Uri imageUri);
}
