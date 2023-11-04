package com.wnexample.a0909_bottom_nav2.ui.dashboard;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DashboardViewModel extends ViewModel {

    private final MutableLiveData<CharSequence> mText;

    public DashboardViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is dashboard fragment");
    }
    public void setText(CharSequence a) {
        mText.setValue(a);
    }
    public LiveData<CharSequence> getText() {
        return mText;
    }
}