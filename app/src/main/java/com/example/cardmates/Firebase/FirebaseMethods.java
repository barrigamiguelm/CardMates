package com.example.cardmates.Firebase;

import android.content.Context;
import android.net.Uri;
import android.nfc.Tag;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.cardmates.interfaces.FirebaseInterface;
import com.example.cardmates.interfaces.RegisterInterface;
import com.example.cardmates.interfaces.UserProfileInterface;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class FirebaseMethods implements FirebaseInterface {

    private RegisterInterface registerInterface;
    private UserProfileInterface userProfileInterface;

    private Context context;
    private String userID;

    private FirebaseStorage storage;
    private StorageReference storageReference;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    public FirebaseMethods(Context context) {
        setUpFirebaseAuth();
        this.context = context;

        if (mAuth.getCurrentUser() != null) {
            userID = mAuth.getCurrentUser().getUid();
        }
    }

    private void setUpFirebaseAuth() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
    }

    @Override
    public void initializeViewInterface(RegisterInterface registerInterface) {
        this.registerInterface = registerInterface;
    }

    @Override
    public void initializeUserProfileInterface(UserProfileInterface userProfileInterface) {
        this.userProfileInterface = userProfileInterface;
    }





    public void registerNewUser(String email, String password, String name) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    userID = mAuth.getCurrentUser().getUid();
                    DocumentReference documentReference = db.collection("Users").document(userID);
                    Map<String, Object> user = new HashMap<>();
                    user.put("Email", email);
                    user.put("Name", name);
                    documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            registerInterface.showUserRegister();
                        }
                    });
                } else if (!task.isSuccessful()) {
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthUserCollisionException e) {
                        registerInterface.showErrorEmail();
                    } catch (FirebaseAuthWeakPasswordException e) {
                        registerInterface.showErrorWeakPassword();
                    } catch (Exception e) {
                        Log.e("TAG", e.getMessage());
                    }
                }
            }
        });

    }

    public void addAditionalInfo(String desc, String datebirth){
        Map<String, Object> data = new HashMap<>();
        data.put("description", desc);
        data.put("Date",datebirth);
        db.collection("Users").document(userID)
                .set(data, SetOptions.merge());
       userProfileInterface.showInfo();
    }


    public void uploadPhotoFirebase(Uri imageUri) {
        StorageReference profilePhotos = storageReference.child("ProfilePhotos/" + userID);
        profilePhotos.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        userProfileInterface.setPhoto(imageUri);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        userProfileInterface.setErrorPhoto();
                    }
                });
    }

}

