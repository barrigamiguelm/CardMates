package com.example.cardmates.interfaces;

import android.net.Uri;

public interface UserProfileInterface {
    void setPhoto(Uri imageUri);

    void showInfo();

    void setErrorPhoto();
}
