package com.mycryptobinder.service;

import android.content.Context;

import com.mycryptobinder.entities.AppDatabase;
import com.mycryptobinder.entities.Currency;
import com.mycryptobinder.entities.Exchange;
import com.mycryptobinder.entities.PoloniexAsset;
import com.mycryptobinder.entities.PoloniexTrade;
import com.mycryptobinder.entities.Transaction;
import com.mycryptobinder.helpers.UtilsHelper;
import com.mycryptobinder.models.PoloniexAssetValue;
import com.mycryptobinder.models.PoloniexTradeValue;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import retrofit2.Call;

/**
 * Created by Yann
 * Created on 04/12/2017
 */

public class PoloniexManager {

    private static Properties properties;
    private static AppDatabase appDatabase;
    private static SimpleDateFormat sdf;
    private static String key;
    private static PoloniexService poloniexService;
    private static List<PoloniexTrade> poloniexTradeEntities;
    private static long startTime;

    public PoloniexManager(Context context) {
        appDatabase = AppDatabase.getDatabase(context);
        properties = new Properties();
        try {
            InputStream inputStream = context.getAssets().open("myCryptoBinder.properties");
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        UtilsHelper uh = new UtilsHelper(context);
        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", uh.getCurrentLocale());
        key = properties.getProperty("POLONIEX_API_PUBLIC_KEY");
        poloniexService = PoloniexService.retrofit.create(PoloniexService.class);
        poloniexTradeEntities = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2016);
        cal.set(Calendar.MONTH, Calendar.DECEMBER);
        cal.set(Calendar.DAY_OF_MONTH, 13);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        startTime = cal.getTimeInMillis() / 1000;
    }

    /**
     * Calculate the signature for the Poloniex API call
     * Message signature using HMAC-SHA512 of (URI path + SHA256(nonce + POST data)) and base64 decoded secret API key
     *
     * @param data POST data
     * @return The base64 encoded signature string
     */
    private String calculatePoloniexSignature(String data) {
        String signature = null;
        try {
            Mac mac = Mac.getInstance("HmacSHA512");
            String secret = properties.getProperty("POLONIEX_API_PRIVATE_KEY");
            mac.init(new SecretKeySpec(secret.getBytes("UTF-8"), "HmacSHA512"));
            signature = bytesToHex(mac.doFinal(data.getBytes("UTF-8")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return signature;
    }

    /**
     * Convert a byte array to an hexadecimal String
     *
     * @param bytes The byte array to convert
     * @return The converted byte array as String
     */
    private String bytesToHex(byte[] bytes) {
        char[] hexArray = "0123456789ABCDEF".toCharArray();
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    private void getPoloniexTrades() {

        String start = String.valueOf(startTime);
        String nonce = String.valueOf(System.currentTimeMillis());
        String parameters = "currencyPair=all&command=returnTradeHistory&start=" + start + "&limit=10000&nonce=" + nonce;
        String sign = calculatePoloniexSignature(parameters);

        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("Key", key);
        headerMap.put("Sign", sign);

        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("command", "returnTradeHistory");
        paramsMap.put("currencyPair", "all");
        paramsMap.put("start", start);
        paramsMap.put("nonce", nonce);
        paramsMap.put("limit", "10000");

        Map<String, List<PoloniexTradeValue>> poloniexTrades = null;
        try {
            Call<Map<String, List<PoloniexTradeValue>>> call = poloniexService.getTradeHistory(headerMap, paramsMap);
            //todo return {"error":"Invalid API key\/secret pair."} if error so it raises Expected BEGIN_ARRAY but was STRING
            poloniexTrades = call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // if it returned something without error
        if (poloniexTrades != null && !poloniexTrades.isEmpty()) {
            int cpt = 0;
            Date highestDate = new Date(startTime);

            // get any existing trades
            List<Long> trades = appDatabase.poloniexTradeDao().getTradeIds();

            for (Map.Entry<String, List<PoloniexTradeValue>> entry : poloniexTrades.entrySet()) {
                String pair = entry.getKey();
                for (PoloniexTradeValue poloniexTradeValue : entry.getValue()) {
                    Long globalTradeId = poloniexTradeValue.getGlobalTradeID();
                    // keep only if it does not already exists
                    if (trades == null || !trades.contains(globalTradeId)) {
                        //get all values as strings
                        String tradeIdStr = poloniexTradeValue.getTradeId();
                        String dateStr = poloniexTradeValue.getDate();
                        String rateStr = poloniexTradeValue.getRate();
                        String amountStr = poloniexTradeValue.getAmount();
                        String totalStr = poloniexTradeValue.getTotal();
                        String feeStr = poloniexTradeValue.getFee();
                        String orderNumberStr = poloniexTradeValue.getOrderNumber();
                        String type = poloniexTradeValue.getType();
                        String category = poloniexTradeValue.getCategory();

                        Long tradeId = Long.parseLong(tradeIdStr);
                        Date date = null;
                        try {
                            date = sdf.parse(dateStr);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Double rate = Double.parseDouble(rateStr);
                        Double amount = Double.parseDouble(amountStr);
                        Double total = Double.parseDouble(totalStr);
                        Double fee = Double.parseDouble(feeStr);
                        Long orderNumber = Long.parseLong(orderNumberStr);

                        if (highestDate.before(date)) {
                            startTime = date.getTime();
                        }

                        poloniexTradeEntities.add(new PoloniexTrade(pair, globalTradeId, tradeId, date, rate, amount, total, fee, orderNumber, type, category));
                    }
                    cpt++;
                }
            }

            // API result is limited to 10000 entries maximum, so if result contains 10000 entries, call it again with a start time corresponding to the highest found trade date
            if (cpt > 10000) {
                getPoloniexTrades();
            }
        }


    }

    /**
     * Insert trade history from Poloniex exchange API
     *
     * @return The number of trades inserted
     */
    public int populateTradeHistory() {
        getPoloniexTrades();
        if (poloniexTradeEntities != null && !poloniexTradeEntities.isEmpty()) {
            // insert into the database
            PoloniexTrade[] poloniexTradesArray = new PoloniexTrade[poloniexTradeEntities.size()];
            poloniexTradesArray = poloniexTradeEntities.toArray(poloniexTradesArray);
            appDatabase.poloniexTradeDao().insert(poloniexTradesArray);
            return poloniexTradeEntities.size();
        }
        return 0;
    }

    /**
     * Insert Poloniex exchange if it does not exist
     *
     * @return 1 if exchange has been inserted, 0 if not
     */
    public int populateExchange() {
        if (appDatabase.exchangeDao().getByName("Poloniex") == null) {
            appDatabase.exchangeDao().insert(new Exchange("Poloniex", "https://www.poloniex.com", "Poloniex exchange"));
            return 1;
        } else {
            return 0;
        }
    }

    /**
     * Insert assets from Poloniex exchange API
     *
     * @return number of assets inserted, or -1 if an error occurred
     */
    public int populateAssets() {
        Map<String, PoloniexAssetValue> poloniexAssets = null;
        try {
            // API synchronous call
            PoloniexService poloniexService = PoloniexService.retrofit.create(PoloniexService.class);
            Call<Map<String, PoloniexAssetValue>> call = poloniexService.getAssets();
            poloniexAssets = call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<PoloniexAsset> poloniexAssetEntities = new ArrayList<>();
        // if it returned something without error
        if (poloniexAssets != null && !poloniexAssets.isEmpty()) {

            //get any existing asset
            List<String> assets = appDatabase.poloniexAssetDao().getCodes();

            for (Map.Entry<String, PoloniexAssetValue> entry : poloniexAssets.entrySet()) {
                String asset = entry.getKey();

                // keep only if it does not already exists
                if (assets == null || !assets.contains(asset)) {
                    PoloniexAssetValue poloniexAssetValue = entry.getValue();
                    String name = poloniexAssetValue.getName();
                    poloniexAssetEntities.add(new PoloniexAsset(asset, name));
                }
            }

            // insert into the database
            PoloniexAsset[] poloniexAssetArray = new PoloniexAsset[poloniexAssetEntities.size()];
            poloniexAssetArray = poloniexAssetEntities.toArray(poloniexAssetArray);
            appDatabase.poloniexAssetDao().insert(poloniexAssetArray);
        } else {
            return 0;
        }
        return poloniexAssetEntities.size();
    }

    /**
     * Insert currencies from Poloniex assets
     *
     * @return number of currencies inserted
     */
    public int populateCurrencies() {
        List<Currency> poloniexCurrencyList = appDatabase.currencyDao().getFromPoloniex();
        if (poloniexCurrencyList != null) {
            Currency[] currencyArray2 = new Currency[poloniexCurrencyList.size()];
            currencyArray2 = poloniexCurrencyList.toArray(currencyArray2);
            appDatabase.currencyDao().insert(currencyArray2);
            return poloniexCurrencyList.size();
        } else {
            return 0;
        }
    }

    /**
     * Insert transaction from Poloniex assets
     *
     * @return number of transactions inserted
     */
    public int populateTransactions() {
        List<Transaction> transactionList = appDatabase.transactionDao().getPoloniexTransactions();
        if (transactionList != null) {
            Transaction[] transactionArray = new Transaction[transactionList.size()];
            transactionArray = transactionList.toArray(transactionArray);
            appDatabase.transactionDao().insert(transactionArray);
            return transactionList.size();
        } else {
            return 0;
        }
    }

    /**
     * delete all data
     */
    public void deleteAll() {
        appDatabase.poloniexTradeDao().deleteAll();
        appDatabase.poloniexAssetDao().deleteAll();
    }

}
