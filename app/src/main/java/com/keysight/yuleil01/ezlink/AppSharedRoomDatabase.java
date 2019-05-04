package com.keysight.yuleil01.ezlink;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

@Database(entities = {Remark.class}, version = 1)
public abstract class AppSharedRoomDatabase extends RoomDatabase
{
    public abstract RemarkDao remarkDao();

    private static volatile AppSharedRoomDatabase INSTANCE;

    static AppSharedRoomDatabase getDatabase(final Context context)
    {
        if (INSTANCE == null)
        {
            synchronized (AppSharedRoomDatabase.class)
            {
                if (INSTANCE == null)
                {
                    // Create database here
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), AppSharedRoomDatabase.class, "app_shared_database").fallbackToDestructiveMigration().addCallback(sRoomDatabaseCallback).build();
                }
            }
        }
        return INSTANCE;
    }

    private static Callback sRoomDatabaseCallback = new Callback()
    {
        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db)
        {
            super.onOpen(db);
        }
    };
}
