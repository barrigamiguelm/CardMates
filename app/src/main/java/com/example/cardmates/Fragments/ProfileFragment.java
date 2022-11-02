package com.example.cardmates.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.cardmates.Activities.LoadingDialog;
import com.example.cardmates.Dagger.CardMatesApp;
import com.example.cardmates.R;
import com.example.cardmates.interfaces.FirebaseInterface;

import java.util.Map;

import javax.inject.Inject;

public class ProfileFragment extends Fragment {

    @Inject
    FirebaseInterface firebaseInterface;

    private Map<String, Object> userInfo;
    private Button btnEditarPerfil;
    private TextView tvDes, tvName, tvEdad;
    private ImageView imgUser;
    private LoadingDialog loadingDialog;


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

        btnEditarPerfil = (Button) view.findViewById(R.id.btnEditarPerfil);
        tvDes = (TextView) view.findViewById(R.id.tvDes);
        tvName = (TextView) view.findViewById(R.id.tvName);
        tvEdad = (TextView) view.findViewById(R.id.tvEdad);
        imgUser = (ImageView) view.findViewById(R.id.imgUser);


        completar();


        return view;
    }

    private void completar() {

        userInfo = firebaseInterface.provideUserInfo();

        String desc = String.valueOf(userInfo.get("Desc"));
        String name = String.valueOf(userInfo.get("Nombre"));
        String edad = String.valueOf(userInfo.get("Edad"));

        tvName.setText(name);
        tvDes.setText(desc);
        tvEdad.setText(edad);

        Glide.with(this)
                .load(userInfo.get("Imagen"))
                .into(imgUser);
    }

}