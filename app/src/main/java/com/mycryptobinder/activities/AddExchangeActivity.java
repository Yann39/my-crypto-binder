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
 * Activity responsible for new exchange creation
 * <p>
 * Created by Yann on 21/05/2017
 */

public class AddExchangeActivity extends AppCompatActivity {

    private EditText exchangeNameEditText;
    private EditText exchangeLinkEditText;
    private EditText exchangeDescriptionEditText;
    private ExchangeManager exchangeManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(getResources().getString(R.string.title_add_exchange));
        setContentView(R.layout.activity_add_exchange);

        // modal window full width
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        // get view components
        exchangeNameEditText = (EditText) findViewById(R.id.exchange_name_add_edittext);
        exchangeLinkEditText = (EditText) findViewById(R.id.exchange_link_add_edittext);
        exchangeDescriptionEditText = (EditText) findViewById(R.id.exchange_description_add_edittext);
        Button createExchangeButton = (Button) findViewById(R.id.btn_create_exchange);

        // open database connection
        exchangeManager = new ExchangeManager(this);
        exchangeManager.open();

        // set click listener for the create exchange button
        createExchangeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // get values
                String name = exchangeNameEditText.getText().toString();
                String link = exchangeLinkEditText.getText().toString();
                String description = exchangeDescriptionEditText.getText().toString();

                // insert values into the database
                exchangeManager.insert(name, link, description);

                // update intent so all top activities are closed
                Intent main = new Intent(AddExchangeActivity.this, ExchangeListActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(main);

                // show a notification about the created item
                Toast.makeText(view.getContext(), view.getResources().getString(R.string.msg_exchange_created, name), Toast.LENGTH_SHORT).show();
            }
        });

    }

}