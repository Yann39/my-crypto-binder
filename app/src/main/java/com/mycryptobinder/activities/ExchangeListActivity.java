package com.mycryptobinder.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.mycryptobinder.R;

public class ExchangeListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exchange_list);
    }

    /**
     * Navigate to the Create Exchange page
     *
     * @param view The current view
     */
    public void goToCreateExchange(View view) {
        Intent add_exc = new Intent(this, AddExchangeActivity.class);
        startActivity(add_exc);
    }
}
