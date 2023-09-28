package com.example.sensorscanner;

import static android.content.ContentValues.TAG;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.UUID;

import no.nordicsemi.android.ble.BleManager;

public class ClientConnection extends BleManager {
    private BluetoothGattCharacteristic characteristic;
    private BluetoothGattCharacteristic reliableCharacteristics;
    private BluetoothGattCharacteristic readCharacteristics;
    private BluetoothGattCharacteristic indicationCharacteristics;

    private BluetoothGattCharacteristic fluxCapacitorControlPoint;

    public ClientConnection(@NonNull Context context) {
        super(context);
    }

    public ClientConnection(@NonNull Context context, @NonNull Handler handler) {
        super(context, handler);
    }

    @Override
    public int getMinLogPriority() {
        return Log.VERBOSE;
    }

    @Override
    public void log(int priority, @NonNull String message) {
        Log.println(priority, TAG, message);
    }

    @Override
    protected boolean isRequiredServiceSupported(@NonNull BluetoothGatt gatt) {
//        gatt.getService()

        BluetoothGattService fluxCapacitorService = gatt.getService(UUID.fromString("6E400001-B5A3-F393-E0A9-E50E24DCCA9E"));
        if (fluxCapacitorService != null) {
//            fluxCapacitorControlPoint = fluxCapacitorService.getCharacteristic(FLUX_CHAR_UUID);
        }
        return fluxCapacitorControlPoint != null;
    }

    @Override
    protected void initialize() {
        requestMtu(517)
                .enqueue();



    }

    @Override
    protected void onServicesInvalidated() {
        fluxCapacitorControlPoint = null;
    }

//    public void enableFluxCapacitor() {
//        writeCharacteristic(fluxCapacitorControlPoint, Flux.enable(), BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE)
//                .enqueue();
//    }
}
