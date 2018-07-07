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

package com.mycryptobinder.managers;

import android.content.Context;
import android.text.TextUtils;
import android.util.Base64;
import android.widget.Toast;

import com.mycryptobinder.R;
import com.mycryptobinder.entities.AppDatabase;
import com.mycryptobinder.entities.Currency;
import com.mycryptobinder.entities.Exchange;
import com.mycryptobinder.entities.Transaction;
import com.mycryptobinder.entities.kraken.KrakenAsset;
import com.mycryptobinder.entities.kraken.KrakenAssetPair;
import com.mycryptobinder.entities.kraken.KrakenTrade;
import com.mycryptobinder.helpers.UtilsHelper;
import com.mycryptobinder.models.kraken.KrakenAssetPairValue;
import com.mycryptobinder.models.kraken.KrakenAssetPairs;
import com.mycryptobinder.models.kraken.KrakenAssetValue;
import com.mycryptobinder.models.kraken.KrakenAssets;
import com.mycryptobinder.models.kraken.KrakenTradeValue;
import com.mycryptobinder.models.kraken.KrakenTrades;
import com.mycryptobinder.services.KrakenService;

import java.io.IOException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import retrofit2.Call;

public class KrakenManager {

    private static AppDatabase appDatabase;
    private static KrakenService krakenService;
    private static List<KrakenTrade> krakenTradeEntities;
    private final Context context;
    private String publicKey;
    private String privateKey;

    public KrakenManager(Context context) {
        this.context = context;
        appDatabase = AppDatabase.getInstance(context);
        krakenService = KrakenService.retrofit.create(KrakenService.class);
        krakenTradeEntities = new ArrayList<>();
    }

    /**
     * Calculate the signature for the Kraken API calls
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
            mac.init(new SecretKeySpec(Base64.decode(privateKey.getBytes(), 2), "HmacSHA512"));
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
     * @param offset Result offset (there is a limitation of 50 results per call)
     */
    private void getKrakenTrades(int offset) {
        String nonce = String.valueOf(System.currentTimeMillis());
        // use alphabetical order as it must be in the same order as POST body parameters
        String parameters = "nonce=" + nonce + "&ofs=" + offset;
        String sign = calculateKrakenSignature("/0/private/TradesHistory", nonce, parameters);

        // query headers
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("API-Key", publicKey);
        headerMap.put("API-Sign", sign);

        // query parameters, here order doesn't matter as we will order them in an interceptor while sending request
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("nonce", nonce);
        paramsMap.put("ofs", String.valueOf(offset));

        // service call
        KrakenTrades krakenTrades = null;
        String errorMessage = null;
        try {
            Call<KrakenTrades> call = krakenService.getTradeHistory(headerMap, paramsMap);
            krakenTrades = call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
            errorMessage = e.getLocalizedMessage();
        }

        // if an error occurred on service call
        if (errorMessage != null) {
            // show a notification about the error
            Toast.makeText(context, context.getString(R.string.error_retrieving_data_from_api, errorMessage), Toast.LENGTH_SHORT).show();
        }
        // if service call returned null
        else if (krakenTrades == null) {
            // show a notification about the error
            Toast.makeText(context, context.getString(R.string.error_service_call_returned_null), Toast.LENGTH_SHORT).show();
        }
        // if an error was returned in the response body
        else if (krakenTrades.getError() != null && krakenTrades.getError().size() > 0) {
            errorMessage = TextUtils.join("; ", krakenTrades.getError());
            // show a notification about the error
            Toast.makeText(context, context.getString(R.string.error_retrieving_data_from_api, errorMessage), Toast.LENGTH_SHORT).show();
        } else {
            // if at least one result
            if (krakenTrades.getResult() != null && krakenTrades.getResult().getTrades() != null && krakenTrades.getResult().getTrades().size() > 0) {

                //todo make sure kraken trade ids are unique ?
                //get any existing trade
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

                        // Kraken BTC is named XBT, change it...
                        if (pair.contains("XBT")) {
                            pair = pair.replaceAll("XBT", "BTC");
                        }

                        // add it to our result set
                        krakenTradeEntities.add(new KrakenTrade(orderTxId, pair, time, type, orderType, price, cost, fee, vol, margin, misc));
                    }
                }

                // in case API returns max 50 results we have to loop
                getKrakenTrades(offset + 50);
            }
        }
    }

    /**
     * Insert trade history from Kraken exchange API
     *
     * @return The number of trades inserted
     */
    public int populateTradeHistory() {
        // get encryption key and vector from properties
        Properties properties = UtilsHelper.getProperties(context);
        String key = properties.getProperty("RSA_KEY");
        String initVector = properties.getProperty("RSA_INIT_VECTOR");

        // get encrypted public API key from database and decrypt it
        String encryptedPublicKey = appDatabase.exchangeDao().getByName("Kraken").getPublicApiKey();
        publicKey = UtilsHelper.decrypt(key, initVector, encryptedPublicKey);

        // get encrypted private API key from database and decrypt it
        String encryptedPrivateKey = appDatabase.exchangeDao().getByName("Kraken").getPrivateApiKey();
        privateKey = UtilsHelper.decrypt(key, initVector, encryptedPrivateKey);

        // call to recursive function
        getKrakenTrades(0);

        // insert results into the database
        if (krakenTradeEntities != null && krakenTradeEntities.size() > 0) {
            KrakenTrade[] krakenTradeArray = new KrakenTrade[krakenTradeEntities.size()];
            krakenTradeArray = krakenTradeEntities.toArray(krakenTradeArray);
            appDatabase.krakenTradeDao().insert(krakenTradeArray);
            return krakenTradeEntities.size();
        }
        return 0;
    }

    /**
     * Insert asset pairs from Kraken exchange API
     *
     * @return number of asset pairs inserted, or -1 if an error occurred
     */
    public int populateAssetPairs() {
        KrakenAssetPairs krakenAssetPairs = null;
        String errorMessage = null;
        try {
            // API synchronous call
            KrakenService krakenService = KrakenService.retrofit.create(KrakenService.class);
            Call<KrakenAssetPairs> call = krakenService.getAssetPairs();
            krakenAssetPairs = call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
            errorMessage = e.getLocalizedMessage();
        }

        // if an error occurred on service call
        if (errorMessage != null) {
            // show a notification about the error
            Toast.makeText(context, context.getString(R.string.error_retrieving_data_from_api, errorMessage), Toast.LENGTH_SHORT).show();
            return -1;
        }
        // if service call returned null
        else if (krakenAssetPairs == null) {
            // show a notification about the error
            Toast.makeText(context, context.getString(R.string.error_service_call_returned_null), Toast.LENGTH_SHORT).show();
            return -1;
        }
        // if an error was returned in the response body
        else if (krakenAssetPairs.getError() != null && krakenAssetPairs.getError().size() > 0) {
            errorMessage = TextUtils.join("; ", krakenAssetPairs.getError());
            // show a notification about the error
            Toast.makeText(context, context.getString(R.string.error_retrieving_data_from_api, errorMessage), Toast.LENGTH_SHORT).show();
            return -1;
        } else {
            List<KrakenAssetPair> krakenAssetPairEntities = new ArrayList<>();

            // if at least one result
            if (krakenAssetPairs.getResult() != null && krakenAssetPairs.getResult().size() > 0) {

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
    }

    /**
     * Insert assets from Kraken exchange API
     *
     * @return number of assets inserted, or -1 if an error occurred
     */
    public int populateAssets() {
        KrakenAssets krakenAssets = null;
        String errorMessage = null;
        try {
            // API synchronous call
            KrakenService krakenService = KrakenService.retrofit.create(KrakenService.class);
            Call<KrakenAssets> call = krakenService.getAssets();
            krakenAssets = call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
            errorMessage = e.getLocalizedMessage();
        }

        // if an error occurred on service call
        if (errorMessage != null) {
            // show a notification about the error
            Toast.makeText(context, context.getString(R.string.error_retrieving_data_from_api, errorMessage), Toast.LENGTH_SHORT).show();
            return -1;
        }
        // if service call returned null
        else if (krakenAssets == null) {
            // show a notification about the error
            Toast.makeText(context, context.getString(R.string.error_service_call_returned_null), Toast.LENGTH_SHORT).show();
            return -1;
        }
        // if an error was returned in the response body
        else if (krakenAssets.getError() != null && krakenAssets.getError().size() > 0) {
            errorMessage = TextUtils.join("; ", krakenAssets.getError());
            // show a notification about the error
            Toast.makeText(context, context.getString(R.string.error_retrieving_data_from_api, errorMessage), Toast.LENGTH_SHORT).show();
            return -1;
        } else {
            List<KrakenAsset> krakenAssetEntities = new ArrayList<>();

            // if at least one result
            if (krakenAssets.getResult() != null && krakenAssets.getResult().size() > 0) {

                //get any existing asset
                List<String> assets = appDatabase.krakenAssetDao().getName();

                for (Map.Entry<String, KrakenAssetValue> entry : krakenAssets.getResult().entrySet()) {
                    String asset = entry.getKey();

                    // Kraken BTC is named XBT, change it...
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
    }

    /**
     * Insert currencies from Kraken assets
     *
     * @return number of currencies inserted
     */
    public int populateCurrencies() {
        List<Currency> currencyList = appDatabase.currencyDao().getFromKraken();
        if (currencyList != null) {
            Currency[] currencyArray = new Currency[currencyList.size()];
            currencyArray = currencyList.toArray(currencyArray);
            appDatabase.currencyDao().insert(currencyArray);
            return currencyList.size();
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
     * Delete all data
     */
    public void deleteAll() {
        appDatabase.krakenTradeDao().deleteAll();
        appDatabase.krakenAssetDao().deleteAll();
        appDatabase.krakenAssetPairDao().deleteAll();
    }

    /**
     * Check if the exchange API keys are defined
     */
    public boolean areApiKeysDefined() {
        Exchange exchange = appDatabase.exchangeDao().getByName("Kraken");
        return exchange != null
                && exchange.getPublicApiKey() != null
                && exchange.getPublicApiKey().length() > 0
                && exchange.getPrivateApiKey() != null
                && exchange.getPrivateApiKey().length() > 0;
    }

}
