package com.mycryptobinder.activities;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mycryptobinder.R;
import com.mycryptobinder.entities.Currency;
import com.mycryptobinder.viewmodels.AddCurrencyViewModel;

/**
 * Activity responsible for new currency creation
 * <p>
 * Created by Yann on 21/05/2017
 */

public class AddCurrencyActivity extends AppCompatActivity {

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

        // hide edit button and show create button
        editCurrencyButton.setVisibility(View.INVISIBLE);
        createCurrencyButton.setVisibility(View.VISIBLE);

        // get the view model
        addCurrencyViewModel = ViewModelProviders.of(this).get(AddCurrencyViewModel.class);

        // set click listener for the create currency button
        createCurrencyButton.setOnClickListener(new View.OnClickListener() {
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
                    addCurrencyViewModel.addCurrency(new Currency(isoCode, name, symbol));

                    // close current activity and return to previous activity if there is any
                    finish();

                    // show a notification about the created item
                    Toast.makeText(view.getContext(), view.getResources().getString(R.string.msg_currency_created, name), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // add the save/cancel buttons to the options menu
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_currency_action_bar, menu);
        return true;
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