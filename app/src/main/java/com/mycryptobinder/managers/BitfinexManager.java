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
import android.util.Base64;

import com.mycryptobinder.R;
import com.mycryptobinder.entities.AppDatabase;
import com.mycryptobinder.entities.Currency;
import com.mycryptobinder.entities.Exchange;
import com.mycryptobinder.entities.Transaction;
import com.mycryptobinder.entities.bitfinex.BitfinexAsset;
import com.mycryptobinder.entities.bitfinex.BitfinexOrder;
import com.mycryptobinder.entities.bitfinex.BitfinexSymbol;
import com.mycryptobinder.helpers.UtilsHelper;
import com.mycryptobinder.services.BitfinexService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import retrofit2.Call;

public class BitfinexManager {

    private static AppDatabase appDatabase;
    private static BitfinexService bitfinexService;
    private static List<BitfinexOrder> bitfinexOrderEntities;
    private static List<BitfinexAsset> bitfinexAssetEntities;
    private final Context context;
    private String publicKey;
    private String privateKey;

    public BitfinexManager(Context context) {
        this.context = context;
        appDatabase = AppDatabase.getInstance(context);
        bitfinexService = BitfinexService.retrofit.create(BitfinexService.class);
        bitfinexOrderEntities = new ArrayList<>();
        bitfinexAssetEntities = new ArrayList<>();

        // get encryption key and vector from properties
        Properties properties = UtilsHelper.getProperties(context);
        String key = properties.getProperty("RSA_KEY");
        String initVector = properties.getProperty("RSA_INIT_VECTOR");

        // get encrypted API keys from database and decrypt them
        Exchange exchange = appDatabase.exchangeDao().getByName("Bitfinex");
        if (exchange != null) {
            String encryptedPublicApiKey = exchange.getPublicApiKey();
            String encryptedPrivateApiKey = exchange.getPrivateApiKey();
            if (encryptedPublicApiKey != null) {
                publicKey = UtilsHelper.decrypt(key, initVector, encryptedPublicApiKey);
            }
            if (encryptedPrivateApiKey != null) {
                privateKey = UtilsHelper.decrypt(key, initVector, encryptedPrivateApiKey);
            }
        }
    }

    /**
     * Calculate the signature for the Bitfinex API call
     * Standard HMAC-SHA384 signing
     *
     * @param data POST data
     * @return The base64 encoded signature string
     */
    private String calculateBitfinexSignature(String data) {
        String signature = null;
        try {
            Mac mac = Mac.getInstance("HmacSHA384");
            mac.init(new SecretKeySpec(privateKey.getBytes("UTF-8"), "HmacSHA384"));
            signature = UtilsHelper.bytesToHex(mac.doFinal(data.getBytes("ASCII")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return signature;
    }

    /**
     * Get Bitfinex trade history
     */
    private void getBitfinexOrders() {
        String nonce = String.valueOf((new Date()).getTime());
        String parameters = "request=/v1/orders/hist?nonce=" + nonce;

        JSONObject jo = new JSONObject();
        try {
            jo.put("request", "/v1/orders/hist");
            jo.put("nonce", nonce);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String payload_base64 = Base64.encodeToString(jo.toString().getBytes(), Base64.NO_WRAP);
        String sign = calculateBitfinexSignature(payload_base64);

        // query header
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("Content-Type", "application/json");
        headerMap.put("Accept", "application/json");
        headerMap.put("X-BFX-APIKEY", publicKey);
        headerMap.put("X-BFX-PAYLOAD", payload_base64);
        headerMap.put("X-BFX-SIGNATURE", sign);

        // request parameters
        Map<String, String> paramsMap = new HashMap<>();
        headerMap.put("X-BFX-APIKEY", publicKey);
        headerMap.put("X-BFX-PAYLOAD", payload_base64);
        headerMap.put("X-BFX-SIGNATURE", sign);

        // service call
        List<com.mycryptobinder.models.bitfinex.BitfinexOrder> bitfinexOrders = null;
        String errorMessage = null;
        try {
            Call<List<com.mycryptobinder.models.bitfinex.BitfinexOrder>> call = bitfinexService.getOrderHistory(headerMap, paramsMap);
            bitfinexOrders = call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
            errorMessage = e.getLocalizedMessage();
        }

        // if an error occurred on service call
        if (errorMessage != null) {
            // show a notification about the error
            //Toast.makeText(context, context.getString(R.string.error_retrieving_data_from_api, errorMessage), Toast.LENGTH_SHORT).show();
            System.out.println(context.getString(R.string.error_retrieving_data_from_api, errorMessage));
        }
        // if service call returned null
        else if (bitfinexOrders == null) {
            // show a notification about the error
            //Toast.makeText(context, context.getString(R.string.error_service_call_returned_null), Toast.LENGTH_SHORT).show();
            System.out.println(context.getString(R.string.error_service_call_returned_null));
        } else {
            // if it returned something
            if (bitfinexOrders.size() > 0) {

                // get any existing trades
                List<Long> orders = appDatabase.bitfinexOrderDao().getOrderIds();

                for (com.mycryptobinder.models.bitfinex.BitfinexOrder bitfinexOrderValue : bitfinexOrders) {
                    long orderId = bitfinexOrderValue.getId();
                    // keep only if it does not already exists
                    if (orders == null || !orders.contains(orderId)) {
                        //get all values
                        String symbol = bitfinexOrderValue.getSymbol();
                        String exchange = bitfinexOrderValue.getExchange();
                        String priceStr = bitfinexOrderValue.getPrice();
                        String avgExecutionPriceStr = bitfinexOrderValue.getAvgExecutionPrice();
                        String side = bitfinexOrderValue.getSide();
                        String type = bitfinexOrderValue.getType();
                        String timeStampStr = bitfinexOrderValue.getTimestamp();
                        boolean isLive = bitfinexOrderValue.isLive();
                        boolean isCancelled = bitfinexOrderValue.isCancelled();
                        boolean isHidden = bitfinexOrderValue.isHidden();
                        boolean isWasForced = bitfinexOrderValue.isWasForced();
                        String originalAmountStr = bitfinexOrderValue.getOriginalAmount();
                        String remainingAmountStr = bitfinexOrderValue.getRemainingAmount();
                        String executedAmountStr = bitfinexOrderValue.getExecutedAmount();

                        Double price = null;
                        Double avgExecutionPrice = null;
                        Double timeStamp = null;
                        Double originalAmount = null;
                        Double remainingAmount = null;
                        Double executedAmount = null;
                        if (priceStr != null) {
                            try {
                                price = Double.parseDouble(priceStr);
                                avgExecutionPrice = Double.parseDouble(avgExecutionPriceStr);
                                timeStamp = Double.parseDouble(timeStampStr);
                                originalAmount = Double.parseDouble(originalAmountStr);
                                remainingAmount = Double.parseDouble(remainingAmountStr);
                                executedAmount = Double.parseDouble(executedAmountStr);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        bitfinexOrderEntities.add(new BitfinexOrder(orderId, symbol, exchange, price, avgExecutionPrice, side, type, timeStamp, isLive, isCancelled, isHidden, isWasForced, originalAmount, remainingAmount, executedAmount));
                    }
                }
            }
        }
    }

    /**
     * Insert order history from Bitfinex exchange API
     *
     * @return The number of orders inserted
     */
    public int populateOrderHistory() {
        getBitfinexOrders();
        if (bitfinexOrderEntities != null && !bitfinexOrderEntities.isEmpty()) {
            // insert into the database
            BitfinexOrder[] bitfinexTradesArray = new BitfinexOrder[bitfinexOrderEntities.size()];
            bitfinexTradesArray = bitfinexOrderEntities.toArray(bitfinexTradesArray);
            appDatabase.bitfinexOrderDao().insert(bitfinexTradesArray);
            return bitfinexOrderEntities.size();
        }
        return 0;
    }

    /**
     * Get Bitfinex assets from balances
     */
    private void getBitfinexAssets() {
        String nonce = String.valueOf((new Date()).getTime());
        String parameters = "/v1/balances?nonce=" + nonce;

        JSONObject jo = new JSONObject();
        try {
            jo.put("request", "/v1/orders/hist");
            jo.put("nonce", nonce);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String payload_base64 = Base64.encodeToString(jo.toString().getBytes(), Base64.NO_WRAP);
        String sign = calculateBitfinexSignature(payload_base64);

        // query header
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("X-BFX-APIKEY", publicKey);
        headerMap.put("X-BFX-PAYLOAD", payload_base64);
        headerMap.put("X-BFX-SIGNATURE", sign);

        // request parameters
        Map<String, String> paramsMap = new HashMap<>();
        headerMap.put("X-BFX-APIKEY", publicKey);
        headerMap.put("X-BFX-PAYLOAD", payload_base64);
        headerMap.put("X-BFX-SIGNATURE", sign);

        // service call
        List<com.mycryptobinder.models.bitfinex.BitfinexBalance> bitfinexBalances = null;
        String errorMessage = null;
        try {
            Call<List<com.mycryptobinder.models.bitfinex.BitfinexBalance>> call = bitfinexService.getBalances(headerMap, paramsMap);
            bitfinexBalances = call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
            errorMessage = e.getLocalizedMessage();
        }

        // if an error occurred on service call
        if (errorMessage != null) {
            // show a notification about the error
            //Toast.makeText(context, context.getString(R.string.error_retrieving_data_from_api, errorMessage), Toast.LENGTH_SHORT).show();
            System.out.println(context.getString(R.string.error_retrieving_data_from_api, errorMessage));
        }
        // if service call returned null
        else if (bitfinexBalances == null) {
            // show a notification about the error
            //Toast.makeText(context, context.getString(R.string.error_service_call_returned_null), Toast.LENGTH_SHORT).show();
            System.out.println(context.getString(R.string.error_service_call_returned_null));
        } else {
            // if it returned something
            if (bitfinexBalances.size() > 0) {

                // get any existing asset
                List<String> currencyNames = appDatabase.bitfinexAssetDao().getCodes();

                for (com.mycryptobinder.models.bitfinex.BitfinexBalance bitfinexBalanceValue : bitfinexBalances) {
                    String currencyName = bitfinexBalanceValue.getCurrency();
                    // keep only if it does not already exists
                    if (currencyNames == null || !currencyNames.contains(currencyName)) {
                        //get all values
                        String currency = bitfinexBalanceValue.getCurrency();

                        bitfinexAssetEntities.add(new BitfinexAsset(currency));
                    }
                }
            }
        }
    }

    /**
     * Insert assets from balances got from Bitfinex exchange API
     *
     * @return The number of assets inserted
     */
    public int populateAssets() {
        getBitfinexAssets();
        if (bitfinexAssetEntities != null && !bitfinexAssetEntities.isEmpty()) {
            // insert into the database
            BitfinexAsset[] bitfinexAssetsArray = new BitfinexAsset[bitfinexAssetEntities.size()];
            bitfinexAssetsArray = bitfinexAssetEntities.toArray(bitfinexAssetsArray);
            appDatabase.bitfinexAssetDao().insert(bitfinexAssetsArray);
            return bitfinexAssetEntities.size();
        }
        return 0;
    }

    /**
     * Insert symbols from Bitfinex exchange API
     *
     * @return number of symbols inserted, or -1 if an error occurred
     */
    public int populateSymbols() {
        List<String> bitfinexSymbols = null;
        try {
            // API synchronous call
            BitfinexService bitfinexService = BitfinexService.retrofit.create(BitfinexService.class);
            Call<List<String>> call = bitfinexService.getSymbols();
            bitfinexSymbols = call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<BitfinexSymbol> bitfinexSymbolEntities = new ArrayList<>();
        // if it returned something without error
        if (bitfinexSymbols != null && bitfinexSymbols.size() > 0) {

            //get any existing symbol
            List<String> symbols = appDatabase.bitfinexSymbolDao().getNames();

            for (String bitfinexSymbolValue : bitfinexSymbols) {
                // keep only if it does not already exists
                if (!symbols.contains(bitfinexSymbolValue)) {
                    bitfinexSymbolEntities.add(new BitfinexSymbol(bitfinexSymbolValue));
                }
            }

            // insert into the database
            BitfinexSymbol[] bitfinexSymbolArray = new BitfinexSymbol[bitfinexSymbolEntities.size()];
            bitfinexSymbolArray = bitfinexSymbolEntities.toArray(bitfinexSymbolArray);
            appDatabase.bitfinexSymbolDao().insert(bitfinexSymbolArray);
        } else {
            return 0;
        }
        return bitfinexSymbolEntities.size();
    }

    /**
     * Insert currencies from Bitfinex assets
     *
     * @return number of currencies inserted
     */
    public int populateCurrencies() {
        List<Currency> currencyList = appDatabase.currencyDao().getFromBitfinex();
        if (currencyList != null) {
            Currency[] currencyArray2 = new Currency[currencyList.size()];
            currencyArray2 = currencyList.toArray(currencyArray2);
            appDatabase.currencyDao().insert(currencyArray2);
            return currencyList.size();
        } else {
            return 0;
        }
    }

    /**
     * Insert transaction from Bitfinex assets
     *
     * @return number of transactions inserted
     */
    public int populateTransactions() {
        List<Transaction> transactionList = appDatabase.transactionDao().getBitfinexTransactions();
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
        appDatabase.bitfinexSymbolDao().deleteAll();
        appDatabase.bitfinexOrderDao().deleteAll();
    }

    /**
     * Check if the exchange API keys are defined
     */
    public boolean areApiKeysDefined() {
        Exchange exchange = appDatabase.exchangeDao().getByName("Bitfinex");
        return exchange != null
                && exchange.getPublicApiKey() != null
                && exchange.getPublicApiKey().length() > 0
                && exchange.getPrivateApiKey() != null
                && exchange.getPrivateApiKey().length() > 0;
    }

}
