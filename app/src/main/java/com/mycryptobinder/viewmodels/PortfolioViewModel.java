package com.mycryptobinder.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import com.mycryptobinder.entities.AppDatabase;
import com.mycryptobinder.entities.Currency;
import com.mycryptobinder.models.HoldingData;

import java.util.List;

/**
 * Created by Yann
 * Created on 08/11/2017
 */

public class PortfolioViewModel extends AndroidViewModel {

    private final LiveData<List<Currency>> currencyList;
    private final LiveData<List<HoldingData>> holdings;
    private AppDatabase appDatabase;

    public PortfolioViewModel(Application application) {
        super(application);
        appDatabase = AppDatabase.getDatabase(this.getApplication());
        currencyList = appDatabase.currencyDao().getAll();
        holdings = appDatabase.transactionDao().getHoldings();
    }

    public LiveData<List<Currency>> getCurrencyList() {
        return currencyList;
    }

    public LiveData<List<HoldingData>> getHoldings() {
        return holdings;
    }

    public Double getCurrencyQuantity(String isoCode) {
        return appDatabase.transactionDao().getCurrencyQuantity(isoCode);
    }

    public int getNbDifferentCurrencies() {
        return appDatabase.transactionDao().getNbDifferentCurrencies();
    }

    public void deleteItem(Currency currency) {
        new deleteAsyncTask(appDatabase).execute(currency);
    }

    private static class deleteAsyncTask extends AsyncTask<Currency, Void, Void> {
        private AppDatabase db;

        deleteAsyncTask(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(final Currency... currencies) {
            db.currencyDao().delete(currencies[0]);
            return null;
        }
    }

}
