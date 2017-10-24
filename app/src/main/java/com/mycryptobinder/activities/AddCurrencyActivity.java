package com.mycryptobinder.activities;

import android.content.Intent;
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
import com.mycryptobinder.managers.CurrencyManager;

/**
 * Activity responsible for new currency creation
 * <p>
 * Created by Yann on 21/05/2017
 */

public class AddCurrencyActivity extends AppCompatActivity {

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

        // hide edit button and show create button
        editCurrencyButton.setVisibility(View.INVISIBLE);
        createCurrencyButton.setVisibility(View.VISIBLE);

        // set click listener for the create currency button
        createCurrencyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // get values
                String name = currencyNameEditText.getText().toString();
                String isoCode = currencyIsoCodeEditText.getText().toString();
                String symbol = currencySymbolEditText.getText().toString();

                // check mandatory fields
                if (name.trim().equals("")) {
                    currencyNameEditText.setError("Currency name is required!");
                } else if (isoCode.trim().equals("")) {
                    currencyIsoCodeEditText.setError("ISO code is required!");
                } else {

                    // insert values into the database
                    CurrencyManager currencyManager = new CurrencyManager(view.getContext());
                    currencyManager.open();
                    currencyManager.insert(name, isoCode, symbol);
                    currencyManager.close();

                    // back to currency list and clear all top activities
                    Intent main = new Intent(AddCurrencyActivity.this, CurrencyListActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(main);

                    // show a notification about the created item
                    Toast.makeText(view.getContext(), view.getResources().getString(R.string.msg_currency_created, name), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_currency_action_bar, menu);
        return true;
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