/*
 * Copyright (c) 2018 by Yann39.
 *
 * This file is part of MyCryptoBinder.
 *
 * MyCryptoBinder is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MyCryptoBinder is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MyCryptoBinder. If not, see <http://www.gnu.org/licenses/>.
 */

package com.mycryptobinder.activities;

import android.app.AlertDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.mycryptobinder.R;
import com.mycryptobinder.adapters.ExchangeCardAdapter;
import com.mycryptobinder.entities.Exchange;
import com.mycryptobinder.viewmodels.ExchangeListViewModel;

import java.util.ArrayList;

public class ExchangeListActivity extends AppCompatActivity implements View.OnClickListener {

    private ExchangeCardAdapter exchangeCardAdapter;
    private ExchangeListViewModel exchangeListViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exchange_list);

        // set toolbar as actionbar
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // prepare the recycler view with a linear layout
        final RecyclerView exchangeListRecyclerView = findViewById(R.id.exchange_list_recycler_view);
        exchangeListRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // initialize the adapter for the list
        exchangeCardAdapter = new ExchangeCardAdapter(new ArrayList<>(), this, this);
        exchangeListRecyclerView.setAdapter(exchangeCardAdapter);

        // get view model
        exchangeListViewModel = ViewModelProviders.of(this).get(ExchangeListViewModel.class);

        // observe the application settings list from the view model so it will always be up to date
        exchangeListViewModel.getExchangeList().observe(ExchangeListActivity.this, exchanges -> exchangeCardAdapter.addItems(exchanges));

        // set click listener for the add exchange button
        final FloatingActionButton button = findViewById(R.id.btn_add_exchange);
        button.setOnClickListener(view -> {
            final Intent add_exc = new Intent(view.getContext(), AddExchangeActivity.class);
            startActivity(add_exc);
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int id = item.getItemId();

        // back arrow click
        if (id == android.R.id.home) {
            // close current activity and return to previous activity if there is any
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        // get the clicked exchange, stored in this view as a tag
        final Exchange exchange = (Exchange) v.getTag();

        // show a confirm dialog
        final AlertDialog.Builder alert = new AlertDialog.Builder(v.getContext());
        alert.setTitle(v.getContext().getResources().getString(R.string.title_confirmation));
        alert.setMessage(Html.fromHtml(v.getContext().getResources().getString(R.string.confirm_delete_exchange, exchange.getName())));

        // "yes" button click
        alert.setPositiveButton(v.getContext().getResources().getString(R.string.label_yes), (dialog, which) -> {

            // delete from the database
            exchangeListViewModel.deleteItem(exchange);

            // show a notification about the removed item
            Toast.makeText(v.getContext(), v.getContext().getResources().getString(R.string.success_exchange_removed, exchange.getName()), Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        // "no" button click
        alert.setNegativeButton(v.getContext().getResources().getString(R.string.label_no), (dialog, which) -> dialog.dismiss());

        alert.show();
    }
}