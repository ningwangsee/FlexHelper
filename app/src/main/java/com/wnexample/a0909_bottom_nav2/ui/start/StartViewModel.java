package com.wnexample.a0909_bottom_nav2.ui.start;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Date;

public class StartViewModel extends ViewModel {

    private final MutableLiveData<String> mText;
    private Date now;
    public StartViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is start fragment");
    }
    public void setText(String a) {
        mText.setValue(a);
    }
    public LiveData<String> getText() {
        return mText;
    }
    public String getDate() {
        Date now = new Date();
        return now.toString();
    }
}