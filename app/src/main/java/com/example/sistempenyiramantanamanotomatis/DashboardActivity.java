package com.example.sistempenyiramantanamanotomatis;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;

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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class DashboardActivity extends AppCompatActivity {

    private ProgressBar progressMoisture;
    private TextView txtProgressPercentage;
    private TextView txtPumpStatus, txtStatusTanah;
    private TextView txtOnLabel, txtOffLabel;
    private SwitchMaterial switchAuto;
    private ImageView iconNotification;

    private DatabaseReference moistureRef, autoModeRef, relayRef, historyRef;
    private boolean sudahMenyiram = false;
    private static final int AMBANG_KELEMBABAN = 60;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        progressMoisture = findViewById(R.id.progress_moisture);
        txtProgressPercentage = findViewById(R.id.progress_text);
        txtPumpStatus = findViewById(R.id.txt_pump_status);
        txtStatusTanah = findViewById(R.id.txt_status_tanah);
        txtOnLabel = findViewById(R.id.text_on);
        txtOffLabel = findViewById(R.id.text_off);
        switchAuto = findViewById(R.id.switch_auto);
        iconNotification = findViewById(R.id.btn_notifications);

        FirebaseDatabase db = FirebaseDatabase.getInstance("https://sistem-penyiraman-otomat-4bdd3-default-rtdb.asia-southeast1.firebasedatabase.app/");
        moistureRef = db.getReference("sensor/soil_moisture");
        autoModeRef = db.getReference("control/auto_mode");
        relayRef = db.getReference("control/relay");
        historyRef = db.getReference("control/history");

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setSelectedItemId(R.id.navigation_dashboard);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.navigation_dashboard) return true;
            else if (item.getItemId() == R.id.navigation_history) {
                startActivity(new Intent(this, HistoryActivity.class));
                finish();
                return true;
            } else if (item.getItemId() == R.id.navigation_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                finish();
                return true;
            }
            return false;
        });

        updateSwitchLabels(switchAuto.isChecked());
        switchAuto.setOnCheckedChangeListener((buttonView, isChecked) -> {
            autoModeRef.setValue(isChecked);
            Toast.makeText(this, isChecked ? "Penyiraman Otomatis Aktif" : "Penyiraman Otomatis Nonaktif", Toast.LENGTH_SHORT).show();
            updateSwitchLabels(isChecked);
        });

        autoModeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Boolean auto = snapshot.getValue(Boolean.class);
                if (auto != null) switchAuto.setChecked(auto);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        moistureRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Integer moistureValue = snapshot.getValue(Integer.class);
                if (moistureValue != null) {
                    updateUIWithMoisture(moistureValue);
                    if (switchAuto.isChecked()) {
                        handleAutoPenyiraman(moistureValue);
                    }
                } else {
                    Toast.makeText(DashboardActivity.this, "Data kelembaban kosong", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DashboardActivity.this, "Gagal mengambil data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        iconNotification.setOnClickListener(view -> {
            String statusPompa = txtPumpStatus.getText().toString();
            new AlertDialog.Builder(this)
                    .setTitle("Status Pompa")
                    .setMessage("Status Pompa Saat Ini: " + statusPompa)
                    .setPositiveButton("OK", null)
                    .show();
        });
    }

    private void handleAutoPenyiraman(int moisture) {
        if (moisture < AMBANG_KELEMBABAN) {
            relayRef.setValue(true);
            txtPumpStatus.setText("Pompa Menyala");
            sudahMenyiram = true;
        } else {
            relayRef.setValue(false);
            txtPumpStatus.setText("Pompa Mati");
            if (sudahMenyiram) {
                String tanggal = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                String id = historyRef.push().getKey();
                if (id != null) {
                    HashMap<String, Object> log = new HashMap<>();
                    log.put("tanggal", tanggal);
                    log.put("kelembaban", moisture);
                    log.put("status", true);
                    historyRef.child(id).setValue(log);
                    Toast.makeText(this, "Penyiraman otomatis tercatat", Toast.LENGTH_SHORT).show();
                }
                sudahMenyiram = false;
            }
        }
    }

    private void updateUIWithMoisture(int moisture) {
        progressMoisture.setProgress(moisture);
        txtProgressPercentage.setText(moisture + "%");
        if (moisture >= 70) {
            txtStatusTanah.setText("Basah");
        } else if (moisture >= 40) {
            txtStatusTanah.setText("Cukup");
        } else {
            txtStatusTanah.setText("Kering");
        }
    }

    private void updateSwitchLabels(boolean isChecked) {
        if (txtOnLabel == null || txtOffLabel == null) return;
        txtOnLabel.setTextColor(ContextCompat.getColor(this,
                isChecked ? android.R.color.white : android.R.color.darker_gray));
        txtOffLabel.setTextColor(ContextCompat.getColor(this,
                isChecked ? android.R.color.darker_gray : android.R.color.white));
    }
}