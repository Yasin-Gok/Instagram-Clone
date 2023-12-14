package com.yasingok.instagram.Views;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.TooltipCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.yasingok.instagram.R;
import com.yasingok.instagram.databinding.ActivityLoginBinding;

public class Login extends AppCompatActivity {
    private ActivityLoginBinding loginBinding;
    private FirebaseAuth auth;
    private AlertDialog.Builder alertDialogBuilder;
    private Intent intentLogin;
    Animation animation;;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loginBinding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(loginBinding.getRoot());

        auth = FirebaseAuth.getInstance();

        FirebaseUser firebaseUser = auth.getCurrentUser();
        if (firebaseUser != null){
            intentLogin = new Intent(Login.this, Main.class);
            startActivity(intentLogin);
        }

        alertDialogBuilder = new AlertDialog.Builder(Login.this);

        // Animasyon kaynağını yükle
        animation = AnimationUtils.loadAnimation(this, R.anim.animation);

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
            }
        };
        spannableString.setSpan(clickableSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        // TextView içine SpannableString'i yerleştir
        loginBinding.signUpText.setText(spannableString);

        // TextView'ın tıklanabilir özelliğini etkinleştir
        loginBinding.signUpText.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public void apiLogin(View view){
        alertDialogBuilder.setTitle("Info");
        alertDialogBuilder.setMessage("ApiLogin Completed Succesfully");
        alertDialogBuilder.setPositiveButton("Okay", null); // Tamam butonu ekleyebilirsiniz
        alertDialogBuilder.show();
    }

    public void forgotPassword(View view){
        Intent intentToPassword = new Intent(this, Password.class);
        startActivity(intentToPassword);
    }

    public void login(View view){
        loginBinding.loginLayout.startAnimation(animation);

        String mail = loginBinding.mailText.getText().toString();
        String pass = loginBinding.passwordText.getText().toString();

        if (mail.equals("") || pass.equals("")){
            alertDialogBuilder.setTitle("Warning");
            alertDialogBuilder.setMessage("Fill all the blanks");
            alertDialogBuilder.setPositiveButton("Okay", null); // Tamam butonu ekleyebilirsiniz
            alertDialogBuilder.show();
        }
        else {
            auth.signInWithEmailAndPassword(mail, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        alertDialogBuilder.setTitle("Info");
                        alertDialogBuilder.setMessage("Login Completed Succesfully");
                        alertDialogBuilder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                intentLogin = new Intent(Login.this, Main.class);
                                startActivity(intentLogin);
                                finish();
                            }
                        }); // Tamam butonu ekleyebilirsiniz
                        alertDialogBuilder.show();
                    }
                    else{
                        alertDialogBuilder.setTitle("Error");
                        alertDialogBuilder.setMessage(task.getException().getLocalizedMessage());
                        alertDialogBuilder.setPositiveButton("Okay", null); // Tamam butonu ekleyebilirsiniz
                        alertDialogBuilder.show();
                    }
                }
            });
        }
    }

    public void showHide(View view){
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