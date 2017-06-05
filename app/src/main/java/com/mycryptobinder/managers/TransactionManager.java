package com.mycryptobinder.managers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.mycryptobinder.helpers.DatabaseHelper;
import com.mycryptobinder.models.Transaction;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Yann
 * Created on 03/06/2017
 */
public class TransactionManager {

    private DatabaseHelper dbHelper;
    private Context context;
    private SQLiteDatabase database;

    public TransactionManager(Context c) {
        context = c;
    }

    public TransactionManager open() throws SQLException {
        dbHelper = new DatabaseHelper(context);
        database = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    /**
     * Get the list of all transactions from the database
     *
     * @return list of Transaction elements representing the transactions
     */
    public List<Transaction> getAll() {
        List<Transaction> list = new ArrayList<>();
        Cursor cursor = fetch();
        CurrencyManager cm = new CurrencyManager(context);
        cm.open();
        ExchangeManager em = new ExchangeManager(context);
        em.open();
        try {
            while (cursor.moveToNext()) {
                Transaction trans = new Transaction();
                trans.setId(cursor.getLong(0));
                trans.setExchange(em.getById(cursor.getLong(1)));
                trans.setCurrency1(cm.getById(cursor.getLong(2)));
                trans.setCurrency2(cm.getById(cursor.getLong(3)));
                trans.setFees(cursor.getDouble(4));
                trans.setDate(new Date(cursor.getLong(5)));
                trans.setType(cursor.getString(6));
                trans.setQuantity(cursor.getDouble(7));
                trans.setPrice(cursor.getDouble(8));
                trans.setComment(cursor.getString(9));
                list.add(trans);
            }
        } finally {
            cursor.close();
        }
        return list;
    }

    /**
     * Fetch all transactions from the database into a cursor
     *
     * @return A Cursor containing all the records
     */
    private Cursor fetch() {
        String[] columns = new String[]{
                DatabaseHelper.COLUMN_ID,
                DatabaseHelper.COLUMN_EXCHANGE,
                DatabaseHelper.COLUMN_CURRENCY1,
                DatabaseHelper.COLUMN_CURRENCY2,
                DatabaseHelper.COLUMN_FEES,
                DatabaseHelper.COLUMN_DATE,
                DatabaseHelper.COLUMN_TYPE,
                DatabaseHelper.COLUMN_QUANTITY,
                DatabaseHelper.COLUMN_PRICE,
                DatabaseHelper.COLUMN_COMMENT
        };
        Cursor cursor = database.query(DatabaseHelper.TABLE_TRANSACTIONS, columns, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    /**
     * Insert a new transaction into the database
     *
     * @param exchangeId  The id of the exchange where the transaction has been done
     * @param currency1Id The id of the first currency used in the transaction
     * @param currency2Id The id of the first currency used in the transaction
     * @param fees        The transaction fees
     * @param date        The transaction date
     * @param type        The transaction type
     * @param quantity    The quantity of bought currency
     * @param price       The price of the bought currency
     * @param comment     Any optional comment
     */
    public void insert(long exchangeId, long currency1Id, long currency2Id, double fees, Date date, String type, double quantity, double price, String comment) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.COLUMN_EXCHANGE, exchangeId);
        contentValues.put(DatabaseHelper.COLUMN_CURRENCY1, currency1Id);
        contentValues.put(DatabaseHelper.COLUMN_CURRENCY2, currency2Id);
        contentValues.put(DatabaseHelper.COLUMN_FEES, fees);
        contentValues.put(DatabaseHelper.COLUMN_DATE, date.getTime());
        contentValues.put(DatabaseHelper.COLUMN_TYPE, type);
        contentValues.put(DatabaseHelper.COLUMN_QUANTITY, quantity);
        contentValues.put(DatabaseHelper.COLUMN_PRICE, price);
        contentValues.put(DatabaseHelper.COLUMN_COMMENT, comment);
        database.insert(DatabaseHelper.TABLE_TRANSACTIONS, null, contentValues);
    }

    /**
     * Update an existing transaction in the database
     *
     * @param id          The id of the currency to edit
     * @param exchangeId  The id of the exchange where the transaction has been done
     * @param currency1Id The id of the first currency used in the transaction
     * @param currency2Id The id of the first currency used in the transaction
     * @param fees        The transaction fees
     * @param date        The transaction date
     * @param type        The transaction type
     * @param quantity    The quantity of bought currency
     * @param price       The price of the bought currency
     * @param comment     Any optional comment
     * @return An integer representing the number of rows affected
     */
    public int update(long id, long exchangeId, long currency1Id, long currency2Id, double fees, Date date, String type, double quantity, double price, String comment) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.COLUMN_EXCHANGE, exchangeId);
        contentValues.put(DatabaseHelper.COLUMN_CURRENCY1, currency1Id);
        contentValues.put(DatabaseHelper.COLUMN_CURRENCY2, currency2Id);
        contentValues.put(DatabaseHelper.COLUMN_FEES, fees);
        contentValues.put(DatabaseHelper.COLUMN_DATE, date.getTime());
        contentValues.put(DatabaseHelper.COLUMN_TYPE, type);
        contentValues.put(DatabaseHelper.COLUMN_QUANTITY, quantity);
        contentValues.put(DatabaseHelper.COLUMN_PRICE, price);
        contentValues.put(DatabaseHelper.COLUMN_COMMENT, comment);
        return database.update(DatabaseHelper.TABLE_TRANSACTIONS, contentValues, DatabaseHelper.COLUMN_ID + " = " + id, null);
    }

    /**
     * Delete an existing transaction from the database
     *
     * @param id The id of the transaction to delete
     */
    public void delete(long id) {
        database.delete(DatabaseHelper.TABLE_TRANSACTIONS, DatabaseHelper.COLUMN_ID + "=" + id, null);
    }

}
