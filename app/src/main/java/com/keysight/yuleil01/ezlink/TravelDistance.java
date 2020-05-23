package com.keysight.yuleil01.ezlink;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "travel_distance_table", primaryKeys = {"transit", "from", "to"})
public class TravelDistance
{
    @NonNull
    @ColumnInfo(name = "transit")
    private String transit;

    @NonNull
    @ColumnInfo(name = "from")
    private String from;

    @NonNull
    @ColumnInfo(name = "to")
    private String to;

    public TravelDistance(@NonNull String transit, @NonNull String from, @NonNull String to)
    {
        this.transit = transit;
        this.from = from;
        this.to = to;
    }

    public String getTransit()
    {
        return this.transit;
    }

    public void setTransit(String transit)
    {
        this.transit = transit;
    }

    public String getFrom()
    {
        return this.from;
    }

    public void setFrom(String from)
    {
        this.from = from;
    }

    public String getTo()
    {
        return this.to;
    }

    public void setTo(String to)
    {
        this.to = to;
    }
}
