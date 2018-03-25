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

import com.mycryptobinder.entities.AppDatabase;
import com.mycryptobinder.entities.Currency;
import com.mycryptobinder.entities.Transaction;
import com.mycryptobinder.entities.poloniex.PoloniexAsset;
import com.mycryptobinder.entities.poloniex.PoloniexDeposit;
import com.mycryptobinder.entities.poloniex.PoloniexTrade;
import com.mycryptobinder.entities.poloniex.PoloniexWithdrawal;
import com.mycryptobinder.helpers.UtilsHelper;
import com.mycryptobinder.models.poloniex.PoloniexAssetValue;
import com.mycryptobinder.models.poloniex.PoloniexDepositsWithdrawals;
import com.mycryptobinder.models.poloniex.PoloniexTradeValue;
import com.mycryptobinder.services.PoloniexService;

import java.io.IOException;
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

public class PoloniexManager {

    private static AppDatabase appDatabase;
    private static SimpleDateFormat sdf;
    private static PoloniexService poloniexService;
    private static List<PoloniexTrade> poloniexTradeEntities;
    private static List<PoloniexDeposit> poloniexDepositEntities;
    private static List<PoloniexWithdrawal> poloniexWithdrawalEntities;
    private static long startTime;
    private final UtilsHelper uh;

    public PoloniexManager(Context context) {
        appDatabase = AppDatabase.getInstance(context);
        uh = new UtilsHelper(context);
        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", uh.getCurrentLocale());
        poloniexService = PoloniexService.retrofit.create(PoloniexService.class);
        poloniexTradeEntities = new ArrayList<>();
        poloniexDepositEntities = new ArrayList<>();
        poloniexWithdrawalEntities = new ArrayList<>();
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
            // get encrypted private API key from database
            String encryptedPrivateKey = appDatabase.exchangeDao().getByName("Poloniex").getPrivateApiKey();
            // decrypt it
            Properties properties = uh.getProperties();
            String key = properties.getProperty("RSA_KEY");
            String initVector = properties.getProperty("RSA_INIT_VECTOR");
            String privateKey = uh.decrypt(key, initVector, encryptedPrivateKey);
            // encode it into the signature
            Mac mac = Mac.getInstance("HmacSHA512");
            mac.init(new SecretKeySpec(privateKey.getBytes("UTF-8"), "HmacSHA512"));
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

    /**
     * Get Poloniex trade history
     */
    private void getPoloniexTrades() {

        String start = String.valueOf(startTime);
        String nonce = String.valueOf((new Date()).getTime());
        //use alphabetical order as it must be in the same order as POST body parameters
        String parameters = "command=returnTradeHistory&currencyPair=all&limit=10000&nonce=" + nonce + "&start=" + start;
        String sign = calculatePoloniexSignature(parameters);

        // get encrypted public API key from database and decrypt it
        String encryptedPublicKey = appDatabase.exchangeDao().getByName("Poloniex").getPublicApiKey();
        Properties properties = uh.getProperties();
        String key = properties.getProperty("RSA_KEY");
        String initVector = properties.getProperty("RSA_INIT_VECTOR");
        String publicKey = uh.decrypt(key, initVector, encryptedPublicKey);

        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("Key", publicKey);
        headerMap.put("Sign", sign);

        //here order doesn't matter as we will order them in an interceptor while sending request
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

                        if (date != null && highestDate.before(date)) {
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
     * Insert deposits and withdrawals from Poloniex exchange API
     *
     * @return The number of deposits and withdrawals inserted
     */
    public int populateDepositsWithdrawals() {

        String start = String.valueOf(startTime);
        String end = String.valueOf(Calendar.getInstance().getTimeInMillis() / 1000);
        String nonce = String.valueOf(System.currentTimeMillis());
        //use alphabetical order as it must be in the same order as POST body parameters
        String parameters = "command=returnDepositsWithdrawals&end=" + end + "&nonce=" + nonce + "&start=" + start;
        String sign = calculatePoloniexSignature(parameters);

        // get encrypted public API key from database and decrypt it
        String encryptedPublicKey = appDatabase.exchangeDao().getByName("Poloniex").getPublicApiKey();
        Properties properties = uh.getProperties();
        String key = properties.getProperty("RSA_KEY");
        String initVector = properties.getProperty("RSA_INIT_VECTOR");
        String publicKey = uh.decrypt(key, initVector, encryptedPublicKey);

        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("Key", publicKey);
        headerMap.put("Sign", sign);

        //here order doesn't matter as we will order them in an interceptor while sending request
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("command", "returnDepositsWithdrawals");
        paramsMap.put("start", start);
        paramsMap.put("end", end);
        paramsMap.put("nonce", nonce);

        PoloniexDepositsWithdrawals poloniexDepositsWithdrawals = null;
        try {
            Call<PoloniexDepositsWithdrawals> call = poloniexService.getDepositsWithdrawals(headerMap, paramsMap);
            //todo return {"error":"Invalid API key\/secret pair."} if error so it raises Expected BEGIN_ARRAY but was STRING
            poloniexDepositsWithdrawals = call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // if it returned something without error
        if (poloniexDepositsWithdrawals != null) {

            // get any existing deposits
            List<String> deposits = appDatabase.poloniexDepositDao().getTxIds();

            for (com.mycryptobinder.models.poloniex.PoloniexDeposit entry : poloniexDepositsWithdrawals.getDeposits()) {
                String transactionId = entry.getTxId();
                // keep only if it does not already exists
                if (deposits == null || !deposits.contains(transactionId)) {
                    //get all values
                    String address = entry.getAddress();
                    String currency = entry.getCurrency();
                    long timestamp = entry.getTimestamp();
                    String status = entry.getStatus();
                    String amountStr = entry.getAmount();
                    Double amount = Double.parseDouble(amountStr);

                    poloniexDepositEntities.add(new PoloniexDeposit(transactionId, currency, address, amount, timestamp, status));
                }
            }

            // insert into the database
            PoloniexDeposit[] poloniexDepositArray = new PoloniexDeposit[poloniexDepositEntities.size()];
            poloniexDepositArray = poloniexDepositEntities.toArray(poloniexDepositArray);
            appDatabase.poloniexDepositDao().insert(poloniexDepositArray);

            // get any existing withdrawals
            List<Long> withdrawals = appDatabase.poloniexWithdrawalDao().getWithdrawalNumbers();

            for (com.mycryptobinder.models.poloniex.PoloniexWithdrawal entry : poloniexDepositsWithdrawals.getWithdrawals()) {
                long withdrawalNumber = entry.getWithdrawalNumber();
                // keep only if it does not already exists
                if (withdrawals == null || !withdrawals.contains(withdrawalNumber)) {
                    //get all values
                    String address = entry.getAddress();
                    String currency = entry.getCurrency();
                    long timestamp = entry.getTimestamp();
                    String status = entry.getStatus();
                    String amountStr = entry.getAmount();
                    Double amount = Double.parseDouble(amountStr);

                    poloniexWithdrawalEntities.add(new PoloniexWithdrawal(withdrawalNumber, currency, address, amount, timestamp, status));
                }
            }

            // insert into the database
            PoloniexWithdrawal[] poloniexWithdrawalArray = new PoloniexWithdrawal[poloniexWithdrawalEntities.size()];
            poloniexWithdrawalArray = poloniexWithdrawalEntities.toArray(poloniexWithdrawalArray);
            appDatabase.poloniexWithdrawalDao().insert(poloniexWithdrawalArray);

        }

        return poloniexDepositEntities.size() + poloniexWithdrawalEntities.size();

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
