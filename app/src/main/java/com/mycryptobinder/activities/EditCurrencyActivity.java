package com.mycryptobinder.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
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
        currencyNameEditText = (EditText) findViewById(R.id.add_currency_name_editText);
        currencyIsoCodeEditText = (EditText) findViewById(R.id.add_currency_iso_code_editText);
        currencySymbolEditText = (EditText) findViewById(R.id.add_currency_symbol_editText);
        Button createCurrencyButton = (Button) findViewById(R.id.btn_create_currency);
        Button editCurrencyButton = (Button) findViewById(R.id.btn_update_currency);

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

        // set click listener for the update currency button
        editCurrencyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // get values
                String name = currencyNameEditText.getText().toString();
                String isoCode = currencyIsoCodeEditText.getText().toString();
                String symbol = currencySymbolEditText.getText().toString();

                // update values into the database
                CurrencyManager currencyManager = new CurrencyManager(view.getContext());
                currencyManager.open();
                currencyManager.update(isoCode, name, symbol);
                currencyManager.close();

                // update intent so all top activities are closed
                Intent main = new Intent(EditCurrencyActivity.this, CurrencyListActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(main);

                // show a notification about the updated item
                Toast.makeText(view.getContext(), view.getResources().getString(R.string.msg_currency_updated, name), Toast.LENGTH_SHORT).show();
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
