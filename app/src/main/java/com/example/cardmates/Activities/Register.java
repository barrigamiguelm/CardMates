package com.example.cardmates.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.cardmates.Dagger.CardMatesApp;
import com.example.cardmates.R;
import com.example.cardmates.interfaces.FirebaseInterface;
import com.example.cardmates.interfaces.RegisterInterface;

import com.google.android.material.snackbar.Snackbar;

import javax.inject.Inject;


public class Register extends AppCompatActivity implements RegisterInterface {

    private Button btnRegisterRegister, btnLoginRegister;
    private EditText etPasswordConf, etPasswordRegister, etEmailRegister, etUserName;
    private LoadingDialog loadingDialog;
    @Inject
    FirebaseInterface firebaseInterface;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ((CardMatesApp) getApplicationContext()).getCardComponent().inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initializeAll();
        initializeRegisterView();


        btnRegisterRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateUser();
            }
        });

        btnLoginRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Register.this, Login.class));
            }
        });

    }

    private void initializeAll() {
        btnLoginRegister = (Button) findViewById(R.id.btnLoginRegister);
        btnRegisterRegister = (Button) findViewById(R.id.btnRegisterRegister);
        etPasswordConf = (EditText) findViewById(R.id.etPasswordConf);
        etPasswordRegister = (EditText) findViewById(R.id.etPasswordRegister);
        etEmailRegister = (EditText) findViewById(R.id.etEmailRegister);
        etUserName = (EditText) findViewById(R.id.etUserName);
        loadingDialog = new LoadingDialog(this);
    }

    private void validateUser() {
        String email = etEmailRegister.getText().toString();
        String password = etPasswordRegister.getText().toString();
        String passwordconfirm = etPasswordConf.getText().toString();
        String name = etUserName.getText().toString();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(passwordconfirm) || TextUtils.isEmpty(name)) {
            etEmailRegister.setError("Rellena todos los campos");
            etEmailRegister.requestFocus();
        } else if (!isEmailValid(email)) {
            etEmailRegister.setError("Introduce un correo valido");
            etEmailRegister.requestFocus();
        } else if (!passwordconfirm.equals(password)) {
            etPasswordConf.setError("Las contraseñas no coinciden");
            etPasswordConf.requestFocus();
        } else {
            loadingDialog.showDialog();
            firebaseInterface.registerNewUser(email, password, name);
        }

    }


    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }


    private void initializeRegisterView() {
        firebaseInterface.initializeRegisterView(this);
    }


    @Override
    public void showErrorEmail() {
        loadingDialog.hideDialog();
        etEmailRegister.setError("El correo ya esta en uso");
        etEmailRegister.requestFocus();
    }

    @Override
    public void showErrorWeakPassword() {
        loadingDialog.hideDialog();
        etPasswordRegister.setError("La contraseña es demasiado debil");
        etPasswordRegister.requestFocus();
    }

    @Override
    public void showUserRegister() {
        loadingDialog.hideDialog();
        Snackbar.make(findViewById(android.R.id.content), "Usuario registrado", Snackbar.LENGTH_LONG).show();
        Intent intent = new Intent(Register.this, UserProfile.class);
        intent.putExtra("name", etUserName.getText().toString());
        startActivity(intent);
    }

}
