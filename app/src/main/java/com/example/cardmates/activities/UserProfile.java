package com.example.cardmates.activities;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cardmates.dagger.CardMatesApp;
import com.example.cardmates.R;
import com.example.cardmates.interfaces.FirebaseInterface;
import com.example.cardmates.interfaces.UserProfileInterface;
import com.google.android.material.snackbar.Snackbar;

import org.checkerframework.checker.units.qual.A;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import javax.inject.Inject;


public class UserProfile extends AppCompatActivity implements UserProfileInterface {

    private EditText etDateBirt, etUserDesc, etUserLocal;
    private TextView tvUserNameUserProfile;
    private ImageView profilePhotoUserProfile;
    private Button btnCreateUser, btnSkipUserProfile;
    private LoadingDialog loadingDialog;
    private String desc = "Sin descripcion", dateBirth = "Sin fecha", localidad = "Sin localidad";
    private Uri imageUri;
    private String encodedImage;

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
                loadingDialog.showDialog();
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
                if (encodedImage == null) {
                    Bitmap icon = BitmapFactory.decodeResource(getResources(),
                            R.drawable.profile);
                    String encodedImage = encodeImage(icon);
                    uploadStockPhoto();
                    firebaseInterface.addAditionalInfo(desc, dateBirth, encodedImage, localidad);
                } else {
                    desc = etUserDesc.getText().toString();
                    dateBirth = etDateBirt.getText().toString();
                    localidad = etUserLocal.getText().toString();
                    firebaseInterface.addAditionalInfo(desc, dateBirth, encodedImage, localidad);
                }

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
        etUserLocal = (EditText) findViewById(R.id.etUserLocal);
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

                //Make image a String
                Bitmap icon = BitmapFactory.decodeResource(getResources(),
                        R.drawable.profile);
                String encodedImage = encodeImage(icon);
                firebaseInterface.addAditionalInfoWithNoInfo(encodedImage);

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

    private String encodeImage(Bitmap bitmap) {
        int previewWidth = 150;
        int previewHeight = bitmap.getHeight() * previewWidth / bitmap.getWidth();
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
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
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(uri);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        profilePhotoUserProfile.setImageBitmap(bitmap);
                        encodedImage = encodeImage(bitmap);
                        firebaseInterface.uploadPhotoFirebase(uri);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

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