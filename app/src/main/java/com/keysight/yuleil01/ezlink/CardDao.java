package com.keysight.yuleil01.ezlink;


import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface CardDao
{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Card card);

    @Query("DELETE FROM card_table")
    void deleteAll();

    @Query("SELECT * from card_table ORDER BY card_number ASC")
    LiveData<List<Card>> getAllCards();
}
