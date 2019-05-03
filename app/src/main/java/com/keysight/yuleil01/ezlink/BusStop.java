package com.keysight.yuleil01.ezlink;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "bus_stop_table")
public class BusStop
{
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "stop_name")
    private String stopName;

    public BusStop(@NonNull String stopName)
    {
        this.stopName = stopName;
    }

    public String getStopName()
    {
        return this.stopName;
    }

    public void setStopName(String stopName)
    {
        this.stopName = stopName;
    }
}
