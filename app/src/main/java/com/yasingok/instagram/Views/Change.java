package com.yasingok.instagram.Views;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.yasingok.instagram.databinding.ActivityChangeBinding;

public class Change extends AppCompatActivity {

    private ActivityChangeBinding changeBinding;
    FirebaseAuth auth;
    FirebaseUser firebaseUser;
    String pass1, pass2;
    private AlertDialog.Builder alertDialogBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changeBinding = ActivityChangeBinding.inflate(getLayoutInflater());
        setContentView(changeBinding.getRoot());

        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();

        alertDialogBuilder = new AlertDialog.Builder(this);
    }

    public void changePassword(View view) {
        pass1 = changeBinding.passText1.getText().toString();
        pass2 = changeBinding.passText2.getText().toString();
        if (pass1.equals("") || pass2.equals("")){
            alertDialogBuilder.setTitle("Error");
            alertDialogBuilder.setMessage("Enter passwords for changing password");
            alertDialogBuilder.setPositiveButton("Okay", null); // Tamam butonu ekleyebilirsiniz
            alertDialogBuilder.show();
        }
        else {
            if (pass1.equals(pass2)){
                // Şifreyi buna değiş
                firebaseUser.updatePassword(pass1).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){

                            Snackbar snackbar = Snackbar.make(view, "Succesfully changed your password, going to login page", Snackbar.LENGTH_INDEFINITE);
                            snackbar.show();
                            snackbar.setAction("Okay", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    auth.signOut();
                                    Intent intent = new Intent(Change.this, Login.class);
                                    startActivity(intent);
                                }
                            });
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
            else {
                alertDialogBuilder.setTitle("Error");
                alertDialogBuilder.setMessage("You wrote different passwords");
                alertDialogBuilder.setPositiveButton("Okay", null); // Tamam butonu ekleyebilirsiniz
                alertDialogBuilder.show();
            }
        }
    }

    public void backToProfile(View view) {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            // Kullanıcı oturum açıkken profil ekranına git
            Intent intentToProfile = new Intent(this, Main.class);
            startActivity(intentToProfile);
            finish();
        } else {
            // Kullanıcı oturum açık değilse giriş ekranına yönlendir
            Intent intentToLogin = new Intent(this, Login.class);
            startActivity(intentToLogin);
            finish();
        }
    }
}