package com.example.sensorscanner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.digidactylus.recorder.R;
import com.example.sensorscanner.interfaces.ScanOptionsListener;

import java.util.ArrayList;
import java.util.List;

import no.nordicsemi.android.support.v18.scanner.ScanResult;

public class ScanResultAdapter extends RecyclerView.Adapter<ScanResultAdapter.ViewHolder> {
    private final Context context;
    private final List<ScanResult> mData = new ArrayList<>();
    private final ScanOptionsListener mScanOptionsListener;

    public ScanResultAdapter(Context context, ScanOptionsListener scanOptions) {
        this.context = context;
        this.mScanOptionsListener = scanOptions;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Fix that later
        //View view = LayoutInflater.from(context).inflate(R.layout.fragment_scanning_list, parent, false);
        //return new ScanResultAdapter.ViewHolder(view);
        return new ScanResultAdapter.ViewHolder(null);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        ScanResult scanResult = mData.get(position);
        holder.tvUUID.setText(scanResult.getScanRecord().getDeviceName());
        holder.tvMacAddress.setText(context.getString(R.string.device_mac, scanResult.getDevice().getAddress()));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void updateList(List<ScanResult> list) {
        mData.clear();
        mData.addAll(list);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvUUID, tvMacAddress;
        RelativeLayout rlConnect;

        ViewHolder(View itemView) {
            super(itemView);

            // MGG Fix later
            //tvUUID = itemView.findViewById(R.id.tvUUID);
            //rlConnect = itemView.findViewById(R.id.rlConnect);
            //tvMacAddress = itemView.findViewById(R.id.tvMacAddress);

            rlConnect.setOnClickListener(view -> mScanOptionsListener.connect(mData.get(getAdapterPosition())));
        }
    }
}