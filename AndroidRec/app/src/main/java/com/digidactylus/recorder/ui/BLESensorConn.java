package com.digidactylus.recorder.ui;

public class BLESensorConn {

    public BLESensorConn() {
        // IMPLEMENT: Init here
    }

    // list available devices
    public BLEDevice[] getAvailable() {
        // IMPLEMENT: Scan for devices in range and return as a list
        return null; // Placeholder
    }

    // connect to a device
    public boolean connect(BLEDevice dev) {
        // IMPLEMENT: Scan for devices in range and return as a list
        return false; // Placeholder
    }

    // disconnect; a disconnectAll() is also OK
    public boolean disconnect(BLEDevice dev) {
        // IMPLEMENT: Drop this device
        return false; // Placeholder
    }

    // start recording
    public void start() {
        // IMPLEMENT: Start data acquisition
    }

    // stop recording
    public void stop() {
        // IMPLEMENT: Stop data acquisition
    }

    // collect the data in a byte buffer
    public byte [] getDataFrame() {
        // IMPLEMENT: get data and return in buffer
        return null; // Placeholder
    }


}
