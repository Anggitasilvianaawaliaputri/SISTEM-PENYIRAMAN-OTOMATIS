package com.example.sistempenyiramantanamanotomatis;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {
    private final List<HistoryItem> historyList;
    private final Context context;

    public HistoryAdapter(Context context, List<HistoryItem> historyList) {
        this.context = context;
        this.historyList = historyList;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.history_item, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        HistoryItem item = historyList.get(position);

        holder.txtTimestamp.setText("Waktu: " + item.getTimestamp());
        holder.txtMoisture.setText("Kelembaban: " + item.getSoil_moisture() + "%");
        holder.txtStatus.setText("Status: " + item.getStatus());

        // Log debug
        Log.d("AdapterBind", "Binding data: " + item.getTimestamp());

        // Set warna status
        int colorResId = item.getStatus().equalsIgnoreCase("ON") ?
                android.R.color.holo_green_dark : android.R.color.holo_red_dark;
        holder.txtStatus.setTextColor(ContextCompat.getColor(context, colorResId));
    }

    @Override
    public int getItemCount() {
        return historyList != null ? historyList.size() : 0;
    }

    public static class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView txtTimestamp, txtMoisture, txtStatus;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTimestamp = itemView.findViewById(R.id.txtTimestamp);
            txtMoisture = itemView.findViewById(R.id.txtMoisture);
            txtStatus = itemView.findViewById(R.id.txtStatus);
        }
    }
}
