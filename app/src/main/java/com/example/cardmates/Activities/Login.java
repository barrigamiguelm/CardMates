package com.example.cardmates.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.airbnb.lottie.LottieAnimationView;
import com.example.cardmates.Dagger.CardMatesApp;
import com.example.cardmates.MainActivity;
import com.example.cardmates.R;
import com.example.cardmates.interfaces.FirebaseInterface;
import com.example.cardmates.interfaces.LoginInterface;
import com.google.android.material.snackbar.Snackbar;

import javax.inject.Inject;

public class Login extends AppCompatActivity implements LoginInterface {

    private EditText etPassword, etEmail;
    private Button btnLogin, btnRegister;
    private LoadingDialog loadingDialog;

    @Inject
    FirebaseInterface firebaseInterface;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ((CardMatesApp) getApplicationContext()).getCardComponent().inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initializeUserProfileInterface();
        initializeAll();


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etEmail == null && etPassword == null) {
                    Snackbar.make(findViewById(android.R.id.content), "Porfavor ingresa los datos", Snackbar.LENGTH_LONG).show();
                } else {
                    loginUser();
                }
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login.this, Register.class));
            }
        });
    }

    private void initializeAll() {
        etPassword = (EditText) findViewById(R.id.etPassword);
        etEmail = (EditText) findViewById(R.id.etEmail);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        loadingDialog = new LoadingDialog(this);
    }


    private void loginUser() {
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Introduce un correo");
            etEmail.requestFocus();
        } else if (TextUtils.isEmpty(password)) {
            etPassword.setError("Introduce una contraseña");
            etPassword.requestFocus();
        } else {
            loadingDialog.showDialog();
            firebaseInterface.loginUser(email, password);
            loadingDialog.hideDialog();
        }
    }


    private void initializeUserProfileInterface() {
        firebaseInterface.initializeLoginInterface(this);
    }


    @Override
    public void openMain() {
        startActivity(new Intent(Login.this, MainActivity.class));
    }

    @Override
    public void setErrorLogin() {
        Snackbar.make(findViewById(android.R.id.content), "Error al autenticar", Snackbar.LENGTH_LONG).show();
    }


    protected void onStart() {
        super.onStart();
        firebaseInterface.checkUser();
    }


}
