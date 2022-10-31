package com.example.cardmates.Firebase;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.cardmates.Activities.model.User;
import com.example.cardmates.interfaces.FirebaseInterface;
import com.example.cardmates.interfaces.LoginInterface;
import com.example.cardmates.interfaces.RegisterInterface;
import com.example.cardmates.interfaces.UserProfileInterface;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class FirebaseMethods implements FirebaseInterface {

    private RegisterInterface registerInterface;
    private UserProfileInterface userProfileInterface;
    private LoginInterface loginInterface;

    private Context context;
    private String userID;

    private FirebaseStorage storage;
    private StorageReference storageReference;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private DocumentSnapshot document;
    private Map<String, Object> userInfo;

    //TODO: no recoge la foto de perfil


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
    private String getUserId(){
        userID = mAuth.getCurrentUser().getUid();
        return userID;
    }

    @Override
    public void initializeUserProfileInterface(UserProfileInterface userProfileInterface) {
        this.userProfileInterface = userProfileInterface;
    }

    @Override
    public void initializeLoginInterface(LoginInterface loginInterface) {
        this.loginInterface = loginInterface;
    }


    @Override
    public void initializeRegisterView(RegisterInterface registerInterface) {
        this.registerInterface = registerInterface;
    }

    public void logOut() {
        FirebaseAuth.getInstance().signOut();
    }

    @Override
    public String getUserID() {
        String userID = mAuth.getUid();
        return userID;
    }

    @Override
    public void checkUser() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            loginInterface.openMain();
        }

    }

    @Override
    public FirebaseFirestore getDatabase() {
        return db;
    }

    public void getAge() {

    }

    @Override
    public Map<String, Object> provideUserInfo() {
        Calendar today = Calendar.getInstance();
        DocumentReference docRef = db.collection("Users").document(userID);
        Task<DocumentSnapshot> task = docRef.get();
        while (!task.isComplete()) {
        }
        document = task.getResult();
        User user = document.toObject(User.class);
        int age = today.get(Calendar.YEAR) - Integer.parseInt(user.getDate().substring(0, 4));
        userInfo = new HashMap<>();
        userInfo.put("Nombre", user.getName());
        userInfo.put("Desc", user.getDescription());
        userInfo.put("Edad", String.valueOf(age));
       //getUserPhoto();
        return userInfo;
    }

   @Override
    public StorageReference getStorageReference(){
        StorageReference profilePhotos = FirebaseStorage.getInstance().getReference("ProfilePhotos/"+userID+".jpg");

        return profilePhotos;
    }

    @Override
    public void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            loginInterface.openMain();
                        } else {
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            loginInterface.setErrorLogin();
                        }
                    }
                });
    }


    @Override
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


    @Override
    public void addAditionalInfo(String desc, String datebirth) {
        Map<String, Object> data = new HashMap<>();
        data.put("description", desc);
        data.put("Date", datebirth);
        db.collection("Users").document(userID).set(data, SetOptions.merge());

        userProfileInterface.showInfo();
    }


    @Override
    public void uploadPhotoFirebase(Uri imageUri) {
        StorageReference profilePhotos = storageReference.child("ProfilePhotos/" + userID);
        profilePhotos.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                userProfileInterface.setPhoto(imageUri);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                userProfileInterface.setErrorPhoto();
            }
        });
    }


}

