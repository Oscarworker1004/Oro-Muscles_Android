package com.example.sensorscanner;

import static com.example.sensorscanner.utils.UUIDS.ACTION_DATA_AVAILABLE;
import static com.example.sensorscanner.utils.UUIDS.BATTERY_LEVEL_CHAR_UUID;
import static com.example.sensorscanner.utils.UUIDS.BATTERY_SERVICE_UUID;
import static com.example.sensorscanner.utils.UUIDS.SERVICE_UUID;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.sensorscanner.utils.UUIDS;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import no.nordicsemi.android.support.v18.scanner.BluetoothLeScannerCompat;
import no.nordicsemi.android.support.v18.scanner.ScanCallback;
import no.nordicsemi.android.support.v18.scanner.ScanFilter;
import no.nordicsemi.android.support.v18.scanner.ScanResult;
import no.nordicsemi.android.support.v18.scanner.ScanSettings;
/*
public class MainActivity extends AppCompatActivity {
    //    private BleManager bleManager;
    private TextView tvLoading;
    //    String invoice_html = "";
    //    ActivityResultLauncher<Intent> createInvoiceActivityResultLauncher;
    private String deviceName = "";
    private BluetoothGatt bluetoothGatt;
    public BluetoothAdapter mBluetoothAdapter;
    private String uuid, charUuid;

    private ScanningFragment scanningFragment;
    private BluetoothDetailsFragment bluetoothDetailsFragment;
    private BluetoothDevice bluetoothDevice;

    private OutputStream outputStream;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BluetoothManager bluetoothManager = (BluetoothManager) this.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        scanningFragment = ScanningFragment.newInstance(this);
        transaction.add(R.id.fragment, scanningFragment);
        transaction.commit();


        //==================================================
        File dir = getFilesDir();
        String path = dir.getAbsolutePath();

        String fileName = path + "/cometa.bin";

        try {
            outputStream = new FileOutputStream(fileName);
        } catch (Exception e) {

        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!checkScanPermission()) {
            requestPermission();
        } else {
            if (mBluetoothAdapter.isEnabled()) {
                startScan();
            }
        }
    }

    private boolean checkScanPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.BLUETOOTH_CONNECT}, 100);
    }

    private void startScan() {
        ScanCallback scanCallback = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, @NonNull ScanResult result) {
                super.onScanResult(callbackType, result);

            }

            @Override
            public void onBatchScanResults(@NonNull List<ScanResult> results) {
                super.onBatchScanResults(results);
                HashMap<String, ScanResult> scanResultHashMap = new HashMap<>();
                for (int i = 0; i < results.size(); i++) {
                    if (results.get(i).getScanRecord() != null) {
                        String deviceName = results.get(i).getScanRecord().getDeviceName();
                        if (deviceName != null && !scanResultHashMap.containsKey(deviceName) && deviceName.contains("picoblue")) {
                            scanResultHashMap.put(deviceName, results.get(i));
                        }
                    }
                }
                List<ScanResult> list = new ArrayList<>(scanResultHashMap.values());

                scanningFragment.updateList(list);

            }

            @Override
            public void onScanFailed(int errorCode) {
                super.onScanFailed(errorCode);
            }
        };

        BluetoothLeScannerCompat scanner = BluetoothLeScannerCompat.getScanner();
        ScanSettings settings = new ScanSettings.Builder().setLegacy(false).setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).setReportDelay(5000).setUseHardwareBatchingIfSupported(true).build();
        List<ScanFilter> filters = new ArrayList<>();
        ParcelUuid parcelUuid = new ParcelUuid(UUID.fromString(SERVICE_UUID));
        ScanFilter scanFilter = new ScanFilter.Builder().setServiceUuid(parcelUuid).build();
        filters.add(scanFilter);
        scanner.startScan(filters, settings, scanCallback);
//
//        for (double i = 1; i < 1000; i++) {
//            String number = String.valueOf(i * 0.001);
//            if (number.length() > 5) {
//                invoice_html = invoice_html.concat(number.substring(0, number.indexOf("000")) + "&emsp;1.00708<br/>");
//            } else {
//                invoice_html = invoice_html.concat(i * 0.001 + "&emsp;1.00708<br/>");
//            }
//        }
//        createInvoiceActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
//            if (result.getResultCode() == Activity.RESULT_OK) {
//                Uri uri;
//                if (result.getData() != null) {
//                    uri = result.getData().getData();
//                    createInvoice(uri);
//                }
//            }
//        });
    }

    public void connect(ScanResult scanResult) {
        deviceName = scanResult.getScanRecord().getDeviceName();
        bluetoothDevice = scanResult.getDevice();
        connectToDeviceSelected(bluetoothDevice);
//        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
//        intent.addCategory(Intent.CATEGORY_OPENABLE);
//        intent.setType("text/html");
//        intent.putExtra(Intent.EXTRA_TITLE, "MyFile");
//
//        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, Uri.parse("/Documents"));
//
//        createInvoiceActivityResultLauncher.launch(intent);


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean allGranted = false;
        for (int i = 0; i < grantResults.length; i++) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                allGranted = true;
            } else {
                Toast.makeText(this, "Grant all permissions in settings.", Toast.LENGTH_SHORT).show();
                break;
            }
        }
        if (allGranted) {
            if (mBluetoothAdapter.isEnabled())
                startScan();
            else
                scanningFragment.onResume();
        }

    }

//    private void createInvoice(Uri uri) {
//        try {
//            ParcelFileDescriptor pfd = getContentResolver().openFileDescriptor(uri, "w");
//            if (pfd != null) {
//                FileOutputStream fileOutputStream = new FileOutputStream(pfd.getFileDescriptor());
//                fileOutputStream.write(invoice_html.getBytes());
//                fileOutputStream.close();
//                pfd.close();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    private final BluetoothGattCallback btleGattCallback = new BluetoothGattCallback() {

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {
            bluetoothDetailsFragment.setDataStream(characteristic.getValue());
            try {
                outputStream.write(characteristic.getValue());
            } catch (Exception e) {

            }
        }

        @SuppressLint("MissingPermission")
        @Override
        public void onConnectionStateChange(final BluetoothGatt gatt, final int status, final int newState) {
            switch (newState) {
                case 0:
                    MainActivity.this.runOnUiThread(() -> Toast.makeText(MainActivity.this, "device disconnected", Toast.LENGTH_SHORT).show());
                    break;
                case 2:
                    MainActivity.this.runOnUiThread(() -> Toast.makeText(MainActivity.this, "Connected", Toast.LENGTH_SHORT).show());
                    bluetoothGatt.discoverServices();
                    break;
                default:
                    MainActivity.this.runOnUiThread(() -> Toast.makeText(MainActivity.this, "we encountered an unknown state", Toast.LENGTH_SHORT).show());
                    break;
            }
        }

        @Override
        public void onServicesDiscovered(final BluetoothGatt gatt, final int status) {
            displayGattServices(gatt, bluetoothGatt.getServices());
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {

            }
        }

        @Override
        public void onDescriptorRead(@NonNull BluetoothGatt gatt, @NonNull BluetoothGattDescriptor descriptor, int status, @NonNull byte[] value) {
            super.onDescriptorRead(gatt, descriptor, status, value);
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorRead(gatt, descriptor, status);
        }
    };

    private void broadcastUpdate(final String action,
                                 final BluetoothGattCharacteristic characteristic) {
        int batteryNumber = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0);
        double batteryLevel = ((batteryNumber - 182.0) / 27) * 100;

        Bundle bundle = new Bundle();
        bundle.putString(UUIDS.DEVICE_NAME, deviceName);
        bundle.putString(UUIDS._SERVICE_UUID, uuid);
        bundle.putString(UUIDS.CHARACTERISTIC_UUID, charUuid);
        bundle.putString(UUIDS.BATTERY_LEVEL, String.format("%.2f", batteryLevel));

        bluetoothDetailsFragment = BluetoothDetailsFragment.newInstance(this, bundle);

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.fragment, bluetoothDetailsFragment);
        transaction.commit();

    }

    public void connectToDeviceSelected(BluetoothDevice bluetoothDevice) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.ACCESS_FINE_LOCATION}, 100);
        } else {
            MainActivity.this.runOnUiThread(() -> Toast.makeText(MainActivity.this, "Connecting..", Toast.LENGTH_SHORT).show());
            bluetoothGatt = bluetoothDevice.connectGatt(this, false, btleGattCallback);
        }
    }

    @SuppressLint("MissingPermission")
    private void displayGattServices(BluetoothGatt gatt, List<BluetoothGattService> gattServices) {
        if (gattServices == null) return;

        byte[] enableNotificationsValue = {0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06};
        BluetoothGattCharacteristic batteryLevel = gatt.getService(BATTERY_SERVICE_UUID).getCharacteristic(BATTERY_LEVEL_CHAR_UUID);
        gatt.readCharacteristic(batteryLevel);

        for (BluetoothGattService gattService : gattServices) {
            uuid = gattService.getUuid().toString();
            new ArrayList<HashMap<String, String>>();

            List<BluetoothGattCharacteristic> gattCharacteristics =
                    gattService.getCharacteristics();

            for (BluetoothGattCharacteristic gattCharacteristic :
                    gattCharacteristics) {
                charUuid = gattCharacteristic.getUuid().toString();
            }
        }

        gatt.writeCharacteristic(batteryLevel);

    }

    public void enableBT() {
        if (!mBluetoothAdapter.isEnabled()) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            Intent blueToothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(blueToothIntent, 123);
        }
    }

    @SuppressLint("MissingPermission")
    public void getDataStream() {


        BluetoothGattCharacteristic characteristic = bluetoothGatt.getService(UUIDS.NUS_SERVICE_UUID).getCharacteristic(UUIDS.TX_CHARACTERISTIC_UUID);

        bluetoothGatt.setCharacteristicNotification(characteristic, true);

        String CCC_DESCRIPTOR_UUID = "00002902-0000-1000-8000-00805f9b34fb";
        final BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString(CCC_DESCRIPTOR_UUID));
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        bluetoothGatt.writeDescriptor(descriptor);


//        if (characteristic == null) {
//            return;
//        }
//
//        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(characteristic.getDescriptors().get(0).getUuid());
//
//        if (descriptor == null) {
//            System.err.println("Descriptor not found.");
//            System.exit(1);
//        }
//
//        descriptor.(new BluetoothNotification() {
//            @Override
//            public void run(BluetoothGattCharacteristic characteristic, byte[] value) {
//                // Handle the received notification data here
//                System.out.println("Received notification: " + bytesToHex(value));
//            }
//        });
//
//        // Enable notifications by writing the appropriate value to the descriptor
//        descriptor.writeValue(new byte[] { 0x01, 0x00 });
//
//        // Wait for notifications
//        try {
//            while (true) {
//                Thread.sleep(1000);
//            }
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        device.disconnect();
    }

    @SuppressLint("MissingPermission")
    public void stopDataStream() {
        BluetoothGattCharacteristic characteristic = bluetoothGatt.getService(UUIDS.NUS_SERVICE_UUID).getCharacteristic(UUIDS.TX_CHARACTERISTIC_UUID);

        bluetoothGatt.setCharacteristicNotification(characteristic, true);

        String CCC_DESCRIPTOR_UUID = "00002902-0000-1000-8000-00805f9b34fb";
        final BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString(CCC_DESCRIPTOR_UUID));
        descriptor.setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
        bluetoothGatt.writeDescriptor(descriptor);
    }

//
//    public void enableNotifications(BluetoothGattCharacteristic characteristic) {
//        val cccdUuid = UUID.fromString(CCC_DESCRIPTOR_UUID)
//        val payload = when {
//            characteristic.isIndicatable() -> BluetoothGattDescriptor.ENABLE_INDICATION_VALUE
//            characteristic.isNotifiable() -> BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
//        else -> {
//                Log.e("ConnectionManager", "${characteristic.uuid} doesn't support notifications/indications")
//                return
//            }
//        }
//
//        characteristic.getDescriptor(cccdUuid)?.let { cccDescriptor ->
//            if (bluetoothGatt?.setCharacteristicNotification(characteristic, true) == false) {
//                Log.e("ConnectionManager", "setCharacteristicNotification failed for ${characteristic.uuid}")
//                return
//            }
//            writeDescriptor(cccDescriptor, payload)
//        } ?: Log.e("ConnectionManager", "${characteristic.uuid} doesn't contain the CCC descriptor!")
//    }
//
//    fun disableNotifications(characteristic: BluetoothGattCharacteristic) {
//        if (!characteristic.isNotifiable() && !characteristic.isIndicatable()) {
//            Log.e("ConnectionManager", "${characteristic.uuid} doesn't support indications/notifications")
//            return
//        }
//
//        val cccdUuid = UUID.fromString(CCC_DESCRIPTOR_UUID)
//        characteristic.getDescriptor(cccdUuid)?.let { cccDescriptor ->
//            if (bluetoothGatt?.setCharacteristicNotification(characteristic, false) == false) {
//                Log.e("ConnectionManager", "setCharacteristicNotification failed for ${characteristic.uuid}")
//                return
//            }
//            writeDescriptor(cccDescriptor, BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE)
//        } ?: Log.e("ConnectionManager", "${characteristic.uuid} doesn't contain the CCC descriptor!")
//    }


} */