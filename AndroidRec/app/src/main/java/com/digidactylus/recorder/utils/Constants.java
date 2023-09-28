package com.digidactylus.recorder.utils;

public class Constants {

    public enum MuscleSide {
        RIGHT,
        LEFT,
    }

    public enum MuscleBody {
        UPPER_LEG,
        LOWER_LEG,
        UPPER_BODY,
        ARMS,
        BACK,
        HEAD
    }

    public static final String PREF_ATHLETE_LIST = "athletesList";
    public static final String PREF_SELECTED_ATHLETE = "selectedAthlete";
    public static final String PREF_CUSTOM_EXERCIESES = "prefCustomExercises";
    public static final String PREF_SELECTED_EXERCIESES = "preSelectedExercises";
    public static final String PREF_SELECTED_MUSCLE= "preSelectedMuscle";

    public static final String PREF_WORKOUT_NAME  = "prefWorkoutName";

    public static final String PREF_LABELING_DATA = "prefLabelingData";
    public static final String LABELING_LIST = "labelingList";


    public static final double AUTO_SCALE_INTENSITY = (double) 200/9000;
    public static final double AUTO_SCALE_WORK_CAPACITY = (double) 200/3000;
}
