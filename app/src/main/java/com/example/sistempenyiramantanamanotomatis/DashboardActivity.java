package com.example.sistempenyiramantanamanotomatis;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class DashboardActivity extends AppCompatActivity {

    private ProgressBar progressMoisture;
    private TextView txtProgressPercentage;
    private TextView txtPumpStatus, txtStatusTanah;
    private TextView txtOnLabel, txtOffLabel;
    private SwitchMaterial switchAuto;

    private int simulatedMoisture = 65;
    private Handler handler = new Handler();

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Bind Views
        progressMoisture = findViewById(R.id.progress_moisture);
        txtProgressPercentage = findViewById(R.id.progress_text); // Pastikan ada TextView di layout
        txtPumpStatus = findViewById(R.id.txt_pump_status);
        txtStatusTanah = findViewById(R.id.txt_status_tanah);
        txtOnLabel = findViewById(R.id.text_on);  // Tambahkan ke XML jika belum ada
        txtOffLabel = findViewById(R.id.text_off); // Tambahkan ke XML jika belum ada
        switchAuto = findViewById(R.id.switch_auto);

        // BottomNavigationView
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.navigation_dashboard:
                    Toast.makeText(this, "Dashboard dipilih", Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.navigation_history:
                    Toast.makeText(this, "History dipilih", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, HistoryActivity.class));
                    return true;
                case R.id.navigation_profile:
                    Toast.makeText(this, "Profile dipilih", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, ProfileActivity.class));
                    return true;
                default:
                    return false;
            }
        });

        // Inisialisasi awal
        progressMoisture.setProgress(simulatedMoisture);
        updateProgressText(simulatedMoisture);
        updateSoilStatus(simulatedMoisture);
        updateSwitchLabels(switchAuto.isChecked());

        // Switch Listener
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

        // Jalankan simulasi kelembaban
        simulateMoistureChange();
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

    private void simulateMoistureChange() {
        handler.postDelayed(() -> {
            simulatedMoisture += (Math.random() > 0.5 ? 5 : -5);
            simulatedMoisture = Math.max(0, Math.min(simulatedMoisture, 100));

            progressMoisture.setProgress(simulatedMoisture);
            updateProgressText(simulatedMoisture);
            updateSoilStatus(simulatedMoisture);

            simulateMoistureChange();
        }, 5000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}
