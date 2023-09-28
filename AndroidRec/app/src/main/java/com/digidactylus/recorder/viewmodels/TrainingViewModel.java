package com.digidactylus.recorder.viewmodels;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class TrainingViewModel extends ViewModel {
    private MutableLiveData<Boolean> trainingModifiedLiveData = new MutableLiveData<>();

    public LiveData<Boolean> getTrainingModifiedLiveData() {
        return trainingModifiedLiveData;
    }

    public void setTrainingModified(boolean value) {
        trainingModifiedLiveData.setValue(value);
    }
}
