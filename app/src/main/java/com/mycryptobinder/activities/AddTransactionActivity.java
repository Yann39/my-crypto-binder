package com.mycryptobinder.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.mycryptobinder.R;

/**
 * Activity responsible for new transaction creation
 * <p>
 * Created by Yann
 * Created on 21/05/2017
 */

public class AddTransactionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);
    }
}