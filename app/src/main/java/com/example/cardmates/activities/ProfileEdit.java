package com.example.cardmates.activities;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.cardmates.dagger.CardMatesApp;
import com.example.cardmates.MainActivity;
import com.example.cardmates.R;
import com.example.cardmates.interfaces.FirebaseInterface;
import com.example.cardmates.interfaces.ProfileEditInterface;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;

public class ProfileEdit extends AppCompatActivity implements ProfileEditInterface {

    @Inject
    FirebaseInterface firebaseInterface;

    private Map<String, Object> userInfo;
    private EditText edtDescProfileEdit, etDateBirtProfileEdit, edtProfileNameEdit,etLocalidadProfileEdit;
    private ImageView imgUser;
    private Button saveUserChangesProfileEdit;
    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ((CardMatesApp) getApplicationContext()).getCardComponent().inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);

        initializeProfileEditInterface();

        edtDescProfileEdit = findViewById(R.id.edtDescProfileEdit);
        etDateBirtProfileEdit = findViewById(R.id.etDateBirtProfileEdit);
        edtProfileNameEdit = findViewById(R.id.edtProfileNameEdit);
        etLocalidadProfileEdit = findViewById(R.id.etLocalidadProfileEdit);
        imgUser = findViewById(R.id.imgUser);
        saveUserChangesProfileEdit = findViewById(R.id.saveUserChangesProfileEdit);
        loadingDialog = new LoadingDialog(this);
        completar();


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

        saveUserChangesProfileEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseInterface.editUserInfo(
                        edtDescProfileEdit.getText().toString(),
                        etDateBirtProfileEdit.getText().toString(),
                        edtProfileNameEdit.getText().toString(),
                        etLocalidadProfileEdit.getText().toString());
                Toast.makeText(ProfileEdit.this, "Cambios realizados correctamente", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(ProfileEdit.this, MainActivity.class));
            }
        });

        imgUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chosePicture();
            }
        });

        etDateBirtProfileEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(ProfileEdit.this, date,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH))
                        .show();
            }
        });
    }


    private void chosePicture() {
        mGetContent.launch("image/*");
    }

    ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri uri) {
                    if (uri != null){
                        loadingDialog.showDialog();
                        firebaseInterface.editPhotoUser(uri);
                        setPhoto(uri);
                    }

                }
            });


    private void updateCalendar(Calendar calendar) {
        String Format = "yyyy/MM/dd";
        SimpleDateFormat sdf = new SimpleDateFormat(Format, Locale.ENGLISH);
        etDateBirtProfileEdit.setText(sdf.format(calendar.getTime()));
    }

    public void setPhoto(Uri imageUri) {
        loadingDialog.hideDialog();
        imgUser.setImageURI(imageUri);
    }


    private void completar() {

        userInfo = firebaseInterface.provideUserInfo();

        String desc = String.valueOf(userInfo.get("Desc"));
        String name = String.valueOf(userInfo.get("Nombre"));
        String date = String.valueOf(userInfo.get("Fecha"));
        String localidad = String.valueOf(userInfo.get("Localidad"));


        edtProfileNameEdit.setText(name);
        edtDescProfileEdit.setText(desc);
        etDateBirtProfileEdit.setText(date);
        etLocalidadProfileEdit.setText(localidad);

        Glide.with(this)
                .load(userInfo.get("Imagen"))
                .into(imgUser);

    }

    @Override
    public void setSnackBarSuccess() {
        Snackbar.make(findViewById(android.R.id.content), "Foto de perfil subida", Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void setErrorPhoto() {
        Snackbar.make(findViewById(android.R.id.content), "Hubo un error al cambiar de imagen", Snackbar.LENGTH_LONG).show();
    }

    private void initializeProfileEditInterface() {
        firebaseInterface.initializeProfileEditInterface(this);
    }
}