package com.example.sistempenyiramantanamanotomatis;

import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.switchmaterial.SwitchMaterial;

public class DashboardActivity extends AppCompatActivity {

    private ProgressBar progressMoisture;
    private TextView txtProgressPercentage;
    private TextView txtPumpStatus, txtStatusTanah;
    private TextView txtOnLabel, txtOffLabel;
    private SwitchMaterial switchAuto;

    private int simulatedMoisture = 65;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Bind View
        progressMoisture = findViewById(R.id.progress_moisture);
        txtProgressPercentage = findViewById(R.id.progress_text);
        txtPumpStatus = findViewById(R.id.txt_pump_status);
        txtStatusTanah = findViewById(R.id.txt_status_tanah);
        txtOnLabel = findViewById(R.id.text_on);
        txtOffLabel = findViewById(R.id.text_off);
        switchAuto = findViewById(R.id.switch_auto);

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

    // Update text di tengah progress
    private void updateProgressText(int value) {
        txtProgressPercentage.setText(value + "%");
    }

    // Update status tanah
    private void updateSoilStatus(int moistureValue) {
        if (moistureValue >= 70) {
            txtStatusTanah.setText("Basah");
        } else if (moistureValue >= 40) {
            txtStatusTanah.setText("Cukup");
        } else {
            txtStatusTanah.setText("Kering");
        }
    }

    // Update label ON/OFF
    private void updateSwitchLabels(boolean isChecked) {
        if (isChecked) {
            txtOnLabel.setTextColor(ContextCompat.getColor(this, android.R.color.white));
            txtOffLabel.setTextColor(ContextCompat.getColor(this, android.R.color.darker_gray));
        } else {
            txtOnLabel.setTextColor(ContextCompat.getColor(this, android.R.color.darker_gray));
            txtOffLabel.setTextColor(ContextCompat.getColor(this, android.R.color.white));
        }
    }

    // Simulasi kelembaban berubah tiap 5 detik
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