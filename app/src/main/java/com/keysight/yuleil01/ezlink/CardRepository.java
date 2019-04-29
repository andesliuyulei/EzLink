package com.keysight.yuleil01.ezlink;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;

public class CardRepository
{
    private CardDao mCardDao;
    private LiveData<List<Card>> mAllCards;

    CardRepository(Application application)
    {
        EzLinkRoomDatabase db = EzLinkRoomDatabase.getDatabase(application);
        mCardDao = db.cardDao();
        mAllCards = mCardDao.getAllCards();
    }

    LiveData<List<Card>> getAllCards()
    {
        return mAllCards;
    }

    public void insert(Card card)
    {
        new insertAsyncTask(mCardDao).execute(card);
    }

    private static class insertAsyncTask extends AsyncTask<Card, Void, Void>
    {
        private CardDao mAsyncTaskDao;

        insertAsyncTask(CardDao dao)
        {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Card... params)
        {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }
}
