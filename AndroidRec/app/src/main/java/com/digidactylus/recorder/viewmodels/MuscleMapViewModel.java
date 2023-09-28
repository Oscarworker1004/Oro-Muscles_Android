package com.digidactylus.recorder.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MuscleMapViewModel extends ViewModel {


    // Left Right Side Buttons
    private MutableLiveData<Integer> currentSide = new MutableLiveData<>();
    public LiveData<Integer> getLiveCurrentSide() {
        return currentSide;
    }
    public void setCurrentSide(int index) {
        currentSide.setValue(index);
    }

    // Body Parts
    private MutableLiveData<Integer> currentBodyPart = new MutableLiveData<>();
    public LiveData<Integer> getLiveCurrentBodyPart() {
        return currentBodyPart;
    }
    public void setCurrentBodyPart(int index) {
        currentBodyPart.setValue(index);
    }

}
