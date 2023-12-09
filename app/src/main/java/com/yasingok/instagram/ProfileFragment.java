package com.yasingok.instagram;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.yasingok.instagram.databinding.FragmentProfileBinding;

public class ProfileFragment extends Fragment {
    private FragmentProfileBinding binding;
    private FirebaseUser firebaseUser;
    private FirebaseAuth auth;
    private AlertDialog.Builder alertDialogBuilder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false);

        alertDialogBuilder = new AlertDialog.Builder(getContext());
        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        if (firebaseUser != null){
            binding.usernameText.setText(firebaseUser.getEmail());
        }

        binding.profil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.usernameText.setText("Profile Photo");
            }
        });

        binding.menuImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(getContext(), view);
                popupMenu.getMenuInflater().inflate(R.menu.exit_menu, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        if (menuItem.getItemId() == R.id.quit) {
                            // "Quit" menü öğesine tıklandığında yapılacak işlemler
                            auth.signOut();

                            alertDialogBuilder.setTitle("Info");
                            alertDialogBuilder.setMessage("Succesfully quited");
                            alertDialogBuilder.setPositiveButton("Okay", null); // Tamam butonu ekleyebilirsiniz
                            alertDialogBuilder.show();

                            Intent intent = new Intent(getContext(), Login.class);
                            startActivity(intent);
                            requireActivity().finish();

                            return true;
                        } else {
                            return false;
                        }

                    }

                });

                popupMenu.show();
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.exit_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
}