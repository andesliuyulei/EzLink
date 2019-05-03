package com.keysight.yuleil01.ezlink;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;

public class BusStopRepository
{
    private BusStopDao dao;
    private LiveData<List<BusStop>> allElements;

    BusStopRepository(Application application)
    {
        EzLinkRoomDatabase db = EzLinkRoomDatabase.getDatabase(application);
        dao = db.busStopDao();
        allElements = dao.getAllElements();
    }

    LiveData<List<BusStop>> getAllElements()
    {
        return allElements;
    }

    public void deleteAllElements()
    {
        new deleteAllElementsAsyncTask(dao).execute();
    }

    public void insert(BusStop busStop)
    {
        new insertAsyncTask(dao).execute(busStop);
    }

    private static class deleteAllElementsAsyncTask extends AsyncTask<BusStop, Void, Void>
    {
        private BusStopDao mAsyncTaskDao;

        deleteAllElementsAsyncTask(BusStopDao dao)
        {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final BusStop... params)
        {
            mAsyncTaskDao.deleteAll();
            return null;
        }
    }

    private static class insertAsyncTask extends AsyncTask<BusStop, Void, Void>
    {
        private BusStopDao mAsyncTaskDao;

        insertAsyncTask(BusStopDao dao)
        {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final BusStop... params)
        {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }
}
