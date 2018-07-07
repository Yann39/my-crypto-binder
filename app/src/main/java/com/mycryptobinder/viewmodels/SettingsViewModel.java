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

package com.mycryptobinder.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;
import android.widget.Toast;

import com.mycryptobinder.R;
import com.mycryptobinder.entities.AppDatabase;
import com.mycryptobinder.helpers.UtilsHelper;
import com.mycryptobinder.managers.BitfinexManager;
import com.mycryptobinder.managers.BittrexManager;
import com.mycryptobinder.managers.KrakenManager;
import com.mycryptobinder.managers.PoloniexManager;
import com.mycryptobinder.managers.PortfolioManager;
import com.mycryptobinder.models.HoldingData;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class SettingsViewModel extends AndroidViewModel {

    private static MutableLiveData<String> logs;
    private static MutableLiveData<Integer> percentDone;
    private final SimpleDateFormat sdfLog;
    private final AppDatabase appDatabase;

    public SettingsViewModel(Application application) {
        super(application);
        appDatabase = AppDatabase.getInstance(application.getBaseContext());
        logs = new MutableLiveData<>();
        percentDone = new MutableLiveData<>();
        sdfLog = new SimpleDateFormat("k:mm:ss", UtilsHelper.getCurrentLocale(application.getBaseContext()));
    }

    /**
     * Get current logs from the executing asynchronous task
     *
     * @return The current logs live data
     */
    public MutableLiveData<String> getCurrentLogs() {
        return logs;
    }

    /**
     * Get current percentage done from the executing asynchronous task
     *
     * @return The current percentage done live data
     */
    public MutableLiveData<Integer> getPercentDone() {
        return percentDone;
    }

    /**
     * Get current holding data from the database
     *
     * @return The current holding data
     */
    public LiveData<List<HoldingData>> getHoldings() {
        return appDatabase.transactionDao().getHoldings();
    }

    /**
     * Populate the database with data from the selected exchange API
     *
     * @param reset             A boolean indicating if the existing data must be deleted
     * @param selectedExchanges Boolean array representing the exchanges that must be processed
     *                          Indexes : 0 - Kraken, 1 - Poloniex, 2 - Bittrex, 3 - Bitfinex
     */
    public void populateDatabase(boolean reset, boolean[] selectedExchanges) {
        new populateDatabaseAsyncTask(getApplication(), reset, selectedExchanges, sdfLog).execute();
    }

    private static class populateDatabaseAsyncTask extends AsyncTask<Void, Void, Void> {
        private SimpleDateFormat sdfLog;
        private boolean reset;
        private boolean[] selectedExchanges;
        // use a weak reference to the application to get the context, to avoid memory leak by using a static variable
        private WeakReference<Application> appReference;

        populateDatabaseAsyncTask(Application application, boolean reset, boolean[] selectedExchanges, SimpleDateFormat sdfLog) {
            this.sdfLog = sdfLog;
            this.reset = reset;
            this.selectedExchanges = selectedExchanges;
            this.appReference = new WeakReference<>(application);
        }

        //todo logInfo does not log every time...
        private void logInfo(String message) {
            logs.postValue((logs.getValue() != null ? (logs.getValue() + "\n") : "") + sdfLog.format(Calendar.getInstance().getTime()) + " : " + message);
        }

        @Override
        protected Void doInBackground(Void... voids) {

            // get a reference to the application if it is still there
            Application app = appReference.get();
            if (app == null) return null;

            // get managers
            KrakenManager krakenManager = new KrakenManager(app.getBaseContext());
            PoloniexManager poloniexManager = new PoloniexManager(app.getBaseContext());
            BittrexManager bittrexManager = new BittrexManager(app.getBaseContext());
            BitfinexManager bitfinexManager = new BitfinexManager(app.getBaseContext());
            PortfolioManager portfolioManager = new PortfolioManager(app.getBaseContext());

            percentDone.postValue(2);

            // if user requested to clean existing data
            if (reset) {
                logInfo("Deleting data...");
                krakenManager.deleteAll();
                poloniexManager.deleteAll();
                bittrexManager.deleteAll();
                bitfinexManager.deleteAll();
                portfolioManager.deleteAll();
                logInfo("All data has been cleared");
            }
            percentDone.postValue(8);

            // if at least one exchange selected
            if (selectedExchanges != null && selectedExchanges.length > 0) {

                int nbIns;

                // Kraken exchange
                if (selectedExchanges[0]) {
                    if (krakenManager.areApiKeysDefined()) {
                        // insert Kraken deposits/withdrawals from API
                        /*logInfo("Collecting deposits/withdrawals from Kraken API...");
                        int nbIns = krakenManager.populateDepositsWithdrawals();
                        logInfo(nbIns + " new deposits/withdrawals inserted");
                        percentDone.postValue(9);*/

                        // insert Kraken trade history from API
                        logInfo("Collecting trade history from Kraken API...");
                        nbIns = krakenManager.populateTradeHistory();
                        logInfo(nbIns + " new trades inserted");

                        // insert Kraken asset pairs from API
                        logInfo("Collecting asset pairs from Kraken API...");
                        nbIns = krakenManager.populateAssetPairs();
                        logInfo(nbIns + " new asset pairs inserted");

                        // insert Kraken assets from API
                        logInfo("Collecting assets from Kraken API...");
                        nbIns = krakenManager.populateAssets();
                        logInfo(nbIns + " new assets inserted");

                        // insert Kraken currencies from assets
                        logInfo("Inserting currencies from Kraken assets...");
                        nbIns = krakenManager.populateCurrencies();
                        logInfo(nbIns + " currencies inserted");

                        // insert Kraken transactions from trade history
                        logInfo("Inserting transactions from Kraken trade history...");
                        nbIns = krakenManager.populateTransactions();
                        logInfo(nbIns + " transactions inserted");
                    } else {
                        Toast.makeText(app.getBaseContext(), app.getBaseContext().getString(R.string.error_api_keys_not_defined_fro_exchange, "Kraken"), Toast.LENGTH_SHORT).show();
                    }
                }

                percentDone.postValue(31);

                // Poloniex exchange
                if (selectedExchanges.length > 1 && selectedExchanges[1]) {
                    if (poloniexManager.areApiKeysDefined()) {
                        // insert Poloniex deposits/withdrawals from API
                        logInfo("Collecting deposits/withdrawals from Poloniex API...");
                        nbIns = poloniexManager.populateDepositsWithdrawals();
                        logInfo(nbIns + " new deposits/withdrawals inserted");

                        // insert Poloniex trade history from API
                        logInfo("Collecting trade history from Poloniex API...");
                        nbIns = poloniexManager.populateTradeHistory();
                        logInfo(nbIns + " new trades inserted");

                        // insert Poloniex assets from API
                        logInfo("Collecting assets from Poloniex assets...");
                        nbIns = poloniexManager.populateAssets();
                        logInfo(nbIns + " currencies inserted");

                        // insert Poloniex currencies from assets
                        logInfo("Inserting currencies from Poloniex assets...");
                        nbIns = poloniexManager.populateCurrencies();
                        logInfo(nbIns + " currencies inserted");

                        // insert Poloniex transactions from trade history
                        logInfo("Inserting transactions from Poloniex trade history...");
                        nbIns = poloniexManager.populateTransactions();
                        logInfo(nbIns + " transactions inserted");
                    } else {
                        Toast.makeText(app.getBaseContext(), app.getBaseContext().getString(R.string.error_api_keys_not_defined_fro_exchange, "Poloniex"), Toast.LENGTH_SHORT).show();
                    }
                }
                percentDone.postValue(54);

                // Bittrex exchange
                if (selectedExchanges.length > 2 && selectedExchanges[2]) {
                    if (bittrexManager.areApiKeysDefined()) {
                        // insert Bittrex deposits from API
                        logInfo("Collecting deposits from Bittrex API...");
                        nbIns = bittrexManager.populateDeposits();
                        logInfo(nbIns + " new deposits/withdrawals inserted");

                        // insert Bittrex withdrawals from API
                        logInfo("Collecting withdrawals from Bittrex API...");
                        nbIns = bittrexManager.populateWithdrawals();
                        logInfo(nbIns + " new withdrawals inserted");

                        // insert Bittrex trade history from API
                        logInfo("Collecting trade history from Bittrex API...");
                        nbIns = bittrexManager.populateTradeHistory();
                        logInfo(nbIns + " new trades inserted");

                        // insert Bittrex assets from API
                        logInfo("Collecting assets from Bittrex assets...");
                        nbIns = bittrexManager.populateAssets();
                        logInfo(nbIns + " currencies inserted");

                        // insert Bittrex currencies from assets
                        logInfo("Inserting currencies from Bittrex assets...");
                        nbIns = bittrexManager.populateCurrencies();
                        logInfo(nbIns + " currencies inserted");

                        // insert Bittrex transactions from trade history
                        logInfo("Inserting transactions from Bittrex trade history...");
                        nbIns = bittrexManager.populateTransactions();
                        logInfo(nbIns + " transactions inserted");
                    } else {
                        Toast.makeText(app.getBaseContext(), app.getBaseContext().getString(R.string.error_api_keys_not_defined_fro_exchange, "Bittrex"), Toast.LENGTH_SHORT).show();
                    }
                }
                percentDone.postValue(77);

                // Bitfinex exchange
                if (selectedExchanges.length > 3 && selectedExchanges[3]) {
                    if (bitfinexManager.areApiKeysDefined()) {
                        // insert Bitfinex trade history from API
                        logInfo("Collecting trade history from Bitfinex API...");
                        nbIns = bitfinexManager.populateOrderHistory();
                        logInfo(nbIns + " new trades inserted");

                        // insert Bitfinex assets from API
                        logInfo("Collecting assets from Bitfinex assets...");
                        nbIns = bitfinexManager.populateAssets();
                        logInfo(nbIns + " currencies inserted");

                        // insert Bitfinex currencies from assets
                        logInfo("Inserting currencies from Bitfinex assets...");
                        nbIns = bitfinexManager.populateCurrencies();
                        logInfo(nbIns + " currencies inserted");

                        // insert Bitfinex transactions from trade history
                        logInfo("Inserting transactions from Bitfinex trade history...");
                        nbIns = bitfinexManager.populateTransactions();
                        logInfo(nbIns + " transactions inserted");
                    } else {
                        Toast.makeText(app.getBaseContext(), app.getBaseContext().getString(R.string.error_api_keys_not_defined_fro_exchange, "Bitfinex"), Toast.LENGTH_SHORT).show();
                    }
                }
            }
            percentDone.postValue(100);


            return null;
        }
    }

}
