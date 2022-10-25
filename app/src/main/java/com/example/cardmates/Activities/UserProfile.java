package com.example.cardmates.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cardmates.Firebase.FirebaseMethods;
import com.example.cardmates.MainActivity;
import com.example.cardmates.R;
import com.example.cardmates.interfaces.UserProfileInterface;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


//todo editar para que la foto no sea tan grande y alinear con el nombre, cambiar el presentate porque esta raro
public class UserProfile extends AppCompatActivity implements UserProfileInterface {

    private EditText etDateBirt, etUserDesc;
    private FirebaseMethods firebaseMethods;
    private TextView tvUserNameUserProfile;
    private ImageView profilePhotoUserProfile;
    private Button btnCreateUser, btnSkipUserProfile;

    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
                String desc = etUserDesc.getText().toString();
                String datebirth = etDateBirt.getText().toString();
                firebaseMethods.addAditionalInfo(desc, datebirth);
            }
        });


    }

    private void initializeUserProfileInterface() {
        firebaseMethods.initializeUserProfileInterface(this);
    }


    private void initializeAll() {
        btnCreateUser = (Button) findViewById(R.id.btnCreateUser);
        btnSkipUserProfile = (Button) findViewById(R.id.btnSkipUserProfile);
        etDateBirt = (EditText) findViewById(R.id.etDateBirt);
        etUserDesc = (EditText) findViewById(R.id.etUserDesc);
        tvUserNameUserProfile = (TextView) findViewById(R.id.tvUserNameUserProfile);
        profilePhotoUserProfile = (ImageView) findViewById(R.id.profilePhotoUserProfile);
        firebaseMethods = new FirebaseMethods(this);

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
            firebaseMethods.uploadPhotoFirebase(imageUri);
        }
    }


    private void updateCalendar(Calendar calendar) {
        String Format = "dd/MM/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(Format, Locale.ENGLISH);
        etDateBirt.setText(sdf.format(calendar.getTime()));
    }


    @Override
    public void setPhoto(Uri imageUri) {
        Snackbar.make(findViewById(android.R.id.content), "Foto de perfil subida", Snackbar.LENGTH_LONG).show();
        profilePhotoUserProfile.setImageURI(imageUri);
    }


    @Override
    public void showInfo() {
        startActivity(new Intent(UserProfile.this, MainActivity.class));
    }

    @Override
    public void setErrorPhoto() {
        Snackbar.make(findViewById(android.R.id.content), "Hubo un problema al subir la foto", Snackbar.LENGTH_LONG).show();
    }


}