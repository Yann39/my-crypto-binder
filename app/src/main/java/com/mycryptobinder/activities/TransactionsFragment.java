package com.mycryptobinder.activities;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.mycryptobinder.entities.Transaction;
import com.mycryptobinder.viewmodels.TransactionsViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment responsible for displaying transactions
 * <p>
 * Created by Yann on 21/05/2017
 */

public class TransactionsFragment extends Fragment {

    protected RecyclerView transactionsRecyclerView;
    protected RecyclerView.LayoutManager transactionsLayoutManager;
    protected TransactionListAdapter transactionListAdapter;
    protected boolean col0Asc = false;
    protected boolean col1Asc = false;
    protected boolean col2Asc = false;
    protected boolean col3Asc = false;
    protected TextView transactionsPairColumnHeaderText;
    protected LinearLayout transactionsPairColumnHeader;
    protected TextView transactionsQuantityColumnHeaderText;
    protected LinearLayout transactionsQuantityColumnHeader;
    protected TextView transactionsPriceColumnHeaderText;
    protected LinearLayout transactionsPriceColumnHeader;
    protected TextView transactionsTotalColumnHeaderText;
    protected LinearLayout transactionsTotalColumnHeader;
    protected Drawable caretDown;
    protected Drawable caretUp;

    public TransactionsFragment() {
        // required empty public constructor
    }

    public static TransactionsFragment newInstance() {
        TransactionsFragment fragment = new TransactionsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_transactions, container, false);

        // prepare the recycler view with a linear layout
        RecyclerView transactionsRecyclerView = view.findViewById(R.id.transactions_list_recycler_view);
        transactionsRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));

        // initialize the adapter for the list
        transactionListAdapter = new TransactionListAdapter(this.getContext(), new ArrayList<Transaction>());
        transactionsRecyclerView.setAdapter(transactionListAdapter);

        // get view model
        final TransactionsViewModel transactionsViewModel = ViewModelProviders.of(this).get(TransactionsViewModel.class);

        transactionsViewModel.getTransactionList().observe(TransactionsFragment.this, new Observer<List<Transaction>>() {
            @Override
            public void onChanged(@Nullable List<Transaction> transactionList) {
                transactionListAdapter.addItems(transactionList);
            }
        });

        // add horizontal separator between rows
        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(transactionsRecyclerView.getContext(), LinearLayoutManager.VERTICAL);
        transactionsRecyclerView.addItemDecoration(mDividerItemDecoration);

        // set click listener for the add transaction button
        FloatingActionButton button = view.findViewById(R.id.btn_add_transaction);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent add_trans = new Intent(view.getContext(), AddTransactionActivity.class);
                startActivityForResult(add_trans, 1);
            }
        });

        // get view elements
        transactionsPairColumnHeader = view.findViewById(R.id.transactions_pair_column_header);
        transactionsPairColumnHeaderText = view.findViewById(R.id.transactions_pair_column_header_text);
        transactionsQuantityColumnHeader = view.findViewById(R.id.transactions_quantity_column_header);
        transactionsQuantityColumnHeaderText = view.findViewById(R.id.transactions_quantity_column_header_text);
        transactionsPriceColumnHeader = view.findViewById(R.id.transactions_price_column_header);
        transactionsPriceColumnHeaderText = view.findViewById(R.id.transactions_price_column_header_text);
        transactionsTotalColumnHeader = view.findViewById(R.id.transactions_total_column_header);
        transactionsTotalColumnHeaderText = view.findViewById(R.id.transactions_total_column_header_text);

        // prepare drawables (change color)
        caretDown = ContextCompat.getDrawable(getActivity(), R.drawable.ic_expand_more_black_24px);
        caretDown = DrawableCompat.wrap(caretDown);
        DrawableCompat.setTint(caretDown, ContextCompat.getColor(getContext(), R.color.colorDark30));
        DrawableCompat.setTintMode(caretDown, PorterDuff.Mode.SRC_IN);
        caretDown.setBounds(0, 0, caretDown.getIntrinsicWidth(), caretDown.getIntrinsicHeight());
        caretUp = ContextCompat.getDrawable(getActivity(), R.drawable.ic_expand_less_black_24px);
        caretUp = DrawableCompat.wrap(caretUp);
        DrawableCompat.setTint(caretUp, ContextCompat.getColor(getContext(), R.color.colorDark30));
        DrawableCompat.setTintMode(caretUp, PorterDuff.Mode.SRC_IN);
        caretUp.setBounds(0, 0, caretUp.getIntrinsicWidth(), caretUp.getIntrinsicHeight());

        // add click listener on header to sort rows
        transactionsPairColumnHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                transactionListAdapter.sortTransactions(0, col0Asc);
                transactionListAdapter.notifyDataSetChanged();
                transactionsPairColumnHeaderText.setCompoundDrawablesWithIntrinsicBounds(null, null, col0Asc ? caretDown : caretUp, null);
                transactionsQuantityColumnHeaderText.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                transactionsPriceColumnHeaderText.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                transactionsTotalColumnHeaderText.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                col0Asc = !col0Asc;
            }
        });

        // add click listener on header to sort rows
        transactionsQuantityColumnHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                transactionListAdapter.sortTransactions(1, col1Asc);
                transactionListAdapter.notifyDataSetChanged();
                transactionsQuantityColumnHeaderText.setCompoundDrawablesWithIntrinsicBounds(null, null, col1Asc ? caretDown : caretUp, null);
                transactionsPairColumnHeaderText.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                transactionsPriceColumnHeaderText.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                transactionsTotalColumnHeaderText.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                col1Asc = !col1Asc;
            }
        });

        // add click listener on header to sort rows
        transactionsPriceColumnHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                transactionListAdapter.sortTransactions(2, col2Asc);
                transactionListAdapter.notifyDataSetChanged();
                transactionsPriceColumnHeaderText.setCompoundDrawablesWithIntrinsicBounds(null, null, col2Asc ? caretDown : caretUp, null);
                transactionsQuantityColumnHeaderText.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                transactionsPairColumnHeaderText.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                transactionsTotalColumnHeaderText.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                col2Asc = !col2Asc;
            }
        });

        // add click listener on header to sort rows
        transactionsTotalColumnHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                transactionListAdapter.sortTransactions(3, col3Asc);
                transactionListAdapter.notifyDataSetChanged();
                transactionsTotalColumnHeaderText.setCompoundDrawablesWithIntrinsicBounds(null, null, col3Asc ? caretDown : caretUp, null);
                transactionsQuantityColumnHeaderText.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                transactionsPriceColumnHeaderText.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                transactionsPairColumnHeaderText.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                col3Asc = !col3Asc;
            }
        });

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