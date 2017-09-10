package com.mycryptobinder.adapters;


import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.mycryptobinder.R;
import com.mycryptobinder.activities.EditTransactionActivity;
import com.mycryptobinder.managers.TransactionManager;
import com.mycryptobinder.models.Transaction;
import com.mycryptobinder.viewholders.TransactionListViewHolder;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;

/**
 * Provide views to RecyclerView with data from mDataSet.
 */
public class TransactionListAdapter extends RecyclerView.Adapter<TransactionListViewHolder> {

    private List<Transaction> transactions;

    public TransactionListAdapter(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public TransactionListViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view.
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_item_transaction, viewGroup, false);

        return new TransactionListViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final TransactionListViewHolder viewHolder, final int position) {

        // number formatter
        DecimalFormat df = new DecimalFormat("#.#####");
        df.setRoundingMode(RoundingMode.CEILING);

        // get element from your data set at this position and replace the contents of the view with that element
        viewHolder.transactionItemTypeTextView.setText(transactions.get(position).getType());
        viewHolder.transactionItemPairTextView.setText(transactions.get(position).getCurrency1().getIsoCode() +"/"+ transactions.get(position).getCurrency2().getIsoCode());
        viewHolder.transactionItemQuantityTextView.setText(String.valueOf(transactions.get(position).getQuantity()));
        viewHolder.transactionItemPriceTextView.setText(String.valueOf(transactions.get(position).getPrice()));
        viewHolder.transactionItemTotalTextView.setText(df.format(transactions.get(position).getPrice()*transactions.get(position).getQuantity()) + "(-" + df.format(transactions.get(position).getFees()) + ")");

        viewHolder.transactionItemDetailsImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //creating a popup menu
                PopupMenu popup = new PopupMenu(view.getContext(), viewHolder.transactionItemDetailsImageView);
                //inflating menu from xml resource
                popup.inflate(R.menu.menu_main);
                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                        }
                        return false;
                    }
                });
                //displaying the popup
                popup.show();

            }
        });

        // set click initializer for item row
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // position of the clicked item
                final int position = viewHolder.getAdapterPosition();

                // store element values in the intent so we can access them later
                Intent edit_trans = new Intent(v.getContext(), EditTransactionActivity.class);
                edit_trans.putExtra("id", transactions.get(position).getId());
                edit_trans.putExtra("type", transactions.get(position).getType());
                edit_trans.putExtra("currency1", transactions.get(position).getCurrency1().getIsoCode());
                edit_trans.putExtra("currency2", transactions.get(position).getCurrency2().getIsoCode());
                edit_trans.putExtra("exchange", transactions.get(position).getExchange().getName());
                edit_trans.putExtra("quantity", transactions.get(position).getQuantity());
                edit_trans.putExtra("price", transactions.get(position).getPrice());
                edit_trans.putExtra("fees", transactions.get(position).getFees());
                edit_trans.putExtra("date", transactions.get(position).getDate());
                edit_trans.putExtra("comment", transactions.get(position).getComment());
                v.getContext().startActivity(edit_trans);
            }
        });
    }

    // Return the size of your data set (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return transactions.size();
    }
}