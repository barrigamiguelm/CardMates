package com.example.cardmates.Activities;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cardmates.Dagger.CardMatesApp;
import com.example.cardmates.R;
import com.example.cardmates.interfaces.FirebaseInterface;
import com.example.cardmates.interfaces.UserProfileInterface;
import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import javax.inject.Inject;


//todo editar para que la foto no sea tan grande y alinear con el nombre, cambiar el presentate porque esta raro
public class UserProfile extends AppCompatActivity implements UserProfileInterface {

    private EditText etDateBirt, etUserDesc;
    private TextView tvUserNameUserProfile;
    private ImageView profilePhotoUserProfile;
    private Button btnCreateUser, btnSkipUserProfile;
    private LoadingDialog loadingDialog;
    private String desc = "Sin descripcion", dateBirth = "Sin fecha";
    private Uri imageUri;

    @Inject
    FirebaseInterface firebaseInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ((CardMatesApp) getApplicationContext()).getCardComponent().inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        initializeAll();
        initializeUserProfileInterface();


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
                new DatePickerDialog(UserProfile.this, date,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH))
                        .show();
            }


        });

        btnCreateUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                desc = etUserDesc.getText().toString();
                dateBirth = etDateBirt.getText().toString();
                firebaseInterface.addAditionalInfo(desc, dateBirth);
            }
        });


    }

    private void initializeUserProfileInterface() {
        firebaseInterface.initializeUserProfileInterface(this);
    }


    private void initializeAll() {
        btnCreateUser = (Button) findViewById(R.id.btnCreateUser);
        btnSkipUserProfile = (Button) findViewById(R.id.btnSkipUserProfile);
        etDateBirt = (EditText) findViewById(R.id.etDateBirt);
        etUserDesc = (EditText) findViewById(R.id.etUserDesc);
        tvUserNameUserProfile = (TextView) findViewById(R.id.tvUserNameUserProfile);
        profilePhotoUserProfile = (ImageView) findViewById(R.id.profilePhotoUserProfile);
        loadingDialog = new LoadingDialog(this);
        Bundle bundle = getIntent().getExtras();
        String name = bundle.getString("name");
        tvUserNameUserProfile.setText(name);
    }


    public void createDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(UserProfile.this);
        builder.setMessage("Podra modificar su perfil posteriormente")
                .setTitle("¿Saltar?");
        builder.setPositiveButton("Siguiente", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                firebaseInterface.addAditionalInfo(desc, dateBirth);
                uploadStockPhoto();
                startActivity(new Intent(UserProfile.this, Tags.class));
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void uploadStockPhoto() {
        profilePhotoUserProfile.setDrawingCacheEnabled(true);
        profilePhotoUserProfile.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) profilePhotoUserProfile.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        firebaseInterface.uploadStockPhoto(data);
    }


    private void chosePicture() {
        mGetContent.launch("image/*");
    }


    ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri uri) {
                    loadingDialog.showDialog();
                    firebaseInterface.uploadPhotoFirebase(uri);
                }
            });


    private void updateCalendar(Calendar calendar) {
        String Format = "yyyy/MM/dd";
        SimpleDateFormat sdf = new SimpleDateFormat(Format, Locale.ENGLISH);
        etDateBirt.setText(sdf.format(calendar.getTime()));
    }


    @Override
    public void setPhoto(Uri imageUri) {
        loadingDialog.hideDialog();
        Snackbar.make(findViewById(android.R.id.content), "Foto de perfil subida", Snackbar.LENGTH_LONG).show();
        profilePhotoUserProfile.setImageURI(imageUri);
    }


    @Override
    public void showInfo() {
        startActivity(new Intent(UserProfile.this, Tags.class));
    }

    @Override
    public void setErrorPhoto() {
        Snackbar.make(findViewById(android.R.id.content), "Hubo un problema al subir la foto", Snackbar.LENGTH_LONG).show();
    }


}