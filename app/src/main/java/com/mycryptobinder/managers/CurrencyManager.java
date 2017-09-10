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
import java.util.logging.Logger;

/**
 * Created by Yann
 * Created on 21/05/2017
 */
public class CurrencyManager {

    private DatabaseHelper dbHelper;
    private Context context;
    private SQLiteDatabase database;
    private static final Logger logger = Logger.getLogger(CurrencyManager.class.getName());

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

    //region Read

    /**
     * Get a specific currency from the database given its ISO code
     *
     * @param isoCode The ISO code of the currency to retrieve
     * @return A Currency element representing the currency
     */
    Currency getByIsoCode(String isoCode) {
        Currency curr = new Currency();
        Cursor cursor = database.query(DatabaseHelper.TABLE_CURRENCIES, null, DatabaseHelper.COLUMN_ISO_CODE + "=?", new String[]{isoCode}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            try {
                curr.setIsoCode(cursor.getString(0));
                curr.setName(cursor.getString(1));
                curr.setSymbol(cursor.getString(2));
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
        String[] columns = new String[]{DatabaseHelper.COLUMN_ISO_CODE, DatabaseHelper.COLUMN_NAME, DatabaseHelper.COLUMN_SYMBOL};
        Cursor cursor = database.query(DatabaseHelper.TABLE_CURRENCIES, columns, null, null, null, null, null);
        if (cursor != null) {
            try {
                while (cursor.moveToNext()) {
                    Currency curr = new Currency();
                    curr.setIsoCode(cursor.getString(0));
                    curr.setName(cursor.getString(1));
                    curr.setSymbol(cursor.getString(2));
                    list.add(curr);
                }
            } finally {
                cursor.close();
            }
        }
        return list;
    }
    //endregion

    //region Create Update Delete

    /**
     * Insert a new currency into the database
     *
     * @param isoCode The currency ISO code (ex: EUR)
     * @param name    The currency name (ex: Euro)
     * @param symbol  The currency symbol (ex: €)
     */
    public void insert(String isoCode, String name, String symbol) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.COLUMN_ISO_CODE, isoCode);
        contentValues.put(DatabaseHelper.COLUMN_NAME, name);
        contentValues.put(DatabaseHelper.COLUMN_SYMBOL, symbol);
        database.insert(DatabaseHelper.TABLE_CURRENCIES, null, contentValues);
    }

    /**
     * Update an existing currency in the database
     *
     * @param isoCode The ISO code of the currency to edit (ex: EUR)
     * @param name    The new name of the currency (ex: Euro)
     * @param symbol  The new symbol of the currency (ex: €)
     * @return An integer representing the number of rows affected
     */
    public int update(String isoCode, String name, String symbol) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.COLUMN_ISO_CODE, isoCode);
        contentValues.put(DatabaseHelper.COLUMN_NAME, name);
        contentValues.put(DatabaseHelper.COLUMN_SYMBOL, symbol);
        return database.update(DatabaseHelper.TABLE_CURRENCIES, contentValues, DatabaseHelper.COLUMN_ISO_CODE + " = " + isoCode, null);
    }

    /**
     * Delete an existing currency from the database
     *
     * @param isoCode The ISO code of the currency to delete
     */
    public void delete(String isoCode) {
        database.delete(DatabaseHelper.TABLE_CURRENCIES, DatabaseHelper.COLUMN_ISO_CODE + "=" + isoCode, null);
    }
    //endregion

    public void populateCurrencies() {
        String req = "INSERT INTO " + DatabaseHelper.TABLE_CURRENCIES + "(" +
                DatabaseHelper.COLUMN_ISO_CODE + ", " +
                DatabaseHelper.COLUMN_NAME + ", " +
                DatabaseHelper.COLUMN_SYMBOL + ") " +
                "SELECT " +
                DatabaseHelper.COLUMN_POLONIEX_ASSET_CODE + ", " +
                DatabaseHelper.COLUMN_POLONIEX_ASSET_NAME + ", " +
                "null " +
                "FROM " + DatabaseHelper.TABLE_POLONIEX_ASSETS + " " +
                "WHERE " + DatabaseHelper.COLUMN_POLONIEX_ASSET_CODE + " NOT IN (" +
                "SELECT " + DatabaseHelper.COLUMN_ISO_CODE + " " +
                "FROM " + DatabaseHelper.TABLE_CURRENCIES + ")";
        database.execSQL(req);

        req = "INSERT INTO " + DatabaseHelper.TABLE_CURRENCIES + "(" +
                DatabaseHelper.COLUMN_ISO_CODE + ", " +
                DatabaseHelper.COLUMN_NAME + ", " +
                DatabaseHelper.COLUMN_SYMBOL + ") " +
                "SELECT " +
                DatabaseHelper.COLUMN_KRAKEN_ALTNAME + ", " +
                "null, " +
                "null " +
                "FROM " + DatabaseHelper.TABLE_KRAKEN_ASSETS + " " +
                "WHERE " + DatabaseHelper.COLUMN_KRAKEN_ALTNAME + " NOT IN (" +
                "SELECT " + DatabaseHelper.COLUMN_ISO_CODE + " " +
                "FROM " + DatabaseHelper.TABLE_CURRENCIES + ")";
        database.execSQL(req);

        List<Currency> cur = getAll();
        for (Currency t : cur) {
            logger.info("==================> " + t.getIsoCode() + " " + t.getName() + " " + t.getSymbol());
        }
    }

}