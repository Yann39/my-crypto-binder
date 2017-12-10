package com.mycryptobinder.activities;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mycryptobinder.R;
import com.mycryptobinder.adapters.PortfolioCardAdapter;
import com.mycryptobinder.models.HoldingData;
import com.mycryptobinder.models.Price;
import com.mycryptobinder.viewmodels.PortfolioViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Fragment responsible for displaying portfolio data
 * <p>
 * Created by Yann on 21/05/2017
 */

public class PortfolioFragment extends Fragment {

    private PortfolioCardAdapter portfolioCardAdapter;
    private PortfolioViewModel portfolioViewModel;

    public PortfolioFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_portfolio, container, false);

        // prepare the recycler view with a linear layout
        RecyclerView portfolioRecyclerView = view.findViewById(R.id.portfolio_recycler_view);
        portfolioRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));

        // initialize the adapter for the list
        portfolioCardAdapter = new PortfolioCardAdapter(this.getContext(), new ArrayList<>(), new HashMap<>());
        portfolioRecyclerView.setAdapter(portfolioCardAdapter);

        // add horizontal separator between rows
        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(portfolioRecyclerView.getContext(), LinearLayoutManager.VERTICAL);
        portfolioRecyclerView.addItemDecoration(mDividerItemDecoration);

        // get view model
        portfolioViewModel = ViewModelProviders.of(this).get(PortfolioViewModel.class);

        // set total number of different currencies
        TextView nbCoinTextView = view.findViewById(R.id.portfolio_nbcoin_value_textView);

        // display a progress bar while loading holdings data
        ProgressBar progressBar = view.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);

        // observe the holding data from the view model so it will always be up to date in the UI
        portfolioViewModel.getHoldings().observe(PortfolioFragment.this, new Observer<List<HoldingData>>() {
            @Override
            public void onChanged(@Nullable List<HoldingData> holdingDataList) {
                // update data in the adapter
                portfolioCardAdapter.setItems(holdingDataList);
                // hide the progress bar
                progressBar.setVisibility(View.GONE);
            }
        });

        // observe the current prices data from the view model so it will always be up to date in the UI
        portfolioViewModel.getCurrentPrices().observe(this, new Observer<Map<String, Price>>() {
            @Override
            public void onChanged(@Nullable Map<String, Price> prices) {
                // update data in the adapter
                portfolioCardAdapter.setPrices(prices);
                // hide the progress bar
                progressBar.setVisibility(View.GONE);
            }
        });

        // observe the current prices data from the view model so it will always be up to date in the UI
        portfolioViewModel.getDifferentCurrencies().observe(this, new Observer<List<String>>() {
            @Override
            public void onChanged(@Nullable List<String> differentCurrencies) {
                nbCoinTextView.setText(String.valueOf(differentCurrencies != null ? differentCurrencies.size() : "0"));
                // update data in the adapter
                portfolioViewModel.setCurrencyCodes(differentCurrencies);
                // hide the progress bar
                progressBar.setVisibility(View.GONE);
            }
        });

        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_refresh:
                portfolioViewModel.refresh();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

}