package com.yasingok.instagram.Views;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.yasingok.instagram.databinding.ActivitySignUpBinding;

import java.util.HashMap;

public class SignUp extends AppCompatActivity {

    private com.yasingok.instagram.databinding.ActivitySignUpBinding signUpBinding;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private FirebaseStorage firebaseStorage;
    private StorageReference reference;
    private AlertDialog.Builder alertDialogBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        signUpBinding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(signUpBinding.getRoot());

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        reference = firebaseStorage.getReference();

        alertDialogBuilder = new AlertDialog.Builder(SignUp.this);

        // TextView içindeki metni al
        String fullText = signUpBinding.loginAlreadyText.getText().toString();

        // SpannableString oluştur
        SpannableString spannableString = new SpannableString(fullText);

        // "sign up" kısmını mavi yap
        int startIndex = fullText.indexOf("Log in");
        int endIndex = startIndex + "Log in".length();
        spannableString.setSpan(new ForegroundColorSpan(Color.BLUE), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        ClickableSpan clickableSpan = new ClickableSpan() {     // LOG IN KISMI
            @Override
            public void onClick(@NonNull View view) {
                Intent signupintent = new Intent(SignUp.this, Login.class);
                startActivity(signupintent);
            }
        };
        spannableString.setSpan(clickableSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        // TextView içine SpannableString'i yerleştir
        signUpBinding.loginAlreadyText.setText(spannableString);

        // TextView'ın tıklanabilir özelliğini etkinleştir
        signUpBinding.loginAlreadyText.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public void signUp(View view){
        String email = signUpBinding.mailText.getText().toString();
        String password = signUpBinding.passwordText.getText().toString();

        String fullName = signUpBinding.nameText.getText().toString();
        String username = signUpBinding.usernameText.getText().toString();

        // fullName'i boşluğa göre ayır
        String[] parts = fullName.split(" ");
        String name = " ";
        String surname = " ";

        if (email.equals("") || password.equals("") || fullName.equals("") || username.equals("")){
            alertDialogBuilder.setTitle("Warning");
            alertDialogBuilder.setMessage("Fill all the blanks");
            alertDialogBuilder.setPositiveButton("Okay", null); // Tamam butonu ekleyebilirsiniz
            alertDialogBuilder.show();
        }
        else{
            // Eğer boşluğa göre ayrılabiliyorsa ve en az iki parça varsa ismi ve soyismi al
            if (parts.length >= 2) {
                name = parts[0];
                surname = parts[1];
            }

            String finalSurname = surname;
            String finalName = name;
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        String userId = auth.getCurrentUser().getUid();

                        // Key value mantığı ile verileri firestore'a ekliyoruz
                        HashMap<String, Object> kayit = new HashMap<>();
                        kayit.put("userEmail", email);
                        kayit.put("name", finalName);
                        kayit.put("surname", finalSurname);
                        kayit.put("username", username);
                        kayit.put("date", FieldValue.serverTimestamp());

                        firestore.collection("Users").add(kayit).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                if (task.isSuccessful()){
                                    // Firestore kayıt başarılı oldu
                                    alertDialogBuilder.setTitle("Info");
                                    alertDialogBuilder.setMessage("Firestore Completed Succesfully");
                                    alertDialogBuilder.setPositiveButton("Okay", null); // Tamam butonu ekleyebilirsiniz
                                    alertDialogBuilder.show();
                                }
                                else {
                                    // Firestore kayıt başarısız oldu
                                    alertDialogBuilder.setTitle("Warning");
                                    alertDialogBuilder.setMessage("Failed to save data");
                                    alertDialogBuilder.setPositiveButton("Okay", null); // Tamam butonu ekleyebilirsiniz
                                    alertDialogBuilder.show();
                                }
                            }
                        });

                        // Key value mantığı ile verileri firestore'a ekliyoruz
                        HashMap<String, Object> kayitFoto = new HashMap<>();
                        kayitFoto.put("userEmail", email);
                        kayitFoto.put("profileUrl", "");
                        kayitFoto.put("date", FieldValue.serverTimestamp());

                        firestore.collection("ProfileImages").add(kayitFoto).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                if (task.isSuccessful()){
                                    // Firestore kayıt başarılı oldu
                                    alertDialogBuilder.setTitle("Info");
                                    alertDialogBuilder.setMessage("Signup Completed Succesfully");
                                    alertDialogBuilder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            Intent intent = new Intent(SignUp.this, Login.class);
                                            startActivity(intent);
                                        }
                                    }); // Tamam butonu ekleyebilirsiniz
                                    alertDialogBuilder.show();
                                }
                                else {
                                    // Firestore kayıt başarısız oldu
                                    alertDialogBuilder.setTitle("Warning");
                                    alertDialogBuilder.setMessage("Failed to save data");
                                    alertDialogBuilder.setPositiveButton("Okay", null); // Tamam butonu ekleyebilirsiniz
                                    alertDialogBuilder.show();
                                }
                            }
                        });
                    }
                    else{       // Kayıt başarısız
                        alertDialogBuilder.setTitle("Error");
                        alertDialogBuilder.setMessage(task.getException().getLocalizedMessage());
                        alertDialogBuilder.setPositiveButton("Okay", null); // Tamam butonu ekleyebilirsiniz
                        alertDialogBuilder.show();
                    }
                }
            });
        }
    }
}