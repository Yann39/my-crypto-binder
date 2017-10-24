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
import com.mycryptobinder.adapters.ExchangeCardAdapter;
import com.mycryptobinder.managers.ExchangeManager;

/**
 * Activity responsible for displaying the list of exchanges
 * <p>
 * Created by Yann on 24/05/2017
 */

public class ExchangeListActivity extends AppCompatActivity {

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
        RecyclerView recList = (RecyclerView) findViewById(R.id.exchange_list_recycler_view);
        recList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);

        // get the adapter with last data set and apply it to the recycler view
        ExchangeManager exchangeManager = new ExchangeManager(this);
        exchangeManager.open();
        ExchangeCardAdapter exchangeCardAdapter = new ExchangeCardAdapter(this, exchangeManager.getAll());
        exchangeCardAdapter.notifyDataSetChanged();
        recList.setAdapter(exchangeCardAdapter);
        exchangeManager.close();

        // set click listener for the add exchange button
        FloatingActionButton button = (FloatingActionButton) findViewById(R.id.btn_add_exchange);
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

        // handle back arrow click (close this activity and return to previous activity if there is any)
        if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}