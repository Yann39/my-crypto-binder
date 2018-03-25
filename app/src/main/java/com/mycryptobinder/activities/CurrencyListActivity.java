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
import android.content.Context;
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
import com.mycryptobinder.adapters.CurrencyCardAdapter;
import com.mycryptobinder.entities.Currency;
import com.mycryptobinder.viewmodels.CurrencyListViewModel;

public class CurrencyListActivity extends AppCompatActivity implements View.OnClickListener {

    private CurrencyCardAdapter currencyCardAdapter;
    private CurrencyListViewModel currencyListViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency_list);

        // set toolbar as actionbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // prepare the recycler view with a linear layout
        RecyclerView currencyListRecyclerView = findViewById(R.id.currency_list_recycler_view);
        currencyListRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // initialize the adapter for the list
        currencyCardAdapter = new CurrencyCardAdapter(this);
        currencyListRecyclerView.setAdapter(currencyCardAdapter);

        // get view model
        currencyListViewModel = ViewModelProviders.of(this).get(CurrencyListViewModel.class);

        // observe the currency list from the view model so it is always up to date
        currencyListViewModel.getPagedCurrencyList().observe(this, pagedList -> currencyCardAdapter.setList(pagedList));

        // set click listener for the add currency button
        FloatingActionButton button = findViewById(R.id.btn_add_currency);
        button.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), AddCurrencyActivity.class);
            startActivity(intent);
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

    @Override
    public void onClick(View view) {
        Currency currency = (Currency) view.getTag();
        // show a confirm dialog
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(getString(R.string.title_confirmation));
        alert.setMessage(Html.fromHtml(getString(R.string.confirm_delete_currency, " <b>" + currency.getName() + "</b>")));

        Context context = this;

        // "yes" button click
        alert.setPositiveButton(getString(R.string.label_yes), (dialog, which) -> {
            // delete from the database
            currencyListViewModel.deleteItem(currency);

            // show a notification about the removed item
            Toast.makeText(context, getString(R.string.success_currency_removed, currency.getIsoCode()), Toast.LENGTH_SHORT).show();

            dialog.dismiss();
        });

        // "no" button click
        alert.setNegativeButton(getString(R.string.label_no), (dialog, which) -> dialog.dismiss());

        alert.show();
    }
}