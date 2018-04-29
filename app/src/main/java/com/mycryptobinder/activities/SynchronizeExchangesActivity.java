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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mycryptobinder.R;
import com.mycryptobinder.components.MultiSpinner;
import com.mycryptobinder.viewmodels.SettingsViewModel;

import java.util.ArrayList;
import java.util.List;

public class SynchronizeExchangesActivity extends AppCompatActivity implements MultiSpinner.MultiSpinnerListener {

    private boolean[] selectedExchanges;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_synchronize_exchanges);

        // set toolbar as actionbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // fill exchange list
        List<String> items = new ArrayList<>();
        items.add(getString(R.string.label_exchange_name_kraken));
        items.add(getString(R.string.label_exchange_name_poloniex));
        items.add(getString(R.string.label_exchange_name_bittrex));
        items.add(getString(R.string.label_exchange_name_bitfinex));
        MultiSpinner multiSpinner = findViewById(R.id.synchronize_exchange_MultiSpinner);
        multiSpinner.setItems(items, getString(R.string.label_all), this);

        // get view model
        final SettingsViewModel settingsViewModel = ViewModelProviders.of(this).get(SettingsViewModel.class);

        // get view elements
        TextView synchronizeLogEditText = findViewById(R.id.synchronize_exchanges_log_editText);
        ProgressBar synchronizeLogProgressBar = findViewById(R.id.synchronize_exchanges_log_progressBar);
        Button synchronizeButton = findViewById(R.id.btn_synchronize_exchanges);
        CheckBox checkBox = findViewById(R.id.checkbox_clean_synchronize_exchanges);

        // observe logs and percentage done from the view model so it will always be up to date in the UI
        settingsViewModel.getCurrentLogs().observe(this, s -> synchronizeLogEditText.setText(s));
        settingsViewModel.getPercentDone().observe(this, percent -> synchronizeLogProgressBar.setProgress(percent != null ? percent : 0));

        // set click listener for the synchronize button
        synchronizeButton.setOnClickListener(view12 -> settingsViewModel.populateDatabase(checkBox.isChecked(), selectedExchanges));
    }

    public void onItemsSelected(boolean[] selected) {
        selectedExchanges = selected;
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