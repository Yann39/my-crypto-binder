/*
 * Copyright (c) 2018 by Yann39.
 *
 * This file is part of MyCryptoBinder.
 *
 * MyCryptoBinder is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MyCryptoBinder is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MyCryptoBinder. If not, see <http://www.gnu.org/licenses/>.
 */

package com.mycryptobinder.activities;

import android.app.DatePickerDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mycryptobinder.R;
import com.mycryptobinder.adapters.CurrencyAutoCompleteAdapter;
import com.mycryptobinder.adapters.ExchangeSpinnerAdapter;
import com.mycryptobinder.entities.Transaction;
import com.mycryptobinder.helpers.UtilsHelper;
import com.mycryptobinder.viewmodels.AddTransactionViewModel;
import com.mycryptobinder.viewmodels.CurrencyListViewModel;
import com.mycryptobinder.viewmodels.ExchangeListViewModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class AddTransactionActivity extends AppCompatActivity {

    private AddTransactionViewModel addTransactionViewModel;
    private EditText transactionDateEditText;
    private CurrencyAutoCompleteAdapter currencyAutoCompleteAdapter1;
    private CurrencyAutoCompleteAdapter currencyAutoCompleteAdapter2;
    private ExchangeSpinnerAdapter exchangeSpinnerAdapter;
    private Spinner transactionExchangeSpinner;
    private SimpleDateFormat sdf;
    private AutoCompleteTextView transactionCurrency1AutoCompleteText;
    private AutoCompleteTextView transactionCurrency2AutoCompleteText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_transaction);

        // set toolbar as actionbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // get view components
        transactionCurrency1AutoCompleteText = findViewById(R.id.transaction_currency1_autoCompleteText);
        transactionCurrency2AutoCompleteText = findViewById(R.id.transaction_currency2_autoCompleteText);
        transactionExchangeSpinner = findViewById(R.id.transaction_exchange_spinner);
        RadioButton transactionTypeBuyRadioButton = findViewById(R.id.buy_layout_rb);
        RadioButton transactionTypeSellRadioButton = findViewById(R.id.sell_layout_rb);
        transactionDateEditText = findViewById(R.id.transaction_date_edittext);
        Button createTransactionButton = findViewById(R.id.btn_create_transaction);
        Button editTransactionButton = findViewById(R.id.btn_update_transaction);

        // hide edit button and show create button
        editTransactionButton.setVisibility(View.INVISIBLE);
        createTransactionButton.setVisibility(View.VISIBLE);

        // get the view models
        addTransactionViewModel = ViewModelProviders.of(this).get(AddTransactionViewModel.class);
        CurrencyListViewModel currencyListViewModel = ViewModelProviders.of(this).get(CurrencyListViewModel.class);
        ExchangeListViewModel exchangeListViewModel = ViewModelProviders.of(this).get(ExchangeListViewModel.class);

        // initialize currencies auto complete adapters
        currencyAutoCompleteAdapter1 = new CurrencyAutoCompleteAdapter(new ArrayList<>(), transactionCurrency1AutoCompleteText.getContext(), android.R.layout.simple_dropdown_item_1line);
        transactionCurrency1AutoCompleteText.setAdapter(currencyAutoCompleteAdapter1);
        currencyAutoCompleteAdapter2 = new CurrencyAutoCompleteAdapter(new ArrayList<>(), transactionCurrency2AutoCompleteText.getContext(), android.R.layout.simple_dropdown_item_1line);
        transactionCurrency2AutoCompleteText.setAdapter(currencyAutoCompleteAdapter2);

        // observe the currency list from the view model so the auto complete texts are always up to date
        currencyListViewModel.getCurrencyList().observe(AddTransactionActivity.this, currencies -> {
            currencyAutoCompleteAdapter1.addItems(currencies);
            currencyAutoCompleteAdapter2.addItems(currencies);
        });

        // initialize exchanges spinner adapter
        exchangeSpinnerAdapter = new ExchangeSpinnerAdapter(new ArrayList<>(), transactionExchangeSpinner.getContext(), android.R.layout.simple_dropdown_item_1line);
        transactionExchangeSpinner.setAdapter(exchangeSpinnerAdapter);

        // observe the exchange list from the view model so the auto complete text is always up to date
        exchangeListViewModel.getExchangeList().observe(AddTransactionActivity.this, exchanges -> exchangeSpinnerAdapter.addItems(exchanges));

        // set click listener on the Buy radio button
        transactionTypeBuyRadioButton.setOnClickListener(v -> {
            TextView transactionCurrency1TextView = findViewById(R.id.transaction_currency1_textview);
            TextView transactionCurrency2TextView = findViewById(R.id.transaction_currency2_textview);
            transactionCurrency1TextView.setText(getString(R.string.label_transaction_currency1_buy));
            transactionCurrency2TextView.setText(getString(R.string.label_transaction_currency2_with));
        });

        // set click listener on the Sell radio button
        transactionTypeSellRadioButton.setOnClickListener(v -> {
            TextView transactionCurrency1TextView = findViewById(R.id.transaction_currency1_textview);
            TextView transactionCurrency2TextView = findViewById(R.id.transaction_currency2_textview);
            transactionCurrency1TextView.setText(getString(R.string.label_transaction_currency1_sell));
            transactionCurrency2TextView.setText(getString(R.string.label_transaction_currency2_for));
        });

        // set date format that will be used for date pickers
        UtilsHelper uh = new UtilsHelper(getApplicationContext());
        sdf = new SimpleDateFormat("dd/MM/yyyy", uh.getCurrentLocale());

        // set focus listener to display a date picker on transaction date field focus
        transactionDateEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                // get current date to initialize the date picker
                Calendar c = Calendar.getInstance();

                // open the date picker
                new DatePickerDialog(v.getContext(), (view, year, monthOfYear, dayOfMonth) -> {
                    // format the date and display it in the related text box
                    Calendar c1 = Calendar.getInstance();
                    c1.set(year, monthOfYear - 1, dayOfMonth, 0, 0);
                    transactionDateEditText.setText(sdf.format(c1.getTime()));
                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        // set click listener for the create transaction button
        createTransactionButton.setOnClickListener(view -> {

            // get view components
            EditText transactionTxIdEditText = findViewById(R.id.transaction_txid_edittext);
            EditText transactionQuantityEditText = findViewById(R.id.transaction_quantity_edittext);
            EditText transactionPriceEditText = findViewById(R.id.transaction_price_edittext);
            EditText transactionFeeEditText = findViewById(R.id.transaction_fee_edittext);
            EditText transactionCommentEditText = findViewById(R.id.transaction_comment_edittext);
            EditText transactionTotalEditText = findViewById(R.id.transaction_total_edittext);
            RadioGroup transactionTypeRadioGroup = findViewById(R.id.transaction_type_radio_group);
            RadioButton radioButton = findViewById(transactionTypeRadioGroup.getCheckedRadioButtonId());

            // get field values
            String currency1 = currencyAutoCompleteAdapter1.getItem(0);
            String currency2 = currencyAutoCompleteAdapter2.getItem(0);
            String exchange = exchangeSpinnerAdapter.getItem(transactionExchangeSpinner.getSelectedItemPosition());
            String txId = transactionTxIdEditText.getText().toString();
            String quantityStr = transactionQuantityEditText.getText().toString();
            String priceStr = transactionPriceEditText.getText().toString();
            String feeStr = transactionFeeEditText.getText().toString();
            String totalStr = transactionTotalEditText.getText().toString();
            String type = radioButton.getText().toString();
            String comment = transactionCommentEditText.getText().toString();

            // format doubles
            Double quantity = null;
            Double price = null;
            Double fee = null;
            Double total = null;
            try {
                quantity = Double.parseDouble(quantityStr);
                price = Double.parseDouble(priceStr);
                fee = Double.parseDouble(feeStr);
                total = Double.parseDouble(totalStr);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

            // format date
            Date date = null;
            try {
                date = sdf.parse(transactionDateEditText.getText().toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }

            // check mandatory fields
            if (currency1 == null || currency1.trim().equals("")) {
                transactionCurrency1AutoCompleteText.setError(getString(R.string.error_transaction_currency1_required));
            } else if (currency2 == null || currency2.trim().equals("")) {
                transactionCurrency2AutoCompleteText.setError(getString(R.string.error_transaction_currency2_required));
            } else if (exchange == null || exchange.trim().equals("")) {
                // setError on spinner not possible, so let's use a fake TextView
                TextView errorText = (TextView) transactionExchangeSpinner.getSelectedView();
                errorText.setError("Anything here, just for the icon to be displayed");
                errorText.setTextColor(Color.RED);
                errorText.setText(getString(R.string.error_transaction_exchange_required));
            } else if (txId.trim().equals("")) {
                transactionTxIdEditText.setError(getString(R.string.error_transaction_id_required));
            } else if (quantityStr.trim().equals("")) {
                transactionQuantityEditText.setError(getString(R.string.error_transaction_quantity_required));
            } else if (quantity == null) {
                transactionQuantityEditText.setError(getString(R.string.error_transaction_quantity_not_valid));
            } else if (priceStr.trim().equals("")) {
                transactionPriceEditText.setError(getString(R.string.error_transaction_price_required));
            } else if (price == null) {
                transactionPriceEditText.setError(getString(R.string.error_transaction_price_not_valid));
            } else if (feeStr.trim().equals("")) {
                transactionFeeEditText.setError(getString(R.string.error_transaction_fee_required));
            } else if (fee == null) {
                transactionFeeEditText.setError(getString(R.string.error_transaction_fee_not_valid));
            } else if (totalStr.trim().equals("")) {
                transactionTotalEditText.setError(getString(R.string.error_transaction_total_required));
            } else if (total == null) {
                transactionTotalEditText.setError(getString(R.string.error_transaction_total_not_valid));
            } else if (type.trim().equals("")) {
                radioButton.setError(getString(R.string.error_transaction_type_required));
            } else {
                // add record to the view model who will trigger the insert
                addTransactionViewModel.addTransaction(new Transaction(exchange, txId, currency1, currency2, fee, date, type, quantity, price, total, comment));

                // close current activity and return to previous activity if there is any
                finish();

                // show a notification about the created item
                Toast.makeText(view.getContext(), getString(R.string.success_transaction_created, currency1 + "/" + currency2), Toast.LENGTH_SHORT).show();
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