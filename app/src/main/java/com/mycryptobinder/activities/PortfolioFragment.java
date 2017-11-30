package com.mycryptobinder.activities;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.Base64;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.LogInterface;
import com.loopj.android.http.RequestParams;
import com.mycryptobinder.R;
import com.mycryptobinder.adapters.PortfolioCardAdapter;
import com.mycryptobinder.models.HoldingData;
import com.mycryptobinder.models.KrakenTrade;
import com.mycryptobinder.viewmodels.PortfolioViewModel;
import com.mycryptobinder.viewmodels.SettingsViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import cz.msebera.android.httpclient.Header;

/**
 * Fragment responsible for displaying portfolio
 * <p>
 * Created by Yann on 21/05/2017
 */

public class PortfolioFragment extends Fragment {

    private PortfolioCardAdapter portfolioCardAdapter;

    public PortfolioFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_portfolio, container, false);

        // prepare the recycler view with a linear layout
        RecyclerView portfolioRecyclerView = view.findViewById(R.id.portfolio_recycler_view);
        portfolioRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));

        // initialize the adapter for the list
        portfolioCardAdapter = new PortfolioCardAdapter(this.getContext(), new ArrayList<HoldingData>());
        portfolioRecyclerView.setAdapter(portfolioCardAdapter);

        // get view model
        final PortfolioViewModel portfolioViewModel = ViewModelProviders.of(this).get(PortfolioViewModel.class);

        // set total number of different currencies
        TextView nbCoinTextView = view.findViewById(R.id.portfolio_nbcoin_value_textView);
        portfolioViewModel.getNbDifferentCurrencies().observe(PortfolioFragment.this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer nbDifferentCurrencies) {
                nbCoinTextView.setText(String.valueOf(nbDifferentCurrencies));
            }
        });

        // add horizontal separator between rows
        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(portfolioRecyclerView.getContext(), LinearLayoutManager.VERTICAL);
        portfolioRecyclerView.addItemDecoration(mDividerItemDecoration);

        ProgressBar progressBar = view.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);

        // observe the used currencies list from the view model so the auto complete text will always be up to date
        portfolioViewModel.getHoldings().observe(PortfolioFragment.this, new Observer<List<HoldingData>>() {
            @Override
            public void onChanged(@Nullable List<HoldingData> holdingDataList) {
                portfolioCardAdapter.addItems(holdingDataList);
                progressBar.setVisibility(View.GONE);
            }
        });

        return view;
    }

}