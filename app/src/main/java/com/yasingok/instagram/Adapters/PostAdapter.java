package com.yasingok.instagram.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.yasingok.instagram.Classes.Post;
import com.yasingok.instagram.R;
import com.yasingok.instagram.databinding.MainRecyclerRowBinding;

import java.util.ArrayList;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostHolder>{
    private ArrayList<Post> posts;

    public PostAdapter(ArrayList<Post> posts) {
        this.posts = posts;
    }

    @NonNull
    @Override
    public PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MainRecyclerRowBinding binding = MainRecyclerRowBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new PostHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PostHolder holder, int position) {
        holder.binding.profilAd.setText(posts.get(position).username);
        holder.binding.profilNameText.setText(posts.get(position).username);
        holder.binding.profilAciklamaText.setText(posts.get(position).title);
        Picasso.get().load(posts.get(position).postUrl).into(holder.binding.postFoto);
        Picasso.get().load(posts.get(position).profileUrl).into(holder.binding.profil);

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
        return posts.size();
    }

    class PostHolder extends RecyclerView.ViewHolder{
        MainRecyclerRowBinding binding;

        public PostHolder(MainRecyclerRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}