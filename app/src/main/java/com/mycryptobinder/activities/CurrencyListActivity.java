package com.mycryptobinder.activities;

import android.app.AlertDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.mycryptobinder.R;
import com.mycryptobinder.adapters.CurrencyCardAdapter;
import com.mycryptobinder.entities.Currency;
import com.mycryptobinder.viewholders.CurrencyCardViewHolder;
import com.mycryptobinder.viewmodels.CurrencyListViewModel;

/**
 * Activity responsible for displaying the list of currencies
 * <p>
 * Created by Yann on 24/05/2017
 */

public class CurrencyListActivity extends AppCompatActivity implements View.OnClickListener {

    private CurrencyCardAdapter currencyCardAdapter;
    private CurrencyListViewModel currencyListViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency_list);

        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // prepare the recycler view with a linear layout
        RecyclerView currencyListRecyclerView = findViewById(R.id.currency_list_recycler_view);
        currencyListRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // initialize the adapter for the list
        currencyCardAdapter = new CurrencyCardAdapter(this);
        currencyListRecyclerView.setAdapter(currencyCardAdapter);

        // get view model
        currencyListViewModel = ViewModelProviders.of(this).get(CurrencyListViewModel.class);

        // observe the currency list from the view model so it is always up to date
        currencyListViewModel.getPagedCurrencyList().observe(this, pagedList -> currencyCardAdapter.setList(pagedList));

        // set click listener for the add currency button
        FloatingActionButton button = findViewById(R.id.btn_add_currency);
        button.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), AddCurrencyActivity.class);
            startActivity(intent);
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

    @Override
    public void onClick(View view) {
        Currency currency = (Currency) view.getTag();
        // show a confirm dialog
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(getResources().getString(R.string.lbl_confirmation));
        alert.setMessage(Html.fromHtml(getResources().getString(R.string.dialog_delete_currency_message, " <b>" + currency.getName() + "</b>")));

        Context context = this;

        alert.setPositiveButton(getResources().getString(R.string.lbl_yes), (dialog, which) -> {

            // delete from the database
            currencyListViewModel.deleteItem(currency);

            // notify any registered observers that the item previously located at position
            // has been removed from the data set. The items previously located at and
            // after position may now be found at oldPosition - 1.
            //currencyCardAdapter.notifyItemRemoved(position);

            // notify any registered observers that the itemCount items starting at
            // position positionStart have changed.
            //currencyCardAdapter.notifyItemRangeChanged(position, currencies.size());

            // show a notification about the removed item
            Toast.makeText(context, getResources().getString(R.string.msg_currency_removed, currency.getIsoCode()), Toast.LENGTH_SHORT).show();

            dialog.dismiss();
        });

        alert.setNegativeButton(getResources().getString(R.string.lbl_no), (dialog, which) -> dialog.dismiss());

        alert.show();
    }
}