package com.mycryptobinder.activities;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;

import com.mycryptobinder.R;
import com.mycryptobinder.adapters.CurrencyCardAdapter;
import com.mycryptobinder.entities.Currency;
import com.mycryptobinder.managers.CurrencyManager;
import com.mycryptobinder.viewmodels.CurrencyListViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity responsible for displaying the list of currencies
 * <p>
 * Created by Yann on 24/05/2017
 */

public class CurrencyListActivity extends AppCompatActivity {

    private CurrencyCardAdapter currencyCardAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency_list);

        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // prepare the recycler view with a linear layout
        RecyclerView currencyListRecyclerView = findViewById(R.id.currency_list_recycler_view);
        currencyListRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // initialize the adapter for the list
        currencyCardAdapter = new CurrencyCardAdapter(new ArrayList<Currency>(),this);
        currencyListRecyclerView.setAdapter(currencyCardAdapter);

        // get view model
        CurrencyListViewModel currencyListViewModel = ViewModelProviders.of(this).get(CurrencyListViewModel.class);

        // observe the currency list from the view model so it will always be up to date
        currencyListViewModel.getCurrencyList().observe(CurrencyListActivity.this, new Observer<List<Currency>>() {
            @Override
            public void onChanged(@Nullable List<Currency> currencies) {
                currencyCardAdapter.addItems(currencies);
            }
        });

        // set click listener for the add currency button
        FloatingActionButton button = findViewById(R.id.btn_add_currency);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), AddCurrencyActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // back arrow click
        if (id == android.R.id.home) {
            // close current activity and return to previous activity if there is any
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}