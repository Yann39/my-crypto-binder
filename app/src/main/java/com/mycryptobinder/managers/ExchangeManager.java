package com.mycryptobinder.managers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.mycryptobinder.helpers.DatabaseHelper;

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

    public Cursor fetch() {
        String[] columns = new String[] { DatabaseHelper.COLUMN_ID, DatabaseHelper.COLUMN_NAME, DatabaseHelper.COLUMN_LINK, DatabaseHelper.COLUMN_DESCRIPTION };
        Cursor cursor = database.query(DatabaseHelper.TABLE_EXCHANGES, columns, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public void insert(String name, String link, String description) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.COLUMN_NAME, name);
        contentValues.put(DatabaseHelper.COLUMN_LINK, link);
        contentValues.put(DatabaseHelper.COLUMN_DESCRIPTION, description);
        database.insert(DatabaseHelper.TABLE_EXCHANGES, null, contentValues);
    }

    public int update(long id, String name, String link, String description) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.COLUMN_NAME, name);
        contentValues.put(DatabaseHelper.COLUMN_LINK, link);
        contentValues.put(DatabaseHelper.COLUMN_DESCRIPTION, description);
        return database.update(DatabaseHelper.TABLE_EXCHANGES, contentValues, DatabaseHelper.COLUMN_ID + " = " + id, null);
    }

    public void delete(long id) {
        database.delete(DatabaseHelper.TABLE_CURRENCIES, DatabaseHelper.COLUMN_ID + "=" + id, null);
    }

}
