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
import com.mycryptobinder.entities.Exchange;
import com.mycryptobinder.helpers.UtilsHelper;
import com.mycryptobinder.viewmodels.AddExchangeViewModel;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AddExchangeActivity extends AppCompatActivity {

    private AddExchangeViewModel addExchangeViewModel;
    private EditText exchangeNameEditText;
    private EditText exchangeLinkEditText;
    private EditText exchangeDescriptionEditText;
    private EditText appSettingApiPublicKeyEditText;
    private EditText appSettingApiPrivateKeyEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_exchange);

        // set toolbar as actionbar
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // get view components
        exchangeNameEditText = findViewById(R.id.add_exchange_name_editText);
        exchangeLinkEditText = findViewById(R.id.add_exchange_link_editText);
        exchangeDescriptionEditText = findViewById(R.id.add_exchange_description_editText);
        appSettingApiPublicKeyEditText = findViewById(R.id.add_exchange_api_public_key_editText);
        appSettingApiPrivateKeyEditText = findViewById(R.id.add_exchange_api_private_key_editText);
        final Button createExchangeButton = findViewById(R.id.btn_create_exchange);
        final Button editExchangeButton = findViewById(R.id.btn_update_exchange);

        // hide edit button and show create button
        editExchangeButton.setVisibility(View.INVISIBLE);
        createExchangeButton.setVisibility(View.VISIBLE);

        // get the view model
        addExchangeViewModel = ViewModelProviders.of(this).get(AddExchangeViewModel.class);

        // set click listener for the create exchange button
        createExchangeButton.setOnClickListener(view -> {
            // get field values
            final String name = exchangeNameEditText.getText().toString();
            final String link = exchangeLinkEditText.getText().toString();
            final String description = exchangeDescriptionEditText.getText().toString();
            final String apiPublicKey = appSettingApiPublicKeyEditText.getText().toString();
            final String apiPrivateKey = appSettingApiPrivateKeyEditText.getText().toString();

            // check mandatory fields
            if (name.trim().equals("")) {
                exchangeNameEditText.setError(getString(R.string.error_exchange_name_required));
            } else {

                // encrypt keys
                final Properties properties = new Properties();
                try {
                    final InputStream inputStream = getApplicationContext().getAssets().open("myCryptoBinder.properties");
                    properties.load(inputStream);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                final String key = properties.getProperty("RSA_KEY");
                final String initVector = properties.getProperty("RSA_INIT_VECTOR");
                final String secretApiPublicKey = UtilsHelper.encrypt(key, initVector, apiPublicKey);
                final String secretApiPrivateKey = UtilsHelper.encrypt(key, initVector, apiPrivateKey);

                // add record to the view model who will trigger the insert
                addExchangeViewModel.addExchange(new Exchange(name, link, description, secretApiPublicKey, secretApiPrivateKey));

                // close current activity and return to previous activity if there is any
                finish();

                // show a notification about the created item
                Toast.makeText(view.getContext(), getString(R.string.success_exchange_created, name), Toast.LENGTH_SHORT).show();
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