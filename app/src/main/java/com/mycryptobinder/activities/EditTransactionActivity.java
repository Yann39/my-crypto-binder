package com.mycryptobinder.activities;

import android.app.DatePickerDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mycryptobinder.R;
import com.mycryptobinder.adapters.CurrencyAutoCompleteAdapter;
import com.mycryptobinder.adapters.ExchangeSpinnerAdapter;
import com.mycryptobinder.entities.Currency;
import com.mycryptobinder.entities.Exchange;
import com.mycryptobinder.entities.Transaction;
import com.mycryptobinder.helpers.UtilsHelper;
import com.mycryptobinder.managers.CurrencyManager;
import com.mycryptobinder.managers.ExchangeManager;
import com.mycryptobinder.managers.TransactionManager;
import com.mycryptobinder.viewmodels.AddTransactionViewModel;
import com.mycryptobinder.viewmodels.CurrencyListViewModel;
import com.mycryptobinder.viewmodels.ExchangeListViewModel;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Activity responsible for new transaction creation
 * <p>
 * Created by Yann on 21/05/2017
 */

public class EditTransactionActivity extends AppCompatActivity {

    private AddTransactionViewModel addTransactionViewModel;
    private Spinner transactionExchangeSpinner;
    private EditText transactionDateEditText;
    private AutoCompleteTextView transactionCurrency1AutoCompleteText;
    private AutoCompleteTextView transactionCurrency2AutoCompleteText;
    private CurrencyAutoCompleteAdapter currencyAutoCompleteAdapter1;
    private CurrencyAutoCompleteAdapter currencyAutoCompleteAdapter2;
    private ExchangeSpinnerAdapter exchangeSpinnerAdapter;
    private SimpleDateFormat sdf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_edit_transaction);

        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // get view components
        transactionCurrency1AutoCompleteText = findViewById(R.id.transaction_currency1_autoCompleteText);
        transactionCurrency2AutoCompleteText = findViewById(R.id.transaction_currency2_autoCompleteText);
        RadioButton transactionTypeBuyRadioButton = findViewById(R.id.buy_layout_rb);
        RadioButton transactionTypeSellRadioButton = findViewById(R.id.sell_layout_rb);
        transactionExchangeSpinner = findViewById(R.id.transaction_exchange_spinner);
        transactionDateEditText = findViewById(R.id.transaction_date_edittext);
        EditText transactionQuantityEditText = findViewById(R.id.transaction_quantity_edittext);
        EditText transactionPriceEditText = findViewById(R.id.transaction_price_edittext);
        EditText transactionFeesEditText = findViewById(R.id.transaction_fee_edittext);
        EditText transactionTotalEditText = findViewById(R.id.transaction_total_edittext);
        EditText transactionCommentEditText = findViewById(R.id.transaction_comment_edittext);
        EditText transactionTxIdEditText = findViewById(R.id.transaction_txid_edittext);
        Button createTransactionButton = findViewById(R.id.btn_create_transaction);
        Button editTransactionButton = findViewById(R.id.btn_update_transaction);

        // hide create button and show edit button
        createTransactionButton.setVisibility(View.INVISIBLE);
        editTransactionButton.setVisibility(View.VISIBLE);

        // get the view models
        addTransactionViewModel = ViewModelProviders.of(this).get(AddTransactionViewModel.class);
        CurrencyListViewModel currencyListViewModel = ViewModelProviders.of(this).get(CurrencyListViewModel.class);
        ExchangeListViewModel exchangeListViewModel = ViewModelProviders.of(this).get(ExchangeListViewModel.class);

        // initialize currencies auto complete adapters
        currencyAutoCompleteAdapter1 = new CurrencyAutoCompleteAdapter(new ArrayList<Currency>(), transactionCurrency1AutoCompleteText.getContext(), android.R.layout.simple_dropdown_item_1line);
        transactionCurrency1AutoCompleteText.setAdapter(currencyAutoCompleteAdapter1);
        currencyAutoCompleteAdapter2 = new CurrencyAutoCompleteAdapter(new ArrayList<Currency>(), transactionCurrency2AutoCompleteText.getContext(), android.R.layout.simple_dropdown_item_1line);
        transactionCurrency2AutoCompleteText.setAdapter(currencyAutoCompleteAdapter2);

        // observe the currency list from the view model so the auto complete texts will always be up to date
        currencyListViewModel.getCurrencyList().observe(EditTransactionActivity.this, new Observer<List<Currency>>() {
            @Override
            public void onChanged(@Nullable List<Currency> currencies) {
                currencyAutoCompleteAdapter1.addItems(currencies);
                currencyAutoCompleteAdapter2.addItems(currencies);
            }
        });

        // initialize exchanges spinner adapter
        exchangeSpinnerAdapter = new ExchangeSpinnerAdapter(new ArrayList<Exchange>(), transactionExchangeSpinner.getContext(), android.R.layout.simple_dropdown_item_1line);
        transactionExchangeSpinner.setAdapter(exchangeSpinnerAdapter);

        // observe the exchange list from the view model so the auto complete text will always be up to date
        exchangeListViewModel.getExchangeList().observe(EditTransactionActivity.this, new Observer<List<Exchange>>() {
            @Override
            public void onChanged(@Nullable List<Exchange> exchanges) {
                exchangeSpinnerAdapter.addItems(exchanges);
            }
        });

        // set date format that will be used for date pickers
        UtilsHelper uh = new UtilsHelper(getApplicationContext());
        sdf = new SimpleDateFormat("dd/MM/yyyy", uh.getCurrentLocale());

        // get the intent data and set field values
        Intent intent = getIntent();
        String name = intent.getStringExtra("type");
        if ("buy".equals(name.toLowerCase())) {
            transactionTypeBuyRadioButton.setChecked(true);
        }
        if ("sell".equals(name.toLowerCase())) {
            transactionTypeSellRadioButton.setChecked(true);
        }
        transactionCurrency1AutoCompleteText.setText(intent.getStringExtra("currency1"));
        transactionCurrency2AutoCompleteText.setText(intent.getStringExtra("currency2"));
        transactionExchangeSpinner.setSelection(exchangeSpinnerAdapter.getPosition(intent.getStringExtra("exchange")));
        transactionQuantityEditText.setText(String.valueOf(intent.getDoubleExtra("quantity", 0.0)));
        transactionPriceEditText.setText(String.valueOf(intent.getDoubleExtra("price", 0.0)));
        transactionFeesEditText.setText(String.valueOf(intent.getDoubleExtra("fees", 0.0)));
        transactionTotalEditText.setText(String.valueOf(intent.getDoubleExtra("total", 0.0)));
        transactionTxIdEditText.setText(intent.getStringExtra("txid"));
        transactionCommentEditText.setText(intent.getStringExtra("comment"));
        transactionDateEditText.setText(sdf.format((Date)intent.getSerializableExtra("date")));

        // set click listener on the Buy radio button
        transactionTypeBuyRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView transactionCurrency1TextView = findViewById(R.id.transaction_currency1_textview);
                TextView transactionCurrency2TextView = findViewById(R.id.transaction_currency2_textview);
                transactionCurrency1TextView.setText(v.getResources().getString(R.string.lbl_transaction_currency1_buy));
                transactionCurrency2TextView.setText(v.getResources().getString(R.string.lbl_transaction_currency2_with));
            }
        });

        // set click listener on the Sell radio button
        transactionTypeSellRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView transactionCurrency1TextView = findViewById(R.id.transaction_currency1_textview);
                TextView transactionCurrency2TextView = findViewById(R.id.transaction_currency2_textview);
                transactionCurrency1TextView.setText(v.getResources().getString(R.string.lbl_transaction_currency1_sell));
                transactionCurrency2TextView.setText(v.getResources().getString(R.string.lbl_transaction_currency2_for));
            }
        });

        // set focus listener to display a date picker on transaction date field focus
        transactionDateEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // get current date to initialize the date picker
                    Calendar c = Calendar.getInstance();

                    // open the date picker
                    new DatePickerDialog(v.getContext(), new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            // format the date and display it in the related text box
                            Calendar c = Calendar.getInstance();
                            c.set(year, monthOfYear - 1, dayOfMonth, 0, 0);
                            transactionDateEditText.setText(sdf.format(c.getTime()));
                        }
                    }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
                }
            }
        });

        // set click listener for the create transaction button
        createTransactionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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
                    transactionCurrency1AutoCompleteText.setError("Currency1 is required!");
                } else if (currency2 == null || currency2.trim().equals("")) {
                    transactionCurrency2AutoCompleteText.setError("Currency2 is required!");
                } else if (exchange == null || exchange.trim().equals("")) {
                    //todo setError on spinner not possible
                } else if (txId.trim().equals("")) {
                    transactionTxIdEditText.setError("Transaction ID is required!");
                } else if (quantityStr.trim().equals("")) {
                    transactionQuantityEditText.setError("Quantity is required!");
                } else if (quantity == null) {
                    transactionQuantityEditText.setError("Quantity is invalid!");
                } else if (priceStr.trim().equals("")) {
                    transactionPriceEditText.setError("Price is required!");
                } else if (price == null) {
                    transactionPriceEditText.setError("Price is invalid!");
                } else if (feeStr.trim().equals("")) {
                    transactionFeeEditText.setError("Fee is required!");
                } else if (fee == null) {
                    transactionFeeEditText.setError("Fee is invalid!");
                } else if (totalStr.trim().equals("")) {
                    transactionTotalEditText.setError("Total is required!");
                } else if (total == null) {
                    transactionTotalEditText.setError("Total is invalid!");
                } else if (type.trim().equals("")) {
                    radioButton.setError("Type is required!");
                } else {

                    // calculate the totals
                    // todo totals for all next transactions should be recomputed...
                    double totalCurrency1 = addTransactionViewModel.getCurrencyTotal(currency1) + quantity;
                    double totalCurrency2 = addTransactionViewModel.getCurrencyTotal(currency1) + quantity;

                    // add record to the view model who will trigger the insert
                    addTransactionViewModel.updateTransaction(new Transaction(exchange, txId, currency1, currency2, fee, date, type, quantity, price, total, totalCurrency1, totalCurrency2, comment));

                    // close current activity and return to previous activity if there is any
                    finish();

                    // show a notification about the created item
                    Toast.makeText(view.getContext(), view.getResources().getString(R.string.msg_transaction_updated, currency1 + "/" + currency2), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // handle back arrow click (close this activity and return to previous activity if there is any)
        if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}