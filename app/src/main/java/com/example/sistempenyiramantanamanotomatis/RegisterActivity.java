package com.example.sistempenyiramantanamanotomatis;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    EditText editTextNama, editTextEmail, editTextPassword;
    Button buttonDaftar;
    ImageView showPasswordIcon;
    TextView textMasukLink;

    boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Inisialisasi elemen
        editTextNama = findViewById(R.id.editTextNama);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonDaftar = findViewById(R.id.buttonDaftar);
        showPasswordIcon = findViewById(R.id.showPasswordIcon);
        textMasukLink = findViewById(R.id.textMasukLink);

        // Toggle visibilitas password
        showPasswordIcon.setOnClickListener(v -> {
            if (isPasswordVisible) {
                editTextPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                showPasswordIcon.setImageResource(R.drawable.ic_visibility_off);
            } else {
                editTextPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                showPasswordIcon.setImageResource(R.drawable.ic_visibility);
            }
            isPasswordVisible = !isPasswordVisible;
            editTextPassword.setSelection(editTextPassword.getText().length());
        });

        // Navigasi ke halaman login
        textMasukLink.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        // Tombol Daftar
        buttonDaftar.setOnClickListener(v -> {
            String nama = editTextNama.getText().toString().trim();
            String email = editTextEmail.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();

            if (nama.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(RegisterActivity.this, "Semua field harus diisi", Toast.LENGTH_SHORT).show();
            } else {
                // Di sini nanti bisa ditambahkan Firebase atau SQLite
                Toast.makeText(RegisterActivity.this, "Pendaftaran berhasil!", Toast.LENGTH_SHORT).show();

                // Setelah daftar, pindah ke login
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
