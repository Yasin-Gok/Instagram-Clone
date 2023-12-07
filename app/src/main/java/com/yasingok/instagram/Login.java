package com.yasingok.instagram;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.TooltipCompat;

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
                Intent loginintent = new Intent(Login.this, SignUp.class);
                startActivity(loginintent);
                //loginBinding.OrText.setText("Sign up");
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
    }

    public void forgotPassword(View view){
        loginBinding.OrText.setText("forgotPassword");
    }

    public void login(View view){
        loginBinding.OrText.setText("Login");
    }

    public void showHide(View view){
        loginBinding.OrText.setText("showHide");
        if (loginBinding.passwordText.getInputType() == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
            // Şifre görünürse, gizle
            loginBinding.passwordText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            loginBinding.gozSimgesi.setImageResource(R.drawable.show);
        } else {
            // Şifre gizliyse, görünür yap
            loginBinding.passwordText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            loginBinding.gozSimgesi.setImageResource(R.drawable.hide);
        }
        // İmleci en sonda tutmak için
        loginBinding.passwordText.setSelection(loginBinding.passwordText.getText().length());

        // Tooltip gösterme
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String tooltipText = loginBinding.passwordText.getInputType() == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                    ? "Hide"
                    : "Show";
            TooltipCompat.setTooltipText(loginBinding.gozSimgesi, tooltipText);
        }
    }
}