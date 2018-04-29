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
import android.widget.ArrayAdapter;

import com.mycryptobinder.entities.Currency;

import java.util.List;

public class CurrencySpinnerAdapter extends ArrayAdapter<String> {

    private List<Currency> currencies;

    public CurrencySpinnerAdapter(List<Currency> currencies, Context context, int layoutResourceId) {
        super(context, layoutResourceId);
        this.currencies = currencies;
    }

    public void addItems(List<Currency> currencies) {
        this.currencies = currencies;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return currencies.size();
    }

    @Override
    public String getItem(int index) {
        return currencies.get(index).getIsoCode();
    }

    @Override
    public int getPosition(String item) {
        int i = 0;
        for (Currency c : currencies) {
            if (c.getIsoCode().equals(item)) {
                return i;
            }
            i++;
        }
        return -1;
    }

}