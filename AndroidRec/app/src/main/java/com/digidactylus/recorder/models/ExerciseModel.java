package com.digidactylus.recorder.models;

import java.util.ArrayList;
import java.util.List;

public class ExerciseModel {

    private int id;

    private String name;
    private int weight;
    private int sets;
    private int reps;

    private int tonnage;
    private boolean isSelected;

    private boolean expanded;

    private List<SetsModel> setsModels = new ArrayList<>();

    public ExerciseModel() {

    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getSets() {
        return sets;
    }

    public int getWeight() {
        return weight;
    }

    public int getReps() {
        return reps;
    }

    public int getTonnage() {
        return tonnage;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public List<SetsModel> getSetsModels() {
        return setsModels;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSets(int sets) {
        this.sets = sets;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public void setReps(int reps) {
        this.reps = reps;
    }

    public void setTonnage(int tonnage) {
        this.tonnage = tonnage;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public void setSetsModels(List<SetsModel> setsModels) {
        this.setsModels = setsModels;
    }
}
