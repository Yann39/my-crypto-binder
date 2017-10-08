package com.mycryptobinder.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.mycryptobinder.R;
import com.mycryptobinder.activities.EditCurrencyActivity;
import com.mycryptobinder.managers.CurrencyManager;
import com.mycryptobinder.managers.TransactionManager;
import com.mycryptobinder.models.Currency;
import com.mycryptobinder.viewholders.CurrencyCardViewHolder;
import com.mycryptobinder.viewholders.PortfolioCardViewHolder;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Adapter class for portfolio cards rendering
 * It acts as a bridge between an AdapterView and the underlying data for that view
 * <p>
 * Created by Yann on 08/11/2017
 */

public class PortfolioCardAdapter extends RecyclerView.Adapter<PortfolioCardViewHolder> {

    private List<Currency> currencies;
    private Context context;

    public PortfolioCardAdapter(Context context, List<Currency> currencies) {
        this.currencies = currencies;
        this.context = context;
    }

    @Override
    public PortfolioCardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(context).inflate(R.layout.card_portfolio, parent, false);
        return new PortfolioCardViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final PortfolioCardViewHolder holder, int position) {

        TransactionManager tm = new TransactionManager(context);
        CurrencyManager cm = new CurrencyManager(context);
        tm.open();
        cm.open();

        // get text from the data set at this position and replace it in the view
        //holder.currency_id_textView.setText((String.valueOf(currencies.get(holder.getAdapterPosition()).getId())));
        DecimalFormat df = new DecimalFormat("#.##");
        String isoCode = currencies.get(holder.getAdapterPosition()).getIsoCode();
        holder.portfolio_currency_isocode_textView.setText(isoCode);
        holder.portfolio_holding_value_textView.setText(df.format(tm.getCurrencyTotal(isoCode)));
        holder.portfolio_holding_quantity_textView.setText(df.format(tm.getCurrencyQuantity(isoCode)));

        // card click
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    @Override
    public int getItemCount() {
        return currencies.size();
    }

}
