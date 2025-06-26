package com.example.sistempenyiramantanamanotomatis;

public class HistoryItem {
    private String timestamp;
    private int soil_moisture;
    private String status;

    // Diperlukan untuk Firebase saat memuat data otomatis
    public HistoryItem() {
    }

    public HistoryItem(String timestamp, int soil_moisture, String status) {
        this.timestamp = timestamp;
        this.soil_moisture = soil_moisture;
        this.status = status;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public int getSoil_moisture() {
        return soil_moisture;
    }

    public String getStatus() {
        return status;
    }
}
