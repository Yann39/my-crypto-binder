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
 * Created by Yann on 08/11/2017
 */

public class PortfolioCardViewHolder extends RecyclerView.ViewHolder {

    public TextView portfolio_id_textView;
    public TextView portfolio_currency_isocode_textView;
    public TextView portfolio_holding_value_textView;
    public TextView portfolio_holding_quantity_textView;
    public ImageView portfolio_delete_imageView;

    public PortfolioCardViewHolder(View v) {
        super(v);
        portfolio_id_textView = (TextView) v.findViewById(R.id.portfolio_card_currency_id);
        portfolio_currency_isocode_textView = (TextView) v.findViewById(R.id.portfolio_card_currency_iso_code);
        portfolio_holding_value_textView = (TextView) v.findViewById(R.id.portfolio_card_holding_value);
        portfolio_holding_quantity_textView = (TextView) v.findViewById(R.id.portfolio_card_holding_quantity);
        portfolio_delete_imageView = (ImageView) v.findViewById(R.id.currency_card_btn_delete);
    }

}