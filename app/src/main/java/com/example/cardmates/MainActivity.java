package com.example.cardmates;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import android.widget.Button;

import com.example.cardmates.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;


import com.example.cardmates.Activities.LoadingDialog;
import com.example.cardmates.Dagger.CardMatesApp;
import com.example.cardmates.Fragments.ChatFragment;
import com.example.cardmates.Fragments.HomeFragment;
import com.example.cardmates.Fragments.ProfileFragment;
import com.example.cardmates.databinding.ActivityMainBinding;

import javax.inject.Inject;


public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new ChatFragment());

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


}