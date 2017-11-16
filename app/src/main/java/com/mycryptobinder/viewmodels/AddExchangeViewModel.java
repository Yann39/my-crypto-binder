package com.mycryptobinder.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.os.AsyncTask;

import com.mycryptobinder.entities.AppDatabase;
import com.mycryptobinder.entities.Currency;
import com.mycryptobinder.entities.Exchange;

/**
 * Created by Yann
 * Created on 09/11/2017
 */

public class AddExchangeViewModel extends AndroidViewModel {

    private AppDatabase appDatabase;

    public AddExchangeViewModel(Application application) {
        super(application);
        appDatabase = AppDatabase.getDatabase(this.getApplication());
    }

    public void addExchange(Exchange exchange) {
        new addAsyncTask(appDatabase).execute(exchange);
    }

    public void updateExchange(Exchange exchange) {
        new editAsyncTask(appDatabase).execute(exchange);
    }

    private static class addAsyncTask extends AsyncTask<Exchange, Void, Void> {

        private AppDatabase db;

        addAsyncTask(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(final Exchange... exchanges) {
            db.exchangeDao().insert(exchanges[0]);
            return null;
        }

    }

    private static class editAsyncTask extends AsyncTask<Exchange, Void, Void> {

        private AppDatabase db;

        editAsyncTask(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(final Exchange... exchanges) {
            db.exchangeDao().update(exchanges[0]);
            return null;
        }

    }

}
