package com.example.sensorscanner;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sensorscanner.interfaces.ScanOptionsListener;

import java.util.List;

import no.nordicsemi.android.support.v18.scanner.ScanResult;

public class ScanningFragment extends Fragment implements ScanOptionsListener {
    private TextView tvLoading;
    private String deviceName = "";
    private RecyclerView rvBluetooth;
    private Button buttonEnableBluetooth;
    private ScanResultAdapter scanResultAdapter;

    public ScanningFragment() {
    }

    public static ScanningFragment newInstance(Context mContext) {
        ScanningFragment fragment = new ScanningFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    /*
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_scanning, container, false);
    }*/

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

/*
        tvLoading = view.findViewById(R.id.tvLoading);
        buttonEnableBluetooth = view.findViewById(R.id.buttonEnableBluetooth);

        scanResultAdapter = new ScanResultAdapter(requireContext(), this);

        rvBluetooth = view.findViewById(R.id.rvBluetooth);
        rvBluetooth.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        rvBluetooth.setAdapter(scanResultAdapter);

        buttonEnableBluetooth.setOnClickListener(view1 ->
                ((MainActivity) requireActivity()).enableBT());*/
    }

    @Override
    public void onResume() {
        super.onResume();
        /*
        if (((MainActivity) requireActivity()).mBluetoothAdapter.isEnabled()) {
            hideEnableBluetooth();
        } else {
            showEnableBluetooth();
        }*/
    }

    public void showEnableBluetooth() {
        buttonEnableBluetooth.setVisibility(View.VISIBLE);
        rvBluetooth.setVisibility(View.GONE);
        tvLoading.setVisibility(View.GONE);
    }

    public void hideEnableBluetooth() {
        buttonEnableBluetooth.setVisibility(View.GONE);
        rvBluetooth.setVisibility(View.VISIBLE);
        tvLoading.setVisibility(View.VISIBLE);
    }

    public void updateList(List<ScanResult> list) {
        tvLoading.setVisibility(View.GONE);
        scanResultAdapter.updateList(list);
    }

    @Override
    public void connect(ScanResult scanResult) {
        //((MainActivity) requireActivity()).connect(scanResult);
    }
}