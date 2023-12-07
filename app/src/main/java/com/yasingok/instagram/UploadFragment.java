package com.yasingok.instagram;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yasingok.instagram.databinding.FragmentUploadBinding;

public class UploadFragment extends Fragment {

    private FragmentUploadBinding binding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentUploadBinding.inflate(inflater, container, false);

        // Inflate edilen view nesnesini döndür


        binding.uploadbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {            // UPLOAD BUTTON
                binding.commentText.setText("UPLOAD");
            }
        });

        binding.imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {            // GALLERY BUTTON
                binding.commentText.setText("SELECT");
            }
        });
        return binding.getRoot();
    }

}