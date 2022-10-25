package com.example.cardmates.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.cardmates.Firebase.FirebaseMethods;
import com.example.cardmates.R;
import com.example.cardmates.interfaces.RegisterInterface;

import com.google.android.material.snackbar.Snackbar;



public class Register extends AppCompatActivity implements RegisterInterface {

    private Button btnRegisterRegister, btnLoginRegister;
    private EditText etPasswordConf, etPasswordRegister, etEmailRegister, etUserName;
    private FirebaseMethods firebaseMethods;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        overridePendingTransition(R.anim.atras, R.anim.alante);

        initializeAll();
        initializeView();


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
        firebaseMethods = new FirebaseMethods(this);
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
        }else{
            firebaseMethods.registerNewUser(email, password, name);
        }

    }


    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }


    private void initializeView() {
        firebaseMethods.initializeViewInterface(this);
    }


    @Override
    public void showErrorEmail() {
        etEmailRegister.setError("El correo ya esta en uso");
        etEmailRegister.requestFocus();
    }

    @Override
    public void showErrorWeakPassword() {
        etPasswordRegister.setError("La contraseña es demasiado debil");
        etPasswordRegister.requestFocus();
    }

    @Override
    public void showUserRegister() {
        Snackbar.make(findViewById(android.R.id.content), "Usuario registrado", Snackbar.LENGTH_LONG).show();
        Intent intent = new Intent(Register.this, UserProfile.class);
        intent.putExtra("name", etUserName.getText().toString());
        startActivity(intent);
    }

}
