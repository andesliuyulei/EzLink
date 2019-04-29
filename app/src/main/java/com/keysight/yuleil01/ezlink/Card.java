package com.keysight.yuleil01.ezlink;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "card_table")
public class Card
{
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "card_number")
    private String cardNumber;

    public Card(@NonNull String cardNumber)
    {
        this.cardNumber = cardNumber;
    }

    public String getCardNumber()
    {
        return this.cardNumber;
    }

    public void setCardNumber(String cardNumber)
    {
        this.cardNumber = cardNumber;
    }
}

