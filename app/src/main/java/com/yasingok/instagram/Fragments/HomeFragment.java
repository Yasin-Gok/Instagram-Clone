package com.yasingok.instagram.Fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.yasingok.instagram.Adapters.PostAdapter;
import com.yasingok.instagram.Classes.Post;
import com.yasingok.instagram.databinding.FragmentHomeBinding;

import java.util.ArrayList;
import java.util.Map;

public class HomeFragment extends Fragment {

    FragmentHomeBinding homeBinding;
    private FirebaseUser firebaseUser;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private AlertDialog.Builder alertDialogBuilder;
    ArrayList<Post> posts;

    PostAdapter postAdapter;
    private HomeFragment.OnPostDataLoadedListener postDataLoadedListener;
    String userMail, postUrl, profileUrl, username, postTitle;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        homeBinding = FragmentHomeBinding.inflate(inflater, container, false);

        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        firestore = FirebaseFirestore.getInstance();

        alertDialogBuilder = new AlertDialog.Builder(getContext());

        posts = new ArrayList<>();

        getAllPosts();

        homeBinding.AnaSayfaRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        postAdapter = new PostAdapter(posts);
        homeBinding.AnaSayfaRecycler.setAdapter(postAdapter);

        return homeBinding.getRoot();
    }

    private void getAllPosts() {
        firestore.collection("Posts").orderBy("date", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    // Hata var
                    alertDialogBuilder.setTitle("Warning");
                    alertDialogBuilder.setMessage(error.getLocalizedMessage());
                    alertDialogBuilder.setNeutralButton("Okay", null);
                    alertDialogBuilder.show();
                } else {
                    // Hata yok
                    if (value != null && !value.isEmpty()) {
                        // Değer var
                        for (DocumentSnapshot document : value.getDocuments()) {
                            Map<String, Object> data = document.getData();
                            if (data != null) {
                                // Casting ve Map'e ekleme
                                userMail = (String) data.get("userEmail");
                                postUrl = (String) data.get("postUrl");
                                profileUrl = (String) data.get("profileUrl");
                                username = (String) data.get("username");
                                postTitle = (String) data.get("postTitle");

                                if (profileUrl == null || profileUrl.equals("")){
                                    profileUrl = "https://firebasestorage.googleapis.com/v0/b/instagramclone-1dfd9.appspot.com/o/profileImages%2Fvector_ek1.png?alt=media&token=538f14d6-dda7-47d6-9b27-637395c0e531";
                                }

                                // ProfilePosts nesnesi oluşturup verileri ekleyin
                                Post tumGonderiler = new Post(userMail, postUrl, profileUrl, username, postTitle);
                                posts.add(tumGonderiler);
                            }
                        }
                        postAdapter.notifyDataSetChanged();
                        // PostData yüklendi, listener'ı çağır
                        if (postDataLoadedListener != null) {
                            postDataLoadedListener.onPostDataLoaded(posts);
                        }
                    } else {
                        // Değer yok
                        System.out.println("No post");
                    }
                }
            }
        });
    }

    public interface OnPostDataLoadedListener {
        void onPostDataLoaded(ArrayList<Post> posts);
    }
}