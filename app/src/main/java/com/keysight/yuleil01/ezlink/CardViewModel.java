package com.keysight.yuleil01.ezlink;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import java.util.List;

public class CardViewModel extends AndroidViewModel
{
    private CardRepository mRepository;

    private LiveData<List<Card>> mAllCards;

    public CardViewModel (Application application)
    {
        super(application);
        mRepository = new CardRepository(application);
        mAllCards = mRepository.getAllCards();
    }

    LiveData<List<Card>> getAllCards()
    {
        return mAllCards;
    }

    public void insert(Card card)
    {
        mRepository.insert(card);
    }
}
