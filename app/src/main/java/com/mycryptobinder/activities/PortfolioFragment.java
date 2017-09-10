package com.mycryptobinder.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.mycryptobinder.R;
import com.mycryptobinder.managers.CurrencyManager;
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

        TextView portfolioTotalTextView = (TextView) view.findViewById(R.id.portfolio_total_textView);

        Button synchornizeButton = (Button) view.findViewById(R.id.btn_synchronize);

        // set click listener for the create currency button
        synchornizeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                KrakenManager km = new KrakenManager(view.getContext());
                PoloniexManager pm = new PoloniexManager(view.getContext());
                TransactionManager tm = new TransactionManager(view.getContext());
                CurrencyManager cm = new CurrencyManager(view.getContext());
                km.open();
                pm.open();
                tm.open();
                cm.open();

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

        TransactionManager tm = new TransactionManager(this.getContext());
        CurrencyManager cm = new CurrencyManager(this.getContext());
        tm.open();
        cm.open();

        String tt = "";
        for (Currency c : cm.getAll()) {
            Double tot = tm.getCurrencyQuantity(c.getIsoCode());
            if (tot != null && tot > 0) {
                tt = tt + "Holding " + c.getIsoCode() + ":" + tm.getCurrencyTotal(c.getIsoCode()) + "\n";
            }
        }
        tt = tt + "\n";
        for (Currency c : cm.getAll()) {
            Double tot = tm.getCurrencyQuantity(c.getIsoCode());
            if (tot != null && tot > 0) {
                tt = tt + "Quantity " + c.getIsoCode() + ":" + tm.getCurrencyQuantity(c.getIsoCode()) + "\n";
            }
        }

        portfolioTotalTextView.setText(tt);

        return view;
    }

}