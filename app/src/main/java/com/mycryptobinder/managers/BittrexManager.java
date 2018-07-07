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

import com.mycryptobinder.R;
import com.mycryptobinder.entities.AppDatabase;
import com.mycryptobinder.entities.Currency;
import com.mycryptobinder.entities.Exchange;
import com.mycryptobinder.entities.Transaction;
import com.mycryptobinder.entities.bittrex.BittrexAsset;
import com.mycryptobinder.entities.bittrex.BittrexDeposit;
import com.mycryptobinder.entities.bittrex.BittrexTrade;
import com.mycryptobinder.entities.bittrex.BittrexWithdrawal;
import com.mycryptobinder.helpers.UtilsHelper;
import com.mycryptobinder.models.bittrex.BittrexAssetValue;
import com.mycryptobinder.models.bittrex.BittrexAssets;
import com.mycryptobinder.models.bittrex.BittrexDepositValue;
import com.mycryptobinder.models.bittrex.BittrexDeposits;
import com.mycryptobinder.models.bittrex.BittrexTradeValue;
import com.mycryptobinder.models.bittrex.BittrexTrades;
import com.mycryptobinder.models.bittrex.BittrexWithdrawalValue;
import com.mycryptobinder.models.bittrex.BittrexWithdrawals;
import com.mycryptobinder.services.BittrexService;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import retrofit2.Call;

public class BittrexManager {

    private static AppDatabase appDatabase;
    private static SimpleDateFormat sdf;
    private static BittrexService bittrexService;
    private static List<BittrexTrade> bittrexTradeEntities;
    private static List<BittrexDeposit> bittrexDepositEntities;
    private static List<BittrexWithdrawal> bittrexWithdrawalEntities;
    private final Context context;
    private String publicKey;
    private String privateKey;

    public BittrexManager(Context context) {
        this.context = context;
        appDatabase = AppDatabase.getInstance(context);
        sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", UtilsHelper.getCurrentLocale(context));
        bittrexService = BittrexService.retrofit.create(BittrexService.class);
        bittrexTradeEntities = new ArrayList<>();
        bittrexDepositEntities = new ArrayList<>();
        bittrexWithdrawalEntities = new ArrayList<>();

        // get encryption key and vector from properties
        Properties properties = UtilsHelper.getProperties(context);
        String key = properties.getProperty("RSA_KEY");
        String initVector = properties.getProperty("RSA_INIT_VECTOR");

        // get encrypted API keys from database and decrypt them
        Exchange exchange = appDatabase.exchangeDao().getByName("Bittrex");
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
     * Calculate the signature for the Bittrex API call
     * Standard HMAC-SHA512 signing
     *
     * @param data POST data
     * @return The base64 encoded signature string
     */
    private String calculateBittrexSignature(String data) {
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
     * Get Bittrex trade history
     */
    private void getBittrexTrades() {
        String nonce = String.valueOf((new Date()).getTime());
        String parameters = "https://bittrex.com/api/v1.1/account/getorderhistory?apikey=" + publicKey + "&nonce=" + nonce;
        String sign = calculateBittrexSignature(parameters);

        // query header
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("apikey", publicKey);
        headerMap.put("apisign", sign);

        // request parameters
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("apikey", publicKey);
        paramsMap.put("nonce", nonce);

        // service call
        BittrexTrades bittrexTrades = null;
        String errorMessage = null;
        try {
            Call<BittrexTrades> call = bittrexService.getTradeHistory(headerMap, paramsMap);
            bittrexTrades = call.execute().body();
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
        else if (bittrexTrades == null) {
            // show a notification about the error
            //Toast.makeText(context, context.getString(R.string.error_service_call_returned_null), Toast.LENGTH_SHORT).show();
            System.out.println(context.getString(R.string.error_service_call_returned_null));
        }
        // if an error was returned in the response body
        else if (!bittrexTrades.isSuccess()) {
            // show a notification about the error
            //Toast.makeText(context, context.getString(R.string.error_retrieving_data_from_api, bittrexTrades.getMessage()), Toast.LENGTH_SHORT).show();
            System.out.println(context.getString(R.string.error_retrieving_data_from_api, bittrexTrades.getMessage()));
        } else {
            // if it returned something
            if (bittrexTrades.getResult() != null && bittrexTrades.getResult().size() > 0) {

                // get any existing trades
                List<String> trades = appDatabase.bittrexTradeDao().getTradeIds();

                for (BittrexTradeValue bittrexTradeValue : bittrexTrades.getResult()) {
                    String orderUuId = bittrexTradeValue.getOrderUuid();
                    // keep only if it does not already exists
                    if (trades == null || !trades.contains(orderUuId)) {
                        //get all values
                        String exchange = bittrexTradeValue.getExchange();
                        String timeStamp = bittrexTradeValue.getTimeStamp();
                        String orderType = bittrexTradeValue.getOrderType();
                        Double limit = bittrexTradeValue.getLimit();
                        Double quantity = bittrexTradeValue.getQuantity();
                        Double quantityRemaining = bittrexTradeValue.getQuantityRemaining();
                        Double commission = bittrexTradeValue.getCommission();
                        Double price = bittrexTradeValue.getPrice();
                        Double pricePerUnit = bittrexTradeValue.getPricePerUnit();

                        Date date = null;
                        if (timeStamp != null) {
                            try {
                                date = sdf.parse(timeStamp);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        bittrexTradeEntities.add(new BittrexTrade(orderUuId, exchange, date, orderType, limit, quantity, quantityRemaining, commission, price, pricePerUnit));
                    }
                }
            }
        }
    }

    /**
     * Insert deposits from Bittrex exchange API
     *
     * @return The number of deposits inserted
     */
    public int populateDeposits() {
        String nonce = String.valueOf(System.currentTimeMillis());
        String parameters = "https://bittrex.com/api/v1.1/account/getdeposithistory?apikey=" + publicKey + "&nonce=" + nonce;
        String sign = calculateBittrexSignature(parameters);

        // query header
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("apikey", publicKey);
        headerMap.put("apisign", sign);

        // request parameters
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("apikey", publicKey);
        paramsMap.put("nonce", nonce);

        BittrexDeposits bittrexDeposits = null;
        String errorMessage = null;
        try {
            Call<BittrexDeposits> call = bittrexService.getDeposits(headerMap, paramsMap);
            bittrexDeposits = call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
            errorMessage = e.getLocalizedMessage();
        }

        // if an error occurred on service call
        if (errorMessage != null) {
            // show a notification about the error
            //Toast.makeText(context, context.getString(R.string.error_retrieving_data_from_api, errorMessage), Toast.LENGTH_SHORT).show();
            System.out.println(context.getString(R.string.error_retrieving_data_from_api, errorMessage));
            return -1;
        }
        // if service call returned null
        else if (bittrexDeposits == null) {
            // show a notification about the error
            //Toast.makeText(context, context.getString(R.string.error_service_call_returned_null), Toast.LENGTH_SHORT).show();
            System.out.println(context.getString(R.string.error_service_call_returned_null));
            return -1;
        }
        // if an error was returned in the response body
        else if (!bittrexDeposits.isSuccess()) {
            // show a notification about the error
            //Toast.makeText(context, context.getString(R.string.error_retrieving_data_from_api, bittrexTrades.getMessage()), Toast.LENGTH_SHORT).show();
            System.out.println(context.getString(R.string.error_retrieving_data_from_api, bittrexDeposits.getMessage()));
            return -1;
        } else {
            // if it returned at least one result
            if (bittrexDeposits.getResult() != null && bittrexDeposits.getResult().size() > 0) {

                // get any existing deposits
                List<Long> deposits = appDatabase.bittrexDepositDao().getPaymentUuids();

                for (BittrexDepositValue bittrexDepositValue : bittrexDeposits.getResult()) {
                    long depositId = bittrexDepositValue.getId();
                    // keep only if it does not already exists
                    if (deposits == null || !deposits.contains(depositId)) {
                        //get all values
                        Double amount = bittrexDepositValue.getAmount();
                        String currency = bittrexDepositValue.getCurrency();
                        int confirmations = bittrexDepositValue.getConfirmations();
                        String lastUpdated = bittrexDepositValue.getLastUpdated();
                        String txId = bittrexDepositValue.getTxId();
                        String cryptoAddress = bittrexDepositValue.getCryptoAddress();

                        Date date = null;
                        if (lastUpdated != null) {
                            try {
                                date = sdf.parse(lastUpdated);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        bittrexDepositEntities.add(new BittrexDeposit(depositId, amount, currency, confirmations, date, txId, cryptoAddress));
                    }
                }

                // insert into the database
                BittrexDeposit[] bittrexDepositArray = new BittrexDeposit[bittrexDepositEntities.size()];
                bittrexDepositArray = bittrexDepositEntities.toArray(bittrexDepositArray);
                appDatabase.bittrexDepositDao().insert(bittrexDepositArray);

            }

            return bittrexDepositEntities.size();
        }
    }

    /**
     * Insert withdrawals from Bittrex exchange API
     *
     * @return The number of withdrawals inserted
     */
    public int populateWithdrawals() {

        String nonce = String.valueOf(System.currentTimeMillis());
        //use alphabetical order as it must be in the same order as POST body parameters
        String parameters = "https://bittrex.com/api/v1.1/account/getwithdrawalhistory?apikey=" + publicKey + "&nonce=" + nonce;
        String sign = calculateBittrexSignature(parameters);

        // query header
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("apikey", publicKey);
        headerMap.put("apisign", sign);

        // request parameters
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("apikey", publicKey);
        paramsMap.put("nonce", nonce);

        BittrexWithdrawals bittrexWithdrawals = null;
        String errorMessage = null;
        try {
            Call<BittrexWithdrawals> call = bittrexService.getWithdrawals(headerMap, paramsMap);
            bittrexWithdrawals = call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
            errorMessage = e.getLocalizedMessage();
        }

        // if an error occurred on service call
        if (errorMessage != null) {
            // show a notification about the error
            //Toast.makeText(context, context.getString(R.string.error_retrieving_data_from_api, errorMessage), Toast.LENGTH_SHORT).show();
            System.out.println(context.getString(R.string.error_retrieving_data_from_api, errorMessage));
            return -1;
        }
        // if service call returned null
        else if (bittrexWithdrawals == null) {
            // show a notification about the error
            //Toast.makeText(context, context.getString(R.string.error_service_call_returned_null), Toast.LENGTH_SHORT).show();
            System.out.println(context.getString(R.string.error_service_call_returned_null));
            return -1;
        }
        // if an error was returned in the response body
        else if (!bittrexWithdrawals.isSuccess()) {
            // show a notification about the error
            //Toast.makeText(context, context.getString(R.string.error_retrieving_data_from_api, bittrexTrades.getMessage()), Toast.LENGTH_SHORT).show();
            System.out.println(context.getString(R.string.error_retrieving_data_from_api, bittrexWithdrawals.getMessage()));
            return -1;
        } else {
            // if it returned at least one result
            if (bittrexWithdrawals.getResult() != null && bittrexWithdrawals.getResult().size() > 0) {

                // get any existing withdrawals
                List<String> withdrawals = appDatabase.bittrexWithdrawalDao().getPaymentUuids();

                for (BittrexWithdrawalValue bittrexWithdrawalValue : bittrexWithdrawals.getResult()) {
                    String paymentUuid = bittrexWithdrawalValue.getPaymentUuid();
                    // keep only if it does not already exists
                    if (withdrawals == null || !withdrawals.contains(paymentUuid)) {
                        //get all values
                        String currency = bittrexWithdrawalValue.getCurrency();
                        Double amount = bittrexWithdrawalValue.getAmount();
                        String address = bittrexWithdrawalValue.getAddress();
                        String opened = bittrexWithdrawalValue.getOpened();
                        boolean authorized = bittrexWithdrawalValue.isAuthorized();
                        boolean pendingPayment = bittrexWithdrawalValue.isPendingPayment();
                        Double txCost = bittrexWithdrawalValue.getTxCost();
                        String txId = bittrexWithdrawalValue.getTxId();
                        boolean cancelled = bittrexWithdrawalValue.isCanceled();
                        boolean invalidAddress = bittrexWithdrawalValue.isInvalidAddress();

                        Date date = null;
                        if (opened != null) {
                            try {
                                date = sdf.parse(opened);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        bittrexWithdrawalEntities.add(new BittrexWithdrawal(paymentUuid, currency, amount, address, date, authorized, pendingPayment, txCost, txId, cancelled, invalidAddress));
                    }
                }

                // insert into the database
                BittrexWithdrawal[] bittrexWithdrawalArray = new BittrexWithdrawal[bittrexWithdrawalEntities.size()];
                bittrexWithdrawalArray = bittrexWithdrawalEntities.toArray(bittrexWithdrawalArray);
                appDatabase.bittrexWithdrawalDao().insert(bittrexWithdrawalArray);

            }

            return bittrexWithdrawalEntities.size();
        }
    }

    /**
     * Insert trade history from Bittrex exchange API
     *
     * @return The number of trades inserted
     */
    public int populateTradeHistory() {
        getBittrexTrades();
        if (bittrexTradeEntities != null && !bittrexTradeEntities.isEmpty()) {
            // insert into the database
            BittrexTrade[] bittrexTradesArray = new BittrexTrade[bittrexTradeEntities.size()];
            bittrexTradesArray = bittrexTradeEntities.toArray(bittrexTradesArray);
            appDatabase.bittrexTradeDao().insert(bittrexTradesArray);
            return bittrexTradeEntities.size();
        }
        return 0;
    }

    /**
     * Insert assets from Bittrex exchange API
     *
     * @return number of assets inserted, or -1 if an error occurred
     */
    public int populateAssets() {
        BittrexAssets bittrexAssets = null;
        try {
            // API synchronous call
            BittrexService bittrexService = BittrexService.retrofit.create(BittrexService.class);
            Call<BittrexAssets> call = bittrexService.getAssets();
            bittrexAssets = call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<BittrexAsset> bittrexAssetEntities = new ArrayList<>();
        // if it returned something without error
        if (bittrexAssets != null && bittrexAssets.isSuccess()) {

            //get any existing asset
            List<String> assets = appDatabase.bittrexAssetDao().getCodes();

            for (BittrexAssetValue bittrexAssetValue : bittrexAssets.getResult()) {
                String asset = bittrexAssetValue.getCurrency();

                // keep only if it does not already exists
                if (assets == null || !assets.contains(asset)) {
                    String name = bittrexAssetValue.getCurrency();
                    bittrexAssetEntities.add(new BittrexAsset(asset, name));
                }
            }

            // insert into the database
            BittrexAsset[] bittrexAssetArray = new BittrexAsset[bittrexAssetEntities.size()];
            bittrexAssetArray = bittrexAssetEntities.toArray(bittrexAssetArray);
            appDatabase.bittrexAssetDao().insert(bittrexAssetArray);
        } else {
            return 0;
        }
        return bittrexAssetEntities.size();
    }

    /**
     * Insert currencies from Bittrex assets
     *
     * @return number of currencies inserted
     */
    public int populateCurrencies() {
        List<Currency> currencyList = appDatabase.currencyDao().getFromBittrex();
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
     * Insert transaction from Bittrex assets
     *
     * @return number of transactions inserted
     */
    public int populateTransactions() {
        List<Transaction> transactionList = appDatabase.transactionDao().getBittrexTransactions();
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
        appDatabase.bittrexTradeDao().deleteAll();
        appDatabase.bittrexAssetDao().deleteAll();
    }

    /**
     * Check if the exchange API keys are defined
     */
    public boolean areApiKeysDefined() {
        Exchange exchange = appDatabase.exchangeDao().getByName("Bittrex");
        return exchange != null
                && exchange.getPublicApiKey() != null
                && exchange.getPublicApiKey().length() > 0
                && exchange.getPrivateApiKey() != null
                && exchange.getPrivateApiKey().length() > 0;
    }

}
