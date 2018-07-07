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

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mycryptobinder.R;
import com.mycryptobinder.entities.Currency;
import com.mycryptobinder.viewmodels.AddCurrencyViewModel;

public class AddCurrencyActivity extends AppCompatActivity {

    private AddCurrencyViewModel addCurrencyViewModel;
    private EditText currencyNameEditText;
    private EditText currencyIsoCodeEditText;
    private EditText currencySymbolEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_currency);

        // set toolbar as actionbar
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // get view components
        currencyNameEditText = findViewById(R.id.add_currency_name_editText);
        currencyIsoCodeEditText = findViewById(R.id.add_currency_iso_code_editText);
        currencySymbolEditText = findViewById(R.id.add_currency_symbol_editText);
        final Button createCurrencyButton = findViewById(R.id.btn_create_currency);
        final Button editCurrencyButton = findViewById(R.id.btn_update_currency);

        // hide edit button and show create button
        editCurrencyButton.setVisibility(View.INVISIBLE);
        createCurrencyButton.setVisibility(View.VISIBLE);

        // get the view model
        addCurrencyViewModel = ViewModelProviders.of(this).get(AddCurrencyViewModel.class);

        // set click listener for the create currency button
        createCurrencyButton.setOnClickListener(view -> {
            // get field values
            final String isoCode = currencyIsoCodeEditText.getText().toString();
            final String name = currencyNameEditText.getText().toString();
            final String symbol = currencySymbolEditText.getText().toString();

            // check mandatory fields
            if (name.trim().equals("")) {
                currencyNameEditText.setError(getString(R.string.error_currency_name_required));
            } else if (isoCode.trim().equals("")) {
                currencyIsoCodeEditText.setError(getString(R.string.error_currency_iso_code_required));
            } else {
                // add record to the view model who will trigger the insert
                addCurrencyViewModel.addCurrency(new Currency(isoCode, name, symbol));

                // close current activity and return to previous activity if there is any
                finish();

                // show a notification about the created item
                Toast.makeText(view.getContext(), getString(R.string.success_currency_created, name), Toast.LENGTH_SHORT).show();
            }
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

}