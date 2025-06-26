package com.example.sistempenyiramantanamanotomatis;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private HistoryAdapter adapter;
    private List<HistoryItem> historyList;

    private BottomNavigationView bottomNav;
    private TextView emptyTextView;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("HistoryActivity", "onCreate: HistoryActivity dimulai");

        setContentView(R.layout.activity_history);

        // Inisialisasi view
        recyclerView = findViewById(R.id.historyRecyclerView);

        bottomNav = findViewById(R.id.bottom_nav);
        emptyTextView = findViewById(R.id.emptyTextView);

        // Setup RecyclerView
        historyList = new ArrayList<>();
        adapter = new HistoryAdapter(this, historyList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);



        // Bottom navigation
        bottomNav.setSelectedItemId(R.id.navigation_history);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.navigation_dashboard) {
                startActivity(new Intent(this, DashboardActivity.class));
                finish();
            } else if (id == R.id.navigation_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                finish();
            }
            return true;
        });

        // Panggil Firebase
        loadHistoryFromFirebase();
    }

    private void loadHistoryFromFirebase() {
        Log.d("HistoryActivity", "Memulai loadHistoryFromFirebase()");

        DatabaseReference ref = FirebaseDatabase.getInstance("https://sistem-penyiraman-otomat-4bdd3-default-rtdb.asia-southeast1.firebasedatabase.app/").
                getReference("history");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Log.d("HistoryActivity", "onDataChange triggered.");
                Log.d("HistoryActivity", "snapshot.exists() = " + snapshot.exists());
                Log.d("HistoryActivity", "Jumlah child = " + snapshot.getChildrenCount());
                Log.d("HistoryActivity", "Isi snapshot = " + snapshot.getValue());

                historyList.clear();

                if (!snapshot.exists()) {
                    Log.w("HistoryActivity", "Tidak ada data pada node 'history'");
                    emptyTextView.setVisibility(View.VISIBLE);
                    return;
                }

                for (DataSnapshot data : snapshot.getChildren()) {
                    String timestamp = data.child("timestamp").getValue(String.class);
                    String status = data.child("status").getValue(String.class);

                    Object moistureObj = data.child("soil_moisture").getValue();
                    Long moisture = null;
                    if (moistureObj instanceof Long) {
                        moisture = (Long) moistureObj;
                    } else if (moistureObj instanceof Double) {
                        moisture = ((Double) moistureObj).longValue();
                    } else if (moistureObj instanceof String) {
                        try {
                            moisture = Long.parseLong((String) moistureObj);
                        } catch (NumberFormatException e) {
                            Log.w("HistoryActivity", "Moisture format salah: " + moistureObj);
                        }
                    }

                    if (timestamp != null && moisture != null && status != null) {
                        historyList.add(new HistoryItem(timestamp, moisture.intValue(), status));
                        Log.d("HistoryActivity", "Tambah item: " + timestamp);
                    } else {
                        Log.w("HistoryActivity", "Data tidak lengkap: " + data.getKey());
                    }
                }

                Log.d("HistoryActivity", "Total item ditambahkan: " + historyList.size());

                Collections.reverse(historyList);
                adapter.notifyDataSetChanged();

                if (historyList.isEmpty()) {
                    emptyTextView.setVisibility(View.VISIBLE);
                } else {
                    emptyTextView.setVisibility(View.GONE);
                }

                if (recyclerView.getChildCount() == 0) {
                    Log.d("HistoryActivity", "RecyclerView kosong setelah notify");
                } else {
                    Log.d("HistoryActivity", "RecyclerView menampilkan item");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("HistoryActivity", "Gagal mengambil data Firebase: " + error.getMessage());
                emptyTextView.setVisibility(View.VISIBLE);
                emptyTextView.setText("Gagal mengambil data.");
            }
        });
    }
}

