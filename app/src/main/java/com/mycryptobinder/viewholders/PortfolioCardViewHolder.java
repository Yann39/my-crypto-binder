package com.mycryptobinder.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mycryptobinder.R;
import com.mycryptobinder.models.HoldingData;

/**
 * Provide a reference to the views for each data item
 * Complex data items may need more than one view per item,
 * and you provide access to all the views for a data item in a view holder
 * <p>
 * Created by Yann on 08/11/2017
 */

public class PortfolioCardViewHolder extends RecyclerView.ViewHolder {

    public TextView portfolio_id_textView;
    public TextView portfolio_currency_iso_code_textView;
    public TextView portfolio_holding_total_value_textView;
    public TextView portfolio_holding_quantity_textView;
    public TextView portfolio_card_current_price;
    public TextView portfolio_card_price_24h_change;

    public void setItem(HoldingData holdingData) {
        portfolio_currency_iso_code_textView.setText(holdingData.getIsoCode());
        portfolio_holding_total_value_textView.setText(String.valueOf(holdingData.getCurrentValue()*holdingData.getQuantity()));
        portfolio_holding_quantity_textView.setText(String.valueOf(holdingData.getQuantity()));
        portfolio_card_current_price.setText(String.valueOf(holdingData.getCurrentPrice()));
        portfolio_card_price_24h_change.setText(String.valueOf(holdingData.getCurrentValue()));
    }

    public PortfolioCardViewHolder(View v) {
        super(v);
        portfolio_id_textView = v.findViewById(R.id.portfolio_card_currency_id);
        portfolio_currency_iso_code_textView = v.findViewById(R.id.portfolio_card_currency_iso_code);
        portfolio_holding_total_value_textView = v.findViewById(R.id.portfolio_card_holding_total_value);
        portfolio_holding_quantity_textView = v.findViewById(R.id.portfolio_card_holding_quantity);
        portfolio_card_current_price = v.findViewById(R.id.portfolio_card_current_price);
        portfolio_card_price_24h_change = v.findViewById(R.id.portfolio_card_price_24h_change);
    }

}