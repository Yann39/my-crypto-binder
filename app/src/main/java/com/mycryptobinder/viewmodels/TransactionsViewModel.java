package com.mycryptobinder.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import com.mycryptobinder.entities.AppDatabase;
import com.mycryptobinder.entities.Transaction;

import java.util.List;

/**
 * Created by Yann
 * Created on 08/11/2017
 */

public class TransactionsViewModel extends AndroidViewModel {

    private final AppDatabase appDatabase;
    private final LiveData<List<Transaction>> transactionList;

    public TransactionsViewModel(Application application) {
        super(application);
        appDatabase = AppDatabase.getDatabase(this.getApplication());
        transactionList = appDatabase.transactionDao().getAll();
    }

    public LiveData<List<Transaction>> getTransactionList() {
        return transactionList;
    }

    public void deleteItem(Transaction transaction) {
        new deleteAsyncTask(appDatabase).execute(transaction);
    }

    private static class deleteAsyncTask extends AsyncTask<Transaction, Void, Void> {
        private AppDatabase db;

        deleteAsyncTask(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(final Transaction... transactions) {
            db.transactionDao().delete(transactions[0]);
            return null;
        }
    }

}
