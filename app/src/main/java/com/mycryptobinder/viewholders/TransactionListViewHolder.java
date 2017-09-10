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

public class TransactionListViewHolder extends RecyclerView.ViewHolder {

    public TextView transactionItemIdTextView;
    public TextView transactionItemTypeTextView;
    public TextView transactionItemPairTextView;
    public TextView transactionItemQuantityTextView;
    public TextView transactionItemPriceTextView;
    public TextView transactionItemTotalTextView;
    public ImageView transactionItemDetailsImageView;

    public TransactionListViewHolder(View v) {
        super(v);
        transactionItemIdTextView = (TextView) v.findViewById(R.id.transaction_item_id_textView);
        transactionItemTypeTextView = (TextView) v.findViewById(R.id.transaction_item_type_textView);
        transactionItemPairTextView = (TextView) v.findViewById(R.id.transaction_item_pair_textView);
        transactionItemQuantityTextView = (TextView) v.findViewById(R.id.transaction_item_quantity_textView);
        transactionItemPriceTextView = (TextView) v.findViewById(R.id.transaction_item_price_textView);
        transactionItemTotalTextView = (TextView) v.findViewById(R.id.transaction_item_total_textView);
        transactionItemDetailsImageView = (ImageView) v.findViewById(R.id.transaction_item_details_imageView);
    }

}