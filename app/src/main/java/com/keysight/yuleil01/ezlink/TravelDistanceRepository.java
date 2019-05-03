package com.keysight.yuleil01.ezlink;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;

public class TravelDistanceRepository
{
    private TravelDistanceDao dao;
    private LiveData<List<TravelDistance>> allElements;

    TravelDistanceRepository(Application application)
    {
        EzLinkRoomDatabase db = EzLinkRoomDatabase.getDatabase(application);
        dao = db.travelDistanceDao();
        allElements = dao.getAllElements();
    }

    LiveData<List<TravelDistance>> getAllElements()
    {
        return allElements;
    }

    public void deleteAllElements()
    {
        new deleteAllElementsAsyncTask(dao).execute();
    }

    public void insert(TravelDistance travelDistance)
    {
        new insertAsyncTask(dao).execute(travelDistance);
    }

    private static class deleteAllElementsAsyncTask extends AsyncTask<TravelDistance, Void, Void>
    {
        private TravelDistanceDao mAsyncTaskDao;

        deleteAllElementsAsyncTask(TravelDistanceDao dao)
        {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final TravelDistance... params)
        {
            mAsyncTaskDao.deleteAll();
            return null;
        }
    }

    private static class insertAsyncTask extends AsyncTask<TravelDistance, Void, Void>
    {
        private TravelDistanceDao mAsyncTaskDao;

        insertAsyncTask(TravelDistanceDao dao)
        {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final TravelDistance... params)
        {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }
}
