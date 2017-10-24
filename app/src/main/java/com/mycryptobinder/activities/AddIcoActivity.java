package com.mycryptobinder.activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.mycryptobinder.R;
import com.mycryptobinder.adapters.CurrencyAutoCompleteAdapter;
import com.mycryptobinder.helpers.UtilsHelper;
import com.mycryptobinder.managers.CurrencyManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Activity responsible for new ICO creation
 * <p>
 * Created by Yann on 07/10/2017
 */

public class AddIcoActivity extends AppCompatActivity {

    private EditText addIcoTokenDateEditText;
    private EditText addIcoDateEditText;

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
        addIcoTokenDateEditText = (EditText) findViewById(R.id.add_ico_token_date_editText);
        addIcoDateEditText = (EditText) findViewById(R.id.add_ico_date_editText);
        Spinner addIcoCurrencySpinner = (Spinner) findViewById(R.id.add_ico_currency_spinner);

        // populate the currency spinner with the currency list from database
        CurrencyManager currencyManager = new CurrencyManager(this);
        currencyManager.open();
        CurrencyAutoCompleteAdapter currencyAutoCompleteAdapter1 = new CurrencyAutoCompleteAdapter(addIcoCurrencySpinner.getContext(), android.R.layout.simple_dropdown_item_1line, currencyManager.getAll());
        currencyAutoCompleteAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_item);
        addIcoCurrencySpinner.setAdapter(currencyAutoCompleteAdapter1);
        currencyManager.close();

        // set focus listener to display a date picker on token date field focus
        addIcoTokenDateEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // get current date to initialize the date picker
                    Calendar c = Calendar.getInstance();

                    // open the date picker
                    DatePickerDialog datePickerDialog = new DatePickerDialog(v.getContext(), new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            // format the date and display it in the related text box
                            UtilsHelper uh = new UtilsHelper(getApplicationContext());
                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", uh.getCurrentLocale());
                            Calendar c = Calendar.getInstance();
                            c.set(year, monthOfYear - 1, dayOfMonth, 0, 0);
                            addIcoTokenDateEditText.setText(sdf.format(c.getTime()));
                        }
                    }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
                    datePickerDialog.show();
                }
            }
        });

        // set focus listener to display a date picker on ICO date field focus
        addIcoDateEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // get current date to initialize the date picker
                    Calendar c = Calendar.getInstance();

                    // open the date picker
                    DatePickerDialog datePickerDialog = new DatePickerDialog(v.getContext(), new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            // format the date and display it in the related text box
                            UtilsHelper uh = new UtilsHelper(getApplicationContext());
                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", uh.getCurrentLocale());
                            Calendar c = Calendar.getInstance();
                            c.set(year, monthOfYear - 1, dayOfMonth, 0, 0);
                            addIcoDateEditText.setText(sdf.format(c.getTime()));
                        }
                    }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
                    datePickerDialog.show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_ico_action_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // handle back arrow click (close this activity and return to previous activity if there is any)
        if (id == android.R.id.home) {
            finish();
        }

        if (id == R.id.save_add_ico) {
            // do something here
        }
        return super.onOptionsItemSelected(item);
    }

}
