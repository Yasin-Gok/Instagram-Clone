package com.yasingok.instagram;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.yasingok.instagram.databinding.FragmentUploadBinding;

import java.util.HashMap;
import java.util.UUID;

public class UploadFragment extends Fragment {

    private FragmentUploadBinding binding;
    ActivityResultLauncher<Intent> activityResultLauncher;
    ActivityResultLauncher<String> permissionLauncher;
    Uri imageData;
    Activity activity;
    ContentResolver contentResolver;
    private FirebaseStorage firebaseStorage;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private StorageReference reference;
    private AlertDialog.Builder alertDialogBuilder;
    Animation animation;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentUploadBinding.inflate(inflater, container, false);

        activity = getActivity();
        contentResolver = activity.getContentResolver();

        registerLauncher();

        firebaseStorage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        reference = firebaseStorage.getReference();

        alertDialogBuilder = new AlertDialog.Builder(getActivity());

        // Animasyon kaynağını yükle
        animation = AnimationUtils.loadAnimation(getActivity(), R.anim.animation);

        binding.uploadbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {            // UPLOAD BUTTON
                // Düğmeye animasyonu uygula
                binding.uploadbutton.startAnimation(animation);

                if (imageData != null){
                    // Fotoğraf seçilmişse              // child ile klasör oluşturup ona yüklüyoruz

                    if (binding.titleText.getText().toString().equals("")){
                        alertDialogBuilder.setTitle("Error");
                        alertDialogBuilder.setMessage("Enter an title for post");
                        alertDialogBuilder.setPositiveButton("Okay", null); // Tamam butonu ekleyebilirsiniz
                        alertDialogBuilder.show();
                    }
                    else {
                        // Universal unique id, hep farklı isim olması için
                        UUID uuid = UUID.randomUUID();
                        String imageName = "images/" + uuid + ".jpg";

                        reference.child(imageName).putFile(imageData).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if (task.isSuccessful()){
                                    // Başarılı ise

                                    // foto url almak için ekstra referans yaptık
                                    StorageReference newReference = firebaseStorage.getReference(imageName);
                                    newReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            String downloadUrl = uri.toString();
                                            String postTitle = binding.titleText.getText().toString();
                                            FirebaseUser user = auth.getCurrentUser();
                                            String email = user.getEmail();

                                            // Key value mantığı ile verileri firestore'a ekliyoruz
                                            HashMap<String, Object> hashMap = new HashMap<>();
                                            hashMap.put("userEmail", email);
                                            hashMap.put("postTitle", postTitle);
                                            hashMap.put("postUrl", downloadUrl);
                                            hashMap.put("date", FieldValue.serverTimestamp());

                                            firestore.collection("Posts").add(hashMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                                    if (task.isSuccessful()){
                                                        alertDialogBuilder.setTitle("Info");
                                                        alertDialogBuilder.setMessage("Datas uploaded succesfully");
                                                        alertDialogBuilder.setPositiveButton("Okay", null); // Tamam butonu ekleyebilirsiniz
                                                        alertDialogBuilder.show();
                                                    }
                                                    else {
                                                        alertDialogBuilder.setTitle("Error");
                                                        alertDialogBuilder.setMessage(task.getException().getLocalizedMessage());
                                                        alertDialogBuilder.setPositiveButton("Okay", null); // Tamam butonu ekleyebilirsiniz
                                                        alertDialogBuilder.show();
                                                    }
                                                }
                                            });
                                        }
                                    });
                                    alertDialogBuilder.setTitle("Info");
                                    alertDialogBuilder.setMessage("Photo uploaded succesfully");
                                    alertDialogBuilder.setPositiveButton("Okay", null); // Tamam butonu ekleyebilirsiniz
                                    alertDialogBuilder.show();
                                }
                                else{
                                    // Başarısız ise
                                    alertDialogBuilder.setTitle("Error");
                                    alertDialogBuilder.setMessage(task.getException().getLocalizedMessage());
                                    alertDialogBuilder.setPositiveButton("Okay", null); // Tamam butonu ekleyebilirsiniz
                                    alertDialogBuilder.show();
                                }
                            }
                        });
                    }
                }
                else{
                    // Başarısız ise
                    alertDialogBuilder.setTitle("Error");
                    alertDialogBuilder.setMessage("Select image from gallery");
                    alertDialogBuilder.setPositiveButton("Okay", null); // Tamam butonu ekleyebilirsiniz
                    alertDialogBuilder.show();
                }
            }
        });

        binding.imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {            // GALLERY BUTTON

                binding.imageButton.startAnimation(animation);

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
                else{
                    // İzin verildi
                    Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    activityResultLauncher.launch(intentToGallery);
                }
            }
        });
        return binding.getRoot();
    }

    public void registerLauncher(){
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult o) {
                // Burada galeriye girdikten sonra karşılaşılabilecek iptal, seçmedi vb durumlarını inceliyoruz
                if (o.getResultCode() == Activity.RESULT_OK){
                    Intent intentFromResult = o.getData();
                    if(intentFromResult != null){
                        imageData = intentFromResult.getData();     // Bu seçilen görselin yerini veriyor uri olarak
                        binding.imageButton.setImageURI(imageData);   // Yeri lazımsa bu kullanılabilir
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
}