package com.mycryptobinder.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.mycryptobinder.R;
import com.mycryptobinder.managers.CurrencyManager;

/**
 * Created by Yann
 * Created on 21/05/2017
 */

public class AddCurrencyActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText currencyNameEditText;
    private EditText currencyIsoCodeEditText;
    private EditText currencySymbolEditText;
    private CurrencyManager currencyManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("Add Currency");
        setContentView(R.layout.activity_add_currency);

        currencyNameEditText = (EditText) findViewById(R.id.currency_name_edittext);
        currencyIsoCodeEditText = (EditText) findViewById(R.id.currency_iso_code_edittext);
        currencySymbolEditText = (EditText) findViewById(R.id.currency_symbol_edittext);
        Button createCurrencyButton = (Button) findViewById(R.id.btn_create_currency);

        currencyManager = new CurrencyManager(this);
        currencyManager.open();
        createCurrencyButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_create_currency:

                final String name = currencyNameEditText.getText().toString();
                final String isoCode = currencyIsoCodeEditText.getText().toString();
                final String symbol = currencySymbolEditText.getText().toString();

                currencyManager.insert(name, isoCode, symbol);

                Intent main = new Intent(AddCurrencyActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(main);

                break;
        }
    }
}
