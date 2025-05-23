package com.example.sistempenyiramantanamanotomatis;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DashboardActivity extends AppCompatActivity {

    private ProgressBar progressMoisture;
    private TextView txtProgressPercentage;
    private TextView txtPumpStatus, txtStatusTanah;
    private TextView txtOnLabel, txtOffLabel;
    private SwitchMaterial switchAuto;

    private DatabaseReference moistureRef;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Bind Views
        progressMoisture = findViewById(R.id.progress_moisture);
        txtProgressPercentage = findViewById(R.id.progress_text);
        txtPumpStatus = findViewById(R.id.txt_pump_status);
        txtStatusTanah = findViewById(R.id.txt_status_tanah);
        txtOnLabel = findViewById(R.id.text_on);
        txtOffLabel = findViewById(R.id.text_off);
        switchAuto = findViewById(R.id.switch_auto);

        // BottomNavigationView setup
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setSelectedItemId(R.id.navigation_dashboard);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.navigation_dashboard) {
                return true;
            } else if (itemId == R.id.navigation_history) {
                startActivity(new Intent(DashboardActivity.this, HistoryActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.navigation_profile) {
                startActivity(new Intent(DashboardActivity.this, ProfileActivity.class));
                finish();
                return true;
            } else {
                return false;
            }
        });

        // Firebase Realtime Database reference
        moistureRef = FirebaseDatabase.getInstance("https://sistem-penyiraman-otomat-4bdd3-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("sensor/soil_moisture"); // Pastikan path data kelembaban di database adalah "moisture"

        // Listen perubahan data kelembaban secara realtime
        moistureRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Integer moistureValue = snapshot.getValue(Integer.class);
                if (moistureValue != null) {
                    updateUIWithMoisture(moistureValue);
                } else {
                    Toast.makeText(DashboardActivity.this, "Data kelembaban kosong", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DashboardActivity.this, "Gagal mengambil data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        updateSwitchLabels(switchAuto.isChecked());

        switchAuto.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                Toast.makeText(this, "Penyiraman Otomatis Aktif", Toast.LENGTH_SHORT).show();
                txtPumpStatus.setText("Pompa Menyala");
            } else {
                Toast.makeText(this, "Penyiraman Otomatis Nonaktif", Toast.LENGTH_SHORT).show();
                txtPumpStatus.setText("Pompa Mati");
            }
            updateSwitchLabels(isChecked);
        });
    }

    private void updateUIWithMoisture(int moisture) {
        progressMoisture.setProgress(moisture);
        updateProgressText(moisture);
        updateSoilStatus(moisture);
    }

    private void updateProgressText(int value) {
        txtProgressPercentage.setText(value + "%");
    }

    private void updateSoilStatus(int moistureValue) {
        if (moistureValue >= 70) {
            txtStatusTanah.setText("Basah");
        } else if (moistureValue >= 40) {
            txtStatusTanah.setText("Cukup");
        } else {
            txtStatusTanah.setText("Kering");
        }
    }

    private void updateSwitchLabels(boolean isChecked) {
        if (txtOnLabel == null || txtOffLabel == null) return;

        if (isChecked) {
            txtOnLabel.setTextColor(ContextCompat.getColor(this, android.R.color.white));
            txtOffLabel.setTextColor(ContextCompat.getColor(this, android.R.color.darker_gray));
        } else {
            txtOnLabel.setTextColor(ContextCompat.getColor(this, android.R.color.darker_gray));
            txtOffLabel.setTextColor(ContextCompat.getColor(this, android.R.color.white));
        }
    }
}
