package com.yasingok.instagram.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.yasingok.instagram.Classes.ProfilePosts;
import com.yasingok.instagram.R;
import com.yasingok.instagram.databinding.ProfileRecyclerRowBinding;

import java.util.ArrayList;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ProfileHolder> {

    private ArrayList<ProfilePosts> profilePosts;

    public ProfileAdapter(ArrayList<ProfilePosts> profilePosts) {
        this.profilePosts = profilePosts;
    }

    @NonNull
    @Override
    public ProfileHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ProfileRecyclerRowBinding binding = ProfileRecyclerRowBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ProfileHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileHolder holder, int position) {
        holder.binding.profilNameText.setText(profilePosts.get(position).Username);
        holder.binding.profilAciklamaText.setText(profilePosts.get(position).PostTitle);
        Picasso.get().load(profilePosts.get(position).FotoUrl).into(holder.binding.postFoto);

        holder.binding.Like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.binding.Like.getDrawable().getConstantState().equals(view.getResources().getDrawable(R.drawable.heart).getConstantState())){
                    holder.binding.Like.setImageResource(R.drawable.likedheart);
                }
                else {
                    holder.binding.Like.setImageResource(R.drawable.heart);
                }
            }
        });

        holder.binding.Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.binding.Save.getDrawable().getConstantState().equals(view.getResources().getDrawable(R.drawable.unsave).getConstantState())){
                    holder.binding.Save.setImageResource(R.drawable.save);
                }
                else {
                    holder.binding.Save.setImageResource(R.drawable.unsave);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return profilePosts.size();
    }

    class ProfileHolder extends RecyclerView.ViewHolder{
        ProfileRecyclerRowBinding binding;

        public ProfileHolder(ProfileRecyclerRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}