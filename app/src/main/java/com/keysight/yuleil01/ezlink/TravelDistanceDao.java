package com.keysight.yuleil01.ezlink;


import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface TravelDistanceDao
{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(TravelDistance travelDistance);

    @Query("DELETE FROM travel_distance_table")
    void deleteAll();

    @Query("SELECT * from travel_distance_table ORDER BY transit ASC")
    LiveData<List<TravelDistance>> getAllElements();
}
