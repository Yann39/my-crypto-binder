package com.mycryptobinder.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.os.AsyncTask;

import com.mycryptobinder.entities.AppDatabase;
import com.mycryptobinder.entities.Currency;

/**
 * Created by Yann
 * Created on 09/11/2017
 */

public class AddCurrencyViewModel extends AndroidViewModel {

    private AppDatabase appDatabase;

    public AddCurrencyViewModel(Application application) {
        super(application);
        appDatabase = AppDatabase.getDatabase(this.getApplication());
    }

    public void addCurrency(Currency currency) {
        new addAsyncTask(appDatabase).execute(currency);
    }

    public void updateCurrency(Currency currency) {
        new editAsyncTask(appDatabase).execute(currency);
    }

    private static class addAsyncTask extends AsyncTask<Currency, Void, Void> {

        private AppDatabase db;

        addAsyncTask(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(final Currency... currencies) {
            db.currencyDao().insert(currencies[0]);
            return null;
        }

    }

    private static class editAsyncTask extends AsyncTask<Currency, Void, Void> {

        private AppDatabase db;

        editAsyncTask(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(final Currency... currencies) {
            db.currencyDao().update(currencies[0]);
            return null;
        }

    }

}
