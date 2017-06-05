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

    /**
     * Get a specific exchange from the database
     *
     * @param exchangeId The if of the exchange to retrieve
     * @return A Exchange element representing the exchange
     */
    public Exchange getById(long exchangeId) {
        Exchange curr = new Exchange();
        Cursor cursor = database.query(DatabaseHelper.TABLE_EXCHANGES, null, "id=?", new String[]{Long.toString(exchangeId)}, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            try {
                curr.setId(cursor.getLong(0));
                curr.setName(cursor.getString(1));
                curr.setLink(cursor.getString(2));
                curr.setDescription(cursor.getString(3));
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
        Cursor cursor = fetch();
        try {
            while (cursor.moveToNext()) {
                Exchange exc = new Exchange();
                exc.setId(cursor.getLong(0));
                exc.setName(cursor.getString(1));
                exc.setLink(cursor.getString(2));
                exc.setDescription(cursor.getString(3));
                list.add(exc);
            }
        } finally {
            cursor.close();
        }
        return list;
    }

    /**
     * Fetch all exchanges from the database into a cursor
     *
     * @return A Cursor containing all the records
     */
    private Cursor fetch() {
        String[] columns = new String[]{DatabaseHelper.COLUMN_ID, DatabaseHelper.COLUMN_NAME, DatabaseHelper.COLUMN_LINK, DatabaseHelper.COLUMN_DESCRIPTION};
        Cursor cursor = database.query(DatabaseHelper.TABLE_EXCHANGES, columns, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    /**
     * Insert a new exchange into the database
     *
     * @param name        The exchange name
     * @param link        The exchange link
     * @param description The exchange description
     */
    public void insert(String name, String link, String description) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.COLUMN_NAME, name);
        contentValues.put(DatabaseHelper.COLUMN_LINK, link);
        contentValues.put(DatabaseHelper.COLUMN_DESCRIPTION, description);
        database.insert(DatabaseHelper.TABLE_EXCHANGES, null, contentValues);
    }

    /**
     * Update an existing exchange in the database
     *
     * @param id          The id of the exchange to edit
     * @param name        The new name of the exchange
     * @param link        The new link of the exchange
     * @param description The new description of the exchange
     * @return An integer representing the number of rows affected
     */
    public int update(long id, String name, String link, String description) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.COLUMN_NAME, name);
        contentValues.put(DatabaseHelper.COLUMN_LINK, link);
        contentValues.put(DatabaseHelper.COLUMN_DESCRIPTION, description);
        return database.update(DatabaseHelper.TABLE_EXCHANGES, contentValues, DatabaseHelper.COLUMN_ID + " = " + id, null);
    }

    /**
     * Delete an existing exchange from the database
     *
     * @param id The id of the exchange to delete
     */
    public void delete(long id) {
        database.delete(DatabaseHelper.TABLE_EXCHANGES, DatabaseHelper.COLUMN_ID + "=" + id, null);
    }

}
