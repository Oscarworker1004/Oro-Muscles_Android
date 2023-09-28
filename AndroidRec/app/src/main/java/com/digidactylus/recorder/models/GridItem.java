package com.digidactylus.recorder.models;

public class GridItem {
    private int imageResourceId;
    private String circleText, Intensity, dataSum;

    public GridItem(int imageResourceId, String circleText, String intensity, String dataSum) {
        this.imageResourceId = imageResourceId;
        this.circleText = circleText;
        this.Intensity = intensity;
        this.dataSum = dataSum;
    }

    public int getImageResourceId() {
        return imageResourceId;
    }

    public String getCircleText() {
        return circleText;
    }
    public String getIntensity() {
        return Intensity;
    }
    public String getdataSum() {
        return dataSum;
    }
}
