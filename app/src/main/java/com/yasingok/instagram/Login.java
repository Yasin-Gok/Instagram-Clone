package com.yasingok.instagram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Toast;

import com.yasingok.instagram.databinding.ActivityLoginBinding;

public class Login extends AppCompatActivity {
    private ActivityLoginBinding loginBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loginBinding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(loginBinding.getRoot());

        // TextView içindeki metni al
        String fullText = loginBinding.signUpText.getText().toString();

        // SpannableString oluştur
        SpannableString spannableString = new SpannableString(fullText);

        // "sign up" kısmını mavi yap
        int startIndex = fullText.indexOf("Sign up");
        int endIndex = startIndex + "Sign up".length();
        spannableString.setSpan(new ForegroundColorSpan(Color.BLUE), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        ClickableSpan clickableSpan = new ClickableSpan() {     // SIGN UP KISMI
            @Override
            public void onClick(@NonNull View view) {
                loginBinding.OrText.setText("Sign up");
                //Toast.makeText(Login.this, "Sign Up clicked", Toast.LENGTH_LONG).show();
            }
        };
        spannableString.setSpan(clickableSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        // TextView içine SpannableString'i yerleştir
        loginBinding.signUpText.setText(spannableString);

        // TextView'ın tıklanabilir özelliğini etkinleştir
        loginBinding.signUpText.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public void apiLogin(View view){
        loginBinding.OrText.setText("apiLogin");
        //Toast.makeText(Login.this, "Api Login clicked", Toast.LENGTH_LONG).show();
    }

    public void forgotPassword(View view){
        loginBinding.OrText.setText("forgotPassword");
        //Toast.makeText(Login.this, "Forgot Password clicked", Toast.LENGTH_LONG).show();
    }

    public void login(View view){
        loginBinding.OrText.setText("Login");
        //Toast.makeText(Login.this, "Login clicked", Toast.LENGTH_LONG).show();
    }
}