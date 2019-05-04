package com.keysight.yuleil01.ezlink;


import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface BusStopDao
{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(BusStop busStop);

    @Query("DELETE FROM bus_stop_table")
    void deleteAll();

    @Query("SELECT * from bus_stop_table ORDER BY stop_name ASC")
    LiveData<List<BusStop>> getAllElements();
}
