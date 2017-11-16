package com.mycryptobinder.managers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.mycryptobinder.helpers.DatabaseHelper;
import com.mycryptobinder.models.Ico;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by Yann
 * Created on 26/10/2017
 */
public class IcoManager {

    private DatabaseHelper dbHelper;
    private Context context;
    private SQLiteDatabase database;
    private static final Logger logger = Logger.getLogger(IcoManager.class.getName());

    public IcoManager(Context c) {
        context = c;
    }

    public IcoManager open() throws SQLException {
        dbHelper = new DatabaseHelper(context);
        database = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    //region Read

    /**
     * Get the list of all ICO investments from the database
     *
     * @return list of Ico elements representing the ICO investments
     */
    public List<Ico> getAll() {
        List<Ico> list = new ArrayList<>();
        String[] columns = new String[]{
                DatabaseHelper.COLUMN_ICO_NAME,
                DatabaseHelper.COLUMN_ICO_CURRENCY,
                DatabaseHelper.COLUMN_ICO_AMOUNT,
                DatabaseHelper.COLUMN_ICO_FEES,
                DatabaseHelper.COLUMN_ICO_INVEST_DATE,
                DatabaseHelper.COLUMN_ICO_TOKEN,
                DatabaseHelper.COLUMN_ICO_TOKEN_DATE,
                DatabaseHelper.COLUMN_ICO_TOKEN_QUANTITY,
                DatabaseHelper.COLUMN_ICO_BONUS,
                DatabaseHelper.COLUMN_ICO_COMMENT
        };
        Cursor cursor = database.query(DatabaseHelper.TABLE_ICO_INVESTMENTS, columns, null, null, null, null, null);
        if (cursor != null) {
            try {
                while (cursor.moveToNext()) {
                    Ico ico = new Ico();
                    ico.setName(cursor.getString(0));
                    ico.setCurrency(cursor.getString(1));
                    ico.setAmount(cursor.getDouble(2));
                    ico.setFees(cursor.getDouble(3));
                    ico.setInvest_date(new Date(cursor.getLong(4)));
                    ico.setToken(cursor.getString(5));
                    ico.setToken_date(new Date(cursor.getLong(6)));
                    ico.setToken_quantity(cursor.getDouble(7));
                    ico.setBonus(cursor.getDouble(8));
                    ico.setComment(cursor.getString(9));
                    list.add(ico);
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
     * Insert a new ICO investment into the database
     *
     * @param name          The ICO project name
     * @param currency      The currency used to invest
     * @param amount        The total amount invested
     * @param fees          The total fees if any
     * @param investDate    The investment date
     * @param token         The token name
     * @param tokenDate     The token delivery date
     * @param tokenQuantity The token quantity
     * @param bonus         The bonus if any
     * @param comment       Any optional comment
     */
    public void insert(String name, String currency, double amount, double fees, Date investDate, String token, Date tokenDate, double tokenQuantity, double bonus, String comment) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.COLUMN_ICO_NAME, name);
        contentValues.put(DatabaseHelper.COLUMN_ICO_CURRENCY, currency);
        contentValues.put(DatabaseHelper.COLUMN_ICO_AMOUNT, amount);
        contentValues.put(DatabaseHelper.COLUMN_ICO_FEES, fees);
        contentValues.put(DatabaseHelper.COLUMN_ICO_INVEST_DATE, investDate.getTime());
        contentValues.put(DatabaseHelper.COLUMN_ICO_TOKEN, token);
        contentValues.put(DatabaseHelper.COLUMN_ICO_TOKEN_DATE, tokenDate.getTime());
        contentValues.put(DatabaseHelper.COLUMN_ICO_TOKEN_QUANTITY, tokenQuantity);
        contentValues.put(DatabaseHelper.COLUMN_ICO_BONUS, bonus);
        contentValues.put(DatabaseHelper.COLUMN_ICO_COMMENT, comment);
        database.insert(DatabaseHelper.TABLE_ICO_INVESTMENTS, null, contentValues);
    }

    /**
     * Update an existing ICO investment in the database
     *
     * @param name          The ICO project name
     * @param currency      The currency used to invest
     * @param amount        The total amount invested
     * @param fees          The total fees if any
     * @param investDate    The investment date
     * @param token         The token name
     * @param tokenDate     The token delivery date
     * @param tokenQuantity The token quantity
     * @param bonus         The bonus if any
     * @param comment       Any optional comment
     * @return An integer representing the number of rows affected
     */
    public int update(String name, String currency, double amount, double fees, Date investDate, String token, Date tokenDate, double tokenQuantity, double bonus, String comment) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.COLUMN_ICO_CURRENCY, currency);
        contentValues.put(DatabaseHelper.COLUMN_ICO_AMOUNT, amount);
        contentValues.put(DatabaseHelper.COLUMN_ICO_FEES, fees);
        contentValues.put(DatabaseHelper.COLUMN_ICO_INVEST_DATE, investDate.getTime());
        contentValues.put(DatabaseHelper.COLUMN_ICO_TOKEN, token);
        contentValues.put(DatabaseHelper.COLUMN_ICO_TOKEN_DATE, tokenDate.getTime());
        contentValues.put(DatabaseHelper.COLUMN_ICO_TOKEN_QUANTITY, tokenQuantity);
        contentValues.put(DatabaseHelper.COLUMN_ICO_BONUS, bonus);
        contentValues.put(DatabaseHelper.COLUMN_ICO_COMMENT, comment);
        return database.update(DatabaseHelper.TABLE_ICO_INVESTMENTS, contentValues, DatabaseHelper.COLUMN_ICO_NAME + " = " + name, null);
    }

    /**
     * Delete an existing ICO investment from the database
     *
     * @param name The name of the ICO project to delete
     */
    public void delete(String name) {
        database.delete(DatabaseHelper.TABLE_ICO_INVESTMENTS, DatabaseHelper.COLUMN_ICO_NAME + "=" + name, null);
    }
    //endregion

}
