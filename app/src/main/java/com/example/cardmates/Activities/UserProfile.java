package com.example.cardmates.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.cardmates.MainActivity;
import com.example.cardmates.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class UserProfile extends Activity {

    private EditText etUserUsername, etUserEmail,etUserDesc;
    private ImageView profilePhotoUserProfile;
    private Button btnCreateUser,btnSkipUserProfile;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        btnCreateUser = (Button) findViewById(R.id.btnCreateUser);
        btnSkipUserProfile = (Button) findViewById(R.id.btnSkipUserProfile);
        etUserUsername = (EditText) findViewById(R.id.etUserUsername);
        etUserEmail = (EditText) findViewById(R.id.etUserEmail);
        etUserDesc = (EditText) findViewById(R.id.etUserDesc);
        etUserDesc = (EditText) findViewById(R.id.etUserDesc);
        profilePhotoUserProfile = (ImageView) findViewById(R.id.profilePhotoUserProfile);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        String email = mAuth.getCurrentUser().getEmail();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();


        etUserEmail.setText(email);


        btnSkipUserProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            createDialog();
            }
        });

        btnCreateUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createUser();
            }
        });

        profilePhotoUserProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chosePicture();
            }
        });

    }


    private void createUser() {
        String username = etUserUsername.getText().toString();
        String desc = etUserDesc.getText().toString();

        String userID = mAuth.getCurrentUser().getUid();

        Map<String, Object> data = new HashMap<>();
        data.put("Usuario ", username);
        data.put("Descripcion",desc);
        db.collection("Users").document(userID)
                .set(data, SetOptions.merge());
        uploadPhotoFirebase();
        startActivity(new Intent(UserProfile.this,MainActivity.class));
    }

    public void createDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(UserProfile.this);
        builder.setMessage("Podra modificar su perfil posteriormente")
                .setTitle("¿Saltar?");
        builder.setPositiveButton("Siguiente", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                startActivity(new Intent(UserProfile.this,MainActivity.class));
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void chosePicture() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && data != null && data.getData() != null) {
            imageUri = data.getData();
            profilePhotoUserProfile.setImageURI(imageUri);
            uploadPhotoFirebase();
        }
    }



    private void uploadPhotoFirebase() {
        String userID = mAuth.getCurrentUser().getUid();
        StorageReference profilePhotos = storageReference.child("ProfilePhotos/" + userID);

        profilePhotos.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Snackbar.make(findViewById(android.R.id.content), "Foto de perfil subida", Snackbar.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar.make(findViewById(android.R.id.content), "Hubo un problema", Snackbar.LENGTH_LONG).show();
                    }
                });

    }
}