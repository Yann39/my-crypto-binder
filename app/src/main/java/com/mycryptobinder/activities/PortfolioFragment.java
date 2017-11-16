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
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mycryptobinder.R;
import com.mycryptobinder.adapters.PortfolioCardAdapter;
import com.mycryptobinder.entities.Currency;
import com.mycryptobinder.models.HoldingData;
import com.mycryptobinder.models.Price;
import com.mycryptobinder.service.CryptoCompareService;
import com.mycryptobinder.viewmodels.PortfolioViewModel;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Fragment responsible for displaying portfolio
 * <p>
 * Created by Yann on 21/05/2017
 */

public class PortfolioFragment extends Fragment {

    private PortfolioCardAdapter portfolioCardAdapter;
    private ProgressBar progressBar;

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
        int nbCurrencies = portfolioViewModel.getNbDifferentCurrencies();
        TextView nbCoinTextView = view.findViewById(R.id.portfolio_nbcoin_value_textView);
        nbCoinTextView.setText(String.valueOf(nbCurrencies));

        // add horizontal separator between rows
        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(portfolioRecyclerView.getContext(), LinearLayoutManager.VERTICAL);
        portfolioRecyclerView.addItemDecoration(mDividerItemDecoration);

        progressBar = view.findViewById(R.id.progress_bar);

        progressBar.setVisibility(View.VISIBLE);

        // observe the used currencies list from the view model so the auto complete text will always be up to date
        portfolioViewModel.getHoldings().observe(PortfolioFragment.this, new Observer<List<HoldingData>>() {
            @Override
            public void onChanged(@Nullable List<HoldingData> holdingDataList) {
                for (HoldingData hd : holdingDataList) {
                    hd.setIsoCode(curr.getIsoCode());
                    hd.setName(curr.getName());
                    hd.setSymbol(curr.getSymbol());
                    hd.setQuantity(portfolioViewModel.getCurrencyQuantity(curr.getIsoCode()));

                    try {
                        CryptoCompareService cryptoCompareService = CryptoCompareService.retrofit.create(CryptoCompareService.class);
                        Call<Price> call = cryptoCompareService.getCurrentPrice(curr.getIsoCode());
                        call.enqueue(new Callback<Price>() {
                            @Override
                            public void onResponse(@Nullable Call<Price> call, @Nullable Response<Price> response) {
                                Price price = response.body();
                                if (price != null && price.getEur() != null) {
                                    hd.setCurrentPrice(Double.parseDouble(price.getEur()));
                                    portfolioCardAdapter.notifyDataSetChanged();
                                    progressBar.setVisibility(View.GONE);
                                }
                            }

                            @Override
                            public void onFailure(@Nullable Call<Price> call, @Nullable Throwable t) {
                                System.out.println("Failed: " + t.getLocalizedMessage());
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        return view;
    }

}