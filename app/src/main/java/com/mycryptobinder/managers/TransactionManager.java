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
import java.util.logging.Logger;

/**
 * Created by Yann
 * Created on 03/06/2017
 */
public class TransactionManager {

    private DatabaseHelper dbHelper;
    private Context context;
    private SQLiteDatabase database;
    private static final Logger logger = Logger.getLogger(TransactionManager.class.getName());

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

    //region Read

    /**
     * Get the total price value of the specified currency
     *
     * @param currencyCode The currency code
     * @return The sum of all prices*quantities for the specified currency
     */
    public Double getCurrencyTotal(String currencyCode) {
        Double total = -1.0;
        String req = "SELECT SUM(Total) FROM (" +
                "SELECT " + DatabaseHelper.COLUMN_TOTAL + " * CASE WHEN " + DatabaseHelper.COLUMN_TYPE + " = 'buy' THEN 1 ELSE -1 END AS Total " +
                "FROM " + DatabaseHelper.TABLE_TRANSACTIONS + " " +
                "WHERE " + DatabaseHelper.COLUMN_CURRENCY1 + " = '" + currencyCode + "' " +
                "UNION ALL " +
                "SELECT " + DatabaseHelper.COLUMN_TOTAL + " * CASE WHEN " + DatabaseHelper.COLUMN_TYPE + " = 'buy' THEN -1 ELSE 1 END AS Total " +
                "FROM " + DatabaseHelper.TABLE_TRANSACTIONS + " " +
                "WHERE " + DatabaseHelper.COLUMN_CURRENCY2 + " = '" + currencyCode + "') As Total2";
        Cursor cursor = database.rawQuery(req, null);
        try {
            if (cursor.moveToFirst()) {
                total = cursor.getDouble(0);
            }
        } finally {
            cursor.close();
        }
        return total;
    }

    /**
     * Get the quantity of the specified currency
     *
     * @param currencyCode The currency code
     * @return The sum of all quantities for the specified currency
     */
    public Double getCurrencyQuantity(String currencyCode) {
        Double total = -1.0;
        String req = "SELECT SUM(Total) " +
                "FROM (" +
                "SELECT SUM(" + DatabaseHelper.COLUMN_QUANTITY + " * CASE WHEN " + DatabaseHelper.COLUMN_TYPE + " = 'buy' THEN 1 ELSE -1 END) AS Total " +
                "FROM " + DatabaseHelper.TABLE_TRANSACTIONS + " " +
                "WHERE " + DatabaseHelper.COLUMN_CURRENCY1 + " = '" + currencyCode + "' " +
                "UNION ALL " +
                "SELECT SUM(" + DatabaseHelper.COLUMN_TOTAL + " * CASE WHEN " + DatabaseHelper.COLUMN_TYPE + " = 'buy' THEN -1 ELSE 1 END) AS Total " +
                "FROM " + DatabaseHelper.TABLE_TRANSACTIONS + " " +
                "WHERE " + DatabaseHelper.COLUMN_CURRENCY2 + " = '" + currencyCode + "') T;";
        Cursor cursor = database.rawQuery(req, null);
        try {
            if (cursor.moveToFirst()) {
                total = cursor.getDouble(0);
            }
        } finally {
            cursor.close();
        }
        return total;
    }

    public List<String> getTxIds() {
        List<String> list = new ArrayList<>();
        String[] columns = new String[]{DatabaseHelper.COLUMN_TX_ID};
        Cursor cursor = database.query(DatabaseHelper.TABLE_TRANSACTIONS, columns, null, null, null, null, null);
        if (cursor != null) {
            try {
                while (cursor.moveToNext()) {
                    list.add(cursor.getString(0));
                }
            } finally {
                cursor.close();
            }
        }
        return list;
    }

    /**
     * Get the list of all transactions from the database
     *
     * @return list of Transaction elements representing the transactions
     */
    public List<Transaction> getAll() {
        List<Transaction> list = new ArrayList<>();
        String[] columns = new String[]{
                DatabaseHelper.COLUMN_ID,
                DatabaseHelper.COLUMN_EXCHANGE,
                DatabaseHelper.COLUMN_TX_ID,
                DatabaseHelper.COLUMN_CURRENCY1,
                DatabaseHelper.COLUMN_CURRENCY2,
                DatabaseHelper.COLUMN_FEES,
                DatabaseHelper.COLUMN_DATE,
                DatabaseHelper.COLUMN_TYPE,
                DatabaseHelper.COLUMN_QUANTITY,
                DatabaseHelper.COLUMN_PRICE,
                DatabaseHelper.COLUMN_TOTAL,
                DatabaseHelper.COLUMN_COMMENT
        };
        Cursor cursor = database.query(DatabaseHelper.TABLE_TRANSACTIONS, columns, null, null, null, null, null);
        if (cursor != null) {
            CurrencyManager cm = new CurrencyManager(context);
            ExchangeManager em = new ExchangeManager(context);
            cm.open();
            em.open();
            try {
                while (cursor.moveToNext()) {
                    Transaction trans = new Transaction();
                    trans.setId(cursor.getLong(0));
                    trans.setExchange(em.getByName(cursor.getString(1)));
                    trans.setTransactionId(cursor.getString(2));
                    trans.setCurrency1(cm.getByIsoCode(cursor.getString(3)));
                    trans.setCurrency2(cm.getByIsoCode(cursor.getString(4)));
                    trans.setFees(cursor.getDouble(5));
                    trans.setDate(new Date(cursor.getLong(6)));
                    trans.setType(cursor.getString(7));
                    trans.setQuantity(cursor.getDouble(8));
                    trans.setPrice(cursor.getDouble(9));
                    trans.setTotal(cursor.getDouble(10));
                    trans.setComment(cursor.getString(11));
                    list.add(trans);
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
     * Insert a new transaction into the database
     *
     * @param exchange      The exchange where the transaction has been done
     * @param transactionId The unique id of the transaction
     * @param currency1     The first currency used in the transaction
     * @param currency2     The second currency used in the transaction
     * @param fees          The transaction fees
     * @param date          The transaction date
     * @param type          The transaction type
     * @param quantity      The quantity of bought currency
     * @param price         The price of the bought currency
     * @param total         The total amount of the transaction
     * @param comment       Any optional comment
     */
    public void insert(String exchange, String transactionId, String currency1, String currency2, double fees, Date date, String type, double quantity, double price, double total, String comment) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.COLUMN_EXCHANGE, exchange);
        contentValues.put(DatabaseHelper.COLUMN_TX_ID, transactionId);
        contentValues.put(DatabaseHelper.COLUMN_CURRENCY1, currency1);
        contentValues.put(DatabaseHelper.COLUMN_CURRENCY2, currency2);
        contentValues.put(DatabaseHelper.COLUMN_FEES, fees);
        contentValues.put(DatabaseHelper.COLUMN_DATE, date.getTime());
        contentValues.put(DatabaseHelper.COLUMN_TYPE, type);
        contentValues.put(DatabaseHelper.COLUMN_QUANTITY, quantity);
        contentValues.put(DatabaseHelper.COLUMN_PRICE, price);
        contentValues.put(DatabaseHelper.COLUMN_TOTAL, total);
        contentValues.put(DatabaseHelper.COLUMN_COMMENT, comment);
        database.insert(DatabaseHelper.TABLE_TRANSACTIONS, null, contentValues);
    }

    /**
     * Update an existing transaction in the database
     *
     * @param id            The id of the transaction to edit
     * @param exchange      The exchange where the transaction has been done
     * @param transactionId The unique id of the transaction
     * @param currency1     The first currency used in the transaction
     * @param currency2     The second currency used in the transaction
     * @param fees          The transaction fees
     * @param date          The transaction date
     * @param type          The transaction type
     * @param quantity      The quantity of bought currency
     * @param price         The price of the bought currency
     * @param total         The total amount of the transaction
     * @param comment       Any optional comment
     * @return An integer representing the number of rows affected
     */
    public int update(long id, String exchange, String transactionId, String currency1, String currency2, double fees, Date date, String type, double quantity, double price, double total, String comment) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.COLUMN_EXCHANGE, exchange);
        contentValues.put(DatabaseHelper.COLUMN_TX_ID, transactionId);
        contentValues.put(DatabaseHelper.COLUMN_CURRENCY1, currency1);
        contentValues.put(DatabaseHelper.COLUMN_CURRENCY2, currency2);
        contentValues.put(DatabaseHelper.COLUMN_FEES, fees);
        contentValues.put(DatabaseHelper.COLUMN_DATE, date.getTime());
        contentValues.put(DatabaseHelper.COLUMN_TYPE, type);
        contentValues.put(DatabaseHelper.COLUMN_QUANTITY, quantity);
        contentValues.put(DatabaseHelper.COLUMN_PRICE, price);
        contentValues.put(DatabaseHelper.COLUMN_TOTAL, total);
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
    //endregion

    public void populateTransactions() {
        String req = "INSERT INTO " + DatabaseHelper.TABLE_TRANSACTIONS + "(" +
                DatabaseHelper.COLUMN_EXCHANGE + ", " +
                DatabaseHelper.COLUMN_TX_ID + ", " +
                DatabaseHelper.COLUMN_CURRENCY1 + ", " +
                DatabaseHelper.COLUMN_CURRENCY2 + ", " +
                DatabaseHelper.COLUMN_FEES + ", " +
                DatabaseHelper.COLUMN_DATE + ", " +
                DatabaseHelper.COLUMN_TYPE + ", " +
                DatabaseHelper.COLUMN_QUANTITY + ", " +
                DatabaseHelper.COLUMN_PRICE + ", " +
                DatabaseHelper.COLUMN_TOTAL + ", " +
                DatabaseHelper.COLUMN_COMMENT + ") " +
                "SELECT * FROM (" +
                "SELECT 'Kraken', " +
                "A." + DatabaseHelper.COLUMN_KRAKEN_ORDER_TX_ID + " AS txid, " +
                "C." + DatabaseHelper.COLUMN_KRAKEN_ALTNAME + ", " +
                "D." + DatabaseHelper.COLUMN_KRAKEN_ALTNAME + ", " +
                "A." + DatabaseHelper.COLUMN_KRAKEN_FEE + ", " +
                "A." + DatabaseHelper.COLUMN_KRAKEN_TIME + ", " +
                "A." + DatabaseHelper.COLUMN_KRAKEN_TYPE + ", " +
                "A." + DatabaseHelper.COLUMN_KRAKEN_VOL + ", " +
                "A." + DatabaseHelper.COLUMN_KRAKEN_PRICE + ", " +
                "A." + DatabaseHelper.COLUMN_KRAKEN_COST + ", " +
                "null " +
                "FROM " + DatabaseHelper.TABLE_KRAKEN_TRADE_HISTORY + " A " +
                "INNER JOIN " + DatabaseHelper.TABLE_KRAKEN_ASSETPAIRS + " B ON A." + DatabaseHelper.COLUMN_KRAKEN_PAIR + " = B." + DatabaseHelper.COLUMN_KRAKEN_ASSETPAIR + " " +
                "INNER JOIN " + DatabaseHelper.TABLE_KRAKEN_ASSETS + " C ON B." + DatabaseHelper.COLUMN_KRAKEN_BASE + " = C." + DatabaseHelper.COLUMN_KRAKEN_ASSETNAME + " " +
                "INNER JOIN " + DatabaseHelper.TABLE_KRAKEN_ASSETS + " D ON B." + DatabaseHelper.COLUMN_KRAKEN_QUOTE + " = D." + DatabaseHelper.COLUMN_KRAKEN_ASSETNAME + " " +
                "UNION ALL " +
                "SELECT 'Poloniex', " +
                DatabaseHelper.COLUMN_POLONIEX_GLOBAL_TRADE_ID + " AS txid, " +
                "replace(" + DatabaseHelper.COLUMN_POLONIEX_PAIR + ", rtrim(" + DatabaseHelper.COLUMN_POLONIEX_PAIR + ", replace(" + DatabaseHelper.COLUMN_POLONIEX_PAIR + ", '_', '' ) ), ''), " +
                "replace(" + DatabaseHelper.COLUMN_POLONIEX_PAIR + ", ltrim(" + DatabaseHelper.COLUMN_POLONIEX_PAIR + ", replace(" + DatabaseHelper.COLUMN_POLONIEX_PAIR + ", '_', '' ) ), ''), " +
                DatabaseHelper.COLUMN_POLONIEX_FEE + ", " +
                DatabaseHelper.COLUMN_POLONIEX_DATE + ", " +
                DatabaseHelper.COLUMN_POLONIEX_TYPE + ", " +
                DatabaseHelper.COLUMN_POLONIEX_AMOUNT + ", " +
                DatabaseHelper.COLUMN_POLONIEX_RATE + ", " +
                DatabaseHelper.COLUMN_POLONIEX_TOTAL + ", " +
                "null " +
                "FROM " + DatabaseHelper.TABLE_POLONIEX_TRADE_HISTORY + ") T1 " +
                "WHERE NOT EXISTS (SELECT 1 FROM " + DatabaseHelper.TABLE_TRANSACTIONS + " WHERE " + DatabaseHelper.COLUMN_TX_ID + " = T1.txid)";
        database.execSQL(req);

        List<Transaction> trans = getAll();
        for (Transaction t : trans) {
            logger.info("==================> " + t.getExchange() + " " + t.getTransactionId() + " " + t.getCurrency1() + " " + t.getCurrency2() + " " + t.getDate() + " " + t.getFees() + " " + t.getType() + " " + t.getQuantity() + " " + t.getPrice());
        }
    }

}
