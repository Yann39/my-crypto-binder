package com.mycryptobinder.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.view.View;

import com.mycryptobinder.entities.AppDatabase;
import com.mycryptobinder.entities.Currency;
import com.mycryptobinder.models.HoldingData;
import com.mycryptobinder.models.Price;
import com.mycryptobinder.service.CryptoCompareService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Yann
 * Created on 08/11/2017
 */

public class PortfolioViewModel extends AndroidViewModel {

    private final LiveData<Integer> nbDifferentCurrencies;
    private final LiveData<List<HoldingData>> holdings;
    private AppDatabase appDatabase;

    public PortfolioViewModel(Application application) {
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
