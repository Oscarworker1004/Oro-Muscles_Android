package com.digidactylus.recorder.models;

public class  MuscleModel {
    private int imageResourceId;
    private String itemTitle;

    private boolean isSelected;

    public MuscleModel(int imageResourceId, String itemTitle, boolean isSelected) {
        this.imageResourceId = imageResourceId;
        this.itemTitle = itemTitle;
        this.isSelected = isSelected;
    }

    public int getImageResourceId() {
        return imageResourceId;
    }

    public void setImageResourceId(int imageResourceId) {
        this.imageResourceId = imageResourceId;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public String getItemTitle() {
        return itemTitle;
    }

    public void setItemTitle(String itemTitle) {
        this.itemTitle = itemTitle;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
