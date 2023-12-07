package com.yasingok.instagram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

import com.yasingok.instagram.databinding.ActivitySignUpBinding;

public class SignUp extends AppCompatActivity {

    private ActivitySignUpBinding signUpBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        signUpBinding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(signUpBinding.getRoot());

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
                //signUpBinding.signUpText.setText("Log in");
            }
        };
        spannableString.setSpan(clickableSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        // TextView içine SpannableString'i yerleştir
        signUpBinding.loginAlreadyText.setText(spannableString);

        // TextView'ın tıklanabilir özelliğini etkinleştir
        signUpBinding.loginAlreadyText.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public void signUp(View view){
        signUpBinding.signUpText.setText("Sign Up");
    }
}