package com.example.sistempenyiramantanamanotomatis;

import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class DashboardActivity extends AppCompatActivity {

    private ProgressBar progressMoisture;
    private Switch switchAuto;
    private TextView txtPumpStatus, txtStatusTanah;
    private TextView txtOnLabel, txtOffLabel; // Tambahan label ON/OFF

    private int simulatedMoisture = 65; // nilai awal (bisa diganti dari database nantinya)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Inisialisasi view dari XML
        progressMoisture = findViewById(R.id.progress_moisture);
        switchAuto = findViewById(R.id.switch_auto);
        txtPumpStatus = findViewById(R.id.txt_pump_status);
        txtStatusTanah = findViewById(R.id.txt_status_tanah);
        txtOnLabel = findViewById(R.id.text_on);   // Ambil TextView ON
        txtOffLabel = findViewById(R.id.text_off); // Ambil TextView OFF

        // Set nilai awal progress dan status tanah
        progressMoisture.setProgress(simulatedMoisture);
        updateSoilStatus(simulatedMoisture);

        // Set label awal switch
        updateSwitchLabels(switchAuto.isChecked());

        // Listener untuk Switch
        switchAuto.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                Toast.makeText(this, "Penyiraman Otomatis Aktif", Toast.LENGTH_SHORT).show();
                txtPumpStatus.setText("Sedang Menyiram");
            } else {
                Toast.makeText(this, "Penyiraman Otomatis Nonaktif", Toast.LENGTH_SHORT).show();
                txtPumpStatus.setText("Pompa Mati");
            }

            updateSwitchLabels(isChecked);
        });

        // Simulasi perubahan kelembaban
        simulateMoistureChange();
    }

    private void updateSoilStatus(int moistureValue) {
        if (moistureValue >= 70) {
            txtStatusTanah.setText("Tanah Basah");
        } else if (moistureValue >= 40) {
            txtStatusTanah.setText("Tanah Cukup");
        } else {
            txtStatusTanah.setText("Tanah Kering");
        }
    }

    private void simulateMoistureChange() {
        new Handler().postDelayed(() -> {
            simulatedMoisture += (Math.random() > 0.5 ? 5 : -5);
            if (simulatedMoisture < 0) simulatedMoisture = 0;
            if (simulatedMoisture > 100) simulatedMoisture = 100;

            progressMoisture.setProgress(simulatedMoisture);
            updateSoilStatus(simulatedMoisture);

            simulateMoistureChange();
        }, 5000);
    }

    // Fungsi untuk memperbarui tampilan teks ON/OFF sesuai status Switch
    private void updateSwitchLabels(boolean isChecked) {
        if (isChecked) {
            txtOnLabel.setTextColor(getResources().getColor(android.R.color.white));
            txtOffLabel.setTextColor(getResources().getColor(android.R.color.darker_gray));
        } else {
            txtOnLabel.setTextColor(getResources().getColor(android.R.color.darker_gray));
            txtOffLabel.setTextColor(getResources().getColor(android.R.color.white));
        }
    }
}