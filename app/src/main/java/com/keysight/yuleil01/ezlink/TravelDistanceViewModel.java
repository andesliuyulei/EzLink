package com.keysight.yuleil01.ezlink;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import java.util.List;

public class TravelDistanceViewModel extends AndroidViewModel
{
    private TravelDistanceRepository mRepository;

    private LiveData<List<TravelDistance>> mAllElements;

    public TravelDistanceViewModel(Application application)
    {
        super(application);
        mRepository = new TravelDistanceRepository(application);
        mAllElements = mRepository.getAllElements();
    }

    LiveData<List<TravelDistance>> getAllElements()
    {
        return mAllElements;
    }

    public void insert(TravelDistance travelDistance)
    {
        mRepository.insert(travelDistance);
    }

    public void deleteAllElements()
    {
        mRepository.deleteAllElements();
    }
}
