package com.mycryptobinder.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mycryptobinder.R;
import com.mycryptobinder.adapters.CurrencyAutoCompleteAdapter;
import com.mycryptobinder.adapters.ExchangeSpinnerAdapter;
import com.mycryptobinder.helpers.UtilsHelper;
import com.mycryptobinder.managers.CurrencyManager;
import com.mycryptobinder.managers.ExchangeManager;
import com.mycryptobinder.managers.TransactionManager;
import com.mycryptobinder.models.Currency;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Activity responsible for new transaction creation
 * <p>
 * Created by Yann on 21/05/2017
 */

public class AddTransactionActivity extends AppCompatActivity {

    private EditText transactionQuantityEditText;
    private EditText transactionPriceEditText;
    private EditText transactionFeesEditText;
    private EditText transactionDateEditText;
    private EditText transactionCommentEditText;
    private RadioGroup transactionTypeRadioGroup;
    private TransactionManager transactionManager;
    private CurrencyAutoCompleteAdapter currencyAutoCompleteAdapter1;
    private CurrencyAutoCompleteAdapter currencyAutoCompleteAdapter2;
    private ExchangeSpinnerAdapter exchangeSpinnerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(getResources().getString(R.string.title_add_transaction));
        setContentView(R.layout.activity_add_transaction);

        // modal window full width
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        // get view components
        AutoCompleteTextView transactionCurrency1AutoCompleteText = (AutoCompleteTextView) findViewById(R.id.transaction_currency1_autoCompleteText);
        AutoCompleteTextView transactionCurrency2AutoCompleteText = (AutoCompleteTextView) findViewById(R.id.transaction_currency2_autoCompleteText);
        Spinner transactionExchangeSpinner = (Spinner) findViewById(R.id.transaction_exchange_spinner);
        RadioButton transactionTypeBuyRadioButton = (RadioButton) findViewById(R.id.buy_layout_rb);
        RadioButton transactionTypeSellRadioButton = (RadioButton) findViewById(R.id.sell_layout_rb);
        transactionQuantityEditText = (EditText) findViewById(R.id.transaction_quantity_edittext);
        transactionPriceEditText = (EditText) findViewById(R.id.transaction_price_edittext);
        transactionFeesEditText = (EditText) findViewById(R.id.transaction_fees_edittext);
        transactionDateEditText = (EditText) findViewById(R.id.transaction_date_edittext);
        transactionCommentEditText = (EditText) findViewById(R.id.transaction_comment_edittext);
        transactionTypeRadioGroup = (RadioGroup) findViewById(R.id.transaction_type_radio_group);
        Button createTransactionButton = (Button) findViewById(R.id.btn_create_transaction);
        Button chooseTransactionDateButton = (Button) findViewById(R.id.btn_select_transaction_date);

        // open database connections
        CurrencyManager currencyManager = new CurrencyManager(this);
        ExchangeManager exchangeManager = new ExchangeManager(this);
        transactionManager = new TransactionManager(this);
        currencyManager.open();
        exchangeManager.open();
        transactionManager.open();

        // apply adapters
        currencyAutoCompleteAdapter1 = new CurrencyAutoCompleteAdapter(transactionCurrency1AutoCompleteText.getContext(), android.R.layout.simple_dropdown_item_1line, currencyManager.getAll());
        transactionCurrency1AutoCompleteText.setAdapter(currencyAutoCompleteAdapter1);
        currencyAutoCompleteAdapter2 = new CurrencyAutoCompleteAdapter(transactionCurrency2AutoCompleteText.getContext(), android.R.layout.simple_spinner_dropdown_item, currencyManager.getAll());
        transactionCurrency2AutoCompleteText.setAdapter(currencyAutoCompleteAdapter2);
        exchangeSpinnerAdapter = new ExchangeSpinnerAdapter(transactionExchangeSpinner.getContext(), android.R.layout.simple_dropdown_item_1line, exchangeManager.getAll());
        transactionExchangeSpinner.setAdapter(exchangeSpinnerAdapter);

        // set click listener on the Buy radio button
        transactionTypeBuyRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView transactionTypeTextView = (TextView) findViewById(R.id.transaction_type_textview);
                transactionTypeTextView.setText(v.getResources().getString(R.string.lbl_transaction_type_buy));
            }
        });

        // set click listener on the Sell radio button
        transactionTypeSellRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView transactionTypeTextView = (TextView) findViewById(R.id.transaction_type_textview);
                transactionTypeTextView.setText(v.getResources().getString(R.string.lbl_transaction_type_sell));
            }
        });

        // set click listener for date picker button
        chooseTransactionDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // get current date to initialize the date picker
                Calendar c = Calendar.getInstance();

                // open the date picker
                DatePickerDialog datePickerDialog = new DatePickerDialog(view.getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // format the date and display it in the related text box
                        UtilsHelper uh = new UtilsHelper(getApplicationContext());
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", uh.getCurrentLocale());
                        Calendar c = Calendar.getInstance();
                        c.set(year, monthOfYear - 1, dayOfMonth, 0, 0);
                        transactionDateEditText.setText(sdf.format(c.getTime()));
                    }
                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });

        // set click listener for the create exchange button
        createTransactionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // get fields values
                Long currency1 = currencyAutoCompleteAdapter1.getItemId(0);
                Long currency2 = currencyAutoCompleteAdapter2.getItemId(0);
                Long exchange = exchangeSpinnerAdapter.getItemId(0);
                Double quantity = Double.parseDouble(transactionQuantityEditText.getText().toString());
                Double price = Double.parseDouble(transactionPriceEditText.getText().toString());
                Double fees = Double.parseDouble(transactionFeesEditText.getText().toString());
                Date date = Date.valueOf(transactionDateEditText.getText().toString());
                RadioButton radioButton = (RadioButton) findViewById(transactionTypeRadioGroup.getCheckedRadioButtonId());
                String type = radioButton.getText().toString();
                String comment = transactionCommentEditText.getText().toString();

                // insert values into the database
                transactionManager.insert(exchange, currency1, currency2, fees, date, type, quantity, price, comment);

                // update intent so all top activities are closed
                Intent main = new Intent(AddTransactionActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(main);

                // show a notification about the created item
                Toast.makeText(view.getContext(), view.getResources().getString(R.string.msg_transaction_created, currency1 + "/" + currency2), Toast.LENGTH_SHORT).show();
            }
        });
    }
}