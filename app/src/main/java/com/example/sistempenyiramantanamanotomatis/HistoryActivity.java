package com.example.sistempenyiramantanamanotomatis;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

import androidx.appcompat.app.AppCompatActivity;

public class HistoryActivity extends AppCompatActivity {

    LinearLayout historyContainer;
    LinearLayout navDashboard, navHistory, navProfile;
    ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        // Inisialisasi View
        historyContainer = findViewById(R.id.historyContainer);
        navDashboard = findViewById(R.id.nav_dashboard);
        navHistory = findViewById(R.id.nav_history);
        navProfile = findViewById(R.id.nav_profile);
        backButton = findViewById(R.id.backButton);

        // Tombol kembali
        backButton.setOnClickListener(v -> finish());

        // Navigasi ke halaman lain
        navDashboard.setOnClickListener(v -> {
            Intent intent = new Intent(HistoryActivity.this, DashboardActivity.class);
            startActivity(intent);
            finish();
        });

        navProfile.setOnClickListener(v -> {
            Intent intent = new Intent(HistoryActivity.this, ProfileActivity.class);
            startActivity(intent);
            finish();
        });

        // Data dummy - bisa diganti dengan data dari Firebase/SQLite
        addHistoryItem("10-04-2025", 52, true);
        addHistoryItem("11-04-2025", 49, false);
    }

    private void addHistoryItem(String tanggal, int kelembaban, boolean aktif) {
        LinearLayout itemLayout = new LinearLayout(this);
        itemLayout.setOrientation(LinearLayout.VERTICAL);
        LayoutParams params = new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, 24);
        itemLayout.setLayoutParams(params);
        itemLayout.setPadding(24, 24, 24, 24);
        itemLayout.setBackgroundResource(R.drawable.history_item_bg); // Gunakan drawable bulat hijau muda

        TextView txtTanggal = new TextView(this);
        txtTanggal.setText("Tanggal : " + tanggal);
        txtTanggal.setTextSize(16);
        txtTanggal.setTextColor(getResources().getColor(android.R.color.black));
        txtTanggal.setPadding(0, 0, 0, 8);

        TextView txtKelembaban = new TextView(this);
        txtKelembaban.setText("Kelembaban Tanah : " + kelembaban + "%");
        txtKelembaban.setTextColor(getResources().getColor(android.R.color.black));
        txtKelembaban.setPadding(0, 0, 0, 4);

        TextView txtStatus = new TextView(this);
        txtStatus.setText("Penyiraman " + (aktif ? "aktif" : "tidak aktif"));
        txtStatus.setTextColor(getResources().getColor(android.R.color.black));

        // Tambahkan ke layout item
        itemLayout.addView(txtTanggal);
        itemLayout.addView(txtKelembaban);
        itemLayout.addView(txtStatus);

        // Tambahkan ke container utama
        historyContainer.addView(itemLayout);
    }
}
