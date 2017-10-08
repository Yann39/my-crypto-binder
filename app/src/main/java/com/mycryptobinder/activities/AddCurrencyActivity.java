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

        setTitle(getResources().getString(R.string.title_add_currency));
        setContentView(R.layout.activity_add_currency);

        // modal window full width
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        // get view components
        currencyNameEditText = (EditText) findViewById(R.id.currency_name_add_edittext);
        currencyIsoCodeEditText = (EditText) findViewById(R.id.currency_iso_code_add_edittext);
        currencySymbolEditText = (EditText) findViewById(R.id.currency_symbol_add_edittext);
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
        });

    }

}