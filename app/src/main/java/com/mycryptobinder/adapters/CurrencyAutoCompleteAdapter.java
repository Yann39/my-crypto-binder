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
import android.support.annotation.NonNull;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import com.mycryptobinder.entities.Currency;

import java.util.ArrayList;
import java.util.List;

public class CurrencyAutoCompleteAdapter extends ArrayAdapter<String> implements Filterable {

    private List<Currency> currencies;
    //private List<Currency> filteredCurrencies;

    public CurrencyAutoCompleteAdapter(List<Currency> currencies, Context context, int layoutResourceId) {
        super(context, layoutResourceId);
        this.currencies = currencies;
        //this.filteredCurrencies = currencies;
    }

    @Override
    public int getCount() {
        return currencies.size();
    }

    @Override
    public String getItem(int index) {
        return currencies.get(index).getIsoCode();
    }

    public void addItems(List<Currency> currencies) {
        this.currencies = currencies;
        //this.filteredCurrencies = currencies;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                // get only currencies that corresponds to the specified filter value
                if (constraint != null && currencies != null) {
                    List<Currency> tmpCurrencies = new ArrayList<>();
                    for (Currency c : currencies) {
                        if (c.getIsoCode().toLowerCase().contains(constraint.toString().toLowerCase())) {
                            tmpCurrencies.add(c);
                        }
                    }
                    filterResults.values = tmpCurrencies;
                    filterResults.count = tmpCurrencies.size();
                }
                return filterResults;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results.count > 0) {
                    currencies = (ArrayList<Currency>) results.values;
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
    }

}