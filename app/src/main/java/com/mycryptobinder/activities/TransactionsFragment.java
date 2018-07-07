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

package com.mycryptobinder.activities;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mycryptobinder.R;
import com.mycryptobinder.adapters.TransactionListAdapter;
import com.mycryptobinder.viewmodels.TransactionsViewModel;

import java.util.ArrayList;

public class TransactionsFragment extends Fragment {

    private boolean col0Asc = false;
    private boolean col1Asc = false;
    private boolean col2Asc = false;
    private boolean col3Asc = false;
    private TransactionListAdapter transactionListAdapter;
    private TextView transactionsPairColumnHeaderText;
    private TextView transactionsQuantityColumnHeaderText;
    private TextView transactionsPriceColumnHeaderText;
    private TextView transactionsTotalColumnHeaderText;
    private Drawable caretDown;
    private Drawable caretUp;

    public TransactionsFragment() {
        // required empty public constructor
    }

    public static TransactionsFragment newInstance() {
        final TransactionsFragment fragment = new TransactionsFragment();
        final Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // inflate the layout (create a new view)
        final View view = inflater.inflate(R.layout.fragment_transactions, container, false);

        // prepare the recycler view with a linear layout
        final RecyclerView transactionsRecyclerView = view.findViewById(R.id.transactions_list_recycler_view);
        transactionsRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));

        // initialize the adapter for the list
        transactionListAdapter = new TransactionListAdapter(this.getContext(), new ArrayList<>());
        transactionsRecyclerView.setAdapter(transactionListAdapter);

        // get view model
        final TransactionsViewModel transactionsViewModel = ViewModelProviders.of(this).get(TransactionsViewModel.class);

        // observe the transactions data from the view model so it will always be up to date in the UI
        transactionsViewModel.getTransactionList().observe(TransactionsFragment.this, transactionList -> {
            // update data in the adapter
            transactionListAdapter.setItems(transactionList);
        });

        // add horizontal separator between rows
        final DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(transactionsRecyclerView.getContext(), LinearLayoutManager.VERTICAL);
        transactionsRecyclerView.addItemDecoration(mDividerItemDecoration);

        // set click listener for the add transaction button
        final FloatingActionButton button = view.findViewById(R.id.btn_add_transaction);
        button.setOnClickListener(view1 -> {
            final Intent add_trans = new Intent(view1.getContext(), AddTransactionActivity.class);
            startActivityForResult(add_trans, 1);
        });

        // get view elements
        final LinearLayout transactionsPairColumnHeader = view.findViewById(R.id.transactions_pair_column_header);
        transactionsPairColumnHeaderText = view.findViewById(R.id.transactions_pair_column_header_text);
        final LinearLayout transactionsQuantityColumnHeader = view.findViewById(R.id.transactions_quantity_column_header);
        transactionsQuantityColumnHeaderText = view.findViewById(R.id.transactions_quantity_column_header_text);
        final LinearLayout transactionsPriceColumnHeader = view.findViewById(R.id.transactions_price_column_header);
        transactionsPriceColumnHeaderText = view.findViewById(R.id.transactions_price_column_header_text);
        final LinearLayout transactionsTotalColumnHeader = view.findViewById(R.id.transactions_total_column_header);
        transactionsTotalColumnHeaderText = view.findViewById(R.id.transactions_total_column_header_text);

        if (getActivity() != null && getContext() != null) {

            // prepare drawables (change color)
            caretDown = ContextCompat.getDrawable(getActivity(), R.drawable.ic_expand_more_black_24px);
            if (caretDown != null) {
                caretDown = DrawableCompat.wrap(caretDown);
                DrawableCompat.setTint(caretDown, ContextCompat.getColor(getContext(), R.color.colorDark30));
                DrawableCompat.setTintMode(caretDown, PorterDuff.Mode.SRC_IN);
                caretDown.setBounds(0, 0, caretDown.getIntrinsicWidth(), caretDown.getIntrinsicHeight());
            }
            caretUp = ContextCompat.getDrawable(getActivity(), R.drawable.ic_expand_less_black_24px);
            if (caretUp != null) {
                caretUp = DrawableCompat.wrap(caretUp);
                DrawableCompat.setTint(caretUp, ContextCompat.getColor(getContext(), R.color.colorDark30));
                DrawableCompat.setTintMode(caretUp, PorterDuff.Mode.SRC_IN);
                caretUp.setBounds(0, 0, caretUp.getIntrinsicWidth(), caretUp.getIntrinsicHeight());
            }

            // add click listener on header to sort rows
            transactionsPairColumnHeader.setOnClickListener(view12 -> {
                transactionListAdapter.sortTransactions(0, col0Asc);
                transactionsPairColumnHeaderText.setCompoundDrawablesWithIntrinsicBounds(null, null, col0Asc ? caretDown : caretUp, null);
                transactionsQuantityColumnHeaderText.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                transactionsPriceColumnHeaderText.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                transactionsTotalColumnHeaderText.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                col0Asc = !col0Asc;
            });

            // add click listener on header to sort rows
            transactionsQuantityColumnHeader.setOnClickListener(view13 -> {
                transactionListAdapter.sortTransactions(1, col1Asc);
                transactionsQuantityColumnHeaderText.setCompoundDrawablesWithIntrinsicBounds(null, null, col1Asc ? caretDown : caretUp, null);
                transactionsPairColumnHeaderText.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                transactionsPriceColumnHeaderText.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                transactionsTotalColumnHeaderText.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                col1Asc = !col1Asc;
            });

            // add click listener on header to sort rows
            transactionsPriceColumnHeader.setOnClickListener(view14 -> {
                transactionListAdapter.sortTransactions(2, col2Asc);
                transactionsPriceColumnHeaderText.setCompoundDrawablesWithIntrinsicBounds(null, null, col2Asc ? caretDown : caretUp, null);
                transactionsQuantityColumnHeaderText.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                transactionsPairColumnHeaderText.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                transactionsTotalColumnHeaderText.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                col2Asc = !col2Asc;
            });

            // add click listener on header to sort rows
            transactionsTotalColumnHeader.setOnClickListener(view15 -> {
                transactionListAdapter.sortTransactions(3, col3Asc);
                transactionsTotalColumnHeaderText.setCompoundDrawablesWithIntrinsicBounds(null, null, col3Asc ? caretDown : caretUp, null);
                transactionsQuantityColumnHeaderText.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                transactionsPriceColumnHeaderText.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                transactionsPairColumnHeaderText.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                col3Asc = !col3Asc;
            });
        }

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                transactionListAdapter.notifyDataSetChanged();
            }
        }
    }
}