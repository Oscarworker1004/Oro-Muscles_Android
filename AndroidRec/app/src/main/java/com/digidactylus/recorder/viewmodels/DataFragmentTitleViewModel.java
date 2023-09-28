package com.digidactylus.recorder.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DataFragmentTitleViewModel extends ViewModel {
    private MutableLiveData<String> currentTabTitle = new MutableLiveData<>();
    public LiveData<String> getLiveCurrentTabTitle() {
        return currentTabTitle;
    }

    public void setCurrentTabTitle(String title) {
        currentTabTitle.setValue(title);
    }


    //////////////////////


    private MutableLiveData<String> currentTime = new MutableLiveData<>();
    public LiveData<String> getLivecurrentTime() {
        return currentTime;
    }

    public void setcurrentTime(String time) {
        currentTime.setValue(time);
    }
}
