package com.digidactylus.recorder.models;

import com.digidactylus.recorder.utils.Constants;

import java.util.List;

public class MuscleMapModel {
    private Constants.MuscleSide muscleSide;
    private Constants.MuscleBody muscleBody;
    private MuscleModel muscleModel;

    public MuscleMapModel() {

    }

    public MuscleModel getMuscleModel() {
        return muscleModel;
    }

    public Constants.MuscleBody getMuscleBody() {
        return muscleBody;
    }

    public Constants.MuscleSide getMuscleSide() {
        return muscleSide;
    }

    public void setMuscleBody(Constants.MuscleBody muscleBody) {
        this.muscleBody = muscleBody;
    }

    public void setMuscleModel(MuscleModel muscleModel) {
        this.muscleModel = muscleModel;
    }

    public void setMuscleSide(Constants.MuscleSide muscleSide) {
        this.muscleSide = muscleSide;
    }
}
