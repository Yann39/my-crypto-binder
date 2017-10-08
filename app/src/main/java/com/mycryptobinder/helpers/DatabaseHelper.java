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
    private static final int DB_VERSION = 23;

    //region Table names
    public static final String TABLE_CURRENCIES = "CURRENCIES";
    public static final String TABLE_EXCHANGES = "EXCHANGES";
    public static final String TABLE_TRANSACTIONS = "TRANSACTIONS";
    public static final String TABLE_ICO_INVESTMENTS = "ICO_INVESTMENTS";

    public static final String TABLE_KRAKEN_ASSETPAIRS = "KRAKEN_ASSET_PAIRS";
    public static final String TABLE_KRAKEN_ASSETS = "KRAKEN_ASSETS";
    public static final String TABLE_KRAKEN_TRADE_HISTORY = "KRAKEN_TRADE_HISTORY";

    public static final String TABLE_POLONIEX_ASSETS = "POLONIEX_ASSETS";
    public static final String TABLE_POLONIEX_TRADE_HISTORY = "POLONIEX_TRADE_HISTORY";
    //endregion

    //region Column names
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TX_ID = "tx_id";
    public static final String COLUMN_ISO_CODE = "iso_code";
    public static final String COLUMN_NAME = "name";
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
    public static final String COLUMN_TOTAL = "total";
    public static final String COLUMN_SUM_QUANTITY = "sum_quantity";
    public static final String COLUMN_COMMENT = "comment";

    public static final String COLUMN_KRAKEN_ORDER_TX_ID = "order_tx_id";
    public static final String COLUMN_KRAKEN_PAIR = "pair";
    public static final String COLUMN_KRAKEN_TIME = "time";
    public static final String COLUMN_KRAKEN_TYPE = "type";
    public static final String COLUMN_KRAKEN_ORDER_TYPE = "order_type";
    public static final String COLUMN_KRAKEN_PRICE = "price";
    public static final String COLUMN_KRAKEN_COST = "cost";
    public static final String COLUMN_KRAKEN_FEE = "fee";
    public static final String COLUMN_KRAKEN_VOL = "vol";
    public static final String COLUMN_KRAKEN_MARGIN = "margin";
    public static final String COLUMN_KRAKEN_MISC = "misc";
    public static final String COLUMN_KRAKEN_ASSETPAIR = "asset_pair";
    public static final String COLUMN_KRAKEN_ALTNAME = "alt_name";
    public static final String COLUMN_KRAKEN_BASE = "base";
    public static final String COLUMN_KRAKEN_QUOTE = "quote";
    public static final String COLUMN_KRAKEN_ASSETNAME = "assetname";

    public static final String COLUMN_POLONIEX_ASSET_CODE = "code";
    public static final String COLUMN_POLONIEX_ASSET_NAME = "name";
    public static final String COLUMN_POLONIEX_GLOBAL_TRADE_ID = "global_trade_id";
    public static final String COLUMN_POLONIEX_PAIR = "pair";
    public static final String COLUMN_POLONIEX_TRADE_ID = "trade_id";
    public static final String COLUMN_POLONIEX_DATE = "date";
    public static final String COLUMN_POLONIEX_RATE = "rate";
    public static final String COLUMN_POLONIEX_AMOUNT = "amount";
    public static final String COLUMN_POLONIEX_TOTAL = "total";
    public static final String COLUMN_POLONIEX_FEE = "fee";
    public static final String COLUMN_POLONIEX_ORDER_NUMBER = "order_number";
    public static final String COLUMN_POLONIEX_TYPE = "type";
    public static final String COLUMN_POLONIEX_CATEGORY = "category";

    public static final String COLUMN_ICO_NAME = "name";
    public static final String COLUMN_ICO_CURRENCY = "currency";
    public static final String COLUMN_ICO_AMOUNT = "amount";
    public static final String COLUMN_ICO_FEES = "fees";
    public static final String COLUMN_ICO_INVEST_DATE = "invest_date";
    public static final String COLUMN_ICO_TOKEN = "token";
    public static final String COLUMN_ICO_TOKEN_DATE = "token_date";
    public static final String COLUMN_ICO_TOKEN_QUANTITY = "token_quantity";
    public static final String COLUMN_ICO_BONUS = "bonus";
    public static final String COLUMN_ICO_COMMENT = "comment";
    //endregion

    //region Table creations
    private static final String CREATE_TABLE_CURRENCIES = "CREATE TABLE " + TABLE_CURRENCIES + " (" +
            COLUMN_ISO_CODE + " TEXT PRIMARY KEY NOT NULL, " +
            COLUMN_NAME + " TEXT, " +
            COLUMN_SYMBOL + " TEXT, " +
            "CONSTRAINT currency_iso_code_unique UNIQUE (" + COLUMN_ISO_CODE + "))";

    private static final String CREATE_TABLE_EXCHANGES = "CREATE TABLE " + TABLE_EXCHANGES + " (" +
            COLUMN_NAME + " TEXT PRIMARY KEY NOT NULL, " +
            COLUMN_LINK + " TEXT, " +
            COLUMN_DESCRIPTION + " TEXT, " +
            "CONSTRAINT exchange_name_unique UNIQUE (" + COLUMN_NAME + "))";

    private static final String CREATE_TABLE_TRANSACTIONS = "CREATE TABLE " + TABLE_TRANSACTIONS + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_EXCHANGE + " TEXT NOT NULL, " +
            COLUMN_TX_ID + " TEXT NOT NULL, " +
            COLUMN_CURRENCY1 + " TEXT NOT NULL, " +
            COLUMN_CURRENCY2 + " TEXT NOT NULL, " +
            COLUMN_FEES + " REAL NOT NULL, " +
            COLUMN_DATE + " NUMERIC NOT NULL, " +
            COLUMN_TYPE + " TEXT NOT NULL, " +
            COLUMN_QUANTITY + " REAL NOT NULL, " +
            COLUMN_PRICE + " REAL NOT NULL, " +
            COLUMN_TOTAL + " REAL NOT NULL, " +
            COLUMN_SUM_QUANTITY + " REAL NOT NULL, " +
            COLUMN_COMMENT + " TEXT, " +
            "FOREIGN KEY(" + COLUMN_EXCHANGE + ") REFERENCES " + TABLE_EXCHANGES + "(" + COLUMN_NAME + "), " +
            "FOREIGN KEY(" + COLUMN_CURRENCY1 + ") REFERENCES " + TABLE_CURRENCIES + "(" + COLUMN_ISO_CODE + "), " +
            "FOREIGN KEY(" + COLUMN_CURRENCY2 + ") REFERENCES " + TABLE_CURRENCIES + "(" + COLUMN_ISO_CODE + "))";

    private static final String CREATE_TABLE_ICO_INVESTMENTS = "CREATE TABLE " + TABLE_ICO_INVESTMENTS + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_ICO_NAME + " TEXT NOT NULL, " +
            COLUMN_ICO_CURRENCY + " TEXT NOT NULL, " +
            COLUMN_ICO_AMOUNT + " REAL NOT NULL, " +
            COLUMN_ICO_FEES + " REAL NOT NULL, " +
            COLUMN_ICO_INVEST_DATE + " NUMERIC NOT NULL, " +
            COLUMN_ICO_TOKEN + " TEXT NOT NULL, " +
            COLUMN_ICO_TOKEN_DATE + " NUMERIC NOT NULL, " +
            COLUMN_ICO_TOKEN_QUANTITY + " REAL NOT NULL, " +
            COLUMN_ICO_BONUS + " REAL NOT NULL, " +
            COLUMN_ICO_COMMENT + " TEXT, " +
            "FOREIGN KEY(" + COLUMN_ICO_CURRENCY + ") REFERENCES " + TABLE_CURRENCIES + "(" + COLUMN_ISO_CODE + "))";

    private static final String CREATE_TABLE_KRAKEN_ASSETPAIRS = "CREATE TABLE " + TABLE_KRAKEN_ASSETPAIRS + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_KRAKEN_ASSETPAIR + " TEXT NOT NULL, " +
            COLUMN_KRAKEN_ALTNAME + " TEXT NOT NULL, " +
            COLUMN_KRAKEN_BASE + " TEXT NOT NULL, " +
            COLUMN_KRAKEN_QUOTE + " TEXT NOT NULL)";

    private static final String CREATE_TABLE_KRAKEN_ASSETS = "CREATE TABLE " + TABLE_KRAKEN_ASSETS + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_KRAKEN_ASSETNAME + " TEXT NOT NULL, " +
            COLUMN_KRAKEN_ALTNAME + " TEXT NOT NULL)";

    private static final String CREATE_TABLE_KRAKEN_TRADE_HISTORY = "CREATE TABLE " + TABLE_KRAKEN_TRADE_HISTORY + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_KRAKEN_ORDER_TX_ID + " TEXT NOT NULL, " +
            COLUMN_KRAKEN_PAIR + " TEXT NOT NULL, " +
            COLUMN_KRAKEN_TIME + " NUMERIC NOT NULL, " +
            COLUMN_KRAKEN_TYPE + " TEXT NOT NULL, " +
            COLUMN_KRAKEN_ORDER_TYPE + " TEXT NOT NULL, " +
            COLUMN_KRAKEN_PRICE + " REAL NOT NULL, " +
            COLUMN_KRAKEN_COST + " REAL NOT NULL, " +
            COLUMN_KRAKEN_FEE + " REAL NOT NULL, " +
            COLUMN_KRAKEN_VOL + " REAL NOT NULL, " +
            COLUMN_KRAKEN_MARGIN + " REAL, " +
            COLUMN_KRAKEN_MISC + " TEXT)";

    private static final String CREATE_TABLE_POLONIEX_ASSETS = "CREATE TABLE " + TABLE_POLONIEX_ASSETS + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_POLONIEX_ASSET_CODE + " TEXT NOT NULL, " +
            COLUMN_POLONIEX_ASSET_NAME + " TEXT NOT NULL)";

    private static final String CREATE_TABLE_POLONIEX_TRADE_HISTORY = "CREATE TABLE " + TABLE_POLONIEX_TRADE_HISTORY + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_POLONIEX_GLOBAL_TRADE_ID + " INTEGER NOT NULL, " +
            COLUMN_POLONIEX_PAIR + " TEXT NOT NULL, " +
            COLUMN_POLONIEX_TRADE_ID + " INTEGER NOT NULL, " +
            COLUMN_POLONIEX_DATE + " NUMERIC NOT NULL, " +
            COLUMN_POLONIEX_RATE + " REAL NOT NULL, " +
            COLUMN_POLONIEX_AMOUNT + " REAL NOT NULL, " +
            COLUMN_POLONIEX_TOTAL + " REAL NOT NULL, " +
            COLUMN_POLONIEX_FEE + " REAL NOT NULL, " +
            COLUMN_POLONIEX_ORDER_NUMBER + " INTEGER NOT NULL, " +
            COLUMN_POLONIEX_TYPE + " TEXT NOT NULL, " +
            COLUMN_POLONIEX_CATEGORY + " TEXT NOT NULL)";
    //endregion

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_CURRENCIES);
        db.execSQL(CREATE_TABLE_EXCHANGES);
        db.execSQL(CREATE_TABLE_TRANSACTIONS);
        db.execSQL(CREATE_TABLE_ICO_INVESTMENTS);
        db.execSQL(CREATE_TABLE_KRAKEN_ASSETPAIRS);
        db.execSQL(CREATE_TABLE_KRAKEN_ASSETS);
        db.execSQL(CREATE_TABLE_KRAKEN_TRADE_HISTORY);
        db.execSQL(CREATE_TABLE_POLONIEX_ASSETS);
        db.execSQL(CREATE_TABLE_POLONIEX_TRADE_HISTORY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXCHANGES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CURRENCIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSACTIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ICO_INVESTMENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_KRAKEN_ASSETPAIRS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_KRAKEN_ASSETS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_KRAKEN_TRADE_HISTORY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_POLONIEX_ASSETS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_POLONIEX_TRADE_HISTORY);
        onCreate(db);
    }

}
