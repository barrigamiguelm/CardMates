package com.example.cardmates.interfaces;

import android.net.Uri;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public interface FirebaseInterface {
    void registerNewUser(String email, String password, String username);

    void loginUser(String email, String password);

    void initializeRegisterView(RegisterInterface registerInterface);

    void initializeProfileEditInterface(ProfileEditInterface profileEditInterface);

    void initializeUserProfileInterface(UserProfileInterface userProfileInterface);

    void initializeLoginInterface(LoginInterface loginInterface);

    void checkUser();

    void addAditionalInfo(String desc, String datebirth, String image,String localidad);

    void editUserInfo(String desc, String datebirth,String name);

    void uploadPhotoFirebase(Uri imageUri);

    void editPhotoUser(Uri imageUri);

    void logOut();

    String getUserID();

    FirebaseFirestore getDatabase();

    Map<String, Object> provideUserInfo();


    void uploadStockPhoto(byte[] data);
}
