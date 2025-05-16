package com.example.sistempenyiramantanamanotomatis;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private ImageView showPasswordIcon;
    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        showPasswordIcon = findViewById(R.id.showPasswordIcon);
        Button loginButton = findViewById(R.id.loginButton);
        TextView registerText = findViewById(R.id.registerText);

        showPasswordIcon.setOnClickListener(v -> {
            if (isPasswordVisible) {
                passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                isPasswordVisible = false;
            } else {
                passwordEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                isPasswordVisible = true;
            }
            passwordEditText.setSelection(passwordEditText.getText().length());
        });

        loginButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email dan password harus diisi", Toast.LENGTH_SHORT).show();
            } else {
                // Proses login (sementara langsung ke MainActivity)
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            }
        });

        registerText.setOnClickListener(v -> {
            // Intent ke halaman register
            Toast.makeText(this, "Pindah ke halaman daftar", Toast.LENGTH_SHORT).show();
        });
    }
}
