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

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.mycryptobinder.R;

public class TransactionListViewHolder extends RecyclerView.ViewHolder {

    public TextView transactionItemIdTextView;
    public TextView transactionItemPairTextView;
    public TextView transactionItemQuantityTextView;
    public TextView transactionItemPriceTextView;
    public TextView transactionItemTotalTextView;

    public TransactionListViewHolder(View v) {
        super(v);
        transactionItemIdTextView = v.findViewById(R.id.transaction_item_id_textView);
        transactionItemPairTextView = v.findViewById(R.id.transaction_item_pair_textView);
        transactionItemQuantityTextView = v.findViewById(R.id.transaction_item_quantity_textView);
        transactionItemPriceTextView = v.findViewById(R.id.transaction_item_price_textView);
        transactionItemTotalTextView = v.findViewById(R.id.transaction_item_total_textView);
    }

}