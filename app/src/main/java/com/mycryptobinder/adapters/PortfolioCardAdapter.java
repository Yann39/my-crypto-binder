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

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mycryptobinder.R;
import com.mycryptobinder.helpers.UtilsHelper;
import com.mycryptobinder.models.HoldingData;
import com.mycryptobinder.models.PricesFull;
import com.mycryptobinder.viewholders.PortfolioCardViewHolder;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class PortfolioCardAdapter extends RecyclerView.Adapter<PortfolioCardViewHolder> {

    private List<HoldingData> holdingData;
    private Context context;
    private PricesFull pricesFull;

    public PortfolioCardAdapter(Context context, List<HoldingData> hdList, PricesFull pricesFull) {
        this.holdingData = hdList;
        this.context = context;
        this.pricesFull = pricesFull;
    }

    public void setItems(List<HoldingData> holdingData) {
        this.holdingData = holdingData;
        notifyDataSetChanged();
    }

    public void setPricesFull(PricesFull pricesFull) {
        this.pricesFull = pricesFull;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PortfolioCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // inflate the layout (create a new view)
        View v = LayoutInflater.from(context).inflate(R.layout.card_portfolio, parent, false);

        // get a random color from a predefined color array and apply it to the image view background drawable
        TypedArray ar = context.getResources().obtainTypedArray(R.array.currency_backgrounds);
        int len = ar.length();
        int[] picArray = new int[len];
        for (int i = 0; i < len; i++) {
            picArray[i] = ar.getResourceId(i, 0);
        }
        ar.recycle();
        int randomAndroidColor = picArray[new Random().nextInt(picArray.length)];
        ImageView imageView = v.findViewById(R.id.portfolio_card_currency_logo);
        DrawableCompat.setTint(imageView.getBackground(), ContextCompat.getColor(context, randomAndroidColor));

        return new PortfolioCardViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final PortfolioCardViewHolder holder, int position) {
        HoldingData hd = holdingData.get(position);
        if (hd != null) {
            holder.setItem(hd, pricesFull);
        }
    }

    @Override
    public int getItemCount() {
        return holdingData.size();
    }

    public void sortTransactions(int colIndex, boolean asc) {
        switch (colIndex) {
            case 0:
                if (asc) {
                    Collections.sort(holdingData, (t1, t2) -> (t1.getIsoCode()).compareTo(t2.getIsoCode()));
                } else {
                    Collections.sort(holdingData, (t1, t2) -> (t2.getIsoCode()).compareTo(t1.getIsoCode()));
                }
                break;
            case 1:
                if (asc) {
                    Collections.sort(holdingData, (t1, t2) -> Double.compare(t1.getQuantity() != null ? t1.getQuantity() : 0, t2.getQuantity() != null ? t2.getQuantity() : 0));
                } else {
                    Collections.sort(holdingData, (t1, t2) -> Double.compare(t2.getQuantity() != null ? t2.getQuantity() : 0, t1.getQuantity() != null ? t1.getQuantity() : 0));
                }
                break;
            case 2:
                if (asc) {
                    Collections.sort(holdingData, (t1, t2) -> Double.compare(t1.getQuantity() != null ? t1.getQuantity() : 0, t2.getQuantity() != null ? t2.getQuantity() : 0));
                } else {
                    Collections.sort(holdingData, (t1, t2) -> Double.compare(t2.getQuantity() != null ? t2.getQuantity() : 0, t1.getQuantity() != null ? t1.getQuantity() : 0));
                }
                break;
            default:
                if (asc) {
                    Collections.sort(holdingData, (t1, t2) -> (t1.getIsoCode()).compareTo(t2.getIsoCode()));
                } else {
                    Collections.sort(holdingData, (t1, t2) -> (t2.getIsoCode()).compareTo(t1.getIsoCode()));
                }
                break;
        }
        notifyDataSetChanged();
    }

}