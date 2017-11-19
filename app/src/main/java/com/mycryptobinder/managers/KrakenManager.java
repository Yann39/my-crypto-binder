package com.mycryptobinder.managers;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.Base64;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mycryptobinder.entities.AppDatabase;
import com.mycryptobinder.helpers.DatabaseHelper;
import com.mycryptobinder.models.KrakenTrade;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Yann
 * Created on 05/09/2017
 */
public class KrakenManager {

    //region Properties / constructors
    private DatabaseHelper dbHelper;
    private Context context;
    private SQLiteDatabase database;
    private static int offset = 0;
    private Properties properties;
    private AppDatabase db;

    private static final Logger logger = Logger.getLogger(KrakenManager.class.getName());

    public KrakenManager(Context c) {
        context = c;
    }

    public KrakenManager open() throws SQLException {
        dbHelper = new DatabaseHelper(context);
        database = dbHelper.getWritableDatabase();
        db = AppDatabase.getDatabase(context);
        properties = new Properties();
        try {
            InputStream inputStream = context.getAssets().open("myCryptoBinder.properties");
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }

    public void close() {
        dbHelper.close();
    }
    //endregion

    //region Read

    //region Asset pairs


    /**
     * Get the list of all asset pairs from the database
     *
     * @return list of asset pairs as strings
     */
    public List<String> getAssetPairNames() {
        List<String> list = new ArrayList<>();
        String[] columns = new String[]{DatabaseHelper.COLUMN_KRAKEN_ASSETPAIR};
        Cursor cursor = database.query(DatabaseHelper.TABLE_KRAKEN_ASSETPAIRS, columns, null, null, null, null, null);
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
    //endregion

    //region Assets

    /**
     * Get the list of all asset names from the database
     *
     * @return list of asset names as strings
     */
    public List<String> getAssetNames() {
        List<String> list = new ArrayList<>();
        String[] columns = new String[]{DatabaseHelper.COLUMN_KRAKEN_ASSETNAME};
        Cursor cursor = database.query(DatabaseHelper.TABLE_KRAKEN_ASSETS, columns, null, null, null, null, null);
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
    //endregion

    //region Trades

    /**
     * Get the list of all trades from the database
     *
     * @return list of KrakenTrade elements representing the trades
     */
    public List<KrakenTrade> getAllTrades() {
        List<KrakenTrade> list = new ArrayList<>();
        String[] columns = new String[]{DatabaseHelper.COLUMN_ID, DatabaseHelper.COLUMN_KRAKEN_ORDER_TX_ID, DatabaseHelper.COLUMN_KRAKEN_PAIR, DatabaseHelper.COLUMN_KRAKEN_TIME, DatabaseHelper.COLUMN_KRAKEN_TYPE, DatabaseHelper.COLUMN_KRAKEN_ORDER_TYPE, DatabaseHelper.COLUMN_KRAKEN_PRICE, DatabaseHelper.COLUMN_KRAKEN_COST, DatabaseHelper.COLUMN_KRAKEN_FEE, DatabaseHelper.COLUMN_KRAKEN_VOL, DatabaseHelper.COLUMN_KRAKEN_MARGIN, DatabaseHelper.COLUMN_KRAKEN_MISC};
        Cursor cursor = database.query(DatabaseHelper.TABLE_KRAKEN_TRADE_HISTORY, columns, null, null, null, null, null);
        if (cursor != null) {
            try {
                while (cursor.moveToNext()) {
                    KrakenTrade trade = new KrakenTrade();
                    trade.setId(cursor.getLong(0));
                    trade.setOrderTxId(cursor.getString(1));
                    trade.setPair(cursor.getString(2));
                    trade.setTime(cursor.getLong(3));
                    trade.setType(cursor.getString(4));
                    trade.setOrderType(cursor.getString(5));
                    trade.setPrice(cursor.getDouble(6));
                    trade.setCost(cursor.getDouble(7));
                    trade.setFee(cursor.getDouble(8));
                    trade.setVol(cursor.getDouble(9));
                    trade.setMargin(cursor.getDouble(10));
                    trade.setMisc(cursor.getString(11));
                    list.add(trade);
                }
            } finally {
                cursor.close();
            }
        }
        return list;
    }

    /**
     * Get the list of all trades transaction ids from the database
     *
     * @return list of trade transaction ids as strings
     */
    public List<String> getTradeTransactionIds() {
        List<String> list = new ArrayList<>();
        String[] columns = new String[]{DatabaseHelper.COLUMN_KRAKEN_ORDER_TX_ID};
        Cursor cursor = database.query(DatabaseHelper.TABLE_KRAKEN_TRADE_HISTORY, columns, null, null, null, null, null);
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
     * Get the list of all ledger ids from the database
     *
     * @return list of ledger ids as strings
     */
    public List<String> getLedgerIds() {
        List<String> list = new ArrayList<>();
        String[] columns = new String[]{DatabaseHelper.COLUMN_KRAKEN_LEDGER_ID};
        Cursor cursor = database.query(DatabaseHelper.TABLE_KRAKEN_LEDGER, columns, null, null, null, null, null);
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
    //endregion

    //endregion

    //region Create Update Delete

    //region Asset pairs

    /**
     * Insert a new asset pair into the database
     *
     * @param assetPair The asset pair name
     * @param altName   The asset pair alt name
     * @param base      The asset pair base
     * @param quote     The asset pair quote
     */
    public void insertAssetPair(String assetPair, String altName, String base, String quote) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.COLUMN_KRAKEN_ASSETPAIR, assetPair);
        contentValues.put(DatabaseHelper.COLUMN_KRAKEN_ALTNAME, altName);
        contentValues.put(DatabaseHelper.COLUMN_KRAKEN_BASE, base);
        contentValues.put(DatabaseHelper.COLUMN_KRAKEN_QUOTE, quote);
        database.insert(DatabaseHelper.TABLE_KRAKEN_ASSETPAIRS, null, contentValues);
    }

    /**
     * Update an existing asset pair in the database
     *
     * @param id        The id of the asset pair to edit
     * @param assetPair The asset pair name
     * @param altName   The asset pair alt name
     * @param base      The asset pair base
     * @param quote     The asset pair quote
     * @return An integer representing the number of rows affected
     */
    public int updateAssetPair(long id, String assetPair, String altName, String base, String quote) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.COLUMN_KRAKEN_ASSETPAIR, assetPair);
        contentValues.put(DatabaseHelper.COLUMN_KRAKEN_ALTNAME, altName);
        contentValues.put(DatabaseHelper.COLUMN_KRAKEN_BASE, base);
        contentValues.put(DatabaseHelper.COLUMN_KRAKEN_QUOTE, quote);
        return database.update(DatabaseHelper.TABLE_KRAKEN_ASSETPAIRS, contentValues, DatabaseHelper.COLUMN_ID + " = " + id, null);
    }

    /**
     * Delete an existing asset pair from the database
     *
     * @param id The id of the asset pair to delete
     */
    public void deleteAssetPair(long id) {
        database.delete(DatabaseHelper.TABLE_KRAKEN_ASSETPAIRS, DatabaseHelper.COLUMN_ID + "=" + id, null);
    }
    //endregion

    //region Assets

    /**
     * Insert a new asset into the database
     *
     * @param assetName The asset name
     * @param altName   The asset alt name
     */
    public void insertAsset(String assetName, String altName) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.COLUMN_KRAKEN_ASSETNAME, assetName);
        contentValues.put(DatabaseHelper.COLUMN_KRAKEN_ALTNAME, altName);
        database.insert(DatabaseHelper.TABLE_KRAKEN_ASSETS, null, contentValues);
    }

    /**
     * Update an existing asset in the database
     *
     * @param id        The id of the asset pair to edit
     * @param assetName The asset name
     * @param altName   The asset alt name
     * @return An integer representing the number of rows affected
     */
    public int updateAsset(long id, String assetName, String altName) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.COLUMN_KRAKEN_ASSETNAME, assetName);
        contentValues.put(DatabaseHelper.COLUMN_KRAKEN_ALTNAME, altName);
        return database.update(DatabaseHelper.TABLE_KRAKEN_ASSETS, contentValues, DatabaseHelper.COLUMN_ID + " = " + id, null);
    }

    /**
     * Delete an existing asset from the database
     *
     * @param id The id of the asset to delete
     */
    public void deleteAsset(long id) {
        database.delete(DatabaseHelper.TABLE_KRAKEN_ASSETS, DatabaseHelper.COLUMN_ID + "=" + id, null);
    }
    //endregion

    //region Trades

    /**
     * Insert a new trade into the database
     *
     * @param orderTxId The order transaction id
     * @param pair      The asset pair name
     * @param time      The date as Unix timestamp
     * @param type      The type of order (buy/sell)
     * @param orderType The order type
     * @param price     The Average price order was executed at (quote currency)
     * @param cost      The total cost of order (quote currency)
     * @param fee       The total fee (quote currency)
     * @param vol       The volume (base currency)
     * @param margin    The initial margin (quote currency)
     * @param misc      Comma delimited list of miscellaneous information
     */
    public void insertTrade(String orderTxId, String pair, Long time, String type, String orderType, Double price, Double cost, Double fee, Double vol, Double margin, String misc) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.COLUMN_KRAKEN_ORDER_TX_ID, orderTxId);
        contentValues.put(DatabaseHelper.COLUMN_KRAKEN_PAIR, pair);
        contentValues.put(DatabaseHelper.COLUMN_KRAKEN_TIME, time);
        contentValues.put(DatabaseHelper.COLUMN_KRAKEN_TYPE, type);
        contentValues.put(DatabaseHelper.COLUMN_KRAKEN_ORDER_TYPE, orderType);
        contentValues.put(DatabaseHelper.COLUMN_KRAKEN_PRICE, price);
        contentValues.put(DatabaseHelper.COLUMN_KRAKEN_COST, cost);
        contentValues.put(DatabaseHelper.COLUMN_KRAKEN_FEE, fee);
        contentValues.put(DatabaseHelper.COLUMN_KRAKEN_VOL, vol);
        contentValues.put(DatabaseHelper.COLUMN_KRAKEN_MARGIN, margin);
        contentValues.put(DatabaseHelper.COLUMN_KRAKEN_MISC, misc);
        database.insert(DatabaseHelper.TABLE_KRAKEN_TRADE_HISTORY, null, contentValues);
    }

    /**
     * Update an existing trade in the database
     *
     * @param id        The id of the trade to edit
     * @param orderTxId The order transaction id
     * @param pair      The asset pair name
     * @param time      The date as Unix timestamp
     * @param type      The type of order (buy/sell)
     * @param orderType The order type
     * @param price     The Average price order was executed at (quote currency)
     * @param cost      The total cost of order (quote currency)
     * @param fee       The total fee (quote currency)
     * @param vol       The volume (base currency)
     * @param margin    The initial margin (quote currency)
     * @param misc      Comma delimited list of miscellaneous information
     * @return An integer representing the number of rows affected
     */
    public int updateTrade(long id, String orderTxId, String pair, Long time, String type, String orderType, Double price, Double cost, Double fee, Double vol, Double margin, String misc) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.COLUMN_KRAKEN_ORDER_TX_ID, orderTxId);
        contentValues.put(DatabaseHelper.COLUMN_KRAKEN_PAIR, pair);
        contentValues.put(DatabaseHelper.COLUMN_KRAKEN_TIME, time);
        contentValues.put(DatabaseHelper.COLUMN_KRAKEN_TYPE, type);
        contentValues.put(DatabaseHelper.COLUMN_KRAKEN_ORDER_TYPE, orderType);
        contentValues.put(DatabaseHelper.COLUMN_KRAKEN_PRICE, price);
        contentValues.put(DatabaseHelper.COLUMN_KRAKEN_COST, cost);
        contentValues.put(DatabaseHelper.COLUMN_KRAKEN_FEE, fee);
        contentValues.put(DatabaseHelper.COLUMN_KRAKEN_VOL, vol);
        contentValues.put(DatabaseHelper.COLUMN_KRAKEN_MARGIN, margin);
        contentValues.put(DatabaseHelper.COLUMN_KRAKEN_MISC, misc);
        return database.update(DatabaseHelper.TABLE_KRAKEN_TRADE_HISTORY, contentValues, DatabaseHelper.COLUMN_ID + " = " + id, null);
    }

    /**
     * Delete an existing trade from the database
     *
     * @param id The id of the trade to delete
     */
    public void deleteTrade(long id) {
        database.delete(DatabaseHelper.TABLE_KRAKEN_TRADE_HISTORY, DatabaseHelper.COLUMN_ID + "=" + id, null);
    }
    //endregion

    //region Ledger

    /**
     * Insert a new ledger row into the database
     *
     * @param refId   The ledger ref id
     * @param time    The date as Unix timestamp
     * @param type    The type of order (trade/withdrawal/etc)
     * @param aClass  The class (ie. currency)
     * @param asset   The asset name
     * @param amount  The total amount
     * @param fee     The total fee
     * @param balance The balance
     */
    public void insertLedgerInfo(String refId, Long time, String type, String aClass, String asset, Double amount, Double fee, Double balance) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.COLUMN_KRAKEN_LEDGER_REF_ID, refId);
        contentValues.put(DatabaseHelper.COLUMN_KRAKEN_LEDGER_TIME, time);
        contentValues.put(DatabaseHelper.COLUMN_KRAKEN_LEDGER_TYPE, type);
        contentValues.put(DatabaseHelper.COLUMN_KRAKEN_LEDGER_CLASS, aClass);
        contentValues.put(DatabaseHelper.COLUMN_KRAKEN_LEDGER_ASSET, asset);
        contentValues.put(DatabaseHelper.COLUMN_KRAKEN_LEDGER_AMOUNT, amount);
        contentValues.put(DatabaseHelper.COLUMN_KRAKEN_LEDGER_FEE, fee);
        contentValues.put(DatabaseHelper.COLUMN_KRAKEN_LEDGER_BALANCE, balance);
        database.insert(DatabaseHelper.TABLE_KRAKEN_LEDGER, null, contentValues);
    }

    /**
     * Update an existing ledger row in the database
     *
     * @param id   The ledger id
     * @param refId   The ledger ref id
     * @param time    The date as Unix timestamp
     * @param type    The type of order (trade/withdrawal/etc)
     * @param aClass  The class (ie. currency)
     * @param asset   The asset name
     * @param amount  The total amount
     * @param fee     The total fee
     * @param balance The balance
     * @return An integer representing the number of rows affected
     */
    public int updateLedgerInfo(String id, String refId, Long time, String type, String aClass, String asset, Double amount, Double fee, Double balance) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.COLUMN_KRAKEN_LEDGER_REF_ID, refId);
        contentValues.put(DatabaseHelper.COLUMN_KRAKEN_LEDGER_TIME, time);
        contentValues.put(DatabaseHelper.COLUMN_KRAKEN_LEDGER_TYPE, type);
        contentValues.put(DatabaseHelper.COLUMN_KRAKEN_LEDGER_CLASS, aClass);
        contentValues.put(DatabaseHelper.COLUMN_KRAKEN_LEDGER_ASSET, asset);
        contentValues.put(DatabaseHelper.COLUMN_KRAKEN_LEDGER_AMOUNT, amount);
        contentValues.put(DatabaseHelper.COLUMN_KRAKEN_LEDGER_FEE, fee);
        contentValues.put(DatabaseHelper.COLUMN_KRAKEN_LEDGER_BALANCE, balance);
        return database.update(DatabaseHelper.TABLE_KRAKEN_LEDGER, contentValues, DatabaseHelper.COLUMN_KRAKEN_LEDGER_ID + " = " + id, null);
    }

    /**
     * Delete an existing ledger row from the database
     *
     * @param id The id of the ledger to delete
     */
    public void deleteTrade(String id) {
        database.delete(DatabaseHelper.TABLE_KRAKEN_LEDGER, DatabaseHelper.COLUMN_KRAKEN_LEDGER_ID + "=" + id, null);
    }
    //endregion

    //endregion

    //region Kraken API calls

    /**
     * Calculate the signature for the Kraken API call
     * Message signature using HMAC-SHA512 of (URI path + SHA256(nonce + POST data)) and base64 decoded secret API key
     *
     * @param path  The path of the method to be called (URL part after host name)
     * @param nonce The nonce (always increasing unsigned 64 bit integer, usually the timestamp)
     * @param data  POST data
     * @return The base64 encoded signature string
     */
    private String calculateSignature(String path, String nonce, String data) {
        String signature = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update((nonce + data).getBytes());
            Mac mac = Mac.getInstance("HmacSHA512");
            String secret = properties.getProperty("KRAKEN_API_PRIVATE_KEY");
            mac.init(new SecretKeySpec(Base64.decode(secret.getBytes(), 2), "HmacSHA512"));
            mac.update(path.getBytes());
            signature = new String(Base64.encode(mac.doFinal(md.digest()), 2));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return signature;
    }

    /**
     * Populate trade history table from remote exchange
     */
    public void populateTradeHistory() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2016);
        cal.set(Calendar.MONTH, Calendar.DECEMBER);
        cal.set(Calendar.DAY_OF_MONTH, 13);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        String start = String.valueOf(cal.getTimeInMillis() / 1000);
        String domain = properties.getProperty("KRAKEN_API_BASE_URL");
        String key = properties.getProperty("KRAKEN_API_PUBLIC_KEY");

        String nonce = String.valueOf(System.currentTimeMillis());
        String path = "/0/private/TradesHistory";

        RequestParams params = new RequestParams();
        params.add("nonce", nonce);
        params.add("start", start);
        params.add("ofs", String.valueOf(offset));

        String sign = calculateSignature(path, nonce, params.toString());

        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("API-Key", key);
        client.addHeader("API-Sign", sign);

        client.post(domain + path, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                logger.warning("Error");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                // populate data from remote exchange
                try {
                    if (response.getJSONArray("error").length() > 0) {
                        logger.warning("Error when trying to get Kraken trading history : " + response.getString("error"));
                    } else {

                        JSONObject json_data = response.getJSONObject("result").getJSONObject("trades");

                        if (json_data.length() > 0) {

                            //get any existing trades
                            List<String> txIds = getTradeTransactionIds();

                            for (Iterator<String> rows = json_data.keys(); rows.hasNext(); ) {
                                JSONObject json_row = json_data.getJSONObject(rows.next());
                                String orderTxId = json_row.getString("ordertxid");
                                if (!txIds.contains(orderTxId)) {
                                    String pair = json_row.getString("pair");
                                    Long time = Double.valueOf(json_row.getString("time")).longValue();
                                    String type = json_row.getString("type");
                                    String orderType = json_row.getString("ordertype");
                                    Double price = Double.parseDouble(json_row.getString("price"));
                                    Double cost = Double.parseDouble(json_row.getString("cost"));
                                    Double fee = Double.parseDouble(json_row.getString("fee"));
                                    Double vol = Double.parseDouble(json_row.getString("vol"));
                                    Double margin = Double.parseDouble(json_row.getString("margin"));
                                    String misc = json_row.getString("misc");
                                    insertTrade(orderTxId, pair, time, type, orderType, price, cost, fee, vol, margin, misc);
                                    logger.info("New Kraken trade has been inserted : " + orderTxId);
                                }
                            }

                            offset = offset + 50;

                            // delay 10ms to be sure the nonce will change
                            new android.os.Handler().postDelayed(
                                    new Runnable() {
                                        public void run() {
                                            populateTradeHistory();
                                        }
                                    },
                                    10
                            );
                        }

                        List<KrakenTrade> trades = getAllTrades();
                        for (KrakenTrade t : trades) {
                            logger.info("==================> " + t.getOrderTxId() + " " + t.getPair() + " " + t.getTime() + " " + t.getType() + " " + t.getOrderType() + " " + t.getPrice() + " " + t.getCost() + " " + t.getFee() + " " + t.getVol() + " " + t.getMargin() + " " + t.getMisc());
                        }


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject response) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                logger.warning("Failed to call Kraken API : " + statusCode);
                try {
                    logger.warning("Message is : " + response.getString("error"));
                } catch (JSONException jse) {
                    jse.printStackTrace();
                }
            }
        });
    }

    /**
     * Populate trade history table from remote exchange
     */
    public void populateLedgerInfo() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2016);
        cal.set(Calendar.MONTH, Calendar.DECEMBER);
        cal.set(Calendar.DAY_OF_MONTH, 13);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        String start = String.valueOf(cal.getTimeInMillis() / 1000);
        String domain = properties.getProperty("KRAKEN_API_BASE_URL");
        String key = properties.getProperty("KRAKEN_API_PUBLIC_KEY");

        String nonce = String.valueOf(System.currentTimeMillis());
        String path = "/0/private/Ledgers";

        RequestParams params = new RequestParams();
        params.add("nonce", nonce);
        params.add("start", start);
        params.add("ofs", String.valueOf(offset));

        String sign = calculateSignature(path, nonce, params.toString());

        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("API-Key", key);
        client.addHeader("API-Sign", sign);

        client.post(domain + path, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                logger.warning("Error");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                // populate data from remote exchange
                try {
                    if (response.getJSONArray("error").length() > 0) {
                        logger.warning("Error when trying to get Kraken trading history : " + response.getString("error"));
                    } else {

                        JSONObject json_data = response.getJSONObject("result").getJSONObject("ledger");

                        if (json_data.length() > 0) {

                            //get any existing trades
                            List<String> ledgerIds = getLedgerIds();

                            for (Iterator<String> rows = json_data.keys(); rows.hasNext(); ) {
                                String id = rows.next();
                                JSONObject json_row = json_data.getJSONObject(id);
                                if (!ledgerIds.contains(id)) {
                                    String refId = json_row.getString("refid");
                                    Long time = Double.valueOf(json_row.getString("time")).longValue();
                                    String type = json_row.getString("type");
                                    String aClass = json_row.getString("aclass");
                                    String asset = json_row.getString("asset");
                                    Double amount = Double.parseDouble(json_row.getString("amount"));
                                    Double fee = Double.parseDouble(json_row.getString("fee"));
                                    Double balance = Double.parseDouble(json_row.getString("balance"));
                                    insertLedgerInfo(refId, time, type, aClass, asset, amount, fee, balance);
                                    logger.info("New Kraken ledger row has been inserted : " + id);
                                }
                            }

                            offset = offset + 50;

                            // delay 10ms to be sure the nonce will change
                            new android.os.Handler().postDelayed(
                                    new Runnable() {
                                        public void run() {
                                            populateLedgerInfo();
                                        }
                                    },
                                    10
                            );
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject response) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                logger.warning("Failed to call Kraken API : " + statusCode);
                try {
                    logger.warning("Message is : " + response.getString("error"));
                } catch (JSONException jse) {
                    jse.printStackTrace();
                }
            }
        });
    }
    //endregion

}