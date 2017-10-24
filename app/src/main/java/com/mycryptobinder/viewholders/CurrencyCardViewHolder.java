package com.mycryptobinder.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.mycryptobinder.R;

/**
 * Provide a reference to the views for each data item
 * Complex data items may need more than one view per item,
 * and you provide access to all the views for a data item in a view holder
 * <p>
 * Created by Yann on 26/05/2017
 */

public class CurrencyCardViewHolder extends RecyclerView.ViewHolder {

    public TextView currency_id_textView;
    public TextView currency_name_textView;
    public TextView currency_isocode_textView;
    public TextView currency_symbol_textView;
    public ImageView currency_delete_imageButton;

    public CurrencyCardViewHolder(View v) {
        super(v);
        currency_id_textView = (TextView) v.findViewById(R.id.currency_card_currency_id);
        currency_name_textView = (TextView) v.findViewById(R.id.currency_card_currency_name);
        currency_isocode_textView = (TextView) v.findViewById(R.id.currency_card_currency_iso_code);
        currency_symbol_textView = (TextView) v.findViewById(R.id.currency_card_currency_symbol);
        currency_delete_imageButton = (ImageView) v.findViewById(R.id.currency_card_btn_delete);
    }

}