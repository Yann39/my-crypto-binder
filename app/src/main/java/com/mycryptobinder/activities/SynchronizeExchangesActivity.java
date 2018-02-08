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
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_synchronize_exchanges);

        List<String> items = new ArrayList<>();
        items.add("Kraken");
        items.add("Poloniex");
        items.add("Bittrex");
        items.add("Bitfinex");
        MultiSpinner multiSpinner = findViewById(R.id.synchronize_exchange_MultiSpinner);
        multiSpinner.setItems(items, "All", this);

        final SettingsViewModel settingsViewModel = ViewModelProviders.of(this).get(SettingsViewModel.class);

        TextView synchronizeLogEditText = findViewById(R.id.synchronize_exchanges_log_editText);
        settingsViewModel.getCurrentLogs().observe(this, s -> synchronizeLogEditText.setText(s));

        ProgressBar synchronizeLogProgressBar = findViewById(R.id.synchronize_exchanges_log_progressBar);
        settingsViewModel.getPercentDone().observe(this, percent -> synchronizeLogProgressBar.setProgress(percent != null ? percent : 0));

        Button synchronizeButton = findViewById(R.id.btn_synchronize_exchanges);
        CheckBox checkBox = findViewById(R.id.checkbox_clean_synchronize_exchanges);

        // set click listener for the synchronize button
        synchronizeButton.setOnClickListener(view12 -> settingsViewModel.populateDatabase(checkBox.isChecked()));
    }

    public void onItemsSelected(boolean[] selected) {

    }

}
