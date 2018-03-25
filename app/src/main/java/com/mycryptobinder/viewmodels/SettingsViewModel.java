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

import com.mycryptobinder.entities.AppDatabase;
import com.mycryptobinder.helpers.UtilsHelper;
import com.mycryptobinder.models.HoldingData;
import com.mycryptobinder.managers.KrakenManager;
import com.mycryptobinder.managers.PoloniexManager;
import com.mycryptobinder.managers.PortfolioManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class SettingsViewModel extends AndroidViewModel {

    private static MutableLiveData<String> logs;
    private static MutableLiveData<Integer> percentDone;
    private static KrakenManager krakenManager;
    private static PoloniexManager poloniexManager;
    private static PortfolioManager portfolioManager;
    private final SimpleDateFormat sdfLog;
    private final AppDatabase appDatabase;

    public SettingsViewModel(Application application) {
        super(application);
        appDatabase = AppDatabase.getInstance(this.getApplication());
        logs = new MutableLiveData<>();
        percentDone = new MutableLiveData<>();
        krakenManager = new KrakenManager(getApplication().getBaseContext());
        poloniexManager = new PoloniexManager(getApplication().getBaseContext());
        portfolioManager = new PortfolioManager(getApplication().getBaseContext());
        UtilsHelper uh = new UtilsHelper(getApplication().getBaseContext());
        sdfLog = new SimpleDateFormat("k:mm:ss", uh.getCurrentLocale());
    }

    public MutableLiveData<String> getCurrentLogs() {
        return logs;
    }

    public MutableLiveData<Integer> getPercentDone() {
        return percentDone;
    }

    public LiveData<List<HoldingData>> getHoldings() {
        return appDatabase.transactionDao().getHoldings();
    }

    public void populateDatabase(boolean reset) {
        new populateDatabaseAsyncTask(reset, sdfLog).execute();
    }

    private static class populateDatabaseAsyncTask extends AsyncTask<Void, Void, Void> {
        private SimpleDateFormat sdfLog;
        private boolean reset;

        populateDatabaseAsyncTask(boolean reset, SimpleDateFormat sdfLog) {
            this.sdfLog = sdfLog;
            this.reset = reset;
        }

        private void logInfo(String message) {
            logs.postValue((logs.getValue() != null ? (logs.getValue() + "\n") : "") + sdfLog.format(Calendar.getInstance().getTime()) + " : " + message);
        }

        @Override
        protected Void doInBackground(Void... voids) {

            //todo check that API keys are defined before continuing
            percentDone.postValue(2);

            if (reset) {
                logInfo("Deleting data...");
                krakenManager.deleteAll();
                poloniexManager.deleteAll();
                portfolioManager.deleteAll();
                logInfo("All data has been cleared");
            }
            percentDone.postValue(10);

            // insert Kraken deposits/withdrawals from API
            /*logInfo("Collecting deposits/withdrawals from Kraken API...");
            int nbIns = krakenManager.populateDepositsWithdrawals();
            logInfo(nbIns + " new deposits/withdrawals inserted");
            percentDone.postValue(9);*/

            //todo logInfo does not log every time...

            // insert Kraken trade history from API
            logInfo("Collecting trade history from Kraken API...");
            int nbIns = krakenManager.populateTradeHistory();
            logInfo(nbIns + " new trades inserted");
            percentDone.postValue(30);

            // insert Kraken asset pairs from API
            logInfo("Collecting asset pairs from Kraken API...");
            nbIns = krakenManager.populateAssetPairs();
            logInfo(nbIns + " new asset pairs inserted");
            percentDone.postValue(37);

            // insert Kraken assets from API
            logInfo("Collecting assets from Kraken API...");
            nbIns = krakenManager.populateAssets();
            logInfo(nbIns + " new assets inserted");
            percentDone.postValue(42);

            // insert Kraken currencies from assets
            logInfo("Inserting currencies from Kraken assets...");
            nbIns = krakenManager.populateCurrencies();
            logInfo(nbIns + " currencies inserted");
            percentDone.postValue(47);

            // insert Kraken transactions from trade history
            logInfo("Inserting transactions from Kraken trade history...");
            nbIns = krakenManager.populateTransactions();
            logInfo(nbIns + " transactions inserted");
            percentDone.postValue(52);

            // insert Poloniex deposits/withdrawals from API
            logInfo("Collecting deposits/withdrawals from Poloniex API...");
            nbIns = poloniexManager.populateDepositsWithdrawals();
            logInfo(nbIns + " new deposits/withdrawals inserted");
            percentDone.postValue(60);

            // insert Poloniex trade history from API
            logInfo("Collecting trade history from Poloniex API...");
            nbIns = poloniexManager.populateTradeHistory();
            logInfo(nbIns + " new trades inserted");
            percentDone.postValue(82);

            // insert Poloniex assets from API
            logInfo("Inserting currencies from Poloniex assets...");
            nbIns = poloniexManager.populateAssets();
            logInfo(nbIns + " currencies inserted");
            percentDone.postValue(89);

            // insert Poloniex currencies from assets
            logInfo("Inserting currencies from Poloniex assets...");
            nbIns = poloniexManager.populateCurrencies();
            logInfo(nbIns + " currencies inserted");
            percentDone.postValue(94);

            // insert Poloniex transactions from trade history
            logInfo("Inserting transactions from Poloniex trade history...");
            nbIns = poloniexManager.populateTransactions();
            logInfo(nbIns + " transactions inserted");
            percentDone.postValue(100);

            return null;
        }
    }

}
