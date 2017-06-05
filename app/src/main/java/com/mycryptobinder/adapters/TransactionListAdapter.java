package com.mycryptobinder.adapters;


import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mycryptobinder.R;
import com.mycryptobinder.models.Transaction;

import java.util.List;

/**
 * Provide views to RecyclerView with data from mDataSet.
 */
public class TransactionListAdapter extends RecyclerView.Adapter<TransactionListAdapter.ViewHolder> {

    private List<Transaction> transactions;

    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView transactionItemTypeTextView;
        public TextView transactionItemPairTextView;
        public TextView transactionItemQuantityTextView;

        public ViewHolder(View v) {
            super(v);
            // Define click listener for the ViewHolder's View.
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            transactionItemTypeTextView = (TextView) v.findViewById(R.id.transaction_item_type_textView);
            transactionItemPairTextView = (TextView) v.findViewById(R.id.transaction_item_pair_textView);
            transactionItemQuantityTextView = (TextView) v.findViewById(R.id.transaction_item_quantity_textView);
        }
    }

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param transactions String[] containing the data to populate views to be used by RecyclerView.
     */
    public TransactionListAdapter(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view.
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_item_transaction, viewGroup, false);

        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        // Get element from your dataset at this position and replace the contents of the view with that element
        viewHolder.transactionItemTypeTextView.setText(transactions.get(position).getType());
        viewHolder.transactionItemPairTextView.setText(transactions.get(position).getCurrency1().getIsoCode() +"/"+ transactions.get(position).getCurrency2().getIsoCode());
        viewHolder.transactionItemQuantityTextView.setText(String.valueOf(transactions.get(position).getQuantity()));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return transactions.size();
    }
}