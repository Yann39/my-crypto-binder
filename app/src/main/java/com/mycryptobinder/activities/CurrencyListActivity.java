package com.mycryptobinder.activities;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.mycryptobinder.R;
import com.mycryptobinder.adapters.CurrencyCardAdapter;
import com.mycryptobinder.managers.CurrencyManager;
import com.mycryptobinder.models.Currency;

public class CurrencyListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency_list);

        RecyclerView recList = (RecyclerView) findViewById(R.id.my_recycler_view);
        recList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);

        CurrencyManager currencyManager = new CurrencyManager(this);
        currencyManager.open();

        recList.setAdapter(new CurrencyCardAdapter(currencyManager.getAll()));
    }

    /**
     * Navigate to the Create Currency page
     *
     * @param view The current view
     */
    public void goToCreateCurrency(View view) {
        Intent add_cur = new Intent(this, AddCurrencyActivity.class);
        startActivity(add_cur);
    }
}
