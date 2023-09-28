package com.digidactylus.recorder.utils;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.digidactylus.recorder.MainActivity;
import com.digidactylus.recorder.R;
import com.digidactylus.recorder.adapters.BleDevicesAdapter;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class BleDialog extends Dialog {

    private static final String TAG = "BleDialog";

    final Context context;


    public BleDialog(Context context) {
        super(context);
        this.context = context;
    }

    private LinearLayout connectedList;
    private RecyclerView availableList;

    private BleDevicesAdapter adapter;
    private Map<String, android.bluetooth.le.ScanResult> bleMap;

    Handler handler = new Handler();
    Handler handlerConnect = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.bluetooth_dialog);
        Objects.requireNonNull(getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setCancelable(true);


        connectedList = findViewById(R.id.connectedList);
        availableList = findViewById(R.id.availableList);

        bleMap = MainActivity.getMain().getScanResultHashMap();

        adapter = new BleDevicesAdapter(bleMap, new BleDevicesAdapter.OnBleClicked() {
            @Override
            public void onClick(ScanResult scanResult) {
                if (scanResult != null) {
                    MainActivity.getMain().setScanResult(scanResult);
                    MainActivity.getMain().connect(scanResult);
                    changeConnectedDevice();
                }
            }
        });

        availableList.setLayoutManager(new LinearLayoutManager(context));
        availableList.setAdapter(adapter);


        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                availableBLE();
                handler.postDelayed(this, 1000L);
            }
        }, 1000L);

        handlerConnect = new Handler();
        handlerConnect.post(new Runnable() {
            @Override
            public void run() {
                changeConnectedDevice();
                handlerConnect.postDelayed(this, 1000L);
            }
        });


    }


    private void availableBLE() {
        bleMap = MainActivity.getMain().getScanResultHashMap();

        if (bleMap.size() > 0) {
            adapter.notifyDataSetChanged();
        }
    }

    @SuppressLint("MissingPermission")
    private void changeConnectedDevice() {
        if ( MainActivity.getMain().m_isConnectBLE && MainActivity.getMain().m_scanRes != null) {
            connectedList.removeAllViews();
            View bleitem = LayoutInflater.from(context).inflate(R.layout.ble_item, null);
            TextView devTitle = bleitem.findViewById(R.id.deviceName);
            devTitle.setText(MainActivity.getMain().m_scanRes.getDevice().getName().trim());
            TextView devUuid = bleitem.findViewById(R.id.deviceUuid);
            ImageView connectedStatus = bleitem.findViewById(R.id.connectedStatus);
            ImageView disconnect = bleitem.findViewById(R.id.disconnect);
            connectedStatus.setVisibility(View.VISIBLE);
            devUuid.setText(MainActivity.getMain().m_scanRes.getDevice().getAddress());
            disconnect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainActivity.getMain().disconnect();
                    MainActivity.getMain().setScanResult(null);
                    connectedList.removeAllViews();
                }
            });
            connectedList.addView(bleitem);
        }
        else {
            connectedList.removeAllViews();
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    public void cancel() {
        super.cancel();
        handler.removeCallbacksAndMessages(null);
    }
}

