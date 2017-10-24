package com.mycryptobinder.activities;

import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.mycryptobinder.R;
import com.mycryptobinder.adapters.PortfolioCardAdapter;
import com.mycryptobinder.managers.CurrencyManager;
import com.mycryptobinder.managers.ExchangeManager;
import com.mycryptobinder.managers.KrakenManager;
import com.mycryptobinder.managers.PoloniexManager;
import com.mycryptobinder.managers.TransactionManager;

import java.util.Random;

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

        /*View view2 = inflater.inflate(R.layout.card_portfolio, container, false);
        AppCompatImageView imageView = (AppCompatImageView) view2.findViewById(R.id.portfolio_card_currency_logo);
        //int[] androidColors = getResources().getIntArray(R.array.currency_backgrounds);
        //int randomAndroidColor = androidColors[new Random().nextInt(androidColors.length)];

        TypedArray ar = getContext().getResources().obtainTypedArray(R.array.currency_backgrounds);
        int len = ar.length();
        int[] picArray = new int[len];
        for (int i = 0; i < len; i++) {
            picArray[i] = ar.getResourceId(i, 0);
        }
        ar.recycle();
        int randomAndroidColor = picArray[new Random().nextInt(picArray.length)];

        ContextCompat.getColor(getContext(), R.color.colorCurrency3);
        imageView.setColorFilter(ContextCompat.getColor(getContext(), randomAndroidColor));
        imageView.setSupportBackgroundTintList(ContextCompat.getColorStateList(getContext(), randomAndroidColor));
        //DrawableCompat.setTint(imageView.getDrawable(), ContextCompat.getColor(getContext(), randomAndroidColor));
        //imageView.invalidate();
        //imageView.setSupportBackgroundTintList(ContextCompat.getColorStateList(this, R.color.colorBlue));
        //ViewCompat.setBackgroundTintList(imageView, ContextCompat.getColorStateList(getContext(), randomAndroidColor));*/

        // prepare the recycler view with a linear layout
        RecyclerView recList = (RecyclerView) view.findViewById(R.id.portfolio_recycler_view);
        recList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this.getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);

        // add horizontal separator between rows
        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(recList.getContext(), LinearLayoutManager.VERTICAL);
        recList.addItemDecoration(mDividerItemDecoration);

        // get the adapter with last data set and apply it to the recycler view
        CurrencyManager currencyManager = new CurrencyManager(this.getContext());
        currencyManager.open();
        PortfolioCardAdapter portfolioCardAdapter = new PortfolioCardAdapter(this.getContext(), currencyManager.getUsed());
        portfolioCardAdapter.notifyDataSetChanged();
        recList.setAdapter(portfolioCardAdapter);
        currencyManager.close();

        return view;
    }

}