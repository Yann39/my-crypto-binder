package com.mycryptobinder.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.Nullable;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.Base64;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
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
import com.mycryptobinder.models.PoloniexAssetValue;
import com.mycryptobinder.models.PoloniexTradeValue;
import com.mycryptobinder.service.KrakenService;
import com.mycryptobinder.service.PoloniexService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import cz.msebera.android.httpclient.Header;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Yann
 * Created on 08/11/2017
 */

public class SettingsViewModel extends AndroidViewModel {

    private final LiveData<Integer> nbDifferentCurrencies;
    private final LiveData<List<HoldingData>> holdings;
    private AppDatabase appDatabase;
    private static final Logger logger = Logger.getLogger(SettingsViewModel.class.getName());

    public SettingsViewModel(Application application) {
        super(application);
        appDatabase = AppDatabase.getDatabase(this.getApplication());
        nbDifferentCurrencies = appDatabase.transactionDao().getNbDifferentCurrencies();
        holdings = appDatabase.transactionDao().getHoldings();
    }

    public LiveData<List<HoldingData>> getHoldings() {
        return holdings;
    }

    public LiveData<Integer> getNbDifferentCurrencies() {
        return nbDifferentCurrencies;
    }

    public void populateDatabase() {
        new populateDatabaseAsyncTask(appDatabase, getApplication().getBaseContext()).execute();
    }

    private static class populateDatabaseAsyncTask extends AsyncTask<Void, Void, Void> {
        private AppDatabase db;
        private Properties properties;
        private Context context;
        private static int offset = 0;

        populateDatabaseAsyncTask(AppDatabase appDatabase, Context context) {
            this.db = appDatabase;
            this.context = context;
            properties = new Properties();
            try {
                InputStream inputStream = context.getAssets().open("myCryptoBinder.properties");
                properties.load(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
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

        public void populateKrakenTradeHistory() {
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

            RequestParams params = new RequestParams();
            params.add("nonce", nonce);
            params.add("start", start);
            params.add("ofs", String.valueOf(offset));

            String sign = calculateKrakenSignature(path, nonce, params.toString());

            /*//KrakenBody krakenBody = new KrakenBody();
            //krakenBody.setNonce(nonce);
            //krakenBody.setStart(start);
            //krakenBody.setOfs(String.valueOf(offset));

            Map<String, String> headerMap = new HashMap<>();
            headerMap.put("API-Key", key);
            headerMap.put("API-Sign", sign);

            KrakenService krakenService = KrakenService.retrofit.create(KrakenService.class);
            Call<KrakenTrades> call = krakenService.getTradeHistory(headerMap, nonce);
            call.enqueue(new Callback<KrakenTrades>() {
                @Override
                public void onResponse(@Nullable Call<KrakenTrades> call, @Nullable Response<KrakenTrades> response) {
                    KrakenTrades krakenTrades = response.body();
                    offset = offset + 50;

                    // delay 10ms to be sure the nonce will change
                    new android.os.Handler().postDelayed(
                            new Runnable() {
                                public void run() {
                                    populateTradeHistory();
                                }
                            },
                            10
                    );
                }

                @Override
                public void onFailure(@Nullable Call<KrakenTrades> call, @Nullable Throwable t) {
                    System.out.println("Failed: " + t.getLocalizedMessage());
                }
            });*/

            AsyncHttpClient client = new AsyncHttpClient();
            client.addHeader("API-Key", key);
            client.addHeader("API-Sign", sign);
            client.post("https://api.kraken.com" + path, params, new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    logger.warning("Error");
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                    // populate data from remote exchange
                    try {
                        if (response.getJSONArray("error").length() > 0) {
                            logger.warning("Error when trying to get Kraken trading history : " + response.getString("error"));
                        } else {

                            JSONObject json_data = response.getJSONObject("result").getJSONObject("trades");

                            if (json_data.length() > 0) {

                                //get any existing trades
                                List<String> tradeIds = db.krakenTradeDao().getTradeIds().getValue();

                                List<KrakenTrade> krakenTradeEntities = new ArrayList<>();
                                for (Iterator<String> rows = json_data.keys(); rows.hasNext(); ) {
                                    JSONObject json_row = json_data.getJSONObject(rows.next());
                                    String orderTxId = json_row.getString("ordertxid");
                                    if (tradeIds == null || !tradeIds.contains(orderTxId)) {
                                        String pair = json_row.getString("pair");
                                        Long time = Double.valueOf(json_row.getString("time")).longValue();
                                        String type = json_row.getString("type");
                                        String orderType = json_row.getString("ordertype");
                                        Double price = Double.parseDouble(json_row.getString("price"));
                                        Double cost = Double.parseDouble(json_row.getString("cost"));
                                        Double fee = Double.parseDouble(json_row.getString("fee"));
                                        Double vol = Double.parseDouble(json_row.getString("vol"));
                                        Double margin = Double.parseDouble(json_row.getString("margin"));
                                        String misc = json_row.getString("misc");
                                        krakenTradeEntities.add(new KrakenTrade(orderTxId, pair, time, type, orderType, price, cost, fee, vol, margin, misc));
                                    }
                                }

                                KrakenTrade[] krakenTradesArray = new KrakenTrade[krakenTradeEntities.size()];
                                krakenTradesArray = krakenTradeEntities.toArray(krakenTradesArray);
                                db.krakenTradeDao().insert(krakenTradesArray);

                                offset = offset + 50;

                                // delay 10ms to be sure the nonce will change
                                new android.os.Handler().postDelayed(
                                        new Runnable() {
                                            public void run() {
                                                populateKrakenTradeHistory();
                                            }
                                        },
                                        10
                                );
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject response) {
                    // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                    logger.warning("Failed to call Kraken API : " + statusCode);
                    try {
                        logger.warning("Message is : " + response.getString("error"));
                    } catch (JSONException jse) {
                        jse.printStackTrace();
                    }
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
        public void populatePoloniexTradeHistory() {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.YEAR, 2016);
            cal.set(Calendar.MONTH, 12);
            cal.set(Calendar.DAY_OF_MONTH, 26);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);

            String start = String.valueOf(cal.getTime().getTime() / 1000);

            String domain = properties.getProperty("POLONIEX_API_BASE_URL");
            String key = properties.getProperty("POLONIEX_API_PUBLIC_KEY");
            String nonce = String.valueOf(System.currentTimeMillis());

            RequestParams params = new RequestParams();
            params.add("command", "returnTradeHistory");
            params.add("start", start);
            params.add("currencyPair", "all");
            params.add("nonce", nonce);

            String sign = calculatePoloniexSignature(params.toString());

            Map<String, String> headerMap = new HashMap<>();
            headerMap.put("API-Key", key);
            headerMap.put("API-Sign", sign);


            Map<String, String> paramsMap = new HashMap<>();
            paramsMap.put("command", "returnTradeHistory");
            paramsMap.put("start", start);
            paramsMap.put("currencyPair", "all");
            paramsMap.put("nonce", nonce);

            PoloniexService poloniexService = PoloniexService.retrofit.create(PoloniexService.class);
            Call<Map<String, List<PoloniexTradeValue>>> call = poloniexService.getTradeHistory(headerMap, paramsMap);
            call.enqueue(new Callback<Map<String, List<PoloniexTradeValue>>>() {
                @Override
                public void onResponse(@Nullable Call<Map<String, List<PoloniexTradeValue>>> call, @Nullable Response<Map<String, List<PoloniexTradeValue>>> response) {
                    if (response != null) {
                        Map<String, List<PoloniexTradeValue>> poloniexTrades = response.body();
                    }

                }

                @Override
                public void onFailure(@Nullable Call<Map<String, List<PoloniexTradeValue>>> call, @Nullable Throwable t) {
                    System.out.println("Failed: " + t.getLocalizedMessage());
                }
            });

            /*AsyncHttpClient client = new AsyncHttpClient();
            client.addHeader("Key", key);
            client.addHeader("Sign", sign);
            client.post(domain, params, new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    UtilsHelper uh = new UtilsHelper(context);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", uh.getCurrentLocale());

                    //get any existing trades
                    List<Long> tradeIds = db.poloniexTradeDao().getTradeIds().getValue();

                    // populate data from remote exchange
                    try {
                        for (Iterator<String> rows = response.keys(); rows.hasNext(); ) {
                            String curr = rows.next();
                            if (curr.equals("error")) {
                                logger.warning("Error when trying to get Poloniex trading history : " + response.getString("error"));
                            } else {
                                JSONArray json_arr = response.getJSONArray(curr);
                                List<PoloniexTrade> poloniexTradeEntities = new ArrayList<>();
                                for (int i = 0; i < json_arr.length(); i++) {
                                    JSONObject json_data = json_arr.getJSONObject(i);
                                    Long globalTradeId = json_data.getLong("globalTradeID");
                                    if (tradeIds == null || !tradeIds.contains(globalTradeId)) {
                                        Long tradeId = json_data.getLong("tradeID");
                                        Date date = sdf.parse(json_data.getString("date"));
                                        Double rate = json_data.getDouble("rate");
                                        Double amount = json_data.getDouble("amount");
                                        Double total = Double.parseDouble(json_data.getString("total"));
                                        Double fee = Double.parseDouble(json_data.getString("fee"));
                                        Long orderNumber = json_data.getLong("orderNumber");
                                        String type = json_data.getString("type");
                                        String category = json_data.getString("category");
                                        poloniexTradeEntities.add(new PoloniexTrade(curr, globalTradeId, tradeId, date, rate, amount, total, fee, orderNumber, type, category));
                                    }
                                }
                                PoloniexTrade[] poloniexTradesArray = new PoloniexTrade[poloniexTradeEntities.size()];
                                poloniexTradesArray = poloniexTradeEntities.toArray(poloniexTradesArray);
                                db.poloniexTradeDao().insert(poloniexTradesArray);
                            }
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
            });*/
        }

        @Override
        protected Void doInBackground(Void... voids) {

            //region Kraken

            // insert Kraken exchange if it does not exist
            if (db.exchangeDao().getByName("Kraken") == null) {
                db.exchangeDao().insert(new Exchange("Kraken", "https://www.kraken.com", "Kraken exchange"));
            }

            // insert asset pairs from Kraken API
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

            // insert assets from Kraken API
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

            // insert currencies from Kraken table
            List<Currency> krakenCurrencyList = db.currencyDao().getFromKraken().getValue();
            if (krakenCurrencyList != null) {
                Currency[] currencyArray = new Currency[krakenCurrencyList.size()];
                currencyArray = krakenCurrencyList.toArray(currencyArray);
                db.currencyDao().insert(currencyArray);
            }

            /*populateTradeHistory();

            // insert transaction from Kraken table
            List<Transaction> transactionList = db.transactionDao().getKrakenTransactions().getValue();
            if (transactionList != null) {
                Transaction[] transactionArray = new Transaction[transactionList.size()];
                transactionArray = transactionList.toArray(transactionArray);
                db.transactionDao().insert(transactionArray);
            }*/
            //endregion

            //region Poloniex

            // insert Poloniex exchange if it does not exist
            if (db.exchangeDao().getByName("Poloniex") == null) {
                db.exchangeDao().insert(new Exchange("Poloniex", "https://www.poloniex.com", "Poloniex exchange"));
            }

            // insert assets from Poloniex API
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

            // insert currencies from Poloniex tables
            List<Currency> poloniexCurrencyList = db.currencyDao().getFromPoloniex().getValue();
            if (poloniexCurrencyList != null) {
                Currency[] currencyArray2 = new Currency[poloniexCurrencyList.size()];
                currencyArray2 = poloniexCurrencyList.toArray(currencyArray2);
                db.currencyDao().insert(currencyArray2);
            }

            populatePoloniexTradeHistory();

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
