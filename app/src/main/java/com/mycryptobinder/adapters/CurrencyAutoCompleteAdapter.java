package com.mycryptobinder.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.mycryptobinder.R;
import com.mycryptobinder.models.Currency;
import com.mycryptobinder.viewholders.CurrencyCardViewHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Adapter class for currency list item rendering
 * It acts as a bridge between an AdapterView and the underlying data for that view
 * <p>
 * Created by Yann on 03/05/2017
 */

public class CurrencyAutoCompleteAdapter extends ArrayAdapter<String> implements Filterable {

    private List<Currency> currencies;
    private List<Currency> filteredCurrencies;

    public CurrencyAutoCompleteAdapter(Context context, int layoutResourceId, List<Currency> currencies) {
        super(context, layoutResourceId);
        this.currencies = currencies;
        this.filteredCurrencies = currencies;
    }

    @Override
    public int getCount() {
        return filteredCurrencies.size();
    }

    @Override
    public String getItem(int index) {
        return filteredCurrencies.get(index).getIsoCode() + " (" + filteredCurrencies.get(index).getName() + ")";
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if(constraint != null && currencies != null) {
                    List<Currency> tmpCurrencies = new ArrayList<>();
                    for (Currency c : currencies) {
                        if (c.getName().toLowerCase().contains(constraint.toString().toLowerCase())) {
                            tmpCurrencies.add(c);
                        }
                    }
                    // Now assign the values and count to the FilterResults object
                    filterResults.values = tmpCurrencies;
                    filterResults.count = tmpCurrencies.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if(results.count > 0) {
                    filteredCurrencies = (ArrayList<Currency>) results.values;
                    notifyDataSetChanged();
                }
                else {
                    filteredCurrencies = currencies;
                    notifyDataSetInvalidated();
                }
            }
        };
    }

}
