package com.mycryptobinder.activities;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mycryptobinder.R;
import com.mycryptobinder.entities.Exchange;
import com.mycryptobinder.managers.ExchangeManager;
import com.mycryptobinder.viewmodels.AddExchangeViewModel;

/**
 * Activity responsible for editing an existing exchange
 * <p>
 * Created by Yann on 02/06/2017
 */

public class EditExchangeActivity extends AppCompatActivity {

    private AddExchangeViewModel addExchangeViewModel;
    private EditText exchangeNameEditText;
    private EditText exchangeLinkEditText;
    private EditText exchangeDescriptionEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_exchange);

        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // get view components
        exchangeNameEditText = findViewById(R.id.add_exchange_name_editText);
        exchangeLinkEditText = findViewById(R.id.add_exchange_link_editText);
        exchangeDescriptionEditText = findViewById(R.id.add_exchange_description_editText);
        Button createExchangeButton = findViewById(R.id.btn_create_exchange);
        Button editExchangeButton = findViewById(R.id.btn_update_exchange);

        // hide create button and show edit button
        createExchangeButton.setVisibility(View.INVISIBLE);
        editExchangeButton.setVisibility(View.VISIBLE);

        // get the intent data and set field values
        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String isoCode = intent.getStringExtra("link");
        String symbol = intent.getStringExtra("description");
        exchangeNameEditText.setText(name);
        exchangeLinkEditText.setText(isoCode);
        exchangeDescriptionEditText.setText(symbol);

        // get the view model
        addExchangeViewModel = ViewModelProviders.of(this).get(AddExchangeViewModel.class);

        // set click listener for the update exchange button
        editExchangeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // get field values
                String name = exchangeNameEditText.getText().toString();
                String link = exchangeLinkEditText.getText().toString();
                String description = exchangeDescriptionEditText.getText().toString();

                // check mandatory fields
                if (name.trim().equals("")) {
                    exchangeNameEditText.setError("Exchange name is required!");
                } else {
                    // add record to the view model who will trigger the insert
                    addExchangeViewModel.updateExchange(new Exchange(name, link, description));

                    // close current activity and return to previous activity if there is any
                    finish();

                    // show a notification about the created item
                    Toast.makeText(view.getContext(), view.getResources().getString(R.string.msg_exchange_updated, name), Toast.LENGTH_SHORT).show();
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
