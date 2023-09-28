package com.digidactylus.recorder.viewmodels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class recordTimerViewModel extends ViewModel {
    private MutableLiveData<String> currentTime = new MutableLiveData<>();
    public LiveData<String> getLivecurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(String time) {
        Log.d("kjlfdsfkdsfds","jkfdlsjfds");
        currentTime.setValue(time);
    }
}
