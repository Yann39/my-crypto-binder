package com.mycryptobinder.adapters;

import android.content.Context;
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

public class CurrencyCardAdapter extends RecyclerView.Adapter<CurrencyCardViewHolder> {

    private List<Currency> currencies;
    private Context context;

    public CurrencyCardAdapter(List<Currency> currencies, Context context) {
        this.currencies = currencies;
        this.context = context;
    }

    @Override
    public CurrencyCardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(context).inflate(R.layout.card_currency, parent, false);
        return new CurrencyCardViewHolder(v, this);
    }

    public void addItems(List<Currency> currencies) {
        this.currencies = currencies;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(final CurrencyCardViewHolder holder, int position) {

        // get text from the data set at this position and replace it in the view
        holder.currency_name_textView.setText(currencies.get(holder.getAdapterPosition()).getName());
        holder.currency_iso_code_textView.setText(currencies.get(holder.getAdapterPosition()).getIsoCode());
        holder.currency_symbol_textView.setText(currencies.get(holder.getAdapterPosition()).getSymbol());

    }

    @Override
    public int getItemCount() {
        return currencies.size();
    }

}
