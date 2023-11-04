package com.wnexample.a0909_bottom_nav2.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Date;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<String> mText;
    private Date now;

    public HomeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("WareHouse Setting");
    }

    public LiveData<String> getText() {
        return mText;
    }
    public void setText(String a) {
        mText.setValue(a);
    }
    public String getDate() {
        now = new Date();
        return now.toString();
    }
}