package com.mycryptobinder.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import com.mycryptobinder.R;
import com.mycryptobinder.adapters.TransactionListAdapter;
import com.mycryptobinder.managers.ExchangeManager;
import com.mycryptobinder.managers.TransactionManager;
import com.mycryptobinder.models.Transaction;

/**
 * Fragment responsible for displaying transactions
 * <p>
 * Created by Yann on 21/05/2017
 */

public class TransactionsFragment extends Fragment {

    protected RecyclerView transactionsRecyclerView;
    protected RecyclerView.LayoutManager transactionsLayoutManager;
    protected TransactionListAdapter transactionListAdapter;

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
        View v = inflater.inflate(R.layout.fragment_transactions, container, false);

        // get view elements
        transactionsRecyclerView = (RecyclerView) v.findViewById(R.id.transactions_list_recycler_view);

        // set linear layout for recycler view
        transactionsLayoutManager = new LinearLayoutManager(getActivity());
        transactionsRecyclerView.setLayoutManager(transactionsLayoutManager);

        // open database connections
        TransactionManager transactionManager = new TransactionManager(this.getContext());
        transactionManager.open();

        // get the adapter with last data set and apply it to the recycler view
        transactionListAdapter = new TransactionListAdapter(transactionManager.getAll());
        transactionListAdapter.notifyDataSetChanged();
        transactionsRecyclerView.setAdapter(transactionListAdapter);

        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(transactionsRecyclerView.getContext(), LinearLayoutManager.VERTICAL);
        transactionsRecyclerView.addItemDecoration(mDividerItemDecoration);

        // set click listener for the add transaction button
        FloatingActionButton button = (FloatingActionButton) v.findViewById(R.id.btn_add_transaction);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent add_trans = new Intent(view.getContext(), AddTransactionActivity.class);
                startActivityForResult(add_trans, 1);
            }
        });

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                transactionListAdapter.notifyDataSetChanged();
            }
        }
    }
}