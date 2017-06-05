package com.mycryptobinder.managers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.mycryptobinder.helpers.DatabaseHelper;
import com.mycryptobinder.models.Currency;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yann
 * Created on 21/05/2017
 */
public class CurrencyManager {

    private DatabaseHelper dbHelper;
    private Context context;
    private SQLiteDatabase database;

    public CurrencyManager(Context c) {
        context = c;
    }

    public CurrencyManager open() throws SQLException {
        dbHelper = new DatabaseHelper(context);
        database = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    /**
     * Get a specific currency from the database given its id
     *
     * @param currencyId The if of the currency to retrieve
     * @return A Currency element representing the currency
     */
    public Currency getById(long currencyId) {
        Currency curr = new Currency();
        Cursor cursor = database.query(DatabaseHelper.TABLE_CURRENCIES, null, "id=?", new String[]{Long.toString(currencyId)}, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            try {
                curr.setId(cursor.getLong(0));
                curr.setName(cursor.getString(1));
                curr.setIsoCode(cursor.getString(2));
                curr.setSymbol(cursor.getString(3));
            } finally {
                cursor.close();
            }
        }
        return curr;
    }

    /**
     * Get a specific currency from the database
     *
     * @param currencyName The if of the currency to retrieve
     * @return A Currency element representing the currency
     */
    public Currency getByName(String currencyName) {
        Currency curr = new Currency();
        Cursor cursor = database.query(DatabaseHelper.TABLE_CURRENCIES, null, "name like %?%", new String[]{ currencyName }, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            try {
                curr.setId(cursor.getLong(0));
                curr.setName(cursor.getString(1));
                curr.setIsoCode(cursor.getString(2));
                curr.setSymbol(cursor.getString(3));
            } finally {
                cursor.close();
            }
        }
        return curr;
    }

    /**
     * Get the list of all currencies from the database
     *
     * @return list of Currency elements representing the currencies
     */
    public List<Currency> getAll() {
        List<Currency> list = new ArrayList<>();
        String[] columns = new String[]{DatabaseHelper.COLUMN_ID, DatabaseHelper.COLUMN_NAME, DatabaseHelper.COLUMN_ISO_CODE, DatabaseHelper.COLUMN_SYMBOL};
        Cursor cursor = database.query(DatabaseHelper.TABLE_CURRENCIES, columns, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            try {
                while (cursor.moveToNext()) {
                    Currency curr = new Currency();
                    curr.setId(cursor.getLong(0));
                    curr.setName(cursor.getString(1));
                    curr.setIsoCode(cursor.getString(2));
                    curr.setSymbol(cursor.getString(3));
                    list.add(curr);
                }
            } finally {
                cursor.close();
            }
        }
        return list;
    }

    /**
     * Insert a new currency into the database
     *
     * @param name     The currency name (ex: Euro)
     * @param iso_code The currency ISO code (ex: EUR)
     * @param symbol   The currency symbol (ex: €)
     */
    public void insert(String name, String iso_code, String symbol) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.COLUMN_NAME, name);
        contentValues.put(DatabaseHelper.COLUMN_ISO_CODE, iso_code);
        contentValues.put(DatabaseHelper.COLUMN_SYMBOL, symbol);
        database.insert(DatabaseHelper.TABLE_CURRENCIES, null, contentValues);
    }

    /**
     * Update an existing currency in the database
     *
     * @param id       The id of the currency to edit
     * @param name     The new name of the currency (ex: Euro)
     * @param iso_code The new ISO code of the currency (ex: EUR)
     * @param symbol   The new symbol of the currency (ex: €)
     * @return An integer representing the number of rows affected
     */
    public int update(long id, String name, String iso_code, String symbol) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.COLUMN_NAME, name);
        contentValues.put(DatabaseHelper.COLUMN_ISO_CODE, iso_code);
        contentValues.put(DatabaseHelper.COLUMN_SYMBOL, symbol);
        return database.update(DatabaseHelper.TABLE_CURRENCIES, contentValues, DatabaseHelper.COLUMN_ID + " = " + id, null);
    }

    /**
     * Delete an existing currency from the database
     *
     * @param id The id of the currency to delete
     */
    public void delete(long id) {
        database.delete(DatabaseHelper.TABLE_CURRENCIES, DatabaseHelper.COLUMN_ID + "=" + id, null);
    }

}
