package com.keysight.yuleil01.ezlink;


import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface MrtStationDao
{
    @Insert
    void insert(MrtStation mrtStation);

    @Query("DELETE FROM mrt_station_table")
    void deleteAll();

    @Query("SELECT * from mrt_station_table ORDER BY station_name ASC")
    LiveData<List<MrtStation>> getAllElements();
}
