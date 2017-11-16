package com.mycryptobinder.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.mycryptobinder.R;

/**
 * Provide a reference to the views for each data item
 * Complex data items may need more than one view per item,
 * and you provide access to all the views for a data item in a view holder
 * <p>
 * Created by Yann on 26/05/2017
 */

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