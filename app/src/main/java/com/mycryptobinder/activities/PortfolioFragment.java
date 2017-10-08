package com.mycryptobinder.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.mycryptobinder.R;
import com.mycryptobinder.adapters.PortfolioCardAdapter;
import com.mycryptobinder.managers.CurrencyManager;
import com.mycryptobinder.managers.ExchangeManager;
import com.mycryptobinder.managers.KrakenManager;
import com.mycryptobinder.managers.PoloniexManager;
import com.mycryptobinder.managers.TransactionManager;
import com.mycryptobinder.models.Currency;

/**
 * Fragment responsible for displaying portfolio
 * <p>
 * Created by Yann on 21/05/2017
 */

public class PortfolioFragment extends Fragment {

    private CheckBox checkBox;

    public PortfolioFragment() {
        // required empty public constructor
    }

    public static PortfolioFragment newInstance() {
        PortfolioFragment fragment = new PortfolioFragment();
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
        View view = inflater.inflate(R.layout.fragment_portfolio, container, false);

        Button synchronizeButton = (Button) view.findViewById(R.id.btn_synchronize);
        checkBox = (CheckBox) view.findViewById(R.id.checkbox_clean_synchronize);

        // open database connection
        CurrencyManager currencyManager = new CurrencyManager(this.getContext());
        currencyManager.open();

        // prepare the recycler view with a linear layout
        RecyclerView recList = (RecyclerView) view.findViewById(R.id.portfolio_recycler_view);
        recList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this.getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);

        // get the adapter with last data set and apply it to the recycler view
        PortfolioCardAdapter portfolioCardAdapter = new PortfolioCardAdapter(this.getContext(), currencyManager.getUsed());
        portfolioCardAdapter.notifyDataSetChanged();
        recList.setAdapter(portfolioCardAdapter);

        // set click listener for the synchronize button
        synchronizeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                KrakenManager km = new KrakenManager(view.getContext());
                PoloniexManager pm = new PoloniexManager(view.getContext());
                TransactionManager tm = new TransactionManager(view.getContext());
                CurrencyManager cm = new CurrencyManager(view.getContext());
                ExchangeManager em = new ExchangeManager(view.getContext());

                km.open();
                pm.open();
                tm.open();
                cm.open();
                em.open();

                if (checkBox.isChecked()) {
                    tm.reset();
                    cm.reset();
                    em.reset();
                }

                km.populateExchange();
                pm.populateExchange();

                km.populateAssetPairs();
                km.populateAssets();
                pm.populateAssets();
                cm.populateCurrencies();

                km.populateTradeHistory();
                pm.populateTradeHistory();
                tm.populateTransactions();
            }
        });

        return view;
    }

}