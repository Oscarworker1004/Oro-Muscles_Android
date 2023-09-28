package com.digidactylus.recorder.models;

import java.util.ArrayList;
import java.util.List;

public class LabelingModel {
    private double medianValue;
    private List<String> barList = new ArrayList<>();

    private ExerciseModel exerciseModel;

    private long startTime;

    private MuscleMapModel muscleMapModel;

    private double intensity;

    private double workCapacity;

    private String exerciaseString;

    private String duration;

    private boolean isSelected;

    public interface OnDataChanged {
        void onData();
        void onAllChanged();
        void onStartRecord();
        void onBarChanged();
    }

    private OnDataChanged onDataChanged;

    public LabelingModel() {

    }

    public void setOnDataChanged(OnDataChanged onDataChanged) {
        this.onDataChanged = onDataChanged;
    }

    public double getMedianValue() {
        return medianValue;
    }

    public void setMedianValue(double medianValue) {
        this.medianValue = medianValue;
        if(onDataChanged !=null) {
            onDataChanged.onData();
            onDataChanged.onAllChanged();
        }
    }

    public List<String> getBarList() {
        return barList;
    }

    public void setBarList(List<String> barList) {
        this.barList = barList;
        if(onDataChanged !=null) {
            onDataChanged.onAllChanged();
            onDataChanged.onBarChanged();
        }
    }

    public ExerciseModel getExerciseModel() {
        return exerciseModel;
    }

    public void setExerciseModel(ExerciseModel exerciseModel) {
        this.exerciseModel = exerciseModel;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long totalTime) {
        this.startTime = totalTime;
        if(onDataChanged !=null)
            onDataChanged.onAllChanged();
    }

    public MuscleMapModel getMuscleMapModel() {
        return muscleMapModel;
    }

    public void setMuscleMapModel(MuscleMapModel muscleMapModel) {
        this.muscleMapModel = muscleMapModel;
        if(onDataChanged !=null)
            onDataChanged.onAllChanged();
    }

    public double getIntensity() {
        return intensity;
    }

    public void setIntensity(double intensity) {
        this.intensity = intensity;
        if(onDataChanged !=null)
            onDataChanged.onAllChanged();
    }

    public double getWorkCapacity() {
        return workCapacity;
    }

    public void setWorkCapacity(double workCapacity) {
        this.workCapacity = workCapacity;
        if(onDataChanged !=null)
            onDataChanged.onAllChanged();
    }

    public String getExerciaseString() {
        return exerciaseString;
    }

    public void setExerciaseString(String exerciaseString) {
        this.exerciaseString = exerciaseString;
        if(onDataChanged != null)
            onDataChanged.onAllChanged();
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
        if(onDataChanged !=null)
            onDataChanged.onAllChanged();
    }

    public void startRecord() {
        if(onDataChanged !=null)
            onDataChanged.onStartRecord();
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}