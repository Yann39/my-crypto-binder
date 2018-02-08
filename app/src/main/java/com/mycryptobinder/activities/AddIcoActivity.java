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
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.mycryptobinder.R;
import com.mycryptobinder.adapters.CurrencyAutoCompleteAdapter;
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
    private CurrencyAutoCompleteAdapter currencyAutoCompleteAdapter1;
    private SimpleDateFormat sdf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_ico);

        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // get view components
        addIcoDateEditText = findViewById(R.id.add_ico_date_editText);
        addIcoTokenDateEditText = findViewById(R.id.add_ico_token_date_editText);
        Spinner addIcoCurrencySpinner = findViewById(R.id.add_ico_currency_spinner);

        // get the view models
        addIcoViewModel = ViewModelProviders.of(this).get(AddIcoViewModel.class);
        CurrencyListViewModel currencyListViewModel = ViewModelProviders.of(this).get(CurrencyListViewModel.class);

        // initialize currencies spinner adapter
        currencyAutoCompleteAdapter1 = new CurrencyAutoCompleteAdapter(new ArrayList<>(), addIcoCurrencySpinner.getContext(), android.R.layout.simple_dropdown_item_1line);
        currencyAutoCompleteAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_item);
        addIcoCurrencySpinner.setAdapter(currencyAutoCompleteAdapter1);

        // observe the currency list from the view model so the currency spinner is always up to date
        currencyListViewModel.getCurrencyList().observe(AddIcoActivity.this, currencies -> currencyAutoCompleteAdapter1.addItems(currencies));

        // set date format that will be used for date pickers
        UtilsHelper uh = new UtilsHelper(getApplicationContext());
        sdf = new SimpleDateFormat("dd/MM/yyyy", uh.getCurrentLocale());

        // set focus listener to display a date picker on ICO date field focus
        addIcoDateEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                // get current date to initialize the date picker
                Calendar c = Calendar.getInstance();

                // open the date picker
                new DatePickerDialog(v.getContext(), (view, year, monthOfYear, dayOfMonth) -> {
                    // format the date and display it in the related text box
                    Calendar c1 = Calendar.getInstance();
                    c1.set(year, monthOfYear - 1, dayOfMonth, 0, 0);
                    addIcoDateEditText.setText(sdf.format(c1.getTime()));
                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        // set focus listener to display a date picker on token date field focus
        addIcoTokenDateEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                // get current date to initialize the date picker
                Calendar c = Calendar.getInstance();

                // open the date picker
                new DatePickerDialog(v.getContext(), (view, year, monthOfYear, dayOfMonth) -> {
                    // format the date and display it in the related text box
                    Calendar c12 = Calendar.getInstance();
                    c12.set(year, monthOfYear - 1, dayOfMonth, 0, 0);
                    addIcoTokenDateEditText.setText(sdf.format(c12.getTime()));
                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // add the save/cancel buttons to the options menu
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_ico_action_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // back arrow click
        if (id == android.R.id.home) {
            // close current activity and return to previous activity if there is any
            finish();
        }

        // save icon click
        if (id == R.id.save_add_ico) {

            // get view components
            EditText addIcoNameEditText = findViewById(R.id.add_ico_name_editText);
            EditText addIcoAmountEditText = findViewById(R.id.add_ico_amount_editText);
            Spinner addIcoCurrencySpinner = findViewById(R.id.add_ico_currency_spinner);
            EditText addIcoTokenNameEditText = findViewById(R.id.add_ico_token_name_editText);
            EditText addIcoTokenQuantityEditText = findViewById(R.id.add_ico_token_quantity_editText);
            EditText addIcoFeeEditText = findViewById(R.id.add_ico_fee_editText);
            EditText addIcoBonusEditText = findViewById(R.id.add_ico_bonus_editText);
            EditText addIcoCommentEditText = findViewById(R.id.add_ico_comment_editText);

            // get field values
            String name = addIcoNameEditText.getText().toString();
            String currencyIsoCode = currencyAutoCompleteAdapter1.getItem(addIcoCurrencySpinner.getSelectedItemPosition());
            String amountStr = addIcoAmountEditText.getText().toString();
            String dateStr = addIcoDateEditText.getText().toString();
            String tokenName = addIcoTokenNameEditText.getText().toString();
            String tokenDateStr = addIcoTokenDateEditText.getText().toString();
            String tokenQuantityStr = addIcoTokenQuantityEditText.getText().toString();
            String feeStr = addIcoFeeEditText.getText().toString();
            String bonusStr = addIcoBonusEditText.getText().toString();
            String comment = addIcoCommentEditText.getText().toString();

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
                addIcoNameEditText.setError("ICO name is required!");
            } else if (currencyIsoCode == null || currencyIsoCode.trim().equals("")) {
                //todo setError on spinner not possible
            } else if (amountStr.trim().equals("")) {
                addIcoAmountEditText.setError("Amount is required!");
            } else if (amount == null) {
                addIcoAmountEditText.setError("Amount is invalid!");
            } else if (dateStr.trim().equals("")) {
                addIcoDateEditText.setError("Date is required!");
            } else if (date == null) {
                addIcoDateEditText.setError("Date is invalid!");
            } else if (tokenName.trim().equals("")) {
                addIcoTokenNameEditText.setError("Token name is required!");
            } else if (tokenDateStr.trim().equals("")) {
                addIcoTokenDateEditText.setError("Token date is required!");
            } else if (tokenDate == null) {
                addIcoTokenDateEditText.setError("Token date is invalid!");
            } else if (tokenQuantityStr.trim().equals("")) {
                addIcoTokenQuantityEditText.setError("Token quantity is required!");
            } else if (tokenQuantity == null) {
                addIcoTokenQuantityEditText.setError("Token quantity is invalid!");
            } else if (feeStr.trim().equals("")) {
                addIcoFeeEditText.setError("Fee is required!");
            } else if (fee == null) {
                addIcoFeeEditText.setError("Fee is invalid!");
            } else if (bonusStr.trim().equals("")) {
                addIcoBonusEditText.setError("Bonus is required!");
            } else if (bonus == null) {
                addIcoBonusEditText.setError("Bonus is invalid!");
            } else {
                // add record to the view model who will trigger the insert
                addIcoViewModel.addIco(new Ico(name, currencyIsoCode, amount, fee, date, tokenName, tokenDate, tokenQuantity, bonus, comment));

                // close current activity and return to previous activity if there is any
                finish();

                // show a notification about the created item
                final View view = findViewById(R.id.save_add_ico);
                Toast.makeText(view.getContext(), view.getResources().getString(R.string.msg_ico_created, name), Toast.LENGTH_SHORT).show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

}
