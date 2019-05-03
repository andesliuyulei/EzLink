package com.keysight.yuleil01.ezlink;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

@Database(entities = {Card.class, BusStop.class, MrtStation.class}, version = 4)
public abstract class EzLinkRoomDatabase extends RoomDatabase
{
    public abstract CardDao cardDao();
    public abstract BusStopDao busStopDao();
    public abstract MrtStationDao mrtStationDao();

    private static volatile EzLinkRoomDatabase INSTANCE;

    static EzLinkRoomDatabase getDatabase(final Context context)
    {
        if (INSTANCE == null)
        {
            synchronized (EzLinkRoomDatabase.class)
            {
                if (INSTANCE == null)
                {
                    // Create database here
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), EzLinkRoomDatabase.class, "ezlink_database").fallbackToDestructiveMigration().addCallback(sRoomDatabaseCallback).build();
                }
            }
        }
        return INSTANCE;
    }

    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback()
    {
        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db)
        {
            super.onOpen(db);
            //new PopulateDbAsync(INSTANCE).execute();
        }
    };

    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void>
    {
        private final CardDao mDao;

        PopulateDbAsync(EzLinkRoomDatabase db)
        {
            mDao = db.cardDao();
        }

        @Override
        protected Void doInBackground(final Void... params)
        {
            mDao.deleteAll();
            Card card = new Card("1234", null);
            mDao.insert(card);
            card = new Card("5678", null);
            mDao.insert(card);
            return null;
        }
    }
}
