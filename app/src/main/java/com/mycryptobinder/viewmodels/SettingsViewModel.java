package com.mycryptobinder.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;

import com.mycryptobinder.entities.AppDatabase;
import com.mycryptobinder.helpers.UtilsHelper;
import com.mycryptobinder.models.HoldingData;
import com.mycryptobinder.service.KrakenManager;
import com.mycryptobinder.service.PoloniexManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Yann
 * Created on 08/11/2017
 */

public class SettingsViewModel extends AndroidViewModel {

    private static MutableLiveData<String> logs;
    private static MutableLiveData<Integer> percentDone;
    private static KrakenManager km;
    private static PoloniexManager pm;
    private final SimpleDateFormat sdfLog;
    private final AppDatabase appDatabase;

    public SettingsViewModel(Application application) {
        super(application);
        appDatabase = AppDatabase.getDatabase(this.getApplication());
        logs = new MutableLiveData<>();
        percentDone = new MutableLiveData<>();
        km = new KrakenManager(getApplication().getBaseContext());
        pm = new PoloniexManager(getApplication().getBaseContext());
        UtilsHelper uh = new UtilsHelper(getApplication().getBaseContext());
        sdfLog = new SimpleDateFormat("k:m:ss", uh.getCurrentLocale());
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

    public LiveData<Integer> getNbDifferentCurrencies() {
        return appDatabase.transactionDao().getNbDifferentCurrencies();
    }

    public void populateDatabase() {
        new populateDatabaseAsyncTask(sdfLog).execute();
    }

    private static class populateDatabaseAsyncTask extends AsyncTask<Void, Void, Void> {
        private SimpleDateFormat sdfLog;

        populateDatabaseAsyncTask(SimpleDateFormat sdfLog) {
            this.sdfLog = sdfLog;
        }

        private void logInfo(String message) {
            logs.postValue((logs.getValue() != null ? (logs.getValue() + "\n") : "") + sdfLog.format(Calendar.getInstance().getTime()) + " : " + message);
        }

        @Override
        protected Void doInBackground(Void... voids) {

            percentDone.postValue(2);

            // insert Kraken trade history from API
            logInfo("Collecting trade history from Kraken API...");
            int nbIns = km.populateTradeHistory();
            logInfo(nbIns + " new trades inserted");
            percentDone.postValue(30);

            //insert Kraken exchange
            logInfo("Creating Kraken exchange...");
            nbIns = km.populateExchange();
            if (nbIns > 0) {
                logInfo("Kraken exchange created successfully");
            } else {
                logInfo("Kraken exchange already exist, nothing to do");
            }
            percentDone.postValue(32);

            // insert Kraken asset pairs from API
            logInfo("Collecting asset pairs from Kraken API...");
            nbIns = km.populateAssetPairs();
            logInfo(nbIns + " new asset pairs inserted");
            percentDone.postValue(37);

            // insert Kraken assets from API
            logInfo("Collecting assets from Kraken API...");
            nbIns = km.populateAssets();
            logInfo(nbIns + " new assets inserted");
            percentDone.postValue(42);

            // insert Kraken currencies from assets
            logInfo("Inserting currencies from Kraken assets...");
            nbIns = km.populateCurrencies();
            logInfo(nbIns + " currencies inserted");
            percentDone.postValue(47);

            // insert Kraken transactions from trade history
            logInfo("Inserting transactions from Kraken trade history...");
            nbIns = km.populateTransactions();
            logInfo(nbIns + " transactions inserted");
            percentDone.postValue(52);

            // insert Poloniex trade history from API
            logInfo("Collecting trade history from Poloniex API...");
            nbIns = pm.populateTradeHistory();
            logInfo(nbIns + " new trades inserted");
            percentDone.postValue(82);

            // insert Poloniex exchange
            logInfo("Creating Poloniex exchange...");
            nbIns = pm.populateExchange();
            if (nbIns > 0) {
                logInfo("Poloniex exchange created successfully");
            } else {
                logInfo("Poloniex exchange already exist, nothing to do");
            }
            percentDone.postValue(84);

            // insert Poloniex assets from API
            logInfo("Inserting currencies from Poloniex assets...");
            nbIns = pm.populateAssets();
            logInfo(nbIns + " currencies inserted");
            percentDone.postValue(89);

            // insert Poloniex currencies from assets
            logInfo("Inserting currencies from Poloniex assets...");
            nbIns = pm.populateCurrencies();
            logInfo(nbIns + " currencies inserted");
            percentDone.postValue(94);

            // insert Poloniex transactions from trade history
            logInfo("Inserting transactions from Poloniex trade history...");
            nbIns = pm.populateTransactions();
            logInfo(nbIns + " transactions inserted");
            percentDone.postValue(100);

            return null;
        }
    }

}
