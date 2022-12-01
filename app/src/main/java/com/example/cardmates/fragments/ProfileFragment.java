package com.example.cardmates.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.cardmates.activities.LoadingDialog;
import com.example.cardmates.activities.Login;
import com.example.cardmates.activities.ProfileEdit;
import com.example.cardmates.dagger.CardMatesApp;
import com.example.cardmates.R;
import com.example.cardmates.interfaces.FirebaseInterface;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

import javax.inject.Inject;

public class ProfileFragment extends Fragment {

    @Inject
    FirebaseInterface firebaseInterface;

    private Map<String, Object> userInfo;
    private Button btnEditarPerfil, btnLogOut;
    private TextView tvDes, tvName, tvEdad, tvLocal, tvUserLikes;
    private ImageView imgUser;
    private LoadingDialog loadingDialog;


    //todo:poner loadings
    public ProfileFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        ((CardMatesApp) getActivity().getApplicationContext()).getCardComponent().inject(this);
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        loadingDialog = new LoadingDialog(getContext());

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        btnEditarPerfil = view.findViewById(R.id.btnEditarPerfil);
        tvDes = view.findViewById(R.id.tvDes);
        tvName = view.findViewById(R.id.tvName);
        tvEdad = view.findViewById(R.id.tvEdad);
        tvLocal = view.findViewById(R.id.tvLocal);
        tvUserLikes = view.findViewById(R.id.tvUserCards);
        imgUser = view.findViewById(R.id.imgUser);
        btnLogOut = view.findViewById(R.id.btnLogOut);

        completar();

        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseInterface.logOut();
                startActivity(new Intent(getActivity(), Login.class));
            }
        });
        btnEditarPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ProfileEdit.class);
                startActivity(intent);
            }
        });
        return view;


    }

    private void completar() {
        userInfo = firebaseInterface.provideUserInfo();

        String desc = String.valueOf(userInfo.get("Desc"));
        String name = String.valueOf(userInfo.get("Nombre"));
        String edad = String.valueOf(userInfo.get("Edad"));
        String localidad = String.valueOf(userInfo.get("Localidad"));
        String userLikes = String.valueOf(userInfo.get("userLikes"));

        tvName.setText(name);
        tvDes.setText(desc);
        tvEdad.setText("Edad: " + edad);
        tvLocal.setText(localidad);
        tvUserLikes.setText(userLikes);

        Glide.with(this)
                .load(userInfo.get("Imagen"))
                .into(imgUser);

    }

}