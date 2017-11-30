package com.mycryptobinder.activities;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mycryptobinder.R;
import com.mycryptobinder.entities.Currency;
import com.mycryptobinder.viewmodels.AddCurrencyViewModel;

/**
 * Activity responsible for editing an existing currency
 * <p>
 * Created by Yann on 02/06/2017
 */

public class EditCurrencyActivity extends AppCompatActivity {

    private AddCurrencyViewModel addCurrencyViewModel;
    private EditText currencyNameEditText;
    private EditText currencyIsoCodeEditText;
    private EditText currencySymbolEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_currency);

        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // get view components
        currencyNameEditText = findViewById(R.id.add_currency_name_editText);
        currencyIsoCodeEditText = findViewById(R.id.add_currency_iso_code_editText);
        currencySymbolEditText = findViewById(R.id.add_currency_symbol_editText);
        Button createCurrencyButton = findViewById(R.id.btn_create_currency);
        Button editCurrencyButton = findViewById(R.id.btn_update_currency);

        // hide create button and show edit button
        createCurrencyButton.setVisibility(View.INVISIBLE);
        editCurrencyButton.setVisibility(View.VISIBLE);

        // get the intent data and set field values
        Intent intent = getIntent();
        String isoCode = intent.getStringExtra("isoCode");
        String name = intent.getStringExtra("name");
        String symbol = intent.getStringExtra("symbol");
        currencyIsoCodeEditText.setText(isoCode);
        currencyNameEditText.setText(name);
        currencySymbolEditText.setText(symbol);

        // get the view model
        addCurrencyViewModel = ViewModelProviders.of(this).get(AddCurrencyViewModel.class);

        // set click listener for the update currency button
        editCurrencyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // get field values
                String isoCode = currencyIsoCodeEditText.getText().toString();
                String name = currencyNameEditText.getText().toString();
                String symbol = currencySymbolEditText.getText().toString();

                // check mandatory fields
                if (name.trim().equals("")) {
                    currencyNameEditText.setError("Currency name is required!");
                } else if (isoCode.trim().equals("")) {
                    currencyIsoCodeEditText.setError("ISO code is required!");
                } else {
                    // add record to the view model who will trigger the insert
                    addCurrencyViewModel.updateCurrency(new Currency(isoCode, name, symbol));

                    // close current activity and return to previous activity if there is any
                    finish();

                    // show a notification about the created item
                    Toast.makeText(view.getContext(), view.getResources().getString(R.string.msg_currency_updated, name), Toast.LENGTH_SHORT).show();
                }
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
