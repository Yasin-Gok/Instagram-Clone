package com.yasingok.instagram.Fragments;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.PopupMenu;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.yasingok.instagram.Adapters.ProfileAdapter;
import com.yasingok.instagram.Classes.ProfilePosts;
import com.yasingok.instagram.R;
import com.yasingok.instagram.Views.Change;
import com.yasingok.instagram.Views.Login;
import com.yasingok.instagram.databinding.FragmentProfileBinding;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

public class ProfileFragment extends Fragment{
    private FragmentProfileBinding binding;
    private FirebaseUser firebaseUser;
    private FirebaseAuth auth;
    private AlertDialog.Builder alertDialogBuilder;
    Animation animation;
    Uri profileImage;
    private FirebaseStorage firebaseStorage;
    private FirebaseFirestore firestore;
    private StorageReference reference;
    ActivityResultLauncher<Intent> activityResultLauncher;
    ActivityResultLauncher<String> permissionLauncher;
    String targetMail, profileUsername, profileFullname, profileUrl;
    ArrayList<ProfilePosts> profilePosts;
    private OnPostDataLoadedListener postDataLoadedListener;
    ProfileAdapter profileAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false);

        registerLauncher();

        profilePosts = new ArrayList<>();

        alertDialogBuilder = new AlertDialog.Builder(getContext());

        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        firebaseStorage = FirebaseStorage.getInstance();
        firestore = FirebaseFirestore.getInstance();
        reference = firebaseStorage.getReference();
        if (firebaseUser != null){
            binding.usernameText.setText(firebaseUser.getEmail());
        }

        // Animasyon kaynağını yükle
        animation = AnimationUtils.loadAnimation(getActivity(), R.anim.animation);

        targetMail = firebaseUser.getEmail();
        fetchDataAndDisplay();
        getImage();

        getPostData();

        // Dinleyiciyi ayarla
        setOnPostDataLoadedListener(new OnPostDataLoadedListener() {
            @Override
            public void onPostDataLoaded(ArrayList<ProfilePosts> posts) {
                binding.gonderiSayi.setText(String.valueOf(posts.size()));
            }
        });

        binding.profileRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        profileAdapter = new ProfileAdapter(profilePosts);
        binding.profileRecycler.setAdapter(profileAdapter);

        binding.profil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.profil.startAnimation(animation);

                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    // İzin yok
                    if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)){
                        // İzin bilgisini ver
                        Snackbar.make(view, "NEED PERMISSION FOR GALLERY!", Snackbar.LENGTH_INDEFINITE).setAction("Give permission", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // İzin al
                                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                            }
                        }).show();
                    }
                    else{
                        // İzin al
                        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                    }
                }
                else {
                    // İzin verildi
                    Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    activityResultLauncher.launch(intentToGallery);
                }
            }
        });

        binding.editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (profileImage != null){
                    // Universal unique id, hep farklı isim olması için
                    UUID uuid = UUID.randomUUID();
                    String imageName = "profileImages/" + uuid + ".jpg";

                    reference.child(imageName).putFile(profileImage).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()){
                                // Başarılı ise

                                // foto url almak için ekstra referans yaptık
                                StorageReference newReference = firebaseStorage.getReference(imageName);
                                newReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String profilUrl = uri.toString();
                                        String userMail = firebaseUser.getEmail();
                                        firebaseUser = auth.getCurrentUser();

                                        alertDialogBuilder.setTitle("Info");
                                        alertDialogBuilder.setMessage("Photo uploaded succesfully");
                                        alertDialogBuilder.setPositiveButton("Okay", null); // Tamam butonu ekleyebilirsiniz
                                        alertDialogBuilder.show();

                                        updatePhotoByEmail(profilUrl);
                                    }
                                });
                            }
                            else {
                                // Başarısız ise
                                alertDialogBuilder.setTitle("Error");
                                alertDialogBuilder.setMessage(task.getException().getLocalizedMessage());
                                alertDialogBuilder.setPositiveButton("Okay", null); // Tamam butonu ekleyebilirsiniz
                                alertDialogBuilder.show();
                            }
                        }
                    });
                }
                else{
                    // Başarısız ise
                    alertDialogBuilder.setTitle("Error");
                    alertDialogBuilder.setMessage("Select profile image from gallery");
                    alertDialogBuilder.setPositiveButton("Okay", null); // Tamam butonu ekleyebilirsiniz
                    alertDialogBuilder.show();
                }
            }
        });

        binding.menuImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.menuImage.startAnimation(animation);

                PopupMenu popupMenu = new PopupMenu(getContext(), view);
                popupMenu.getMenuInflater().inflate(R.menu.exit_menu, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        if (menuItem.getItemId() == R.id.quit) {
                            // "Quit" menü öğesine tıklandığında yapılacak işlemler
                            alertDialogBuilder.setTitle("Info");
                            alertDialogBuilder.setMessage("Are you sure?");
                            alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    auth.signOut();
                                    Intent intent = new Intent(getContext(), Login.class);
                                    startActivity(intent);
                                }
                            }).setNegativeButton("No", null); // Geri butonu ekleyebilirsiniz
                            alertDialogBuilder.show();
                            return true;
                        }
                        else if (menuItem.getItemId() == R.id.verify) {
                            // "Verify" menü öğesine tıklandığında yapılacak işlemler
                            auth.getCurrentUser();

                            if (firebaseUser.isEmailVerified()){
                                alertDialogBuilder.setTitle("Info");
                                alertDialogBuilder.setMessage("Your email has been verified already");
                                alertDialogBuilder.setPositiveButton("Okay", null); // Tamam butonu ekleyebilirsiniz
                                alertDialogBuilder.show();
                            }
                            else{
                                String eMail = firebaseUser.getEmail();
                                firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            alertDialogBuilder.setTitle("Info");
                                            alertDialogBuilder.setMessage("Verify message sent to:" + eMail);
                                            alertDialogBuilder.setPositiveButton("Okay", null); // Tamam butonu ekleyebilirsiniz
                                            alertDialogBuilder.show();
                                        }
                                        else {
                                            alertDialogBuilder.setTitle("Warning");
                                            alertDialogBuilder.setMessage("Verify message couldn't send");
                                            alertDialogBuilder.setPositiveButton("Okay", null); // Tamam butonu ekleyebilirsiniz
                                            alertDialogBuilder.show();
                                        }
                                    }
                                });
                            }
                            return true;
                        }
                        else if (menuItem.getItemId() == R.id.changePass) {
                            // "Change password" menü öğesine tıklandığında yapılacak işlemler
                            Intent intentToChange = new Intent(getActivity(), Change.class);
                            startActivity(intentToChange);
                            return true;
                        }
                        else {
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

    public void registerLauncher(){
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult o) {
                // Burada galeriye girdikten sonra karşılaşılabilecek iptal, seçmedi vb durumlarını inceliyoruz
                if (o.getResultCode() == Activity.RESULT_OK){
                    Intent intentFromResult = o.getData();
                    if(intentFromResult != null){
                        profileImage = intentFromResult.getData();     // Bu seçilen görselin yerini veriyor uri olarak
                        binding.profil.setImageURI(profileImage);   // Yeri lazımsa bu kullanılabilir
                    }
                }
            }
        });

        permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean o) {
                if (o){         // İzin verildiyse
                    Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    activityResultLauncher.launch(intentToGallery);     // Başlatmak için
                }
                else{
                    Snackbar.make(getView(), "Permission needed!", Snackbar.ANIMATION_MODE_SLIDE).show();
                }
            }
        });
    }

    private void fetchDataAndDisplay() {
        firestore.collection("Users").whereEqualTo("userEmail", targetMail)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
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
                                DocumentSnapshot document = value.getDocuments().get(0);
                                Map<String, Object> sonuc = document.getData();

                                // Casting
                                profileUsername = (String) sonuc.get("username");
                                String profileName = (String) sonuc.get("name");
                                String profileSurname = (String) sonuc.get("surname");

                                profileFullname = profileName + " " + profileSurname;

                                displayData(profileUsername, profileFullname);
                            } else {
                                // Değer yok
                                alertDialogBuilder.setTitle("Warning");
                                alertDialogBuilder.setMessage("No data found");
                                alertDialogBuilder.setNeutralButton("Okay", null);
                                alertDialogBuilder.show();
                            }
                        }
                    }
                });
    }

    private void displayData(String username, String fullname) {
        if (username != null || fullname != null) {
            // İkisi de var
            binding.usernameText.setText(username);
            binding.nameText.setText(fullname);
        } else {
            alertDialogBuilder.setTitle("Warning");
            alertDialogBuilder.setMessage("Couldn't get user data");
            alertDialogBuilder.setNeutralButton("Okay", null);
            alertDialogBuilder.show();
        }
    }

    private void getImage() {
        firestore.collection("ProfileImages").whereEqualTo("userEmail", targetMail)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            // Hata var
                            System.out.println(error.getLocalizedMessage());
                        } else {
                            // Hata yok
                            if (value != null && !value.isEmpty()) {
                                // Değer var
                                DocumentSnapshot document = value.getDocuments().get(0);
                                Map<String, Object> sonuc = document.getData();

                                // Casting
                                profileUrl = (String) sonuc.get("profileUrl");

                                displayImage(profileUrl);
                            } else {
                                // Değer yok
                                binding.profil.setImageResource(R.drawable.vector_ek1);
                            }
                        }
                    }
                });
    }

    private void displayImage(String photoUrl) {
        if (photoUrl != null && !photoUrl.equals("")) {
            // Foto var
            System.out.println(photoUrl);
            Picasso.get().load(photoUrl).into(binding.profil);
        }
        else {
            binding.profil.setImageResource(R.drawable.vector_ek1);
        }
    }

    // Belirli bir e-posta adresine sahip belgeleri güncellemek için tek fonksiyon
    public void updatePhotoByEmail(String newUrl) {
        // "ProfileImages" koleksiyonunda userEmail alanına göre belgeleri sorgula
        Query query = firestore.collection("ProfileImages").whereEqualTo("userEmail", targetMail);

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    // Sorgu başarıyla tamamlandı, eşleşen belgeleri al
                    for (DocumentSnapshot document : task.getResult().getDocuments()) {
                        // Her bir belgeyi güncelle
                        document.getReference().update("profileUrl", newUrl)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Picasso.get().load(newUrl).into(binding.profil);
                                        System.out.println("Belge başarıyla güncellendi.");
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        System.out.println(e.getLocalizedMessage());
                                        binding.profil.setImageResource(R.drawable.vector_ek1);
                                    }
                                });
                    }
                }
                else {
                    System.out.println(task.getException().getLocalizedMessage());                }
            }
        });
    }

    private void getPostData(){
        firestore.collection("Posts").whereEqualTo("userEmail", targetMail).addSnapshotListener(new EventListener<QuerySnapshot>() {
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
                                String tempTitle = (String) data.get("postTitle");
                                String tempUrl = (String) data.get("postUrl");

                                // ProfilePosts nesnesi oluşturup verileri ekleyin
                                ProfilePosts gonderi = new ProfilePosts(tempUrl, profileUsername, tempTitle, targetMail);
                                profilePosts.add(gonderi);
                            }
                        }
                        profileAdapter.notifyDataSetChanged();
                        // PostData yüklendi, listener'ı çağır
                        if (postDataLoadedListener != null) {
                            postDataLoadedListener.onPostDataLoaded(profilePosts);
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
        void onPostDataLoaded(ArrayList<ProfilePosts> posts);
    }

    public void setOnPostDataLoadedListener(OnPostDataLoadedListener listener) {
        this.postDataLoadedListener = listener;
    }
}