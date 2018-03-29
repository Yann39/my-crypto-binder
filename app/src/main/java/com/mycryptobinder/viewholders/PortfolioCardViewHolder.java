/*
 * Copyright (c) 2018 by Yann39.
 *
 * This file is part of MyCryptoBinder.
 *
 * MyCryptoBinder is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MyCryptoBinder is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MyCryptoBinder. If not, see <http://www.gnu.org/licenses/>.
 */

package com.mycryptobinder.viewholders;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.mycryptobinder.R;
import com.mycryptobinder.models.HoldingData;
import com.mycryptobinder.models.PricesFull;

import java.text.DecimalFormat;

public class PortfolioCardViewHolder extends RecyclerView.ViewHolder {

    private TextView portfolio_currency_iso_code_textView;
    private TextView portfolio_holding_total_value_textView;
    private TextView portfolio_holding_quantity_textView;
    private TextView portfolio_card_current_price;
    private TextView portfolio_card_price_24h_change;

    public PortfolioCardViewHolder(View v) {
        super(v);
        portfolio_currency_iso_code_textView = v.findViewById(R.id.portfolio_card_currency_iso_code);
        portfolio_holding_total_value_textView = v.findViewById(R.id.portfolio_card_holding_total_value);
        portfolio_holding_quantity_textView = v.findViewById(R.id.portfolio_card_holding_quantity);
        portfolio_card_current_price = v.findViewById(R.id.portfolio_card_current_price);
        portfolio_card_price_24h_change = v.findViewById(R.id.portfolio_card_price_24h_change);
    }

    public void setItem(HoldingData holdingData, PricesFull pricesFull) {
        DecimalFormat df = new DecimalFormat("#.##");
        DecimalFormat dfp = new DecimalFormat("+#.##;-#");

        String eurValueStr = "0.00";
        String eurTotalValueStr = "0.00";
        String changeDayStr = "0.00%";
        if (pricesFull != null && pricesFull.getRaw() != null && pricesFull.getRaw().get(holdingData.getIsoCode()) != null) {
            eurValueStr = pricesFull.getRaw().get(holdingData.getIsoCode()).getEur().getPrice();
            Double eurValue = Double.parseDouble(eurValueStr);
            eurValueStr = "€ " + eurValueStr;
            eurTotalValueStr = "€ " + df.format(holdingData.getQuantity() * eurValue);
            Double changeDay = pricesFull.getRaw().get(holdingData.getIsoCode()).getEur().getChangeDayPercent();
            if (changeDay > 0) {
                portfolio_card_price_24h_change.setTextColor(Color.parseColor("#269926"));
            } else if (changeDay < 0) {
                portfolio_card_price_24h_change.setTextColor(Color.parseColor("#992626"));
            }
            changeDayStr = dfp.format(changeDay) + "%";
        }

        portfolio_currency_iso_code_textView.setText(holdingData.getIsoCode());
        portfolio_holding_quantity_textView.setText(holdingData.getQuantity() != null ? df.format(holdingData.getQuantity()) : "0.00");
        portfolio_card_price_24h_change.setText(changeDayStr);
        portfolio_holding_total_value_textView.setText(eurTotalValueStr);
        portfolio_card_current_price.setText(eurValueStr);
    }

}