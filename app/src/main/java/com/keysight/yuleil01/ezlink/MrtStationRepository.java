package com.keysight.yuleil01.ezlink;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;

public class MrtStationRepository
{
    private MrtStationDao dao;
    private LiveData<List<MrtStation>> allElements;

    MrtStationRepository(Application application)
    {
        EzLinkRoomDatabase db = EzLinkRoomDatabase.getDatabase(application);
        dao = db.mrtStationDao();
        allElements = dao.getAllElements();
    }

    LiveData<List<MrtStation>> getAllElements()
    {
        return allElements;
    }

    public void deleteAllElements()
    {
        new deleteAllElementsAsyncTask(dao).execute();
    }

    public void insert(MrtStation mrtStation)
    {
        new insertAsyncTask(dao).execute(mrtStation);
    }

    private static class deleteAllElementsAsyncTask extends AsyncTask<MrtStation, Void, Void>
    {
        private MrtStationDao mAsyncTaskDao;

        deleteAllElementsAsyncTask(MrtStationDao dao)
        {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final MrtStation... params)
        {
            mAsyncTaskDao.deleteAll();
            return null;
        }
    }

    private static class insertAsyncTask extends AsyncTask<MrtStation, Void, Void>
    {
        private MrtStationDao mAsyncTaskDao;

        insertAsyncTask(MrtStationDao dao)
        {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final MrtStation... params)
        {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }
}
