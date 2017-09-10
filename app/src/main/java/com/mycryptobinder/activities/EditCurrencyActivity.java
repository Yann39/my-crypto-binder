package com.mycryptobinder.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mycryptobinder.R;
import com.mycryptobinder.managers.CurrencyManager;

/**
 * Activity responsible for editing an existing currency
 * <p>
 * Created by Yann on 02/06/2017
 */

public class EditCurrencyActivity extends AppCompatActivity {

    private EditText currencyNameEditText;
    private EditText currencyIsoCodeEditText;
    private EditText currencySymbolEditText;
    private CurrencyManager currencyManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(getResources().getString(R.string.title_edit_currency));
        setContentView(R.layout.activity_add_currency);

        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // modal window full width
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        // get view components
        currencyNameEditText = (EditText) findViewById(R.id.currency_name_add_edittext);
        currencyIsoCodeEditText = (EditText) findViewById(R.id.currency_iso_code_add_edittext);
        currencySymbolEditText = (EditText) findViewById(R.id.currency_symbol_add_edittext);
        Button createCurrencyButton = (Button) findViewById(R.id.btn_create_currency);
        Button editCurrencyButton = (Button) findViewById(R.id.btn_update_currency);

        // hide create button and show edit button
        createCurrencyButton.setVisibility(View.INVISIBLE);
        editCurrencyButton.setVisibility(View.VISIBLE);

        // get the intent and its data
        Intent intent = getIntent();
        String isoCode = intent.getStringExtra("isoCode");
        String name = intent.getStringExtra("name");
        String symbol = intent.getStringExtra("symbol");
        currencyIsoCodeEditText.setText(isoCode);
        currencyNameEditText.setText(name);
        currencySymbolEditText.setText(symbol);

        // open database connection
        currencyManager = new CurrencyManager(this);
        currencyManager.open();

        // set click listener for the update currency button
        editCurrencyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // get values
                String name = currencyNameEditText.getText().toString();
                String isoCode = currencyIsoCodeEditText.getText().toString();
                String symbol = currencySymbolEditText.getText().toString();

                // update values into the database
                currencyManager.update(isoCode, name, symbol);

                // update intent so all top activities are closed
                Intent main = new Intent(EditCurrencyActivity.this, CurrencyListActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(main);

                // show a notification about the updated item
                Toast.makeText(view.getContext(), view.getResources().getString(R.string.msg_currency_updated, name), Toast.LENGTH_SHORT).show();
            }

        });
    }
}
