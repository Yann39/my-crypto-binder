package com.mycryptobinder.viewholders;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mycryptobinder.R;
import com.mycryptobinder.activities.EditCurrencyActivity;
import com.mycryptobinder.entities.Currency;

import java.util.List;

/**
 * Provide a reference to the views for each data item
 * Complex data items may need more than one view per item,
 * and you provide access to all the views for a data item in a view holder
 * <p>
 * Created by Yann on 26/05/2017
 */

public class CurrencyCardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView currency_name_textView;
    public TextView currency_iso_code_textView;
    public ImageView currency_delete_imageButton;
    public TextView currency_symbol_textView;
    public List<Currency> currencies;
    private Currency currency;
    //private CurrencyCardListener currencyCardListener;

    public interface CurrencyCardListener {
        void onDeleteButtonClicked(Currency item);
    }

    public void setItem(Currency currency) {
        this.currency = currency;
        currency_name_textView.setText(currency.getName());
        currency_iso_code_textView.setText(currency.getIsoCode());
        currency_symbol_textView.setText(currency.getSymbol());
    }

    public CurrencyCardViewHolder(View view/*, final CurrencyCardListener currencyCardListener*/) {
        super(view);
        //this.currencyCardListener = currencyCardListener;
        view.setOnClickListener(this);

        currency_name_textView = view.findViewById(R.id.currency_card_currency_name);
        currency_iso_code_textView = view.findViewById(R.id.currency_card_currency_iso_code);
        currency_symbol_textView = view.findViewById(R.id.currency_card_currency_symbol);
        currency_delete_imageButton = view.findViewById(R.id.currency_card_btn_delete);

        // delete button click
        /*currency_delete_imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currencyCardListener.onDeleteButtonClicked(currency);
            }
        });*/
    }

    @Override
    public void onClick(View view) {
        // get element values
        String name = currency_name_textView.getText().toString();
        String isoCode = currency_iso_code_textView.getText().toString();
        String symbol = currency_symbol_textView.getText().toString();

        // store element values in the intent so we can access them later
        Intent edit_cur = new Intent(view.getContext(), EditCurrencyActivity.class);
        edit_cur.putExtra("name", name);
        edit_cur.putExtra("isoCode", isoCode);
        edit_cur.putExtra("symbol", symbol);

        view.getContext().startActivity(edit_cur);
    }
}