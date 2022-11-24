package com.example.cardmates;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.widget.Toast;

import com.example.cardmates.dagger.CardMatesApp;
import com.example.cardmates.databinding.ActivityMainBinding;


import com.example.cardmates.fragments.ChatFragment;
import com.example.cardmates.fragments.HomeFragment;
import com.example.cardmates.fragments.ProfileFragment;
import com.example.cardmates.interfaces.FirebaseInterface;
import com.example.cardmates.interfaces.MainInterface;

import javax.inject.Inject;


public class MainActivity extends AppCompatActivity implements MainInterface {

    ActivityMainBinding binding;
    @Inject
    FirebaseInterface firebaseInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ((CardMatesApp) getApplicationContext()).getCardComponent().inject(this);
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initializeMainInterface();

        firebaseInterface.getToken();

        replaceFragment(new HomeFragment());

        binding.bottomNavigation.setOnItemSelectedListener(item -> {

            switch (item.getItemId()) {

                case R.id.home:
                    replaceFragment(new HomeFragment());
                    break;
                case R.id.chat:
                    replaceFragment(new MapsFragment());
                    break;
                case R.id.profile:
                    replaceFragment(new ProfileFragment());
            }

            return true;
        });


    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }


    private void initializeMainInterface() {
        firebaseInterface.initializeMainInterface(this);
    }

    @Override
    public void showToastErrorToken() {
        Toast.makeText(this, "Token updated", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showToastSuccessToken() {
        Toast.makeText(this, "Unable to update token", Toast.LENGTH_SHORT).show();
    }
}