package com.mycryptobinder.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.mycryptobinder.R;
import com.mycryptobinder.managers.ExchangeManager;

/**
 * Created by Yann
 * Created on 21/05/2017
 */

public class AddExchangeActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText exchangeNameEditText;
    private EditText exchangeLinkEditText;
    private EditText exchangeDescriptionEditText;
    private ExchangeManager exchangeManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("Add Exchange");
        setContentView(R.layout.activity_add_exchange);

        exchangeNameEditText = (EditText) findViewById(R.id.exchange_name_edittext);
        exchangeLinkEditText = (EditText) findViewById(R.id.exchange_link_edittext);
        exchangeDescriptionEditText = (EditText) findViewById(R.id.exchange_description_edittext);
        Button createExchangeButton = (Button) findViewById(R.id.btn_create_exchange);

        exchangeManager = new ExchangeManager(this);
        exchangeManager.open();
        createExchangeButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_create_exchange:

                final String name = exchangeNameEditText.getText().toString();
                final String isoCode = exchangeLinkEditText.getText().toString();
                final String symbol = exchangeDescriptionEditText.getText().toString();

                exchangeManager.insert(name, isoCode, symbol);

                Intent main = new Intent(AddExchangeActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(main);

                break;
        }
    }
}
