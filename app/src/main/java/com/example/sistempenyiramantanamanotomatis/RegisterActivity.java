package com.example.sistempenyiramantanamanotomatis;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    EditText editTextNama, editTextEmail, editTextPassword;
    Button buttonDaftar;
    ImageView showPasswordIcon;
    TextView textMasukLink;

    boolean isPasswordVisible = false;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Inisialisasi Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Inisialisasi elemen UI
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
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
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
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                if (user != null) {
                                    String uid = user.getUid();

                                    // Buat data pengguna
                                    Map<String, Object> userData = new HashMap<>();
                                    userData.put("nama", nama);
                                    userData.put("email", email);

                                    // Simpan ke Firestore
                                    db.collection("users").document(uid)
                                            .set(userData)
                                            .addOnSuccessListener(aVoid -> {
                                                Toast.makeText(RegisterActivity.this, "Pendaftaran berhasil!", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                                finish();
                                            })
                                            .addOnFailureListener(e -> {
                                                Toast.makeText(RegisterActivity.this, "Gagal menyimpan data: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                            });
                                }
                            } else {
                                Toast.makeText(RegisterActivity.this, "Gagal daftar: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });
    }
}
