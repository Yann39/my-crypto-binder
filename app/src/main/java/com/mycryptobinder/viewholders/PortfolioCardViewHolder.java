package com.mycryptobinder.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.mycryptobinder.R;
import com.mycryptobinder.models.HoldingData;
import com.mycryptobinder.models.Price;

import java.text.DecimalFormat;
import java.util.Map;

/**
 * Provide a reference to the views for each data item
 * Complex data items may need more than one view per item,
 * and you provide access to all the views for a data item in a view holder
 * <p>
 * Created by Yann on 08/11/2017
 */

public class PortfolioCardViewHolder extends RecyclerView.ViewHolder {

    private TextView portfolio_currency_iso_code_textView;
    private TextView portfolio_holding_total_value_textView;
    private TextView portfolio_holding_quantity_textView;
    private TextView portfolio_card_current_price;
    private TextView portfolio_card_price_24h_change;

    public void setItem(HoldingData holdingData, Map<String, Price> prices) {
        DecimalFormat df = new DecimalFormat("#.##");

        String eurValueStr = "0.00";
        String eurTotalValueStr = "0.00";
        if (prices.get(holdingData.getIsoCode()) != null) {
            eurValueStr = prices.get(holdingData.getIsoCode()).getEur();
            Double eurValue = Double.parseDouble(eurValueStr);
            eurTotalValueStr = df.format(holdingData.getQuantity() * eurValue);
        }

        portfolio_currency_iso_code_textView.setText(holdingData.getIsoCode());
        portfolio_holding_quantity_textView.setText(holdingData.getQuantity() != null ? df.format(holdingData.getQuantity()) : "0.00");
        portfolio_card_price_24h_change.setText(df.format(8.56));
        portfolio_holding_total_value_textView.setText(eurTotalValueStr);
        portfolio_card_current_price.setText(eurValueStr);
    }

    public PortfolioCardViewHolder(View v) {
        super(v);
        portfolio_currency_iso_code_textView = v.findViewById(R.id.portfolio_card_currency_iso_code);
        portfolio_holding_total_value_textView = v.findViewById(R.id.portfolio_card_holding_total_value);
        portfolio_holding_quantity_textView = v.findViewById(R.id.portfolio_card_holding_quantity);
        portfolio_card_current_price = v.findViewById(R.id.portfolio_card_current_price);
        portfolio_card_price_24h_change = v.findViewById(R.id.portfolio_card_price_24h_change);
    }

}