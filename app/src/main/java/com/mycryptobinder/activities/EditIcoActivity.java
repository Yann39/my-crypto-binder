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
import android.content.Intent;
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

public class EditIcoActivity extends AppCompatActivity {

    private AddIcoViewModel addIcoViewModel;
    private TextView icoIdTextView;
    private EditText icoNameEditText;
    private EditText icoDateEditText;
    private EditText icoAmountEditText;
    private EditText icoTokenNameEditText;
    private EditText icoTokenQuantityEditText;
    private EditText icoFeeEditText;
    private EditText icoBonusEditText;
    private EditText icoCommentEditText;
    private EditText icoTokenDateEditText;
    private Spinner icoCurrencySpinner;
    private CurrencySpinnerAdapter currencySpinnerAdapter;
    private SimpleDateFormat sdf;

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
        icoIdTextView = findViewById(R.id.add_ico_id_textView);
        icoNameEditText = findViewById(R.id.add_ico_name_editText);
        icoDateEditText = findViewById(R.id.add_ico_date_editText);
        icoAmountEditText = findViewById(R.id.add_ico_amount_editText);
        icoTokenNameEditText = findViewById(R.id.add_ico_token_name_editText);
        icoTokenDateEditText = findViewById(R.id.add_ico_token_date_editText);
        icoTokenQuantityEditText = findViewById(R.id.add_ico_token_quantity_editText);
        icoFeeEditText = findViewById(R.id.add_ico_fee_editText);
        icoBonusEditText = findViewById(R.id.add_ico_bonus_editText);
        icoCommentEditText = findViewById(R.id.add_ico_comment_editText);
        icoCurrencySpinner = findViewById(R.id.add_ico_currency_spinner);
        final Button createIcoButton = findViewById(R.id.btn_create_ico);
        final Button editIcoButton = findViewById(R.id.btn_update_ico);

        // hide create button and show edit button
        createIcoButton.setVisibility(View.INVISIBLE);
        editIcoButton.setVisibility(View.VISIBLE);

        // get the view models
        addIcoViewModel = ViewModelProviders.of(this).get(AddIcoViewModel.class);
        final CurrencyListViewModel currencyListViewModel = ViewModelProviders.of(this).get(CurrencyListViewModel.class);

        // initialize currencies spinner adapter
        currencySpinnerAdapter = new CurrencySpinnerAdapter(new ArrayList<>(), icoCurrencySpinner.getContext(), android.R.layout.simple_dropdown_item_1line);
        currencySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        icoCurrencySpinner.setAdapter(currencySpinnerAdapter);

        // set date format that will be used for date pickers
        sdf = new SimpleDateFormat("dd/MM/yyyy", UtilsHelper.getCurrentLocale(getApplicationContext()));

        // get the intent data so we get value of the clicked item, then set field values
        final Intent intent = getIntent();
        icoIdTextView.setText(String.valueOf(intent.getLongExtra("id", -1)));
        icoNameEditText.setText(intent.getStringExtra("name"));
        icoDateEditText.setText(sdf.format((Date) intent.getSerializableExtra("investDate")));
        icoAmountEditText.setText(String.valueOf(intent.getDoubleExtra("amount", 0.0)));
        icoTokenNameEditText.setText(intent.getStringExtra("token"));
        icoTokenDateEditText.setText(sdf.format((Date) intent.getSerializableExtra("tokenDate")));
        icoTokenQuantityEditText.setText(String.valueOf(intent.getDoubleExtra("tokenQuantity", 0.0)));
        icoFeeEditText.setText(String.valueOf(intent.getDoubleExtra("fee", 0.0)));
        icoBonusEditText.setText(String.valueOf(intent.getDoubleExtra("bonus", 0.0)));
        icoCommentEditText.setText(intent.getStringExtra("comment"));

        // observe the currency list from the view model so the currency spinner is always up to date
        currencyListViewModel.getCurrencyList().observe(EditIcoActivity.this, currencies -> {
            currencySpinnerAdapter.addItems(currencies);
            // set selection here, once adapter items are set
            icoCurrencySpinner.setSelection(currencySpinnerAdapter.getPosition(intent.getStringExtra("currency")));
        });

        // set focus listener to display a date picker on ICO date field focus
        icoDateEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                // get current date to initialize the date picker
                final Calendar c = Calendar.getInstance();

                // open the date picker
                new DatePickerDialog(v.getContext(), (view, year, monthOfYear, dayOfMonth) -> {
                    // format the date and display it in the related text box
                    final Calendar c1 = Calendar.getInstance();
                    c1.set(year, monthOfYear - 1, dayOfMonth, 0, 0);
                    icoDateEditText.setText(sdf.format(c1.getTime()));
                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        // set focus listener to display a date picker on token date field focus
        icoTokenDateEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                // get current date to initialize the date picker
                final Calendar c = Calendar.getInstance();

                // open the date picker
                new DatePickerDialog(v.getContext(), (view, year, monthOfYear, dayOfMonth) -> {
                    // format the date and display it in the related text box
                    final Calendar c12 = Calendar.getInstance();
                    c12.set(year, monthOfYear - 1, dayOfMonth, 0, 0);
                    icoTokenDateEditText.setText(sdf.format(c12.getTime()));
                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        // set click listener for the edit ICO button
        editIcoButton.setOnClickListener(view -> editIco());

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

    private void editIco() {
        // get field values
        final String idStr = icoIdTextView.getText().toString();
        final String name = icoNameEditText.getText().toString();
        String currencyIsoCode = null;
        if (icoCurrencySpinner.getSelectedItemPosition() >= 0) {
            currencyIsoCode = currencySpinnerAdapter.getItem(icoCurrencySpinner.getSelectedItemPosition());
        }
        final String amountStr = icoAmountEditText.getText().toString();
        final String dateStr = icoDateEditText.getText().toString();
        final String tokenName = icoTokenNameEditText.getText().toString();
        final String tokenDateStr = icoTokenDateEditText.getText().toString();
        final String tokenQuantityStr = icoTokenQuantityEditText.getText().toString();
        final String feeStr = icoFeeEditText.getText().toString();
        final String bonusStr = icoBonusEditText.getText().toString();
        final String comment = icoCommentEditText.getText().toString();

        // format long
        long id = -1;
        try {
            id = Long.parseLong(idStr);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

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
        if (id <= 0) {
            // todo useless as field is hidden
            icoIdTextView.setError(getString(R.string.error_ico_id_invalid));
        } else if (name.trim().equals("")) {
            icoNameEditText.setError(getString(R.string.error_ico_name_required));
        } else if (currencyIsoCode == null || currencyIsoCode.trim().equals("")) {
            // setError on spinner not possible, so let's use a fake TextView
            final TextView errorText = (TextView) icoCurrencySpinner.getSelectedView();
            errorText.setError("Anything here, just for the icon to be displayed");
            errorText.setTextColor(Color.RED);
            errorText.setText(getString(R.string.error_ico_currency_required));
        } else if (amountStr.trim().equals("")) {
            icoAmountEditText.setError(getString(R.string.error_ico_amount_required));
        } else if (amount == null) {
            icoAmountEditText.setError(getString(R.string.error_ico_amount_not_valid));
        } else if (dateStr.trim().equals("")) {
            icoDateEditText.setError(getString(R.string.error_ico_date_required));
        } else if (date == null) {
            icoDateEditText.setError(getString(R.string.error_ico_date_not_valid));
        } else if (tokenName.trim().equals("")) {
            icoTokenNameEditText.setError(getString(R.string.error_ico_token_name_required));
        } else if (tokenDateStr.trim().equals("")) {
            icoTokenDateEditText.setError(getString(R.string.error_ico_token_date_required));
        } else if (tokenDate == null) {
            icoTokenDateEditText.setError(getString(R.string.error_ico_token_date_invalid));
        } else if (tokenQuantityStr.trim().equals("")) {
            icoTokenQuantityEditText.setError(getString(R.string.error_ico_token_quantity_required));
        } else if (tokenQuantity == null) {
            icoTokenQuantityEditText.setError(getString(R.string.error_ico_token_quantity_invalid));
        } else if (feeStr.trim().equals("")) {
            icoFeeEditText.setError(getString(R.string.error_ico_fee_required));
        } else if (fee == null) {
            icoFeeEditText.setError(getString(R.string.error_ico_fee_invalid));
        } else if (bonusStr.trim().equals("")) {
            icoBonusEditText.setError(getString(R.string.error_ico_bonus_required));
        } else if (bonus == null) {
            icoBonusEditText.setError(getString(R.string.error_ico_bonus_invalid));
        } else {
            // add record to the view model who will trigger the insert
            addIcoViewModel.editIco(new Ico(id, name, currencyIsoCode, amount, fee, date, tokenName, tokenDate, tokenQuantity, bonus, comment));

            // close current activity and return to previous activity if there is any
            finish();

            // show a notification about the created item
            Toast.makeText(getApplicationContext(), getString(R.string.success_ico_updated, name), Toast.LENGTH_SHORT).show();
        }
    }

}
