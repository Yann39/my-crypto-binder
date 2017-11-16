package com.mycryptobinder.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import com.mycryptobinder.entities.AppDatabase;
import com.mycryptobinder.entities.Transaction;

/**
 * Created by Yann
 * Created on 09/11/2017
 */

public class AddTransactionViewModel extends AndroidViewModel {

    private AppDatabase appDatabase;

    public AddTransactionViewModel(Application application) {
        super(application);
        appDatabase = AppDatabase.getDatabase(this.getApplication());
    }

    public void addTransaction(Transaction transaction) {
        new addAsyncTask(appDatabase).execute(transaction);
    }

    public void updateTransaction(Transaction transaction) {
        new editAsyncTask(appDatabase).execute(transaction);
    }

    public Double getCurrencyTotal(String isoCode) {
        return appDatabase.transactionDao().getCurrencyTotal(isoCode);
    }

    private static class addAsyncTask extends AsyncTask<Transaction, Void, Void> {

        private AppDatabase db;

        addAsyncTask(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(final Transaction... transactions) {
            db.transactionDao().insert(transactions[0]);
            return null;
        }

    }

    private static class editAsyncTask extends AsyncTask<Transaction, Void, Void> {

        private AppDatabase db;

        editAsyncTask(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(final Transaction... transactions) {
            db.transactionDao().update(transactions[0]);
            return null;
        }

    }

}
