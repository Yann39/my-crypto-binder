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
    private long currencyId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(getResources().getString(R.string.lbl_edit_currency));
        setContentView(R.layout.activity_edit_currency);

        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // modal window full width
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        // get view components
        currencyNameEditText = (EditText) findViewById(R.id.currency_name_edit_edittext);
        currencyIsoCodeEditText = (EditText) findViewById(R.id.currency_iso_code_edit_edittext);
        currencySymbolEditText = (EditText) findViewById(R.id.currency_symbol_edit_edittext);
        Button updateCurrencyButton = (Button) findViewById(R.id.btn_update_currency);

        // get the intent and its data
        Intent intent = getIntent();
        String id = intent.getStringExtra("id");
        String name = intent.getStringExtra("name");
        String isoCode = intent.getStringExtra("isoCode");
        String symbol = intent.getStringExtra("symbol");
        currencyId = Long.parseLong(id);
        currencyNameEditText.setText(name);
        currencyIsoCodeEditText.setText(isoCode);
        currencySymbolEditText.setText(symbol);

        // open database connection
        currencyManager = new CurrencyManager(this);
        currencyManager.open();

        // set click listener for the update currency button
        updateCurrencyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // get values
                final String name = currencyNameEditText.getText().toString();
                final String isoCode = currencyIsoCodeEditText.getText().toString();
                final String symbol = currencySymbolEditText.getText().toString();

                // update values into the database
                currencyManager.update(currencyId, name, isoCode, symbol);

                // update intent so all top activities are closed
                Intent main = new Intent(EditCurrencyActivity.this, CurrencyListActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(main);

                // show a notification about the updated item
                Toast.makeText(view.getContext(), view.getResources().getString(R.string.msg_currency_updated, name), Toast.LENGTH_SHORT).show();
            }

        });
    }
}
