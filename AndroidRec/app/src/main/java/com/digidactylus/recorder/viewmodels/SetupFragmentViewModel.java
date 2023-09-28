package com.digidactylus.recorder.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SetupFragmentViewModel extends ViewModel {
    private MutableLiveData<Integer> currentTab = new MutableLiveData<>();
    public LiveData<Integer> getLiveCurrentTab() {
        return currentTab;
    }

    public void setCurrentTab(int index) {
        currentTab.setValue(index);
    }
}
