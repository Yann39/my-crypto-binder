package com.mycryptobinder.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mycryptobinder.R;
import com.mycryptobinder.managers.TransactionManager;

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

        // open database connections
        TransactionManager transactionManager = new TransactionManager(this.getContext());
        transactionManager.open();

        portfolioTotalTextView.setText(String.valueOf(transactionManager.getTotal()));

        return view;
    }

}