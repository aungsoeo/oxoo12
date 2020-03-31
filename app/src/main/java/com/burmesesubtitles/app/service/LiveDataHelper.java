package com.burmesesubtitles.app.service;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

public class LiveDataHelper {
    private MediatorLiveData<Integer> _percent = new MediatorLiveData<>();
    private MediatorLiveData<Boolean> _isCompleted = new MediatorLiveData<>();

    private LiveDataHelper() {
    }

    private static LiveDataHelper liveDataHelper;

    synchronized public static LiveDataHelper getInstance() {
        if (liveDataHelper == null)
            liveDataHelper = new LiveDataHelper();
        return liveDataHelper;
    }

    void updatePercentage(int percentage) {
        _percent.postValue(percentage);
    }

    public LiveData<Integer> observePercentage() {
        return _percent;
    }

    void completeStatus(boolean isCompleted) {
        _isCompleted.postValue(isCompleted);
    }

    public LiveData<Boolean> observeIsCompleted() {
        return _isCompleted;
    }
}
