package com.mycryptobinder.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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
import com.mycryptobinder.helpers.UtilsHelper;
import com.mycryptobinder.managers.CurrencyManager;
import com.mycryptobinder.managers.ExchangeManager;
import com.mycryptobinder.managers.TransactionManager;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Activity responsible for new transaction creation
 * <p>
 * Created by Yann on 21/05/2017
 */

public class EditTransactionActivity extends AppCompatActivity {

    private EditText transactionQuantityEditText;
    private EditText transactionPriceEditText;
    private EditText transactionFeesEditText;
    private EditText transactionCommentEditText;
    private RadioGroup transactionTypeRadioGroup;
    private Spinner transactionExchangeSpinner;
    private EditText transactionDateEditText;
    private TransactionManager transactionManager;
    private CurrencyAutoCompleteAdapter currencyAutoCompleteAdapter1;
    private CurrencyAutoCompleteAdapter currencyAutoCompleteAdapter2;
    private ExchangeSpinnerAdapter exchangeSpinnerAdapter;
    private long transactionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(getResources().getString(R.string.title_edit_transaction));
        setContentView(R.layout.activity_add_transaction);

        // modal window full width
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        // get view components
        AutoCompleteTextView transactionCurrency1AutoCompleteText = (AutoCompleteTextView) findViewById(R.id.transaction_currency1_autoCompleteText);
        AutoCompleteTextView transactionCurrency2AutoCompleteText = (AutoCompleteTextView) findViewById(R.id.transaction_currency2_autoCompleteText);
        RadioButton transactionTypeBuyRadioButton = (RadioButton) findViewById(R.id.buy_layout_rb);
        RadioButton transactionTypeSellRadioButton = (RadioButton) findViewById(R.id.sell_layout_rb);
        transactionExchangeSpinner = (Spinner) findViewById(R.id.transaction_exchange_spinner);
        transactionDateEditText = (EditText) findViewById(R.id.transaction_date_edittext);
        transactionQuantityEditText = (EditText) findViewById(R.id.transaction_quantity_edittext);
        transactionPriceEditText = (EditText) findViewById(R.id.transaction_price_edittext);
        transactionFeesEditText = (EditText) findViewById(R.id.transaction_fees_edittext);
        transactionCommentEditText = (EditText) findViewById(R.id.transaction_comment_edittext);
        transactionTypeRadioGroup = (RadioGroup) findViewById(R.id.transaction_type_radio_group);
        Button chooseTransactionDateButton = (Button) findViewById(R.id.btn_select_transaction_date);
        Button createTransactionButton = (Button) findViewById(R.id.btn_create_transaction);
        Button editTransactionButton = (Button) findViewById(R.id.btn_update_transaction);

        // hide create button and show edit button
        createTransactionButton.setVisibility(View.INVISIBLE);
        editTransactionButton.setVisibility(View.VISIBLE);

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

        // get the intent and its data
        Intent intent = getIntent();
        transactionId = intent.getLongExtra("id", 0);
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
        transactionCommentEditText.setText(intent.getStringExtra("comment"));

        // format the date and display it in the related text box
        UtilsHelper uh = new UtilsHelper(getApplicationContext());
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", uh.getCurrentLocale());
        transactionDateEditText.setText(sdf.format((Date)intent.getSerializableExtra("date")));

        // set click listener on the Buy radio button
        transactionTypeBuyRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView transactionCurrency1TextView = (TextView) findViewById(R.id.transaction_currency1_textview);
                TextView transactionCurrency2TextView = (TextView) findViewById(R.id.transaction_currency2_textview);
                transactionCurrency1TextView.setText(v.getResources().getString(R.string.lbl_transaction_currency1_buy));
                transactionCurrency2TextView.setText(v.getResources().getString(R.string.lbl_transaction_currency2_with));
            }
        });

        // set click listener on the Sell radio button
        transactionTypeSellRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView transactionCurrency1TextView = (TextView) findViewById(R.id.transaction_currency1_textview);
                TextView transactionCurrency2TextView = (TextView) findViewById(R.id.transaction_currency2_textview);
                transactionCurrency1TextView.setText(v.getResources().getString(R.string.lbl_transaction_currency1_sell));
                transactionCurrency2TextView.setText(v.getResources().getString(R.string.lbl_transaction_currency2_for));
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

        // set click listener for the update transaction button
        editTransactionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // get fields values
                Long currency1 = currencyAutoCompleteAdapter1.getItemId(0);
                Long currency2 = currencyAutoCompleteAdapter2.getItemId(0);
                Long exchange = exchangeSpinnerAdapter.getItemId(transactionExchangeSpinner.getSelectedItemPosition());
                Double quantity = Double.parseDouble(transactionQuantityEditText.getText().toString());
                Double price = Double.parseDouble(transactionPriceEditText.getText().toString());
                Double fees = Double.parseDouble(transactionFeesEditText.getText().toString());

                UtilsHelper uh = new UtilsHelper(getApplicationContext());
                DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", uh.getCurrentLocale());
                Date date = null;
                try {
                    date = formatter.parse(transactionDateEditText.getText().toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                RadioButton radioButton = (RadioButton) findViewById(transactionTypeRadioGroup.getCheckedRadioButtonId());
                String type = radioButton.getText().toString();
                String comment = transactionCommentEditText.getText().toString();

                // insert values into the database
                transactionManager.update(transactionId, exchange, currency1, currency2, fees, date, type, quantity, price, comment);

                // update intent so all top activities are closed
                Intent main = new Intent(EditTransactionActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                setResult(Activity.RESULT_OK, main);
                finish();
                //startActivity(main);

                // show a notification about the created item
                Toast.makeText(view.getContext(), view.getResources().getString(R.string.msg_transaction_updated, currency1 + "/" + currency2), Toast.LENGTH_SHORT).show();
            }
        });
    }
}