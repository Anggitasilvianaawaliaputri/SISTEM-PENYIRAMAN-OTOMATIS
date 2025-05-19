package com.example.sistempenyiramantanamanotomatis;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.UUID;

public class ProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private StorageReference storageRef;

    private ImageView profileImageView;
    private ImageView backButton;
    private TextView nameTextView, emailTextView;
    private ProgressDialog progressDialog;

    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the status bar color to transparent for full-screen experience
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        setContentView(R.layout.activity_profile);

        // Firebase initialization
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();

        // Initialize views
        profileImageView = findViewById(R.id.profile_image);
        backButton = findViewById(R.id.back_button);
        nameTextView = findViewById(R.id.name_text);
        emailTextView = findViewById(R.id.email_text);
        progressDialog = new ProgressDialog(this);

        // Get the current user's Firebase ID
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
            loadUserData(userId);
        }

        // Set up profile image click listener
        profileImageView.setOnClickListener(v -> openImagePicker());

        // Set up back button click listener
        backButton.setOnClickListener(v -> onBackPressed());

        // Set up Bottom Navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setSelectedItemId(R.id.navigation_profile); // Select current item
        bottomNavigationView.setOnItemSelectedListener(this::onNavigationItemSelected);
    }

    // Handle Bottom Navigation Item selection
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

    // Load user data from Firestore
    private void loadUserData(String uid) {
        DocumentReference userRef = db.collection("users").document(uid);
        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String name = documentSnapshot.getString("nama");
                String email = documentSnapshot.getString("email");
                String photoUrl = documentSnapshot.getString("photoUrl");

                nameTextView.setText(name);
                emailTextView.setText(email);

                // Load the profile image using Glide
                if (photoUrl != null && !photoUrl.isEmpty()) {
                    Glide.with(this).load(photoUrl).into(profileImageView);
                } else {
                    profileImageView.setImageResource(R.drawable.my_profile_photo); // Default image
                }
            } else {
                Toast.makeText(this, "Data user tidak ditemukan", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Gagal mengambil data", Toast.LENGTH_SHORT).show();
        });
    }

    // Open image picker for profile photo
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
            uploadImageToFirebase(imageUri);
        }
    }

    // Upload image to Firebase Storage
    private void uploadImageToFirebase(Uri imageUri) {
        progressDialog.setMessage("Mengupload foto...");
        progressDialog.show();

        String fileName = "profile_photos/" + UUID.randomUUID().toString();
        StorageReference fileRef = storageRef.child(fileName);

        fileRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String photoUrl = uri.toString();
                    updatePhotoUrlInFirestore(photoUrl);
                }))
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Gagal upload foto", Toast.LENGTH_SHORT).show();
                });
    }

    // Update the photo URL in Firestore after upload
    private void updatePhotoUrlInFirestore(String photoUrl) {
        DocumentReference userRef = db.collection("users").document(userId);
        userRef.update("photoUrl", photoUrl)
                .addOnSuccessListener(unused -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Foto berhasil diperbarui", Toast.LENGTH_SHORT).show();
                    Glide.with(this).load(photoUrl).into(profileImageView);
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Gagal update URL ke Firestore", Toast.LENGTH_SHORT).show();
                });
    }
}
