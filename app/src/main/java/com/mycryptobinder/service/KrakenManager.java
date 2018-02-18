/*
 * Copyright (c) 2018 by Yann39.
 *
 * This file is part of MyCryptoBinder.
 *
 * MyCryptoBinder is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MyCryptoBinder is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MyCryptoBinder. If not, see <http://www.gnu.org/licenses/>.
 */

package com.mycryptobinder.service;

import android.content.Context;
import android.util.Base64;

import com.mycryptobinder.entities.AppDatabase;
import com.mycryptobinder.entities.Currency;
import com.mycryptobinder.entities.Exchange;
import com.mycryptobinder.entities.KrakenAsset;
import com.mycryptobinder.entities.KrakenAssetPair;
import com.mycryptobinder.entities.KrakenTrade;
import com.mycryptobinder.entities.Transaction;
import com.mycryptobinder.helpers.UtilsHelper;
import com.mycryptobinder.models.KrakenAssetPairValue;
import com.mycryptobinder.models.KrakenAssetPairs;
import com.mycryptobinder.models.KrakenAssetValue;
import com.mycryptobinder.models.KrakenAssets;
import com.mycryptobinder.models.KrakenTradeValue;
import com.mycryptobinder.models.KrakenTrades;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import retrofit2.Call;

public class KrakenManager {

    private static Properties properties;
    private static AppDatabase appDatabase;
    private static String key;
    private static String path;
    private static KrakenService krakenService;
    private static List<KrakenTrade> krakenTradeEntities;
    private static long startTime;

    public KrakenManager(Context context) {
        appDatabase = AppDatabase.getDatabase(context);
        properties = new Properties();
        try {
            InputStream inputStream = context.getAssets().open("myCryptoBinder.properties");
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        UtilsHelper uh = new UtilsHelper(context);
        key = properties.getProperty("KRAKEN_API_PUBLIC_KEY");
        path = "/0/private/TradesHistory";
        krakenService = KrakenService.retrofit.create(KrakenService.class);
        krakenTradeEntities = new ArrayList<>();
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
     * Calculate the signature for the Kraken API call
     * Message signature using HMAC-SHA512 of (URI path + SHA256(nonce + POST data)) and base64 decoded secret API key
     *
     * @param path  The path of the method to be called (URL part after host name)
     * @param nonce The nonce (always increasing unsigned 64 bit integer, usually the timestamp)
     * @param data  POST data
     * @return The base64 encoded signature string
     */
    private String calculateKrakenSignature(String path, String nonce, String data) {
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
     * Retrieve trade history from Kraken exchange API
     *
     * @param offset Result offset (there is a limitation to 50 results per call)
     */
    private void getKrakenTrades(int offset) {

        String start = String.valueOf(startTime);
        String nonce = String.valueOf(System.currentTimeMillis());
        //use alphabetical order as it must be in the same order as POST body parameters
        String parameters = "nonce=" + nonce + "&ofs=" + offset + "&start=" + start;
        String sign = calculateKrakenSignature(path, nonce, parameters);

        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("API-Key", key);
        headerMap.put("API-Sign", sign);

        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("nonce", nonce);
        paramsMap.put("start", start);
        paramsMap.put("ofs", String.valueOf(offset));

        KrakenTrades krakenTrades = null;
        try {
            Call<KrakenTrades> call = krakenService.getTradeHistory(headerMap, paramsMap);
            krakenTrades = call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // if it returned something without error
        if (krakenTrades != null && krakenTrades.getError().size() < 1 && krakenTrades.getResult() != null && krakenTrades.getResult().getTrades().size() > 0) {

            //get any existing asset
            List<String> trades = appDatabase.krakenTradeDao().getTradeIds();

            for (Map.Entry<String, KrakenTradeValue> entry : krakenTrades.getResult().getTrades().entrySet()) {
                String trade = entry.getKey();

                // keep only if it does not already exists
                if (trades == null || !trades.contains(trade)) {
                    KrakenTradeValue krakenTradeValue = entry.getValue();
                    String orderTxId = krakenTradeValue.getOrderTxId();
                    String pair = krakenTradeValue.getPair();
                    Double time = krakenTradeValue.getTime();
                    String type = krakenTradeValue.getType();
                    String orderType = krakenTradeValue.getOrderType();
                    Double price = krakenTradeValue.getPrice();
                    Double cost = krakenTradeValue.getCost();
                    Double fee = krakenTradeValue.getFee();
                    Double vol = krakenTradeValue.getVol();
                    Double margin = krakenTradeValue.getMargin();
                    String misc = krakenTradeValue.getMisc();

                    // Kraken BTC is named as XBT, change it...
                    if (pair.contains("XBT")) {
                        pair = pair.replaceAll("XBT", "BTC");
                    }

                    krakenTradeEntities.add(new KrakenTrade(orderTxId, pair, time, type, orderType, price, cost, fee, vol, margin, misc));
                }
            }

            getKrakenTrades(offset + 50);
        }
    }

    /**
     * Insert trade history from Kraken exchange API
     *
     * @return The number of trades inserted
     */
    public int populateTradeHistory() {
        getKrakenTrades(0);
        if (krakenTradeEntities != null && krakenTradeEntities.size() > 0) {
            KrakenTrade[] krakenTradeArray = new KrakenTrade[krakenTradeEntities.size()];
            krakenTradeArray = krakenTradeEntities.toArray(krakenTradeArray);
            appDatabase.krakenTradeDao().insert(krakenTradeArray);
            return krakenTradeEntities.size();
        }
        return 0;
    }

    /**
     * Insert Kraken exchange if it does not exist
     *
     * @return 1 if exchange has been inserted, 0 if not
     */
    public int populateExchange() {
        if (appDatabase.exchangeDao().getByName("Kraken") == null) {
            appDatabase.exchangeDao().insert(new Exchange("Kraken", "https://www.kraken.com", "Kraken exchange"));
            return 1;
        } else {
            return 0;
        }
    }

    /**
     * Insert asset pairs from Kraken exchange API
     *
     * @return number of asset pairs inserted, or -1 if an error occurred
     */
    public int populateAssetPairs() {
        KrakenAssetPairs krakenAssetPairs;
        try {
            // API synchronous call
            KrakenService krakenService = KrakenService.retrofit.create(KrakenService.class);
            Call<KrakenAssetPairs> call = krakenService.getAssetPairs();
            krakenAssetPairs = call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
        List<KrakenAssetPair> krakenAssetPairEntities = new ArrayList<>();
        // if it returned something without error
        if (krakenAssetPairs != null && krakenAssetPairs.getError().size() < 1) {

            // get any existing asset pairs
            List<String> assetPairs = appDatabase.krakenAssetPairDao().getPairs();

            for (Map.Entry<String, KrakenAssetPairValue> entry : krakenAssetPairs.getResult().entrySet()) {
                String assetPair = entry.getKey();

                // Kraken BTC is named as XBT, change it...
                if (assetPair.contains("XBT")) {
                    assetPair = assetPair.replaceAll("XBT", "BTC");
                }

                // keep only if it does not already exists
                if (assetPairs == null || !assetPairs.contains(assetPair)) {
                    KrakenAssetPairValue krakenAssetPairValue = entry.getValue();
                    String altname = krakenAssetPairValue.getAltName();
                    String base = krakenAssetPairValue.getBase();
                    String quote = krakenAssetPairValue.getQuote();
                    krakenAssetPairEntities.add(new KrakenAssetPair(assetPair, altname, base, quote));
                }
            }

            // insert into the database
            KrakenAssetPair[] krakenAssetPairsArray = new KrakenAssetPair[krakenAssetPairEntities.size()];
            krakenAssetPairsArray = krakenAssetPairEntities.toArray(krakenAssetPairsArray);
            appDatabase.krakenAssetPairDao().insert(krakenAssetPairsArray);
        } else {
            return 0;
        }
        return krakenAssetPairEntities.size();
    }

    /**
     * Insert assets from Kraken exchange API
     *
     * @return number of assets inserted, or -1 if an error occurred
     */
    public int populateAssets() {
        KrakenAssets krakenAssets;
        try {
            // API synchronous call
            KrakenService krakenService = KrakenService.retrofit.create(KrakenService.class);
            Call<KrakenAssets> call = krakenService.getAssets();
            krakenAssets = call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
        List<KrakenAsset> krakenAssetEntities = new ArrayList<>();
        // if it returned something without error
        if (krakenAssets != null && krakenAssets.getError().size() < 1) {

            //get any existing asset
            List<String> assets = appDatabase.krakenAssetDao().getName();

            for (Map.Entry<String, KrakenAssetValue> entry : krakenAssets.getResult().entrySet()) {
                String asset = entry.getKey();

                // Kraken BTC is named as XBT, change it...
                if (asset.contains("XBT")) {
                    asset = asset.replaceAll("XBT", "BTC");
                }

                // keep only if it does not already exists
                if (assets == null || !assets.contains(asset)) {
                    KrakenAssetValue krakenAssetValue = entry.getValue();
                    String altname = krakenAssetValue.getAltName();
                    krakenAssetEntities.add(new KrakenAsset(asset, altname));
                }
            }

            // insert into the database
            KrakenAsset[] krakenAssetArray = new KrakenAsset[krakenAssetEntities.size()];
            krakenAssetArray = krakenAssetEntities.toArray(krakenAssetArray);
            appDatabase.krakenAssetDao().insert(krakenAssetArray);
        } else {
            return 0;
        }
        return krakenAssetEntities.size();
    }

    /**
     * Insert currencies from Kraken assets
     *
     * @return number of currencies inserted
     */
    public int populateCurrencies() {
        List<Currency> krakenCurrencyList = appDatabase.currencyDao().getFromKraken();
        if (krakenCurrencyList != null) {
            Currency[] currencyArray = new Currency[krakenCurrencyList.size()];
            currencyArray = krakenCurrencyList.toArray(currencyArray);
            appDatabase.currencyDao().insert(currencyArray);
            return krakenCurrencyList.size();
        } else {
            return 0;
        }
    }

    /**
     * Insert transaction from Kraken assets
     *
     * @return number of transactions inserted
     */
    public int populateTransactions() {
        List<Transaction> transactionList = appDatabase.transactionDao().getKrakenTransactions();
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
        appDatabase.krakenTradeDao().deleteAll();
        appDatabase.krakenAssetDao().deleteAll();
        appDatabase.krakenAssetPairDao().deleteAll();
    }

}
