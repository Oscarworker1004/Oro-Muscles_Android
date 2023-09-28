package com.digidactylus.recorder.models;

public class SetsModel {
    private int id;
    private int reps;
    private int weight;

    public SetsModel() {

    }

    public int getReps() {
        return reps;
    }

    public int getId() {
        return id;
    }

    public int getWeight() {
        return weight;
    }

    public void setReps(int reps) {
        this.reps = reps;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
}
