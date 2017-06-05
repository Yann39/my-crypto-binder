package com.mycryptobinder.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
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

    private static final String KEY_LAYOUT_MANAGER = "layoutManager";
    private static final int SPAN_COUNT = 3;

    protected RadioButton mLinearLayoutRadioButton;
    protected RadioButton mGridLayoutRadioButton;
    protected RecyclerView transactionsRecyclerView;
    protected RecyclerView.LayoutManager transactionsLayoutManager;
    protected LayoutManagerType transactionsLayoutManagerType;

    private enum LayoutManagerType {
        GRID_LAYOUT_MANAGER,
        LINEAR_LAYOUT_MANAGER
    }

    public TransactionsFragment() {
        // Required empty public constructor
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

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_transactions, container, false);

        // get view elements
        mLinearLayoutRadioButton = (RadioButton) v.findViewById(R.id.linear_layout_rb);
        mGridLayoutRadioButton = (RadioButton) v.findViewById(R.id.grid_layout_rb);
        transactionsRecyclerView = (RecyclerView) v.findViewById(R.id.transactions_list_recycler_view);

        // set linear layout for recycler view
        transactionsLayoutManager = new LinearLayoutManager(getActivity());
        transactionsLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;
        if (savedInstanceState != null) {
            // restore saved layout manager type.
            transactionsLayoutManagerType = (LayoutManagerType) savedInstanceState.getSerializable(KEY_LAYOUT_MANAGER);
        }
        setRecyclerViewLayoutManager(transactionsLayoutManagerType);

        // open database connections
        TransactionManager transactionManager = new TransactionManager(this.getContext());
        transactionManager.open();

        // get the adapter with last data set and apply it to the recycler view
        TransactionListAdapter transactionListAdapter = new TransactionListAdapter(transactionManager.getAll());
        transactionListAdapter.notifyDataSetChanged();
        transactionsRecyclerView.setAdapter(transactionListAdapter);

        // set click listener for the linear layout radio button
        mLinearLayoutRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRecyclerViewLayoutManager(LayoutManagerType.LINEAR_LAYOUT_MANAGER);
            }
        });

        // set click listener for the grid layout radio butto
        mGridLayoutRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRecyclerViewLayoutManager(LayoutManagerType.GRID_LAYOUT_MANAGER);
            }
        });

        // set click listener for the add currency button
        FloatingActionButton button = (FloatingActionButton) v.findViewById(R.id.btn_add_transaction);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent add_trans = new Intent(view.getContext(), AddTransactionActivity.class);
                startActivity(add_trans);
            }
        });

        return v;
    }

    /**
     * Set RecyclerView's LayoutManager to the one given.
     *
     * @param layoutManagerType Type of layout manager to switch to.
     */
    public void setRecyclerViewLayoutManager(LayoutManagerType layoutManagerType) {
        int scrollPosition = 0;

        // If a layout manager has already been set, get current scroll position.
        if (transactionsRecyclerView.getLayoutManager() != null) {
            scrollPosition = ((LinearLayoutManager) transactionsRecyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
        }

        switch (layoutManagerType) {
            case GRID_LAYOUT_MANAGER:
                transactionsLayoutManager = new GridLayoutManager(getActivity(), SPAN_COUNT);
                transactionsLayoutManagerType = LayoutManagerType.GRID_LAYOUT_MANAGER;
                break;
            case LINEAR_LAYOUT_MANAGER:
                transactionsLayoutManager = new LinearLayoutManager(getActivity());
                transactionsLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;
                break;
            default:
                transactionsLayoutManager = new LinearLayoutManager(getActivity());
                transactionsLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;
        }

        transactionsRecyclerView.setLayoutManager(transactionsLayoutManager);
        transactionsRecyclerView.scrollToPosition(scrollPosition);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save currently selected layout manager.
        savedInstanceState.putSerializable(KEY_LAYOUT_MANAGER, transactionsLayoutManagerType);
        super.onSaveInstanceState(savedInstanceState);
    }

}