package com.example.cardmates.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.cardmates.MainActivity;
import com.example.cardmates.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.auth.User;

import java.util.HashMap;
import java.util.Map;

public class UserProfile extends Activity {

    private EditText etUserUsername, etUserEmail,etUserDesc;
    private Button btnCreateUser,btnSkipUserProfile;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        btnCreateUser = (Button) findViewById(R.id.btnCreateUser);
        btnSkipUserProfile = (Button) findViewById(R.id.btnSkipUserProfile);
        etUserUsername = (EditText) findViewById(R.id.etUserUsername);
        etUserEmail = (EditText) findViewById(R.id.etUserEmail);
        etUserDesc = (EditText) findViewById(R.id.etUserDesc);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        String email = mAuth.getCurrentUser().getEmail();
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
}