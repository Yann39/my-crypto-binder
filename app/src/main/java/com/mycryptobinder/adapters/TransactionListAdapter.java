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

package com.mycryptobinder.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mycryptobinder.R;
import com.mycryptobinder.activities.EditTransactionActivity;
import com.mycryptobinder.entities.Transaction;
import com.mycryptobinder.helpers.UtilsHelper;
import com.mycryptobinder.viewholders.TransactionListViewHolder;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.List;

public class TransactionListAdapter extends RecyclerView.Adapter<TransactionListViewHolder> {

    private List<Transaction> transactions;
    private final Context context;

    public TransactionListAdapter(Context context, List<Transaction> transactions) {
        this.transactions = transactions;
        this.context = context;
    }

    public void setItems(List<Transaction> transactions) {
        this.transactions = transactions;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TransactionListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        // inflate the layout (create a new view)
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_item_transaction, viewGroup, false);
        return new TransactionListViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final TransactionListViewHolder viewHolder, final int position) {

        // number formatter
        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.CEILING);

        // set right arrow icon depending on transaction type
        viewHolder.transactionItemPairTextView.setCompoundDrawablePadding(15);
        if (transactions.get(position).getType().equals("buy")) {
            Bitmap bitmap = UtilsHelper.getBitmapFromVectorDrawable(context, R.drawable.ic_arrow_right_green);
            Drawable d = new BitmapDrawable(context.getResources(), Bitmap.createScaledBitmap(bitmap, 70, 70, true));
            viewHolder.transactionItemPairTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(d, null, null, null);
        } else if (transactions.get(position).getType().equals("sell")) {
            Bitmap bitmap = UtilsHelper.getBitmapFromVectorDrawable(context, R.drawable.ic_arrow_left_red);
            Drawable d = new BitmapDrawable(context.getResources(), Bitmap.createScaledBitmap(bitmap, 70, 70, true));
            viewHolder.transactionItemPairTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(d, null, null, null);
        } else if (transactions.get(position).getType().equals("deposit")) {
            Bitmap bitmap = UtilsHelper.getBitmapFromVectorDrawable(context, R.drawable.ic_arrow_right_blue);
            Drawable d = new BitmapDrawable(context.getResources(), Bitmap.createScaledBitmap(bitmap, 70, 70, true));
            viewHolder.transactionItemPairTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(d, null, null, null);
        } else if (transactions.get(position).getType().equals("withdrawal")) {
            Bitmap bitmap = UtilsHelper.getBitmapFromVectorDrawable(context, R.drawable.ic_arrow_left_orange);
            Drawable d = new BitmapDrawable(context.getResources(), Bitmap.createScaledBitmap(bitmap, 70, 70, true));
            viewHolder.transactionItemPairTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(d, null, null, null);
        }

        // get text from the data set at this position and replace it in the view
        viewHolder.transactionItemPairTextView.setText(context.getResources().getString(R.string.label_transaction_pair, transactions.get(position).getCurrency1IsoCode(), transactions.get(position).getCurrency2IsoCode()));
        viewHolder.transactionItemQuantityTextView.setText(df.format(transactions.get(position).getQuantity() != null ? transactions.get(position).getQuantity() : 0));
        viewHolder.transactionItemPriceTextView.setText(transactions.get(position).getPrice() != null ? df.format(transactions.get(position).getPrice().doubleValue()) : "");
        viewHolder.transactionItemTotalTextView.setText(df.format(transactions.get(position).getTotal() != null ? transactions.get(position).getTotal() : 0));

        // set click initializer for item row
        viewHolder.itemView.setOnClickListener(v -> {

            // position of the clicked item
            final int position1 = viewHolder.getAdapterPosition();

            // store element values in the intent so we can access them later in the other activity
            Intent edit_trans = new Intent(v.getContext(), EditTransactionActivity.class);
            edit_trans.putExtra("id", transactions.get(position1).getId());
            edit_trans.putExtra("type", transactions.get(position1).getType());
            edit_trans.putExtra("currency1", transactions.get(position1).getCurrency1IsoCode());
            edit_trans.putExtra("currency2", transactions.get(position1).getCurrency2IsoCode());
            edit_trans.putExtra("exchange", transactions.get(position1).getExchangeName());
            edit_trans.putExtra("quantity", transactions.get(position1).getQuantity());
            edit_trans.putExtra("price", transactions.get(position1).getPrice());
            edit_trans.putExtra("fees", transactions.get(position1).getFee());
            edit_trans.putExtra("date", transactions.get(position1).getDate());
            edit_trans.putExtra("txId", transactions.get(position1).getTransactionId());
            edit_trans.putExtra("total", transactions.get(position1).getTotal());
            edit_trans.putExtra("comment", transactions.get(position1).getComment());
            v.getContext().startActivity(edit_trans);
        });
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    public void sortTransactions(int colIndex, boolean asc) {
        switch (colIndex) {
            case 0:
                if (asc) {
                    Collections.sort(transactions, (t1, t2) -> (t1.getCurrency1IsoCode() + "/" + t1.getCurrency2IsoCode()).compareTo(t2.getCurrency1IsoCode() + "/" + t2.getCurrency2IsoCode()));
                } else {
                    Collections.sort(transactions, (t1, t2) -> (t2.getCurrency1IsoCode() + "/" + t2.getCurrency2IsoCode()).compareTo(t1.getCurrency1IsoCode() + "/" + t1.getCurrency2IsoCode()));
                }
            case 1:
                if (asc) {
                    Collections.sort(transactions, (t1, t2) -> Double.compare(t1.getQuantity(), t2.getQuantity()));
                } else {
                    Collections.sort(transactions, (t1, t2) -> Double.compare(t2.getQuantity(), t1.getQuantity()));
                }
            case 2:
                if (asc) {
                    Collections.sort(transactions, (t1, t2) -> Double.compare(t1.getPrice(), t2.getPrice()));
                } else {
                    Collections.sort(transactions, (t1, t2) -> Double.compare(t2.getPrice(), t1.getPrice()));
                }
            case 3:
                if (asc) {
                    Collections.sort(transactions, (t1, t2) -> Double.compare(t1.getTotal(), t2.getTotal()));
                } else {
                    Collections.sort(transactions, (t1, t2) -> Double.compare(t2.getTotal(), t1.getTotal()));
                }
            default:
                if (asc) {
                    Collections.sort(transactions, (t1, t2) -> (t1.getCurrency1IsoCode() + "/" + t1.getCurrency2IsoCode()).compareTo(t2.getCurrency1IsoCode() + "/" + t2.getCurrency2IsoCode()));
                } else {
                    Collections.sort(transactions, (t1, t2) -> (t2.getCurrency1IsoCode() + "/" + t2.getCurrency2IsoCode()).compareTo(t1.getCurrency1IsoCode() + "/" + t1.getCurrency2IsoCode()));
                }
        }
    }
}