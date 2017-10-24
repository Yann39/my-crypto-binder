package com.mycryptobinder.activities;

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
import com.mycryptobinder.managers.ExchangeManager;

/**
 * Activity responsible for editing an existing exchange
 * <p>
 * Created by Yann on 02/06/2017
 */

public class EditExchangeActivity extends AppCompatActivity {

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
        exchangeNameEditText = (EditText) findViewById(R.id.add_exchange_name_editText);
        exchangeLinkEditText = (EditText) findViewById(R.id.add_exchange_link_editText);
        exchangeDescriptionEditText = (EditText) findViewById(R.id.add_exchange_description_editText);
        Button createExchangeButton = (Button) findViewById(R.id.btn_create_exchange);
        Button editExchangeButton = (Button) findViewById(R.id.btn_update_exchange);

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

        // set click listener for the update exchange button
        editExchangeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // get values
                String name = exchangeNameEditText.getText().toString();
                String link = exchangeLinkEditText.getText().toString();
                String description = exchangeDescriptionEditText.getText().toString();

                // update values into the database
                ExchangeManager exchangeManager = new ExchangeManager(view.getContext());
                exchangeManager.open();
                exchangeManager.update(name, link, description);
                exchangeManager.close();

                // update intent so all top activities are closed
                Intent main = new Intent(EditExchangeActivity.this, ExchangeListActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(main);

                // show a notification about the updated item
                Toast.makeText(view.getContext(), view.getResources().getString(R.string.msg_exchange_updated, name), Toast.LENGTH_SHORT).show();
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
