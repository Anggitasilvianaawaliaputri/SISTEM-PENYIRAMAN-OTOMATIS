package com.example.sistempenyiramantanamanotomatis;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final String PROFILE_IMAGE_NAME = "profile_image.jpg";

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private ImageView profileImageView;
    private ImageView backButton;
    private TextView nameTextView, emailTextView;
    private ProgressDialog progressDialog;
    private Button logoutButton;

    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(Color.TRANSPARENT);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        profileImageView = findViewById(R.id.profile_image);
        backButton = findViewById(R.id.back_button);
        nameTextView = findViewById(R.id.name_text);
        emailTextView = findViewById(R.id.email_text);
        logoutButton = findViewById(R.id.btn_logout);
        progressDialog = new ProgressDialog(this);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
            loadUserData(userId);
        }

        profileImageView.setOnClickListener(v -> openImagePicker());
        backButton.setOnClickListener(v -> onBackPressed());

        logoutButton.setOnClickListener(v -> {
            mAuth.signOut();
            Toast.makeText(ProfileActivity.this, "Berhasil logout", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setSelectedItemId(R.id.navigation_profile);
        bottomNavigationView.setOnItemSelectedListener(this::onNavigationItemSelected);
    }

    private boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.navigation_dashboard) {
            startActivity(new Intent(this, DashboardActivity.class));
            return true;
        } else if (id == R.id.navigation_history) {
            startActivity(new Intent(this, HistoryActivity.class));
            return true;
        } else if (id == R.id.navigation_profile) {
            return true;
        }
        return false;
    }

    private void loadUserData(String uid) {
        DocumentReference userRef = db.collection("users").document(uid);
        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String name = documentSnapshot.getString("nama");
                String email = documentSnapshot.getString("email");

                nameTextView.setText(name);
                emailTextView.setText(email);

                File imageFile = new File(getFilesDir(), PROFILE_IMAGE_NAME);
                if (imageFile.exists()) {
                    Glide.with(this)
                            .load(imageFile)
                            .circleCrop()
                            .into(profileImageView);
                } else {
                    Glide.with(this)
                            .load(R.drawable.my_profile_photo)
                            .circleCrop()
                            .into(profileImageView);
                }
            } else {
                Toast.makeText(this, "Data user tidak ditemukan", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Gagal mengambil data", Toast.LENGTH_SHORT).show();
        });
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Pilih Foto Profil"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            saveImageLocally(imageUri);
        }
    }

    private void saveImageLocally(Uri imageUri) {
        progressDialog.setMessage("Menyimpan foto...");
        progressDialog.show();

        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);

            File file = new File(getFilesDir(), PROFILE_IMAGE_NAME);
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.close();

            progressDialog.dismiss();
            Toast.makeText(this, "Foto berhasil disimpan secara lokal", Toast.LENGTH_SHORT).show();

            Glide.with(this)
                    .load(file)
                    .circleCrop()
                    .into(profileImageView);
        } catch (IOException e) {
            progressDialog.dismiss();
            Toast.makeText(this, "Gagal menyimpan foto", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}