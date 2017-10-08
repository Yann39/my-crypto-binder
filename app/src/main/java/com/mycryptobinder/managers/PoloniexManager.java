package com.mycryptobinder.managers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mycryptobinder.helpers.DatabaseHelper;
import com.mycryptobinder.helpers.UtilsHelper;
import com.mycryptobinder.models.Exchange;
import com.mycryptobinder.models.PoloniexAsset;
import com.mycryptobinder.models.PoloniexTrade;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Yann
 * Created on 05/09/2017
 */
public class PoloniexManager {

    //region Properties / constructors
    private DatabaseHelper dbHelper;
    private Context context;
    private SQLiteDatabase database;

    private static final Logger logger = Logger.getLogger(PoloniexManager.class.getName());

    public PoloniexManager(Context c) {
        context = c;
    }

    public PoloniexManager open() throws SQLException {
        dbHelper = new DatabaseHelper(context);
        database = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dbHelper.close();
    }
    //endregion

    //region Read

    //region Assets

    /**
     * Get the list of all assets from the database
     *
     * @return list of PoloniexAsset elements representing the assets
     */
    public List<PoloniexAsset> getAllAssets() {
        List<PoloniexAsset> list = new ArrayList<>();
        String[] columns = new String[]{DatabaseHelper.COLUMN_ID, DatabaseHelper.COLUMN_POLONIEX_ASSET_CODE, DatabaseHelper.COLUMN_POLONIEX_ASSET_NAME};
        Cursor cursor = database.query(DatabaseHelper.TABLE_POLONIEX_ASSETS, columns, null, null, null, null, null);
        if (cursor != null) {
            try {
                while (cursor.moveToNext()) {
                    PoloniexAsset asset = new PoloniexAsset();
                    asset.setId(cursor.getLong(0));
                    asset.setAssetCode(cursor.getString(1));
                    asset.setAssetName(cursor.getString(2));
                    list.add(asset);
                }
            } finally {
                cursor.close();
            }
        }
        return list;
    }

    /**
     * Get the list of all asset codes from the database
     *
     * @return list of asset codes as strings
     */
    public List<String> getAssetCodes() {
        List<String> list = new ArrayList<>();
        String[] columns = new String[]{DatabaseHelper.COLUMN_POLONIEX_ASSET_CODE};
        Cursor cursor = database.query(DatabaseHelper.TABLE_POLONIEX_ASSETS, columns, null, null, null, null, null);
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
     * @return list of PoloniexTrade elements representing the trades
     */
    public List<PoloniexTrade> getAllTrades() {
        List<PoloniexTrade> list = new ArrayList<>();
        String[] columns = new String[]{DatabaseHelper.COLUMN_ID, DatabaseHelper.COLUMN_POLONIEX_GLOBAL_TRADE_ID, DatabaseHelper.COLUMN_POLONIEX_PAIR, DatabaseHelper.COLUMN_POLONIEX_TRADE_ID, DatabaseHelper.COLUMN_POLONIEX_DATE, DatabaseHelper.COLUMN_POLONIEX_RATE, DatabaseHelper.COLUMN_POLONIEX_AMOUNT, DatabaseHelper.COLUMN_POLONIEX_TOTAL, DatabaseHelper.COLUMN_POLONIEX_FEE, DatabaseHelper.COLUMN_POLONIEX_ORDER_NUMBER, DatabaseHelper.COLUMN_POLONIEX_TYPE, DatabaseHelper.COLUMN_POLONIEX_CATEGORY};
        Cursor cursor = database.query(DatabaseHelper.TABLE_POLONIEX_TRADE_HISTORY, columns, null, null, null, null, null);
        if (cursor != null) {
            try {
                while (cursor.moveToNext()) {
                    PoloniexTrade trade = new PoloniexTrade();
                    trade.setId(cursor.getLong(0));
                    trade.setGlobalTradeID(cursor.getLong(1));
                    trade.setPair(cursor.getString(2));
                    trade.setTradeId(cursor.getLong(3));
                    trade.setDate(new Date(cursor.getLong(4)));
                    trade.setRate(cursor.getDouble(5));
                    trade.setAmount(cursor.getDouble(6));
                    trade.setTotal(cursor.getDouble(7));
                    trade.setFee(cursor.getDouble(8));
                    trade.setOrderNumber(cursor.getLong(9));
                    trade.setType(cursor.getString(10));
                    trade.setCategory(cursor.getString(11));
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
    public List<Long> getTradeTransactionIds() {
        List<Long> list = new ArrayList<>();
        String[] columns = new String[]{DatabaseHelper.COLUMN_POLONIEX_GLOBAL_TRADE_ID};
        Cursor cursor = database.query(DatabaseHelper.TABLE_POLONIEX_TRADE_HISTORY, columns, null, null, null, null, null);
        if (cursor != null) {
            try {
                while (cursor.moveToNext()) {
                    list.add(cursor.getLong(0));
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

    //region Assets

    /**
     * Insert a new asset into the database
     *
     * @param assetCode The asset code
     * @param assetName The asset name
     */
    public void insertAsset(String assetCode, String assetName) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.COLUMN_POLONIEX_ASSET_CODE, assetCode);
        contentValues.put(DatabaseHelper.COLUMN_POLONIEX_ASSET_NAME, assetName);
        database.insert(DatabaseHelper.TABLE_POLONIEX_ASSETS, null, contentValues);
    }

    /**
     * Update an existing asset in the database
     *
     * @param id        The id of the asset to edit
     * @param assetCode The asset code
     * @param assetName The asset name
     * @return An integer representing the number of rows affected
     */
    public int updateAsset(long id, String assetCode, String assetName) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.COLUMN_POLONIEX_ASSET_CODE, assetCode);
        contentValues.put(DatabaseHelper.COLUMN_POLONIEX_ASSET_NAME, assetName);
        return database.update(DatabaseHelper.TABLE_POLONIEX_ASSETS, contentValues, DatabaseHelper.COLUMN_ID + " = " + id, null);
    }

    /**
     * Delete an existing asset from the database
     *
     * @param id The id of the asset to delete
     */
    public void deleteAsset(long id) {
        database.delete(DatabaseHelper.TABLE_POLONIEX_ASSETS, DatabaseHelper.COLUMN_ID + "=" + id, null);
    }
    //endregion

    //region Trades

    /**
     * Insert a new trade into the database
     *
     * @param globalTradeID The global trade id
     * @param pair          The traded pair
     * @param tradeID       The trade id
     * @param date          The date as Unix timestamp
     * @param rate          The transaction rate
     * @param amount        The transaction amount
     * @param total         The transaction total
     * @param fee           The transaction fee
     * @param orderNumber   The order number
     * @param type          The type of order (buy/sell)
     * @param category      The category (usually "exchange")
     */
    public void insertTrade(Long globalTradeID, String pair, Long tradeID, Date date, Double rate, Double amount, Double total, Double fee, Long orderNumber, String type, String category) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.COLUMN_POLONIEX_GLOBAL_TRADE_ID, globalTradeID);
        contentValues.put(DatabaseHelper.COLUMN_POLONIEX_PAIR, pair);
        contentValues.put(DatabaseHelper.COLUMN_POLONIEX_TRADE_ID, tradeID);
        contentValues.put(DatabaseHelper.COLUMN_POLONIEX_DATE, date.getTime()/1000);
        contentValues.put(DatabaseHelper.COLUMN_POLONIEX_RATE, rate);
        contentValues.put(DatabaseHelper.COLUMN_POLONIEX_AMOUNT, amount);
        contentValues.put(DatabaseHelper.COLUMN_POLONIEX_TOTAL, total);
        contentValues.put(DatabaseHelper.COLUMN_POLONIEX_FEE, fee);
        contentValues.put(DatabaseHelper.COLUMN_POLONIEX_ORDER_NUMBER, orderNumber);
        contentValues.put(DatabaseHelper.COLUMN_POLONIEX_TYPE, type);
        contentValues.put(DatabaseHelper.COLUMN_POLONIEX_CATEGORY, category);
        database.insert(DatabaseHelper.TABLE_POLONIEX_TRADE_HISTORY, null, contentValues);
    }

    /**
     * Update an existing trade in the database
     *
     * @param id            The id of the trade to edit
     * @param globalTradeId The global trade id
     * @param pair          The traded pair
     * @param tradeId       The trade id
     * @param date          The date as Unix timestamp
     * @param rate          The transaction rate
     * @param amount        The transaction amount
     * @param total         The transaction total
     * @param fee           The transaction fee
     * @param orderNumber   The order number
     * @param type          The type of order (buy/sell)
     * @param category      The category (usually "exchange")
     */
    public int updateTrade(long id, Long globalTradeId, String pair, Long tradeId, Date date, Double rate, Double amount, Double total, Double fee, Long orderNumber, String type, String category) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.COLUMN_POLONIEX_GLOBAL_TRADE_ID, globalTradeId);
        contentValues.put(DatabaseHelper.COLUMN_POLONIEX_PAIR, pair);
        contentValues.put(DatabaseHelper.COLUMN_POLONIEX_TRADE_ID, tradeId);
        contentValues.put(DatabaseHelper.COLUMN_POLONIEX_DATE, date.getTime());
        contentValues.put(DatabaseHelper.COLUMN_POLONIEX_RATE, rate);
        contentValues.put(DatabaseHelper.COLUMN_POLONIEX_AMOUNT, amount);
        contentValues.put(DatabaseHelper.COLUMN_POLONIEX_TOTAL, total);
        contentValues.put(DatabaseHelper.COLUMN_POLONIEX_FEE, fee);
        contentValues.put(DatabaseHelper.COLUMN_POLONIEX_ORDER_NUMBER, orderNumber);
        contentValues.put(DatabaseHelper.COLUMN_POLONIEX_TYPE, type);
        contentValues.put(DatabaseHelper.COLUMN_POLONIEX_CATEGORY, category);
        return database.update(DatabaseHelper.TABLE_POLONIEX_TRADE_HISTORY, contentValues, DatabaseHelper.COLUMN_ID + " = " + id, null);
    }

    /**
     * Delete an existing trade from the database
     *
     * @param id The id of the trade to delete
     */
    public void deleteTrade(long id) {
        database.delete(DatabaseHelper.TABLE_POLONIEX_TRADE_HISTORY, DatabaseHelper.COLUMN_ID + "=" + id, null);
    }
    //endregion

    //endregion

    //region Poloniex API calls

    /**
     * Populate the Exchange table with the Poloniex exchange
     */
    public void populateExchange() {
        // insert exchange if it does not exist
        String name = "Poloniex";
        String link = "https://www.poloniex.com";
        String description = "Poloniex exchange";
        ExchangeManager em = new ExchangeManager(context);
        em.open();
        Exchange ex = em.getByName(name);
        boolean exist = ex != null && ex.getName() != null;
        if (!exist) {
            em.insert(name, link, description);
            logger.info("Poloniex exchange has been inserted : " + name);
        }
    }

    /**
     * Populate assets table from remote exchange
     */
    public void populateAssets() {

        AsyncHttpClient client = new AsyncHttpClient();
        client.get("https://poloniex.com/public?command=returnCurrencies", new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {

                    //get any existing assets
                    List<String> assCodes = getAssetCodes();
                    for (Iterator<String> assets = response.keys(); assets.hasNext(); ) {
                        String asset = assets.next();

                        // insert only if it does not already exists
                        if (!assCodes.contains(asset)) {
                            String assetName = response.getJSONObject(asset).getString("name");
                            insertAsset(asset, assetName);
                            logger.info("New Poloniex asset has been inserted : " + asset);
                        }

                    }

                    List<PoloniexAsset> asss = getAllAssets();
                    for (PoloniexAsset as : asss) {
                        logger.info("==================> " + as.getAssetCode() + " " + as.getAssetName());
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject response) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
            }
        });
    }

    /**
     * Calculate the signature for the Poloniex API call
     * Message signature using HMAC-SHA512 of (URI path + SHA256(nonce + POST data)) and base64 decoded secret API key
     *
     * @param data POST data
     * @return The base64 encoded signature string
     */
    private String calculateSignature(String data) {
        String signature = null;
        try {
            Mac mac = Mac.getInstance("HmacSHA512");
            String secret = "";
            mac.init(new SecretKeySpec(secret.getBytes("UTF-8"), "HmacSHA512"));
            signature = bytesToHex(mac.doFinal(data.getBytes("UTF-8")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return signature;
    }

    static String bytesToHex(byte[] bytes) {
        char[] hexArray = "0123456789ABCDEF".toCharArray();
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    /**
     * Populate trade history table from remote exchange
     */
    public void populateTradeHistory() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2016);
        cal.set(Calendar.MONTH, 12);
        cal.set(Calendar.DAY_OF_MONTH, 26);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        String start = String.valueOf(cal.getTime().getTime() / 1000);

        String domain = "https://poloniex.com/tradingApi";
        String key = "";
        String nonce = String.valueOf(System.currentTimeMillis());

        RequestParams params = new RequestParams();
        params.add("command", "returnTradeHistory");
        params.add("start", start);
        params.add("currencyPair", "all");
        params.add("nonce", nonce);

        String sign = calculateSignature(params.toString());

        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("Key", key);
        client.addHeader("Sign", sign);
        client.post(domain, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                try {
                    if (response != null && response.length() > 0) {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject json_data = response.getJSONObject(i);
                            Long globalTradeId = json_data.getLong("globalTradeID");
                            Long tradeId = json_data.getLong("tradeID");
                            Date date = new Date(Double.valueOf(json_data.getString("date")).longValue());
                            Double rate = json_data.getDouble("rate");
                            Double amount = json_data.getDouble("amount");
                            Double total = Double.parseDouble(json_data.getString("total"));
                            Double fee = Double.parseDouble(json_data.getString("fee"));
                            Long orderNumber = json_data.getLong("orderNumber");
                            String type = json_data.getString("type");
                            String category = json_data.getString("category");
                            logger.info("==================> " + globalTradeId + " " + tradeId + " " + date + " " + rate + " " + amount + " " + total + " " + fee + " " + orderNumber + " " + type + " " + category);
                        }
                    } else {
                        logger.warning("Response is empty");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                UtilsHelper uh = new UtilsHelper(context);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", uh.getCurrentLocale());

                //get any existing trades
                List<Long> txIds = getTradeTransactionIds();

                // populate data from remote exchange
                try {
                    for (Iterator<String> rows = response.keys(); rows.hasNext(); ) {
                        String curr = rows.next();
                        if (curr.equals("error")) {
                            logger.warning("Error when trying to get Poloniex trading history : " + response.getString("error"));
                        } else {
                            JSONArray json_arr = response.getJSONArray(curr);
                            for (int i = 0; i < json_arr.length(); i++) {
                                JSONObject json_data = json_arr.getJSONObject(i);
                                Long globalTradeId = json_data.getLong("globalTradeID");
                                if (!txIds.contains(globalTradeId)) {
                                    Long tradeId = json_data.getLong("tradeID");
                                    Date date = sdf.parse(json_data.getString("date"));
                                    Double rate = json_data.getDouble("rate");
                                    Double amount = json_data.getDouble("amount");
                                    Double total = Double.parseDouble(json_data.getString("total"));
                                    Double fee = Double.parseDouble(json_data.getString("fee"));
                                    Long orderNumber = json_data.getLong("orderNumber");
                                    String type = json_data.getString("type");
                                    String category = json_data.getString("category");
                                    insertTrade(globalTradeId, curr, tradeId, date, rate, amount, total, fee, orderNumber, type, category);
                                    logger.info("New trade has been inserted : " + globalTradeId);
                                }
                            }
                        }
                    }

                    List<PoloniexTrade> trades = getAllTrades();
                    for (PoloniexTrade t : trades) {
                        logger.info("==================> " + t.getGlobalTradeID() + " " + t.getPair() + " " + t.getTradeId() + " " + t.getDate() + " " + t.getRate() + " " + t.getAmount() + " " + t.getTotal() + " " + t.getFee() + " " + t.getOrderNumber() + " " + t.getType() + " " + t.getCategory());
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject response) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                logger.warning("Failed to call Poloniex API : " + statusCode);
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