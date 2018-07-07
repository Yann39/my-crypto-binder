/*
 * Copyright (c) 2018 by Yann39.
 *
 * This file is part of MyCryptoBinder.
 *
 * MyCryptoBinder is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MyCryptoBinder is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MyCryptoBinder. If not, see <http://www.gnu.org/licenses/>.
 */

package com.mycryptobinder.adapters;

import android.arch.paging.PagedListAdapter;
import android.support.annotation.NonNull;
import android.support.v7.recyclerview.extensions.AsyncListDiffer;
import android.support.v7.util.DiffUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mycryptobinder.R;
import com.mycryptobinder.entities.Currency;
import com.mycryptobinder.viewholders.CurrencyCardViewHolder;

import java.util.List;

public class CurrencyCardAdapter extends PagedListAdapter<Currency, CurrencyCardViewHolder> {

    private View.OnClickListener clickListener;
    private final AsyncListDiffer<Currency> mDiffer = new AsyncListDiffer<>(this, DIFF_CALLBACK);

    public CurrencyCardAdapter(View.OnClickListener clickListener) {
        super(DIFF_CALLBACK);
        this.clickListener = clickListener;
    }

    public void setList(List<Currency> list) {
        mDiffer.submitList(list);
    }

    private static final DiffUtil.ItemCallback<Currency> DIFF_CALLBACK = new DiffUtil.ItemCallback<Currency>() {
        @Override
        public boolean areItemsTheSame(@NonNull Currency oldCurrency, @NonNull Currency newCurrency) {
            return oldCurrency.getIsoCode().equals(newCurrency.getIsoCode());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Currency oldCurrency, @NonNull Currency newCurrency) {
            return oldCurrency.equals(newCurrency);
        }
    };

    @NonNull
    @Override
    public CurrencyCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View v = layoutInflater.inflate(R.layout.card_currency, parent, false);
        return new CurrencyCardViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CurrencyCardViewHolder holder, int position) {
        Currency currency = mDiffer.getCurrentList().get(position);
        if (currency != null) {
            holder.setItem(currency);
            holder.currency_delete_imageButton.setOnClickListener(clickListener);
        }
    }

    @Override
    public int getItemCount() {
        return mDiffer.getCurrentList().size();
    }
}