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
import com.mycryptobinder.managers.ExchangeManager;

/**
 * Activity responsible for new exchange creation
 * <p>
 * Created by Yann on 21/05/2017
 */

public class AddExchangeActivity extends AppCompatActivity {

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

        // hide edit button and show create button
        editExchangeButton.setVisibility(View.INVISIBLE);
        createExchangeButton.setVisibility(View.VISIBLE);

        // set click listener for the create exchange button
        createExchangeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // get values
                String name = exchangeNameEditText.getText().toString();
                String link = exchangeLinkEditText.getText().toString();
                String description = exchangeDescriptionEditText.getText().toString();

                // check mandatory fields
                if (name.trim().equals("")) {
                    exchangeNameEditText.setError("Exchange name is required!");
                } else {
                    // insert values into the database
                    ExchangeManager exchangeManager = new ExchangeManager(view.getContext());
                    exchangeManager.open();
                    exchangeManager.insert(name, link, description);
                    exchangeManager.close();

                    // update intent so all top activities are closed
                    Intent main = new Intent(AddExchangeActivity.this, ExchangeListActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(main);

                    // show a notification about the created item
                    Toast.makeText(view.getContext(), view.getResources().getString(R.string.msg_exchange_created, name), Toast.LENGTH_SHORT).show();
                }
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