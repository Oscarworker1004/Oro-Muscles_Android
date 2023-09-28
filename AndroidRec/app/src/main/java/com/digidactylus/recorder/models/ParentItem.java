package com.digidactylus.recorder.models;

// ParentItem.java

public class ParentItem {
    private int imageResource;
    private String title;
    private String reps;
    private String kg;

    public ParentItem(int imageResource, String title, String reps, String kg) {
        this.imageResource = imageResource;
        this.title = title;
        this.reps = reps;
        this.kg = kg;
    }

    public int getImageResource() {
        return imageResource;
    }

    public void setImageResource(int imageResource) {
        this.imageResource = imageResource;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getReps() {
        return reps;
    }

    public void setReps(String reps) {
        this.reps = reps;
    }

    public String getKg() {
        return kg;
    }

    public void setKg(String kg) {
        this.kg = kg;
    }
}

