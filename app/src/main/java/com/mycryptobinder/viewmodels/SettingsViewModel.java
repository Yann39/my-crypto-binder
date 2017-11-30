package com.mycryptobinder.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import com.loopj.android.http.Base64;
import com.mycryptobinder.entities.AppDatabase;
import com.mycryptobinder.entities.Currency;
import com.mycryptobinder.entities.Exchange;
import com.mycryptobinder.entities.KrakenAsset;
import com.mycryptobinder.entities.KrakenAssetPair;
import com.mycryptobinder.entities.KrakenTrade;
import com.mycryptobinder.entities.PoloniexAsset;
import com.mycryptobinder.entities.PoloniexTrade;
import com.mycryptobinder.entities.Transaction;
import com.mycryptobinder.helpers.UtilsHelper;
import com.mycryptobinder.models.HoldingData;
import com.mycryptobinder.models.KrakenAssetPairValue;
import com.mycryptobinder.models.KrakenAssetPairs;
import com.mycryptobinder.models.KrakenAssetValue;
import com.mycryptobinder.models.KrakenAssets;
import com.mycryptobinder.models.KrakenTradeValue;
import com.mycryptobinder.models.KrakenTrades;
import com.mycryptobinder.models.PoloniexAssetValue;
import com.mycryptobinder.models.PoloniexTradeValue;
import com.mycryptobinder.service.KrakenService;
import com.mycryptobinder.service.PoloniexService;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.text.ParseException;
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
 * Created on 08/11/2017
 */

public class SettingsViewModel extends AndroidViewModel {

    private final LiveData<Integer> nbDifferentCurrencies;
    private final LiveData<List<HoldingData>> holdings;
    private static AppDatabase appDatabase;
    private static Properties properties;
    private SimpleDateFormat sdf;

    public SettingsViewModel(Application application) {
        super(application);
        appDatabase = AppDatabase.getDatabase(this.getApplication());
        nbDifferentCurrencies = appDatabase.transactionDao().getNbDifferentCurrencies();
        holdings = appDatabase.transactionDao().getHoldings();
        properties = new Properties();
        try {
            InputStream inputStream = getApplication().getBaseContext().getAssets().open("myCryptoBinder.properties");
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        UtilsHelper uh = new UtilsHelper(getApplication().getBaseContext());
        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", uh.getCurrentLocale());
    }

    public LiveData<List<HoldingData>> getHoldings() {
        return holdings;
    }

    public LiveData<Integer> getNbDifferentCurrencies() {
        return nbDifferentCurrencies;
    }

    public void populateDatabase() {
        new populateDatabaseAsyncTask(appDatabase, sdf).execute();
    }

    private static class populateDatabaseAsyncTask extends AsyncTask<Void, Void, Void> {
        private AppDatabase db;
        private static int offset = 0;
        private SimpleDateFormat sdf;

        populateDatabaseAsyncTask(AppDatabase appDatabase, SimpleDateFormat sdf) {
            this.db = appDatabase;
            this.sdf = sdf;
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

        /**
         *
         */
        private void populateKrakenTradeHistory() {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.YEAR, 2016);
            cal.set(Calendar.MONTH, Calendar.DECEMBER);
            cal.set(Calendar.DAY_OF_MONTH, 13);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);

            String start = String.valueOf(cal.getTimeInMillis() / 1000);
            String key = properties.getProperty("KRAKEN_API_PUBLIC_KEY");
            String nonce = String.valueOf(System.currentTimeMillis());
            String path = "/0/private/TradesHistory";
            String parameters = "start=" + start + "&ofs=" + offset + "&nonce=" + nonce;
            String sign = calculateKrakenSignature(path, nonce, parameters);

            Map<String, String> headerMap = new HashMap<>();
            headerMap.put("API-Key", key);
            headerMap.put("API-Sign", sign);

            Map<String, String> paramsMap = new HashMap<>();
            paramsMap.put("nonce", nonce);
            paramsMap.put("start", start);
            paramsMap.put("ofs", String.valueOf(offset));
            try {
                KrakenService krakenService = KrakenService.retrofit.create(KrakenService.class);
                Call<KrakenTrades> call = krakenService.getTradeHistory(headerMap, paramsMap);
                KrakenTrades krakenTrades = call.execute().body();

                // if it returned something without error
                if (krakenTrades != null && krakenTrades.getError().size() < 1) {

                    //get any existing asset
                    List<String> trades = appDatabase.krakenTradeDao().getTradeIds().getValue();

                    List<KrakenTrade> krakenTradeEntities = new ArrayList<>();
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
                            krakenTradeEntities.add(new KrakenTrade(orderTxId, pair, time, type, orderType, price, cost, fee, vol, margin, misc));
                        }
                    }

                    // insert into the database
                    KrakenTrade[] krakenTradeArray = new KrakenTrade[krakenTradeEntities.size()];
                    krakenTradeArray = krakenTradeEntities.toArray(krakenTradeArray);
                    appDatabase.krakenTradeDao().insert(krakenTradeArray);

                    offset = offset + 50;
                    populateKrakenTradeHistory();
                }


            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * Populate trade history table from remote exchange
         */
        private void populatePoloniexTradeHistory(Date startDate) {

            String start = String.valueOf(startDate.getTime() / 1000);
            String key = properties.getProperty("POLONIEX_API_PUBLIC_KEY");
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

            try {
                PoloniexService poloniexService = PoloniexService.retrofit.create(PoloniexService.class);
                Call<Map<String, List<PoloniexTradeValue>>> call = poloniexService.getTradeHistory(headerMap, paramsMap);
                //todo return {"error":"Invalid API key\/secret pair."} if error so it raises Expected BEGIN_ARRAY but was STRING
                Map<String, List<PoloniexTradeValue>> poloniexTrades = call.execute().body();

                // if it returned something without error
                if (poloniexTrades != null && !poloniexTrades.isEmpty()) {
                    int cpt = 0;
                    Date highestDate = startDate;

                    // get any existing trades
                    List<Long> trades = db.poloniexTradeDao().getTradeIds().getValue();

                    List<PoloniexTrade> poloniexTradeEntities = new ArrayList<>();
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
                                Date date = sdf.parse(dateStr);
                                Double rate = Double.parseDouble(rateStr);
                                Double amount = Double.parseDouble(amountStr);
                                Double total = Double.parseDouble(totalStr);
                                Double fee = Double.parseDouble(feeStr);
                                Long orderNumber = Long.parseLong(orderNumberStr);

                                if (highestDate.before(date)) {
                                    highestDate = date;
                                }

                                poloniexTradeEntities.add(new PoloniexTrade(pair, globalTradeId, tradeId, date, rate, amount, total, fee, orderNumber, type, category));
                            }
                            cpt++;
                        }
                    }

                    // insert into the database
                    PoloniexTrade[] poloniexTradesArray = new PoloniexTrade[poloniexTradeEntities.size()];
                    poloniexTradesArray = poloniexTradeEntities.toArray(poloniexTradesArray);
                    db.poloniexTradeDao().insert(poloniexTradesArray);

                    // API result is limited to 10000 entries maximum, so if result contains 10000 entries, call it again with a start time corresponding to the highest found trade date
                    if (cpt > 10000) {
                        populatePoloniexTradeHistory(highestDate);
                    }
                }
            } catch (IOException | ParseException e) {
                e.printStackTrace();
            }

        }

        @Override
        protected Void doInBackground(Void... voids) {

            //region insert Kraken exchange
            if (db.exchangeDao().getByName("Kraken") == null) {
                db.exchangeDao().insert(new Exchange("Kraken", "https://www.kraken.com", "Kraken exchange"));
            }
            //endregion

            //region insert Kraken asset pairs from API
            try {

                // API synchronous call
                KrakenService krakenService = KrakenService.retrofit.create(KrakenService.class);
                Call<KrakenAssetPairs> call = krakenService.getAssetPairs();
                KrakenAssetPairs krakenAssetPairs = call.execute().body();

                // if it returned something without error
                if (krakenAssetPairs != null && krakenAssetPairs.getError().size() < 1) {

                    // get any existing asset pairs
                    List<String> assetPairs = db.krakenAssetPairDao().getPairs().getValue();

                    List<KrakenAssetPair> krakenAssetPairEntities = new ArrayList<>();
                    for (Map.Entry<String, KrakenAssetPairValue> entry : krakenAssetPairs.getResult().entrySet()) {
                        String assetPair = entry.getKey();

                        // keep only if it does not already exists
                        if (assetPairs == null || !assetPairs.contains(assetPair)) {
                            KrakenAssetPairValue krakenAssetPairValue = entry.getValue();
                            String altname = krakenAssetPairValue.getAltname();
                            String base = krakenAssetPairValue.getBase();
                            String quote = krakenAssetPairValue.getQuote();
                            krakenAssetPairEntities.add(new KrakenAssetPair(assetPair, altname, base, quote));
                        }
                    }

                    // insert into the database
                    KrakenAssetPair[] krakenAssetPairsArray = new KrakenAssetPair[krakenAssetPairEntities.size()];
                    krakenAssetPairsArray = krakenAssetPairEntities.toArray(krakenAssetPairsArray);
                    db.krakenAssetPairDao().insert(krakenAssetPairsArray);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            //endregion

            //region insert Kraken assets from API
            try {

                // API synchronous call
                KrakenService krakenService = KrakenService.retrofit.create(KrakenService.class);
                Call<KrakenAssets> call = krakenService.getAssets();
                KrakenAssets krakenAssets = call.execute().body();

                // if it returned something without error
                if (krakenAssets != null && krakenAssets.getError().size() < 1) {

                    //get any existing asset
                    List<String> assets = db.krakenAssetDao().getName().getValue();

                    List<KrakenAsset> krakenAssetEntities = new ArrayList<>();
                    for (Map.Entry<String, KrakenAssetValue> entry : krakenAssets.getResult().entrySet()) {
                        String asset = entry.getKey();

                        // keep only if it does not already exists
                        if (assets == null || !assets.contains(asset)) {
                            KrakenAssetValue krakenAssetValue = entry.getValue();
                            String altname = krakenAssetValue.getAltname();
                            krakenAssetEntities.add(new KrakenAsset(asset, altname));
                        }
                    }

                    // insert into the database
                    KrakenAsset[] krakenAssetArray = new KrakenAsset[krakenAssetEntities.size()];
                    krakenAssetArray = krakenAssetEntities.toArray(krakenAssetArray);
                    db.krakenAssetDao().insert(krakenAssetArray);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            //endregion

            //region insert Kraken trade history from API
            populateKrakenTradeHistory();
            //endregion

            //region insert Kraken currencies from assets
            List<Currency> krakenCurrencyList = db.currencyDao().getFromKraken().getValue();
            if (krakenCurrencyList != null) {
                Currency[] currencyArray = new Currency[krakenCurrencyList.size()];
                currencyArray = krakenCurrencyList.toArray(currencyArray);
                db.currencyDao().insert(currencyArray);
            }
            //endregion

            //region insert Kraken transactions from trade history
            List<Transaction> transactionList = db.transactionDao().getKrakenTransactions().getValue();
            if (transactionList != null) {
                Transaction[] transactionArray = new Transaction[transactionList.size()];
                transactionArray = transactionList.toArray(transactionArray);
                db.transactionDao().insert(transactionArray);
            }
            //endregion

            //region insert Poloniex exchange
            if (db.exchangeDao().getByName("Poloniex") == null) {
                db.exchangeDao().insert(new Exchange("Poloniex", "https://www.poloniex.com", "Poloniex exchange"));
            }
            //endregion

            //region insert Poloniex assets from API
            try {

                // API synchronous call
                PoloniexService poloniexService = PoloniexService.retrofit.create(PoloniexService.class);
                Call<Map<String, PoloniexAssetValue>> call = poloniexService.getAssets();
                Map<String, PoloniexAssetValue> poloniexAssets = call.execute().body();

                // if it returned something without error
                if (poloniexAssets != null && !poloniexAssets.isEmpty()) {

                    //get any existing asset
                    List<String> assets = db.poloniexAssetDao().getCodes().getValue();

                    List<PoloniexAsset> poloniexAssetEntities = new ArrayList<>();
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
                    db.poloniexAssetDao().insert(poloniexAssetArray);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            //endregion

            //region insert Poloniex trade history from API
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.YEAR, 2016);
            cal.set(Calendar.MONTH, Calendar.DECEMBER);
            cal.set(Calendar.DAY_OF_MONTH, 26);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);

            populatePoloniexTradeHistory(cal.getTime());
            //endregion

            //region insert Poloniex currencies from assets
            List<Currency> poloniexCurrencyList = db.currencyDao().getFromPoloniex().getValue();
            if (poloniexCurrencyList != null) {
                Currency[] currencyArray2 = new Currency[poloniexCurrencyList.size()];
                currencyArray2 = poloniexCurrencyList.toArray(currencyArray2);
                db.currencyDao().insert(currencyArray2);
            }
            //endregion

            //region insert Poloniex transactions from trade history
            List<Transaction> transactionList2 = db.transactionDao().getPoloniexTransactions().getValue();
            if (transactionList2 != null) {
                Transaction[] transactionArray2 = new Transaction[transactionList2.size()];
                transactionArray2 = transactionList2.toArray(transactionArray2);
                db.transactionDao().insert(transactionArray2);
            }
            //endregion

            return null;
        }
    }

    /*private static class getNbCurrenciesAsyncTask extends AsyncTask<Void, Void, Integer> {
        private AppDatabase db;

        getNbCurrenciesAsyncTask(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            return db.transactionDao().getNbDifferentCurrencies();
        }
    }*/

}
