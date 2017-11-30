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
import com.mycryptobinder.adapters.ExchangeCardAdapter;
import com.mycryptobinder.entities.Exchange;
import com.mycryptobinder.viewmodels.ExchangeListViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity responsible for displaying the list of exchanges
 * <p>
 * Created by Yann on 24/05/2017
 */

public class ExchangeListActivity extends AppCompatActivity {

    private ExchangeCardAdapter exchangeCardAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_exchange_list);

        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // prepare the recycler view with a linear layout
        RecyclerView exchangeListRecyclerView = findViewById(R.id.exchange_list_recycler_view);
        exchangeListRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // initialize the adapter for the list
        exchangeCardAdapter = new ExchangeCardAdapter(new ArrayList<Exchange>(), this);
        exchangeListRecyclerView.setAdapter(exchangeCardAdapter);

        // get view model
        ExchangeListViewModel exchangeListViewModel = ViewModelProviders.of(this).get(ExchangeListViewModel.class);

        // observe the application settings list from the view model so it will always be up to date
        exchangeListViewModel.getExchangeList().observe(ExchangeListActivity.this, new Observer<List<Exchange>>() {
            @Override
            public void onChanged(@Nullable List<Exchange> exchanges) {
                exchangeCardAdapter.addItems(exchanges);
            }
        });

        // set click listener for the add exchange button
        FloatingActionButton button = findViewById(R.id.btn_add_exchange);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent add_exc = new Intent(view.getContext(), AddExchangeActivity.class);
                startActivity(add_exc);
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