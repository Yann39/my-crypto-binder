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

    public List<Currency> getAll() {
        List<Currency> list = new ArrayList<>();

        Cursor cursor = fetch();
        try {
            while (cursor.moveToNext()) {
                Currency curr = new Currency();
                curr.setName(cursor.getString(1));
                curr.setIsoCode(cursor.getString(2));
                curr.setSymbol(cursor.getString(3));
                list.add(curr);
            }
        } finally {
            cursor.close();
        }
        return list;
    }

    public Cursor fetch() {
        String[] columns = new String[] { DatabaseHelper.COLUMN_ID, DatabaseHelper.COLUMN_NAME, DatabaseHelper.COLUMN_ISO_CODE, DatabaseHelper.COLUMN_SYMBOL };
        Cursor cursor = database.query(DatabaseHelper.TABLE_CURRENCIES, columns, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public void insert(String name, String iso_code, String symbol) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.COLUMN_NAME, name);
        contentValues.put(DatabaseHelper.COLUMN_ISO_CODE, iso_code);
        contentValues.put(DatabaseHelper.COLUMN_SYMBOL, symbol);
        database.insert(DatabaseHelper.TABLE_CURRENCIES, null, contentValues);
    }

    public int update(long id, String name, String iso_code, String symbol) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.COLUMN_NAME, name);
        contentValues.put(DatabaseHelper.COLUMN_ISO_CODE, iso_code);
        contentValues.put(DatabaseHelper.COLUMN_SYMBOL, symbol);
        return database.update(DatabaseHelper.TABLE_CURRENCIES, contentValues, DatabaseHelper.COLUMN_ID + " = " + id, null);
    }

    public void delete(long id) {
        database.delete(DatabaseHelper.TABLE_CURRENCIES, DatabaseHelper.COLUMN_ID + "=" + id, null);
    }

}
