package com.example.cardmates.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cardmates.MainActivity;
import com.example.cardmates.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.checkerframework.checker.units.qual.C;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class UserProfile extends AppCompatActivity {

    private EditText etUserUsername, etDateBirt, etUserDesc;
    private ImageView profilePhotoUserProfile;
    private Button btnCreateUser, btnSkipUserProfile;
    private FirebaseAuth mAuth;
    private Calendar calendar;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private String userID;
    private StorageReference storageReference;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        btnCreateUser = (Button) findViewById(R.id.btnCreateUser);
        btnSkipUserProfile = (Button) findViewById(R.id.btnSkipUserProfile);
        etUserUsername = (EditText) findViewById(R.id.etUserUsername);
        etDateBirt = (EditText) findViewById(R.id.etDateBirt);
        etUserDesc = (EditText) findViewById(R.id.etUserDesc);
        etUserDesc = (EditText) findViewById(R.id.etUserDesc);
        profilePhotoUserProfile = (ImageView) findViewById(R.id.profilePhotoUserProfile);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        userID = mAuth.getCurrentUser().getUid();


        Calendar calendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateCalendar(calendar);
            }
        };


        btnSkipUserProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createDialog();
            }
        });
        profilePhotoUserProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chosePicture();
            }
        });

        etDateBirt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               new DatePickerDialog(UserProfile.this,date,
                       calendar.get(Calendar.YEAR),
                       calendar.get(Calendar.MONTH),
                       calendar.get(Calendar.DAY_OF_MONTH))
                               .show();
            }


        });

        btnCreateUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createUser();
            }
        });


    }


    private void createUser() {
        String username = etUserUsername.getText().toString();
        String desc = etUserDesc.getText().toString();
        String datebirth= etDateBirt.getText().toString();

        Map<String, Object> data = new HashMap<>();
        data.put("Usuario ", username);
        data.put("Descripcion", desc);
        data.put("Fecha Nacimineto",datebirth);
        db.collection("Users").document(userID)
                .set(data, SetOptions.merge());
        startActivity(new Intent(UserProfile.this, MainActivity.class));
    }

    public void createDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(UserProfile.this);
        builder.setMessage("Podra modificar su perfil posteriormente")
                .setTitle("¿Saltar?");
        builder.setPositiveButton("Siguiente", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                startActivity(new Intent(UserProfile.this, MainActivity.class));
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

    private void updateCalendar(Calendar calendar) {
        String Format = "dd/MM/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(Format, Locale.ENGLISH);
        etDateBirt.setText(sdf.format(calendar.getTime()));
    }


}