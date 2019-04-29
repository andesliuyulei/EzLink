package com.keysight.yuleil01.ezlink;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

public class CardListAdapter extends RecyclerView.Adapter<CardListAdapter.CardViewHolder>
{
    class CardViewHolder extends RecyclerView.ViewHolder
    {
        private final TextView cardItemView;

        private CardViewHolder(View itemView)
        {
            super(itemView);
            cardItemView = itemView.findViewById(R.id.textView);
        }
    }

    private final LayoutInflater mInFlater;
    private List<Card> mCards = Collections.emptyList();

    CardListAdapter(Context context)
    {
        mInFlater = LayoutInflater.from(context);
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = mInFlater.inflate(R.layout.recyclerview_item, parent, false);
        return new CardViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CardViewHolder holder, int position)
    {
        Card current = mCards.get(position);
        holder.cardItemView.setText(current.getCardNumber());
    }

    void setCards(List<Card> cards)
    {
        mCards = cards;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount()
    {
        return mCards.size();
    }
}
