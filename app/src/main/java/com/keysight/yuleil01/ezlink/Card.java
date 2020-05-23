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

    @ColumnInfo(name = "card_owner")
    private String cardOwner;

    public Card(@NonNull String cardNumber, String cardOwner)
    {
        this.cardNumber = cardNumber;
        this.cardOwner = cardOwner;
    }

    public String getCardNumber()
    {
        return this.cardNumber;
    }

    public String getCardOwner()
    {
        return cardOwner;
    }

    public void setCardNumber(String cardNumber)
    {
        this.cardNumber = cardNumber;
    }

    public void setCardOwner(String cardOwner)
    {
        this.cardOwner = cardOwner;
    }
}

