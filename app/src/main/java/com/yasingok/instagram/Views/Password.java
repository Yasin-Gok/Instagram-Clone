package com.yasingok.instagram.Views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.yasingok.instagram.databinding.ActivityPasswordBinding;

public class Password extends AppCompatActivity {

    private ActivityPasswordBinding passwordBinding;
    FirebaseAuth auth;
    String email;
    private AlertDialog.Builder alertDialogBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        passwordBinding = ActivityPasswordBinding.inflate(getLayoutInflater());
        setContentView(passwordBinding.getRoot());

        auth = FirebaseAuth.getInstance();
        alertDialogBuilder = new AlertDialog.Builder(this);
    }

    public void backToLogin(View view) {
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
        finish();
    }

    public void resetPassword(View view) {
        email = passwordBinding.mailText.getText().toString();

        if (email.equals("")){
            alertDialogBuilder.setTitle("Info");
            alertDialogBuilder.setMessage("Enter your mail for reset password");
            alertDialogBuilder.setPositiveButton("Okay", null); // Tamam butonu ekleyebilirsiniz
            alertDialogBuilder.show();
        }
        else {
            auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        alertDialogBuilder.setTitle("Info");
                        alertDialogBuilder.setMessage("Succesfully sent reset mail");
                        alertDialogBuilder.setPositiveButton("Okay", null); // Tamam butonu ekleyebilirsiniz
                        alertDialogBuilder.show();
                    }
                    else {
                        alertDialogBuilder.setTitle("Warning");
                        alertDialogBuilder.setMessage(task.getException().getLocalizedMessage());
                        alertDialogBuilder.setPositiveButton("Okay", null); // Tamam butonu ekleyebilirsiniz
                        alertDialogBuilder.show();
                    }
                }
            });
        }
    }
}