package com.mycryptobinder.adapters;


import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mycryptobinder.R;
import com.mycryptobinder.activities.EditTransactionActivity;
import com.mycryptobinder.models.Transaction;
import com.mycryptobinder.viewholders.TransactionListViewHolder;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.Comparator;
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
        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.CEILING);

        // get element from your data set at this position and replace the contents of the view with that element
        viewHolder.transactionItemPairTextView.setCompoundDrawablePadding(5);
        if (transactions.get(position).getType().equals("buy")) {
            viewHolder.transactionItemPairTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_arrow_right_green, 0, 0, 0);
        } else if (transactions.get(position).getType().equals("sell")) {
            viewHolder.transactionItemPairTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_arrow_left_red, 0, 0, 0);
        }
        viewHolder.transactionItemPairTextView.setText(transactions.get(position).getCurrency1().getIsoCode() + "/" + transactions.get(position).getCurrency2().getIsoCode());
        viewHolder.transactionItemQuantityTextView.setText(df.format(transactions.get(position).getQuantity()));
        viewHolder.transactionItemPriceTextView.setText(df.format(transactions.get(position).getPrice()));
        viewHolder.transactionItemTotalTextView.setText(df.format(transactions.get(position).getTotal()));

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

    public void sortTransactions(int colIndex, boolean asc) {
        switch (colIndex) {
            case 0:
                if (asc) {
                    Collections.sort(transactions, new Comparator<Transaction>() {
                        @Override
                        public int compare(Transaction t1, Transaction t2) {
                            return (t1.getCurrency1().getIsoCode() + "/" + t1.getCurrency2().getIsoCode()).compareTo(t2.getCurrency1().getIsoCode() + "/" + t2.getCurrency2().getIsoCode());
                        }
                    });
                } else {
                    Collections.sort(transactions, new Comparator<Transaction>() {
                        @Override
                        public int compare(Transaction t1, Transaction t2) {
                            return (t2.getCurrency1().getIsoCode() + "/" + t2.getCurrency2().getIsoCode()).compareTo(t1.getCurrency1().getIsoCode() + "/" + t1.getCurrency2().getIsoCode());
                        }
                    });
                }
            case 1:
                if (asc) {
                    Collections.sort(transactions, new Comparator<Transaction>() {
                        @Override
                        public int compare(Transaction t1, Transaction t2) {
                            return Double.compare(t1.getQuantity(), t2.getQuantity());
                        }
                    });
                } else {
                    Collections.sort(transactions, new Comparator<Transaction>() {
                        @Override
                        public int compare(Transaction t1, Transaction t2) {
                            return Double.compare(t2.getQuantity(), t1.getQuantity());
                        }
                    });
                }
            case 2:
                if (asc) {
                    Collections.sort(transactions, new Comparator<Transaction>() {
                        @Override
                        public int compare(Transaction t1, Transaction t2) {
                            return Double.compare(t1.getPrice(), t2.getPrice());
                        }
                    });
                } else {
                    Collections.sort(transactions, new Comparator<Transaction>() {
                        @Override
                        public int compare(Transaction t1, Transaction t2) {
                            return Double.compare(t2.getPrice(), t1.getPrice());
                        }
                    });
                }
            case 3:
                if (asc) {
                    Collections.sort(transactions, new Comparator<Transaction>() {
                        @Override
                        public int compare(Transaction t1, Transaction t2) {
                            return Double.compare(t1.getTotal(), t2.getTotal());
                        }
                    });
                } else {
                    Collections.sort(transactions, new Comparator<Transaction>() {
                        @Override
                        public int compare(Transaction t1, Transaction t2) {
                            return Double.compare(t2.getTotal(), t1.getTotal());
                        }
                    });
                }
            default:
                if (asc) {
                    Collections.sort(transactions, new Comparator<Transaction>() {
                        @Override
                        public int compare(Transaction t1, Transaction t2) {
                            return (t1.getCurrency1().getIsoCode() + "/" + t1.getCurrency2().getIsoCode()).compareTo(t2.getCurrency1().getIsoCode() + "/" + t2.getCurrency2().getIsoCode());
                        }
                    });
                } else {
                    Collections.sort(transactions, new Comparator<Transaction>() {
                        @Override
                        public int compare(Transaction t1, Transaction t2) {
                            return (t2.getCurrency1().getIsoCode() + "/" + t2.getCurrency2().getIsoCode()).compareTo(t1.getCurrency1().getIsoCode() + "/" + t1.getCurrency2().getIsoCode());
                        }
                    });
                }
        }
    }
}