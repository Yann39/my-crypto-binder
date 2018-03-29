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

import com.mycryptobinder.entities.Exchange;

import java.util.List;

public class ExchangeSpinnerAdapter extends ArrayAdapter<String> {

    private List<Exchange> exchanges;

    public ExchangeSpinnerAdapter(List<Exchange> exchanges, Context context, int layoutResourceId) {
        super(context, layoutResourceId);
        this.exchanges = exchanges;
    }

    public void addItems(List<Exchange> exchanges) {
        this.exchanges = exchanges;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return exchanges.size();
    }

    @Override
    public String getItem(int index) {
        return exchanges.get(index).getName();
    }

    @Override
    public int getPosition(String item) {
        int i = 0;
        for (Exchange ex : exchanges) {
            if (ex.getName().equals(item)) {
                return i;
            }
            i++;
        }
        return -1;
    }

}