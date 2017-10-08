package com.mycryptobinder.managers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.mycryptobinder.helpers.DatabaseHelper;
import com.mycryptobinder.models.Exchange;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yann
 * Created on 21/05/2017
 */
public class ExchangeManager {

    private DatabaseHelper dbHelper;
    private Context context;
    private SQLiteDatabase database;

    public ExchangeManager(Context c) {
        context = c;
    }

    public ExchangeManager open() throws SQLException {
        dbHelper = new DatabaseHelper(context);
        database = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    //region Read

    /**
     * Get a specific exchange from the database
     *
     * @param exchangeName The name of the exchange to retrieve
     * @return An Exchange element representing the exchange
     */
    Exchange getByName(String exchangeName) {
        Exchange curr = new Exchange();
        Cursor cursor = database.query(DatabaseHelper.TABLE_EXCHANGES, null, DatabaseHelper.COLUMN_NAME + "=?", new String[]{exchangeName}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            try {
                curr.setName(cursor.getString(0));
                curr.setLink(cursor.getString(1));
                curr.setDescription(cursor.getString(2));
            } finally {
                cursor.close();
            }
        }
        return curr;
    }

    /**
     * Get the list of all exchanges from the database
     *
     * @return list of Exchange elements representing the exchanges
     */
    public List<Exchange> getAll() {
        List<Exchange> list = new ArrayList<>();
        String[] columns = new String[]{DatabaseHelper.COLUMN_NAME, DatabaseHelper.COLUMN_LINK, DatabaseHelper.COLUMN_DESCRIPTION};
        Cursor cursor = database.query(DatabaseHelper.TABLE_EXCHANGES, columns, null, null, null, null, null);
        if (cursor != null) {
            try {
                while (cursor.moveToNext()) {
                    Exchange exc = new Exchange();
                    exc.setName(cursor.getString(0));
                    exc.setLink(cursor.getString(1));
                    exc.setDescription(cursor.getString(2));
                    list.add(exc);
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
     * Insert a new exchange into the database
     *
     * @param name        The exchange name
     * @param link        The exchange link
     * @param description The exchange description
     */
    public long insert(String name, String link, String description) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.COLUMN_NAME, name);
        contentValues.put(DatabaseHelper.COLUMN_LINK, link);
        contentValues.put(DatabaseHelper.COLUMN_DESCRIPTION, description);
        return database.insert(DatabaseHelper.TABLE_EXCHANGES, null, contentValues);
    }

    /**
     * Update an existing exchange in the database
     *
     * @param name        The name of the exchange tro edit
     * @param link        The new link of the exchange
     * @param description The new description of the exchange
     * @return An integer representing the number of rows affected
     */
    public int update(String name, String link, String description) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.COLUMN_NAME, name);
        contentValues.put(DatabaseHelper.COLUMN_LINK, link);
        contentValues.put(DatabaseHelper.COLUMN_DESCRIPTION, description);
        return database.update(DatabaseHelper.TABLE_EXCHANGES, contentValues, DatabaseHelper.COLUMN_NAME + " = " + name, null);
    }

    /**
     * Delete an existing exchange from the database
     *
     * @param name The name of the exchange to delete
     */
    public void delete(String name) {
        database.delete(DatabaseHelper.TABLE_EXCHANGES, DatabaseHelper.COLUMN_NAME + "=" + name, null);
    }
    //endregion

    /**
     * Delete all data from the EXCHANGES table and reset the sequence
     */
    public void reset() {
        database.execSQL("DELETE FROM " + DatabaseHelper.TABLE_EXCHANGES);
        database.execSQL("DELETE FROM sqlite_sequence WHERE name = '" + DatabaseHelper.TABLE_EXCHANGES + "'");
    }

}