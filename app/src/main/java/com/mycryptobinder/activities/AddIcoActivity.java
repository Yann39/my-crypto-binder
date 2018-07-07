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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mycryptobinder.R;
import com.mycryptobinder.adapters.CurrencySpinnerAdapter;
import com.mycryptobinder.entities.Ico;
import com.mycryptobinder.helpers.UtilsHelper;
import com.mycryptobinder.viewmodels.AddIcoViewModel;
import com.mycryptobinder.viewmodels.CurrencyListViewModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class AddIcoActivity extends AppCompatActivity {

    private AddIcoViewModel addIcoViewModel;
    private EditText addIcoDateEditText;
    private EditText addIcoTokenDateEditText;
    private Spinner addIcoCurrencySpinner;
    private CurrencySpinnerAdapter currencySpinnerAdapter;
    private SimpleDateFormat sdf;
    private EditText addIcoNameEditText;
    private EditText addIcoAmountEditText;
    private EditText addIcoTokenNameEditText;
    private EditText addIcoTokenQuantityEditText;
    private EditText addIcoFeeEditText;
    private EditText addIcoBonusEditText;
    private EditText addIcoCommentEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_ico);

        // set toolbar as actionbar
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // get view components
        addIcoDateEditText = findViewById(R.id.add_ico_date_editText);
        addIcoTokenDateEditText = findViewById(R.id.add_ico_token_date_editText);
        addIcoCurrencySpinner = findViewById(R.id.add_ico_currency_spinner);
        addIcoNameEditText = findViewById(R.id.add_ico_name_editText);
        addIcoAmountEditText = findViewById(R.id.add_ico_amount_editText);
        addIcoTokenNameEditText = findViewById(R.id.add_ico_token_name_editText);
        addIcoTokenQuantityEditText = findViewById(R.id.add_ico_token_quantity_editText);
        addIcoFeeEditText = findViewById(R.id.add_ico_fee_editText);
        addIcoBonusEditText = findViewById(R.id.add_ico_bonus_editText);
        addIcoCommentEditText = findViewById(R.id.add_ico_comment_editText);
        final Button createIcoButton = findViewById(R.id.btn_create_ico);
        final Button editIcoButton = findViewById(R.id.btn_update_ico);

        // hide edit button and show create button
        editIcoButton.setVisibility(View.INVISIBLE);
        createIcoButton.setVisibility(View.VISIBLE);

        // get the view models
        addIcoViewModel = ViewModelProviders.of(this).get(AddIcoViewModel.class);
        final CurrencyListViewModel currencyListViewModel = ViewModelProviders.of(this).get(CurrencyListViewModel.class);

        // initialize currencies spinner adapter
        currencySpinnerAdapter = new CurrencySpinnerAdapter(new ArrayList<>(), addIcoCurrencySpinner.getContext(), android.R.layout.simple_dropdown_item_1line);
        currencySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        addIcoCurrencySpinner.setAdapter(currencySpinnerAdapter);

        // observe the currency list from the view model so the currency spinner is always up to date
        currencyListViewModel.getCurrencyList().observe(AddIcoActivity.this, currencies -> currencySpinnerAdapter.addItems(currencies));

        // set date format that will be used for date pickers
        sdf = new SimpleDateFormat("dd/MM/yyyy", UtilsHelper.getCurrentLocale(getApplicationContext()));

        // set focus listener to display a date picker on ICO date field focus
        addIcoDateEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                // get current date to initialize the date picker
                final Calendar c = Calendar.getInstance();

                // open the date picker
                new DatePickerDialog(v.getContext(), (view, year, monthOfYear, dayOfMonth) -> {
                    // format the date and display it in the related text box
                    final Calendar c1 = Calendar.getInstance();
                    c1.set(year, monthOfYear - 1, dayOfMonth, 0, 0);
                    addIcoDateEditText.setText(sdf.format(c1.getTime()));
                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        // set focus listener to display a date picker on token date field focus
        addIcoTokenDateEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                // get current date to initialize the date picker
                final Calendar c = Calendar.getInstance();

                // open the date picker
                new DatePickerDialog(v.getContext(), (view, year, monthOfYear, dayOfMonth) -> {
                    // format the date and display it in the related text box
                    final Calendar c12 = Calendar.getInstance();
                    c12.set(year, monthOfYear - 1, dayOfMonth, 0, 0);
                    addIcoTokenDateEditText.setText(sdf.format(c12.getTime()));
                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        // set click listener for the create ICO button
        createIcoButton.setOnClickListener(view -> {
            // get field values as strings
            final String name = addIcoNameEditText.getText().toString();
            String currencyIsoCode = null;
            if (addIcoCurrencySpinner.getSelectedItemPosition() >= 0) {
                currencyIsoCode = currencySpinnerAdapter.getItem(addIcoCurrencySpinner.getSelectedItemPosition());
            }
            final String amountStr = addIcoAmountEditText.getText().toString();
            final String dateStr = addIcoDateEditText.getText().toString();
            final String tokenName = addIcoTokenNameEditText.getText().toString();
            final String tokenDateStr = addIcoTokenDateEditText.getText().toString();
            final String tokenQuantityStr = addIcoTokenQuantityEditText.getText().toString();
            final String feeStr = addIcoFeeEditText.getText().toString();
            final String bonusStr = addIcoBonusEditText.getText().toString();
            final String comment = addIcoCommentEditText.getText().toString();

            // format doubles
            Double amount = null;
            Double tokenQuantity = null;
            Double fee = null;
            Double bonus = null;
            try {
                amount = Double.parseDouble(amountStr);
                tokenQuantity = Double.parseDouble(tokenQuantityStr);
                fee = Double.parseDouble(feeStr);
                bonus = Double.parseDouble(bonusStr);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

            // format dates
            Date date = null;
            Date tokenDate = null;
            try {
                date = sdf.parse(dateStr);
                tokenDate = sdf.parse(tokenDateStr);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            // check mandatory fields
            if (name.trim().equals("")) {
                addIcoNameEditText.setError(getString(R.string.error_ico_name_required));
            } else if (currencyIsoCode == null || currencyIsoCode.trim().equals("")) {
                // setError on spinner not possible, so let's use a fake TextView
                final TextView errorText = (TextView) addIcoCurrencySpinner.getSelectedView();
                errorText.setError("Anything here, just for the icon to be displayed");
                errorText.setTextColor(Color.RED);
                errorText.setText(getString(R.string.error_ico_currency_required));
            } else if (amountStr.trim().equals("")) {
                addIcoAmountEditText.setError(getString(R.string.error_ico_amount_required));
            } else if (amount == null) {
                addIcoAmountEditText.setError(getString(R.string.error_ico_amount_not_valid));
            } else if (dateStr.trim().equals("")) {
                addIcoDateEditText.setError(getString(R.string.error_ico_date_required));
            } else if (date == null) {
                addIcoDateEditText.setError(getString(R.string.error_ico_date_not_valid));
            } else if (tokenName.trim().equals("")) {
                addIcoTokenNameEditText.setError(getString(R.string.error_ico_token_name_required));
            } else if (tokenDateStr.trim().equals("")) {
                addIcoTokenDateEditText.setError(getString(R.string.error_ico_token_date_required));
            } else if (tokenDate == null) {
                addIcoTokenDateEditText.setError(getString(R.string.error_ico_token_date_invalid));
            } else if (tokenQuantityStr.trim().equals("")) {
                addIcoTokenQuantityEditText.setError(getString(R.string.error_ico_token_quantity_required));
            } else if (tokenQuantity == null) {
                addIcoTokenQuantityEditText.setError(getString(R.string.error_ico_token_quantity_invalid));
            } else if (feeStr.trim().equals("")) {
                addIcoFeeEditText.setError(getString(R.string.error_ico_fee_required));
            } else if (fee == null) {
                addIcoFeeEditText.setError(getString(R.string.error_ico_fee_invalid));
            } else if (bonusStr.trim().equals("")) {
                addIcoBonusEditText.setError(getString(R.string.error_ico_bonus_required));
            } else if (bonus == null) {
                addIcoBonusEditText.setError(getString(R.string.error_ico_bonus_invalid));
            } else {
                // add record to the view model who will trigger the insert
                addIcoViewModel.addIco(new Ico(null, name, currencyIsoCode, amount, fee, date, tokenName, tokenDate, tokenQuantity, bonus, comment));

                // close current activity and return to previous activity if there is any
                finish();

                // show a notification about the created item
                Toast.makeText(view.getContext(), getString(R.string.success_ico_created, name), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int id = item.getItemId();

        // back arrow click
        if (id == android.R.id.home) {
            // close current activity and return to previous activity if there is any
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

}
