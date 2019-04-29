package com.keysight.yuleil01.ezlink;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import java.util.List;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

public class DisplayRecyclerView extends AppCompatActivity
{
    private CardViewModel mCardViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_recycler_view);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        final CardListAdapter adapter = new CardListAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mCardViewModel = ViewModelProviders.of(this).get(CardViewModel.class);
        mCardViewModel.getAllCards().observe(this, new Observer<List<Card>>()
        {
            @Override
            public void onChanged(@Nullable final List<Card> cards)
            {
                adapter.setCards(cards);
            }
        });
    }
}
