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

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.mycryptobinder.models.PriceFull;
import com.mycryptobinder.models.PricesFull;
import com.mycryptobinder.viewmodels.PortfolioViewModel;

import java.text.DecimalFormat;
import java.util.ArrayList;

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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_portfolio, container, false);

        // prepare the recycler view with a linear layout
        RecyclerView portfolioRecyclerView = view.findViewById(R.id.portfolio_recycler_view);
        portfolioRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));

        // initialize the adapter for the list
        portfolioCardAdapter = new PortfolioCardAdapter(this.getContext(), new ArrayList<>(), new PricesFull());
        portfolioRecyclerView.setAdapter(portfolioCardAdapter);

        // add horizontal separator between rows
        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(portfolioRecyclerView.getContext(), LinearLayoutManager.VERTICAL);
        portfolioRecyclerView.addItemDecoration(mDividerItemDecoration);

        // get view model
        portfolioViewModel = ViewModelProviders.of(this).get(PortfolioViewModel.class);

        // get view elements
        TextView nbCoinTextView = view.findViewById(R.id.portfolio_nbcoin_value_textView);
        TextView totalHoldingTextView = view.findViewById(R.id.portfolio_total_value_textView);
        TextView totalChange24hTextView = view.findViewById(R.id.portfolio_last24_value_textView);

        // display a progress bar while loading holdings data
        ProgressBar progressBar = view.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);

        // observe the number of currencies from the view model so it will always be up to date in the UI
        portfolioViewModel.getDifferentCurrencies().observe(this, differentCurrencies -> {
            nbCoinTextView.setText(String.valueOf(differentCurrencies != null ? differentCurrencies.size() : "0"));
            // update data in the adapter
            portfolioViewModel.setCurrencyCodes(differentCurrencies);
        });

        // observe the holding data from the view model so it will always be up to date in the UI
        portfolioViewModel.getHoldings().observe(PortfolioFragment.this, holdingDataList -> {
            // update data in the adapter
            portfolioCardAdapter.setItems(holdingDataList);

            DecimalFormat df = new DecimalFormat("#");
            double sum = 0.0;
            if (holdingDataList != null) {
                for (HoldingData holdingData : holdingDataList) {
                    sum += holdingData.getQuantity();
                }
            }
            totalHoldingTextView.setText(getString(R.string.label_euro_price, df.format(sum)));
        });

        // observe the current prices data from the view model so it will always be up to date in the UI
        portfolioViewModel.getCurrentPricesFull().observe(this, pricesFull -> {
            // update data in the adapter
            portfolioCardAdapter.setPricesFull(pricesFull);

            // update UI
            DecimalFormat df = new DecimalFormat("#");
            DecimalFormat df2 = new DecimalFormat("+#.##;-#");
            double sum = 0.0;
            double sum24h = 0.0;
            double totalChange = 0.0;
            if (pricesFull != null && pricesFull.getRaw() != null) {
                for (PriceFull priceFull : pricesFull.getRaw().values()) {
                    sum += Double.parseDouble(priceFull.getEur().getPrice());
                    sum24h += priceFull.getEur().getChangeDayPercent();
                }
                totalChange = sum24h / pricesFull.getRaw().values().size();
            }
            totalHoldingTextView.setText(getString(R.string.label_euro_price, df.format(sum)));
            totalChange24hTextView.setText(getString(R.string.label_percentage, df2.format(totalChange)));

            // hide the progress bar
            progressBar.setVisibility(View.GONE);
        });

        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // refresh icon click
            case R.id.action_refresh:
                portfolioViewModel.refresh();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

}