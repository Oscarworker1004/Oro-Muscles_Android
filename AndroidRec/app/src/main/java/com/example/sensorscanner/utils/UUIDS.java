package com.example.sensorscanner.utils;

import java.util.UUID;

public class UUIDS {
    public static String SERVICE_UUID = "6E400001-B5A3-F393-E0A9-E50E24DCCA9E";
    public static UUID BATTERY_SERVICE_UUID = UUID.fromString("0000180f-0000-1000-8000-00805f9b34fb");
    public static UUID BATTERY_LEVEL_CHAR_UUID = UUID.fromString("00002a19-0000-1000-8000-00805f9b34fb");

    public static UUID NUS_SERVICE_UUID =  UUID.fromString("6E400001-B5A3-F393-E0A9-E50E24DCCA9E");
    public static UUID RX_CHARACTERISTIC_UUID =  UUID.fromString("6E400002-B5A3-F393-E0A9-E50E24DCCA9E");
    public static UUID TX_CHARACTERISTIC_UUID =  UUID.fromString("6E400003-B5A3-F393-E0A9-E50E24DCCA9E");
    public static String ACTION_DATA_AVAILABLE = "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
    public static String DEVICE_NAME = "deviceName";
    public static String _SERVICE_UUID = "serviceUUID";
    public static String CHARACTERISTIC_UUID = "characteristicUUID";
    public static String BATTERY_LEVEL = "batteryLevel";

}
