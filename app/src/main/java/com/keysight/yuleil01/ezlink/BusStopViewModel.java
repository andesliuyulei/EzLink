package com.keysight.yuleil01.ezlink;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import java.util.List;

public class BusStopViewModel extends AndroidViewModel
{
    private BusStopRepository mRepository;

    private LiveData<List<BusStop>> mAllElements;

    public BusStopViewModel(Application application)
    {
        super(application);
        mRepository = new BusStopRepository(application);
        mAllElements = mRepository.getAllElements();
    }

    LiveData<List<BusStop>> getAllElements()
    {
        return mAllElements;
    }

    public void insert(BusStop busStop)
    {
        mRepository.insert(busStop);
    }

    public void deleteAllElements()
    {
        mRepository.deleteAllElements();
    }
}
