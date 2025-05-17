package com.example.sistempenyiramantanamanotomatis;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION = 3000; // 3 detik
    private ProgressBar loadingProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Menemukan ProgressBar dari layout
        loadingProgressBar = findViewById(R.id.loadingProgressBar);

        // Menampilkan ProgressBar
        loadingProgressBar.setVisibility(View.VISIBLE);

        // Handler untuk delay
        new Handler().postDelayed(() -> {
            // Menyembunyikan ProgressBar
            loadingProgressBar.setVisibility(View.GONE);

            // Pindah ke LoginActivity setelah splash
            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }, SPLASH_DURATION); // Delay selama 3 detik
    }
}
