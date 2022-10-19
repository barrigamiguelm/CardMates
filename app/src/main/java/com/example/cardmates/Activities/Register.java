package com.example.cardmates.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.cardmates.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {

    private Button btnRegisterRegister, btnLoginRegister;
    private EditText etPasswordConf, etPasswordRegister, etEmailRegister;

    private String userID;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        overridePendingTransition(R.anim.atras, R.anim.alante);


        btnLoginRegister = (Button) findViewById(R.id.btnLoginRegister);
        btnRegisterRegister = (Button) findViewById(R.id.btnRegisterRegister);
        etPasswordConf = (EditText) findViewById(R.id.etPasswordConf);
        etPasswordRegister = (EditText) findViewById(R.id.etPasswordRegister);
        etEmailRegister = (EditText) findViewById(R.id.etEmailRegister);


        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        btnRegisterRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createUser();
            }
        });

        btnLoginRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Register.this, Login.class));
            }
        });

    }

    private void createUser() {
        String email = etEmailRegister.getText().toString();
        String password = etPasswordRegister.getText().toString();

        if (TextUtils.isEmpty(email)) {
            etEmailRegister.setError("Introduce un correo");
            etEmailRegister.requestFocus();
        } else if (TextUtils.isEmpty(password)) {
            etPasswordRegister.setError("Introduce una contraseña");
            etPasswordRegister.requestFocus();
        } else {
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        userID = mAuth.getCurrentUser().getUid();
                        DocumentReference documentReference = db.collection("Users").document(userID);

                        Map<String,Object> user =new HashMap<>();
                        user.put("Email", email);
                        user.put("Password", password);

                        documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(Register.this, "Usuario registado", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(Register.this,UserProfile.class));
                            }
                        });


                    } else if(!task.isSuccessful()) {
                        try {
                            throw task.getException();
                        } catch (FirebaseAuthUserCollisionException e) {
                            Toast.makeText(Register.this, "El correo ya esta en uso", Toast.LENGTH_SHORT).show();
                        } catch (FirebaseAuthWeakPasswordException e) {
                            Toast.makeText(Register.this, "La contraseña es demasiado corta", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Log.e("TAG", e.getMessage());
                        }
                    }
                }
            });
        }


    }
}