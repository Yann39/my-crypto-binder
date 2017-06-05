package com.mycryptobinder.adapters;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import com.mycryptobinder.models.Currency;
import com.mycryptobinder.models.Exchange;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter class for currency list item rendering
 * It acts as a bridge between an AdapterView and the underlying data for that view
 * <p>
 * Created by Yann on 03/05/2017
 */

public class ExchangeSpinnerAdapter extends ArrayAdapter<String> {

    private List<Exchange> exchanges;

    public ExchangeSpinnerAdapter(Context context, int layoutResourceId, List<Exchange> exchanges) {
        super(context, layoutResourceId);
        this.exchanges = exchanges;
    }

    @Override
    public int getCount() {
        return exchanges.size();
    }

    @Override
    public long getItemId(int index) {
        return exchanges.get(index).getId();
    }

    @Override
    public String getItem(int index) {
        return exchanges.get(index).getName() + " (" + exchanges.get(index).getDescription() + ")";
    }

}
