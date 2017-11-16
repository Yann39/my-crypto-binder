package com.mycryptobinder.adapters;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.mycryptobinder.entities.Exchange;

import java.util.List;

/**
 * Adapter class for currency list item rendering
 * It acts as a bridge between an AdapterView and the underlying data for that view
 * <p>
 * Created by Yann on 03/05/2017
 */

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
