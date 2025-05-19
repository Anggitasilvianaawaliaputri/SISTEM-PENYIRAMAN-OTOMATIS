package com.example.sistempenyiramantanamanotomatis;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HistoryActivity extends AppCompatActivity {

    LinearLayout historyContainer;
    ImageView backButton;
    BottomNavigationView bottomNav;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        // Inisialisasi view
        historyContainer = findViewById(R.id.historyContainer);
        bottomNav = findViewById(R.id.bottom_nav);
        backButton = findViewById(R.id.back_button); // Tambahkan inisialisasi ini!

        // Tombol kembali ke halaman sebelumnya
        backButton.setOnClickListener(v -> finish());

        // Atur item yang sedang dipilih
        bottomNav.setSelectedItemId(R.id.navigation_history);

        // Listener navigasi bawah
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_dashboard) {
                startActivity(new Intent(HistoryActivity.this, DashboardActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.navigation_profile) {
                startActivity(new Intent(HistoryActivity.this, ProfileActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.navigation_history) {
                // Halaman saat ini
                return true;
            }
            return false;
        });

        // Tambahkan data dummy (simulasi)
        addHistoryItem("10-04-2025", 52, true);
        addHistoryItem("11-04-2025", 49, false);
    }

    // Fungsi menambah tampilan history secara dinamis
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
        itemLayout.setBackgroundResource(R.drawable.history_item_bg); // Drawable item (bisa berbentuk rounded bg)

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

        // Tambahkan TextView ke layout item
        itemLayout.addView(txtTanggal);
        itemLayout.addView(txtKelembaban);
        itemLayout.addView(txtStatus);

        // Tambahkan layout item ke container utama
        historyContainer.addView(itemLayout);
    }
}
