package com.yasingok.instagram;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.yasingok.instagram.databinding.FragmentUploadBinding;

public class UploadFragment extends Fragment {

    private FragmentUploadBinding binding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentUploadBinding.inflate(inflater, container, false);

        binding.uploadbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {            // UPLOAD BUTTON
                binding.titleText.setText("UPLOAD");
            }
        });

        binding.imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {            // GALLERY BUTTON
                binding.titleText.setText("SELECT");
            }
        });
        return binding.getRoot();
    }

}