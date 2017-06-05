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
    private ExchangeManager exchangeManager;
    private long exchangeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(getResources().getString(R.string.lbl_edit_exchange));
        setContentView(R.layout.activity_edit_exchange);

        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // modal window full width
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        // get view components
        exchangeNameEditText = (EditText) findViewById(R.id.exchange_name_edit_edittext);
        exchangeLinkEditText = (EditText) findViewById(R.id.exchange_link_edit_edittext);
        exchangeDescriptionEditText = (EditText) findViewById(R.id.exchange_description_edit_edittext);
        Button updateExchangeButton = (Button) findViewById(R.id.btn_update_exchange);

        // get the intent and its data
        Intent intent = getIntent();
        String id = intent.getStringExtra("id");
        String name = intent.getStringExtra("name");
        String isoCode = intent.getStringExtra("link");
        String symbol = intent.getStringExtra("description");
        exchangeId = Long.parseLong(id);
        exchangeNameEditText.setText(name);
        exchangeLinkEditText.setText(isoCode);
        exchangeDescriptionEditText.setText(symbol);

        // open database connection
        exchangeManager = new ExchangeManager(this);
        exchangeManager.open();

        // set click listener for the update exchange button
        updateExchangeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // get values
                final String name = exchangeNameEditText.getText().toString();
                final String link = exchangeLinkEditText.getText().toString();
                final String description = exchangeDescriptionEditText.getText().toString();

                // update values into the database
                exchangeManager.update(exchangeId, name, link, description);

                // update intent so all top activities are closed
                Intent main = new Intent(EditExchangeActivity.this, ExchangeListActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(main);

                // show a notification about the updated item
                Toast.makeText(view.getContext(), view.getResources().getString(R.string.msg_exchange_updated, name), Toast.LENGTH_SHORT).show();
            }

        });
    }
}
