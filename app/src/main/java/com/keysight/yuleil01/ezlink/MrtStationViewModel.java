package com.keysight.yuleil01.ezlink;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import java.util.List;

public class MrtStationViewModel extends AndroidViewModel
{
    private MrtStationRepository mRepository;

    private LiveData<List<MrtStation>> mAllElements;

    public MrtStationViewModel(Application application)
    {
        super(application);
        mRepository = new MrtStationRepository(application);
        mAllElements = mRepository.getAllElements();
    }

    LiveData<List<MrtStation>> getAllElements()
    {
        return mAllElements;
    }

    public void insert(MrtStation mrtStation)
    {
        mRepository.insert(mrtStation);
    }

    public void deleteAllElements()
    {
        mRepository.deleteAllElements();
    }
}
