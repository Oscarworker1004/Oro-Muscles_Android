package com.example.sensorscanner;

import static com.example.sensorscanner.utils.UUIDS.CHARACTERISTIC_UUID;
import static com.example.sensorscanner.utils.UUIDS.DEVICE_NAME;
import static com.example.sensorscanner.utils.UUIDS._SERVICE_UUID;

import android.content.Context;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.digidactylus.recorder.R;

public class BluetoothDetailsFragment extends Fragment {

    private TextView dataStream;

    public BluetoothDetailsFragment() {
    }

    public static BluetoothDetailsFragment newInstance(Context mContext, Bundle bundle) {
        BluetoothDetailsFragment fragment = new BluetoothDetailsFragment();
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Fix later
        //return inflater.inflate(R.layout.fragment_bluetooth_details, container, false);
        return new View(null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Fix later
        /*
        dataStream = view.findViewById(R.id.dataStream);
        TextView tvDeviceName = view.findViewById(R.id.tvDeviceName);
        TextView tvServiceUUID = view.findViewById(R.id.tvServiceUUID);
        TextView tvCharacteristicUUID = view.findViewById(R.id.tvCharacteristicUUID);
        TextView tvBatteryLevel = view.findViewById(R.id.tvBatteryLevel);

        dataStream.setMovementMethod(new ScrollingMovementMethod());

        Bundle bundle = getArguments();

        if (bundle != null) {
            tvDeviceName.setText("Device Name : " + bundle.getString(DEVICE_NAME));
            tvServiceUUID.setText("Service UUID : " + bundle.getString(_SERVICE_UUID));
            tvCharacteristicUUID.setText("Characteristic UUID : " + bundle.getString(CHARACTERISTIC_UUID));
            tvBatteryLevel.setText("Battery Level : " + bundle.getString("batteryLevel") + "%");
        }

        view.findViewById(R.id.getDataStream).setOnClickListener(view1 -> ((MainActivity) requireActivity()).getDataStream());
        view.findViewById(R.id.stopDataStream).setOnClickListener(view1 -> ((MainActivity) requireActivity()).stopDataStream());
        */
    }

    public void setDataStream(byte[] value) {
//        String data = "";
//        for (byte b : value) {
//            data = data.concat(String.valueOf(b) + "  ");
//        }
        toHexString(value);
        dataStream.setText(toHexString(value));
    }

    public String toHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        if (bytes != null)
            for (byte b : bytes) {
                final String hexString = Integer.toHexString(b & 0xff);
                if (hexString.length() == 1)
                    sb.append('0');
                sb.append(hexString);
            }
        return sb.toString();
    }

}
