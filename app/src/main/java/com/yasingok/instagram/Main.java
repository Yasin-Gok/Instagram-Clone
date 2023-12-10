package com.yasingok.instagram;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.yasingok.instagram.databinding.ActivityMainBinding;

public class Main extends AppCompatActivity {
    private ActivityMainBinding mainBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());

        replaceFragment(new HomeFragment());
        mainBinding.bottomNavigationView.setBackground(null);

        mainBinding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.home) {
                replaceFragment(new HomeFragment());
            } else if (itemId == R.id.profile) {
                replaceFragment(new ProfileFragment());
            } else if (itemId == R.id.fab) {
                replaceFragment(new UploadFragment());
            }
            return true;
        });
    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    public void uploadImage(View view) {
        Log.d("UploadFragment", "Upload method called");
    }

    public void selectImage(View view){
        Log.d("UploadFragment", "Select called");
    }

    public void profileImage(View view) {
        Log.d("ProfileFragment", "Profile image called");
    }

    public void showMenu(View view) {
        Log.d("ProfileFragment", "Menu called");
    }

    public void uploadProfile(View view){
        Log.d("ProfileFragment", "Upload Profile called");
    }
}