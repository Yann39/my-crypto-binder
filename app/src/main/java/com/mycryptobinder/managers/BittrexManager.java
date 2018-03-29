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
import java.util.Calendar;
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
    private static long startTime;
    private final UtilsHelper uh;

    public BittrexManager(Context context) {
        appDatabase = AppDatabase.getInstance(context);
        uh = new UtilsHelper(context);
        sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", uh.getCurrentLocale());
        bittrexService = BittrexService.retrofit.create(BittrexService.class);
        bittrexTradeEntities = new ArrayList<>();
        bittrexDepositEntities = new ArrayList<>();
        bittrexWithdrawalEntities = new ArrayList<>();
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
     * Calculate the signature for the Bittrex API call
     * Message signature using HMAC-SHA512 of (URI path + SHA256(nonce + POST data)) and base64 decoded secret API key
     *
     * @param data POST data
     * @return The base64 encoded signature string
     */
    private String calculateBittrexSignature(String data) {
        String signature = null;
        try {
            // get encrypted private API key from database
            String encryptedPrivateKey = appDatabase.exchangeDao().getByName("Bittrex").getPrivateApiKey();
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
     * Get Bittrex trade history
     */
    private void getBittrexTrades() {

        // get encrypted public API key from database and decrypt it
        String encryptedPublicKey = appDatabase.exchangeDao().getByName("Bittrex").getPublicApiKey();
        Properties properties = uh.getProperties();
        String key = properties.getProperty("RSA_KEY");
        String initVector = properties.getProperty("RSA_INIT_VECTOR");
        String publicKey = uh.decrypt(key, initVector, encryptedPublicKey);

        String nonce = String.valueOf((new Date()).getTime());
        //use alphabetical order as it must be in the same order as POST body parameters
        String parameters = "https://bittrex.com/api/v1.1/account/getorderhistory?apikey=" + publicKey + "&nonce=" + nonce;
        String sign = calculateBittrexSignature(parameters);

        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("apisign", sign);

        BittrexTrades bittrexTrades = null;
        try {
            Call<BittrexTrades> call = bittrexService.getTradeHistory(headerMap, null);
            //todo return {"error":"Invalid API key\/secret pair."} if error so it raises Expected BEGIN_ARRAY but was STRING
            bittrexTrades = call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // if it returned something without error
        if (bittrexTrades != null && !bittrexTrades.isSuccess()) {
            int cpt = 0;

            // get any existing trades
            List<String> trades = appDatabase.bittrexTradeDao().getTradeIds();

            for (BittrexTradeValue bittrexTradeValue : bittrexTrades.getResult()) {
                String orderUuId = bittrexTradeValue.getOrderUuid();
                // keep only if it does not already exists
                if (trades == null || !trades.contains(orderUuId)) {
                    //get all values as strings
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
                    try {
                        date = sdf.parse(timeStamp);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    bittrexTradeEntities.add(new BittrexTrade(orderUuId, exchange, date, orderType, limit, quantity, quantityRemaining, commission, price, pricePerUnit));
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

        // get encrypted public API key from database and decrypt it
        String encryptedPublicKey = appDatabase.exchangeDao().getByName("Bittrex").getPublicApiKey();
        Properties properties = uh.getProperties();
        String key = properties.getProperty("RSA_KEY");
        String initVector = properties.getProperty("RSA_INIT_VECTOR");
        String publicKey = uh.decrypt(key, initVector, encryptedPublicKey);

        String nonce = String.valueOf(System.currentTimeMillis());
        //use alphabetical order as it must be in the same order as POST body parameters
        String parameters = "https://bittrex.com/api/v1.1/account/getdeposithistory?apikey=" + publicKey + "&nonce=" + nonce;
        String sign = calculateBittrexSignature(parameters);

        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("apisign", sign);

        BittrexDeposits bittrexDeposits = null;
        try {
            Call<BittrexDeposits> call = bittrexService.getDeposits(headerMap, null);
            //todo return {"error":"Invalid API key\/secret pair."} if error so it raises Expected BEGIN_ARRAY but was STRING
            bittrexDeposits = call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // if it returned something without error
        if (bittrexDeposits != null && bittrexDeposits.isSuccess()) {

            // get any existing deposits
            List<String> deposits = appDatabase.bittrexDepositDao().getPaymentUuids();

            for (BittrexDepositValue bittrexDepositValue : bittrexDeposits.getResult()) {
                String paymentUuid = bittrexDepositValue.getPaymentUuid();
                // keep only if it does not already exists
                if (deposits == null || !deposits.contains(paymentUuid)) {
                    //get all values
                    String currency = bittrexDepositValue.getCurrency();
                    Double amount = bittrexDepositValue.getAmount();
                    String address = bittrexDepositValue.getAddress();
                    String opened = bittrexDepositValue.getOpened();
                    boolean authorized = bittrexDepositValue.isAuthorized();
                    boolean pendingPayment = bittrexDepositValue.isPendingPayment();
                    Double txCost = bittrexDepositValue.getTxCost();
                    String txId = bittrexDepositValue.getTxId();
                    boolean cancelled = bittrexDepositValue.isCanceled();
                    boolean invalidAddress = bittrexDepositValue.isInvalidAddress();

                    Date date = null;
                    try {
                        date = sdf.parse(opened);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    bittrexDepositEntities.add(new BittrexDeposit(paymentUuid, currency, amount, address, date, authorized, pendingPayment, txCost, txId, cancelled, invalidAddress));
                }
            }

            // insert into the database
            BittrexDeposit[] bittrexDepositArray = new BittrexDeposit[bittrexDepositEntities.size()];
            bittrexDepositArray = bittrexDepositEntities.toArray(bittrexDepositArray);
            appDatabase.bittrexDepositDao().insert(bittrexDepositArray);

        }

        return bittrexDepositEntities.size();
    }

    /**
     * Insert withdrawals from Bittrex exchange API
     *
     * @return The number of withdrawals inserted
     */
    public int populateWithdrawals() {

        // get encrypted public API key from database and decrypt it
        String encryptedPublicKey = appDatabase.exchangeDao().getByName("Bittrex").getPublicApiKey();
        Properties properties = uh.getProperties();
        String key = properties.getProperty("RSA_KEY");
        String initVector = properties.getProperty("RSA_INIT_VECTOR");
        String publicKey = uh.decrypt(key, initVector, encryptedPublicKey);

        String nonce = String.valueOf(System.currentTimeMillis());
        //use alphabetical order as it must be in the same order as POST body parameters
        String parameters = "https://bittrex.com/api/v1.1/account/getwithdrawalhistory?apikey=" + publicKey + "&nonce=" + nonce;
        String sign = calculateBittrexSignature(parameters);

        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("apisign", sign);

        BittrexWithdrawals bittrexWithdrawals = null;
        try {
            Call<BittrexWithdrawals> call = bittrexService.getWithdrawals(headerMap, null);
            //todo return {"error":"Invalid API key\/secret pair."} if error so it raises Expected BEGIN_ARRAY but was STRING
            bittrexWithdrawals = call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // if it returned something without error
        if (bittrexWithdrawals != null && bittrexWithdrawals.isSuccess()) {

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
                    try {
                        date = sdf.parse(opened);
                    } catch (Exception e) {
                        e.printStackTrace();
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

}
