package com.keysight.yuleil01.ezlink;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "mrt_station_table")
public class MrtStation
{
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "station_name")
    private String stationName;

    public MrtStation(@NonNull String stationName)
    {
        this.stationName = stationName;
    }

    public String getStationName()
    {
        return this.stationName;
    }

    public void setStationName(String stationName)
    {
        this.stationName = stationName;
    }
}
