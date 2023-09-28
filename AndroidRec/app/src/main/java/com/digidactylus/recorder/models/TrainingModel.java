package com.digidactylus.recorder.models;

import java.util.List;

public class TrainingModel {
    private String category;

    private boolean isCustom;
    private List<ExerciseModel> exerciseModelList;

    public TrainingModel() {

    }

    public List<ExerciseModel> getExerciseModelList() {
        return exerciseModelList;
    }

    public String getCategory() {
        return category;
    }

    public boolean isCustom() {
        return isCustom;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setExerciseModelList(List<ExerciseModel> exerciseModelList) {
        this.exerciseModelList = exerciseModelList;
    }

    public void setCustom(boolean custom) {
        isCustom = custom;
    }
}
