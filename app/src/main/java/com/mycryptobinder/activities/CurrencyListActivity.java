package com.mycryptobinder.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;

import com.mycryptobinder.R;
import com.mycryptobinder.adapters.CurrencyCardAdapter;
import com.mycryptobinder.managers.CurrencyManager;

/**
 * Activity responsible for displaying the list of currencies
 * <p>
 * Created by Yann on 24/05/2017
 */

public class CurrencyListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("Currency list");
        setContentView(R.layout.activity_currency_list);

        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // prepare the recycler view with a linear layout
        RecyclerView recList = (RecyclerView) findViewById(R.id.currency_list_recycler_view);
        recList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);

        // open database connection
        CurrencyManager currencyManager = new CurrencyManager(this);
        currencyManager.open();

        // get the adapter with last data set and apply it to the recycler view
        CurrencyCardAdapter currencyCardAdapter = new CurrencyCardAdapter(this, currencyManager.getAll());
        currencyCardAdapter.notifyDataSetChanged();
        recList.setAdapter(currencyCardAdapter);

        // set click listener for the add currency button
        FloatingActionButton button = (FloatingActionButton) findViewById(R.id.btn_add_currency);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent add_cur = new Intent(view.getContext(), AddCurrencyActivity.class);
                startActivity(add_cur);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // handle back arrow click (close this activity and return to previous activity if there is any)
        if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}