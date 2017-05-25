package com.mycryptobinder.helpers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Yann
 * Created on 21/05/2017
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    // Database name
    private static final String DB_NAME = "MYCRYPTOBINDER.DB";

    // Database version
    private static final int DB_VERSION = 1;

    // Table names
    public static final String TABLE_CURRENCIES = "CURRENCIES";
    public static final String TABLE_EXCHANGES = "EXCHANGES";
    public static final String TABLE_TRANSACTIONS = "TRANSACTIONS";

    // Table column names
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_ISO_CODE = "iso_code";
    public static final String COLUMN_SYMBOL = "symbol";
    public static final String COLUMN_LINK = "link";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_EXCHANGE = "exchange";
    public static final String COLUMN_CURRENCY1 = "currency1";
    public static final String COLUMN_CURRENCY2 = "currency2";
    public static final String COLUMN_FEES = "fees";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_QUANTITY = "quantity";
    public static final String COLUMN_PRICE = "price";
    public static final String COLUMN_COMMENT = "comment";

    // Table creations
    private static final String CREATE_TABLE_CURRENCIES = "CREATE TABLE " + TABLE_CURRENCIES + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_NAME + " TEXT NOT NULL, " +
            COLUMN_ISO_CODE + " TEXT NOT NULL, " +
            COLUMN_SYMBOL + " TEXT NOT NULL)";
    private static final String CREATE_TABLE_EXCHANGES = "CREATE TABLE " + TABLE_EXCHANGES + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_NAME + " TEXT NOT NULL, " +
            COLUMN_LINK + " TEXT, " +
            COLUMN_DESCRIPTION + " TEXT)";
    private static final String CREATE_TABLE_TRANSACTIONS = "CREATE TABLE " + TABLE_TRANSACTIONS + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_EXCHANGE + " INTEGER NOT NULL, " +
            COLUMN_CURRENCY1 + " INTEGER NOT NULL, " +
            COLUMN_CURRENCY2 + " INTEGER NOT NULL, " +
            COLUMN_FEES + " REAL NOT NULL, " +
            COLUMN_DATE + " NUMERIC NOT NULL, " +
            COLUMN_TYPE + " TEXT NOT NULL, " +
            COLUMN_QUANTITY + " REAL NOT NULL, " +
            COLUMN_PRICE + " REAL NOT NULL, " +
            COLUMN_COMMENT + " TEXT, " +
            "FOREIGN KEY(" + COLUMN_EXCHANGE + ") REFERENCES " + TABLE_EXCHANGES + "(" + COLUMN_ID + "), " +
            "FOREIGN KEY(" + COLUMN_CURRENCY1 + ") REFERENCES " + TABLE_CURRENCIES + "(" + COLUMN_ID + "), " +
            "FOREIGN KEY(" + COLUMN_CURRENCY2 + ") REFERENCES " + TABLE_CURRENCIES + "(" + COLUMN_ID + "))";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_CURRENCIES);
        db.execSQL(CREATE_TABLE_EXCHANGES);
        db.execSQL(CREATE_TABLE_TRANSACTIONS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSACTIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXCHANGES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CURRENCIES);
        onCreate(db);
    }
}
