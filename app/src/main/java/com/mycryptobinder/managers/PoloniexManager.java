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
import android.widget.Toast;

import com.mycryptobinder.R;
import com.mycryptobinder.entities.AppDatabase;
import com.mycryptobinder.entities.Currency;
import com.mycryptobinder.entities.Exchange;
import com.mycryptobinder.entities.Transaction;
import com.mycryptobinder.entities.poloniex.PoloniexAsset;
import com.mycryptobinder.entities.poloniex.PoloniexDeposit;
import com.mycryptobinder.entities.poloniex.PoloniexTrade;
import com.mycryptobinder.entities.poloniex.PoloniexWithdrawal;
import com.mycryptobinder.helpers.UtilsHelper;
import com.mycryptobinder.models.poloniex.PoloniexAssetValue;
import com.mycryptobinder.models.poloniex.PoloniexDepositsWithdrawals;
import com.mycryptobinder.models.poloniex.PoloniexError;
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
    private final Context context;
    private static long startTime;
    private String publicKey;
    private String privateKey;

    public PoloniexManager(Context context) {
        this.context = context;
        appDatabase = AppDatabase.getInstance(context);
        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", UtilsHelper.getCurrentLocale(context));
        poloniexService = PoloniexService.retrofit.create(PoloniexService.class);
        poloniexTradeEntities = new ArrayList<>();
        poloniexDepositEntities = new ArrayList<>();
        poloniexWithdrawalEntities = new ArrayList<>();

        // get encryption key and vector from properties
        Properties properties = UtilsHelper.getProperties(context);
        String key = properties.getProperty("RSA_KEY");
        String initVector = properties.getProperty("RSA_INIT_VECTOR");

        // get encrypted API keys from database and decrypt them
        Exchange exchange = appDatabase.exchangeDao().getByName("Poloniex");
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

        // Poloniex was created on january 2014, get trades from that date
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2014);
        cal.set(Calendar.MONTH, Calendar.JANUARY);
        cal.set(Calendar.DAY_OF_MONTH, 1);
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
            mac.init(new SecretKeySpec(privateKey.getBytes("UTF-8"), "HmacSHA512"));
            signature = UtilsHelper.bytesToHex(mac.doFinal(data.getBytes("UTF-8")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return signature;
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

        // request header
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("Key", publicKey);
        headerMap.put("Sign", sign);

        // request parameters, here order doesn't matter as we will order them in an interceptor while sending request
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("command", "returnTradeHistory");
        paramsMap.put("currencyPair", "all");
        paramsMap.put("start", start);
        paramsMap.put("nonce", nonce);
        paramsMap.put("limit", "10000");

        // service call
        Map<String, List<PoloniexTradeValue>> poloniexTrades = null;
        PoloniexError poloniexError = null;
        String errorMessage = null;
        try {
            Call<Map<String, List<PoloniexTradeValue>>> call = poloniexService.getTradeHistory(headerMap, paramsMap);
            poloniexTrades = call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
            errorMessage = e.getLocalizedMessage();
            // API does not return the same structure when an error occurred... so let's try again with new structure to get any error message
            // for example in case of invalid signature it returns {"error":"Invalid API key\/secret pair."} so it raises "Expected BEGIN_ARRAY but was STRING"
            try {
                Call<PoloniexError> call = poloniexService.getTradeHistoryError(headerMap, paramsMap);
                poloniexError = call.execute().body();
            } catch (IOException e2) {
                e.printStackTrace();
                errorMessage = e.getLocalizedMessage();
            }
        }

        // if an error occurred on service call
        if (errorMessage != null) {
            // show a notification about the error
            Toast.makeText(context, context.getString(R.string.error_retrieving_data_from_api, errorMessage), Toast.LENGTH_SHORT).show();
        }
        // if an error was returned in the response body
        else if (poloniexError != null && poloniexError.getError() != null) {
            // show a notification about the error
            Toast.makeText(context, context.getString(R.string.error_retrieving_data_from_api, poloniexError.getError()), Toast.LENGTH_SHORT).show();
        } else {
            // if it returned something
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

                            Date date = null;
                            if (dateStr != null) {
                                try {
                                    date = sdf.parse(dateStr);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            Long tradeId = Long.parseLong(tradeIdStr);
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
        // use alphabetical order as it must be in the same order as POST body parameters
        String parameters = "command=returnDepositsWithdrawals&end=" + end + "&nonce=" + nonce + "&start=" + start;
        String sign = calculatePoloniexSignature(parameters);

        // request header
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("Key", publicKey);
        headerMap.put("Sign", sign);

        // request parameters, here order doesn't matter as we will order them in an interceptor while sending request
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("command", "returnDepositsWithdrawals");
        paramsMap.put("start", start);
        paramsMap.put("end", end);
        paramsMap.put("nonce", nonce);

        // service call
        PoloniexDepositsWithdrawals poloniexDepositsWithdrawals = null;
        PoloniexError poloniexError = null;
        String errorMessage = null;
        try {
            Call<PoloniexDepositsWithdrawals> call = poloniexService.getDepositsWithdrawals(headerMap, paramsMap);
            poloniexDepositsWithdrawals = call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
            errorMessage = e.getLocalizedMessage();
            // API does not return the same structure when an error occurred... so let's try again with new structure to get any error message
            // for example in case of invalid signature it returns {"error":"Invalid API key\/secret pair."} so it raises "Expected BEGIN_ARRAY but was STRING"
            try {
                Call<PoloniexError> call = poloniexService.getDepositsWithdrawalsError(headerMap, paramsMap);
                poloniexError = call.execute().body();
            } catch (IOException e2) {
                e.printStackTrace();
                errorMessage = e.getLocalizedMessage();
            }
        }

        // if an error occurred on service call
        if (errorMessage != null) {
            // show a notification about the error
            Toast.makeText(context, context.getString(R.string.error_retrieving_data_from_api, errorMessage), Toast.LENGTH_SHORT).show();
            return -1;
        }
        // if an error was returned in the response body
        else if (poloniexError != null && poloniexError.getError() != null) {
            // show a notification about the error
            Toast.makeText(context, context.getString(R.string.error_retrieving_data_from_api, poloniexError.getError()), Toast.LENGTH_SHORT).show();
            return -1;
        } else {
            // if it returned something
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

    }

    /**
     * Insert trade history from Poloniex exchange API
     *
     * @return The number of trades inserted
     */
    public int populateTradeHistory() {
        // call to recursive function
        getPoloniexTrades();

        // insert results into the database
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

        //service call
        Map<String, PoloniexAssetValue> poloniexAssets = null;
        PoloniexError poloniexError = null;
        String errorMessage = null;
        try {
            // API synchronous call
            Call<Map<String, PoloniexAssetValue>> call = poloniexService.getAssets();
            poloniexAssets = call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
            errorMessage = e.getLocalizedMessage();
            // API does not return the same structure when an error occurred... so let's try again with new structure to get any error message
            // for example in case of invalid signature it returns {"error":"Invalid API key\/secret pair."} so it raises "Expected BEGIN_ARRAY but was STRING"
            try {
                Call<PoloniexError> call = poloniexService.getAssetsError();
                poloniexError = call.execute().body();
            } catch (IOException e2) {
                e.printStackTrace();
                errorMessage = e.getLocalizedMessage();
            }
        }

        // if an error occurred on service call
        if (errorMessage != null) {
            // show a notification about the error
            Toast.makeText(context, context.getString(R.string.error_retrieving_data_from_api, errorMessage), Toast.LENGTH_SHORT).show();
            return -1;
        }
        // if an error was returned in the response body
        else if (poloniexError != null && poloniexError.getError() != null) {
            // show a notification about the error
            Toast.makeText(context, context.getString(R.string.error_retrieving_data_from_api, poloniexError.getError()), Toast.LENGTH_SHORT).show();
            return -1;
        } else {
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
    }

    /**
     * Insert currencies from Poloniex assets
     *
     * @return number of currencies inserted
     */
    public int populateCurrencies() {
        List<Currency> currencyList = appDatabase.currencyDao().getFromPoloniex();
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
     * Delete all data
     */
    public void deleteAll() {
        appDatabase.poloniexTradeDao().deleteAll();
        appDatabase.poloniexAssetDao().deleteAll();
    }

    /**
     * Check if the exchange API keys are defined
     */
    public boolean areApiKeysDefined() {
        Exchange exchange = appDatabase.exchangeDao().getByName("Poloniex");
        return exchange != null
                && exchange.getPublicApiKey() != null
                && exchange.getPublicApiKey().length() > 0
                && exchange.getPrivateApiKey() != null
                && exchange.getPrivateApiKey().length() > 0;
    }

}
