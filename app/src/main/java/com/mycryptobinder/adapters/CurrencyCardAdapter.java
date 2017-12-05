package com.mycryptobinder.adapters;

import android.arch.paging.PagedListAdapter;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.recyclerview.extensions.DiffCallback;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mycryptobinder.R;
import com.mycryptobinder.entities.Currency;
import com.mycryptobinder.viewholders.CurrencyCardViewHolder;

import java.util.List;

/**
 * Adapter class for currency cards rendering
 * It acts as a bridge between an AdapterView and the underlying data for that view
 * <p>
 * Created by Yann on 25/05/2017
 */

public class CurrencyCardAdapter extends PagedListAdapter<Currency, CurrencyCardViewHolder> {

    /*private List<Currency> currencies;*/
    private CurrencyCardViewHolder.CurrencyCardListener currencyCardListener;

    public CurrencyCardAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffCallback<Currency> DIFF_CALLBACK = new DiffCallback<Currency>() {
        @Override
        public boolean areItemsTheSame(@NonNull Currency oldCurrency, @NonNull Currency newCurrency) {
            // User properties may have changed if reloaded from the DB, but ID is fixed
            return oldCurrency.getIsoCode().equals(newCurrency.getIsoCode());
        }
        @Override
        public boolean areContentsTheSame(@NonNull Currency oldCurrency, @NonNull Currency newCurrency) {
            // NOTE: if you use equals, your object must properly override Object#equals()
            // Incorrectly returning false here will result in too many animations.
            return oldCurrency.equals(newCurrency);
        }
    };

    /*public CurrencyCardAdapter(LayoutInflater layoutInflater, CurrencyCardViewHolder.CurrencyCardListener currencyCardListener) {
        this.layoutInflater = layoutInflater;
        this.currencyCardListener = currencyCardListener;
    }*/

    @Override
    public CurrencyCardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View v = layoutInflater.inflate(R.layout.card_currency, parent, false);
        return new CurrencyCardViewHolder(v, currencyCardListener);
    }

    @Override
    public void onBindViewHolder(CurrencyCardViewHolder holder, int position) {

        // get text from the data set at this position and replace it in the view
        //holder.setItem(currencies.get(position));

        Currency currency = getItem(position);
        if (currency != null) {
            holder.setItem(currency);
        } else {
            // Null defines a placeholder item - PagedListAdapter will automatically invalidate
            // this row when the actual object is loaded from the database
            //holder.clear();
        }

    }

    /*@Override
    public int getItemCount() {
        return currencies.size();
    }*/

}
