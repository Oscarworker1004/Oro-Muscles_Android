package com.digidactylus.recorder.adapters;

import android.bluetooth.le.ScanResult;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.digidactylus.recorder.MainActivity;
import com.digidactylus.recorder.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;



public class BleDevicesAdapter extends RecyclerView.Adapter<BleDevicesAdapter.ViewHolder> {

    private final Map<String, android.bluetooth.le.ScanResult> bleMap;

    public interface OnBleClicked {
        void onClick(ScanResult scanResult);
    }

    OnBleClicked onBleClicked;

    public BleDevicesAdapter(Map<String, android.bluetooth.le.ScanResult> bleMap, OnBleClicked onBleClicked) {
        this.bleMap = bleMap;
        this.onBleClicked = onBleClicked;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ble_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        List<String> keys = new ArrayList<>(bleMap.keySet());
        String key = keys.get(position);
        android.bluetooth.le.ScanResult scanResult = bleMap.get(key);

        holder.deviceNameTextView.setText(key);
        if (scanResult != null) {
            holder.deviceAddressTextView.setText(scanResult.getDevice().getAddress());
        }
        holder.connectedStatus.setVisibility(View.GONE);

        holder.bleLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBleClicked.onClick(scanResult);

            }
        });
    }

    @Override
    public int getItemCount() {
        return bleMap.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView deviceNameTextView;
        TextView deviceAddressTextView;
        LinearLayout bleLayout;

        ImageView connectedStatus;
        public ViewHolder(View itemView) {
            super(itemView);
            bleLayout = itemView.findViewById(R.id.bleLayout);
            deviceNameTextView = itemView.findViewById(R.id.deviceName);
            deviceAddressTextView = itemView.findViewById(R.id.deviceUuid);
            connectedStatus = itemView.findViewById(R.id.connectedStatus);
            ImageView disconnect = itemView.findViewById(R.id.disconnect);
            disconnect.setVisibility(View.GONE);
        }
    }
}
