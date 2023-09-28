package com.digidactylus.recorder.models;

public class graphItemsList {
    private int barBodyHeight;
    private String barBodyColor;
    private String barLabel;
    private double actualValue;

    public graphItemsList(int barBodyHeight, String barBodyColor, String barLabel, double actualValue) {
        this.barBodyHeight = barBodyHeight;
        this.barBodyColor = barBodyColor;
        this.barLabel = barLabel;
        this.actualValue = actualValue;
    }

    public int getBarBodyHeight() {
        return barBodyHeight;
    }

    public void setBarBodyHeight(int barBodyHeight) {
        this.barBodyHeight = barBodyHeight;
    }

    public String getBarBodyColor() {
        return barBodyColor;
    }

    public void setBarBodyColor(String barBodyColor) {
        this.barBodyColor = barBodyColor;
    }

    public String getBarLabel() {
        return barLabel;
    }

    public void setBarLabel(String barLabel) {
        this.barLabel = barLabel;
    }

    public double getActualValue() {
        return actualValue;
    }

    public void setActualValue(double actualValue) {
        this.actualValue = actualValue;
    }
}
