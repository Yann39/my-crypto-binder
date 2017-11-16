package com.mycryptobinder.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import com.mycryptobinder.entities.AppDatabase;
import com.mycryptobinder.entities.Currency;
import com.mycryptobinder.entities.Exchange;

import java.util.List;

/**
 * Created by Yann
 * Created on 08/11/2017
 */

public class ExchangeListViewModel extends AndroidViewModel {

    private final LiveData<List<Exchange>> exchanges;
    private AppDatabase appDatabase;

    public ExchangeListViewModel(Application application) {
        super(application);
        appDatabase = AppDatabase.getDatabase(this.getApplication());
        exchanges = appDatabase.exchangeDao().getAll();
    }

    public LiveData<List<Exchange>> getExchangeList() {
        return exchanges;
    }

    public void deleteItem(Exchange exchange) {
        new deleteAsyncTask(appDatabase).execute(exchange);
    }

    private static class deleteAsyncTask extends AsyncTask<Exchange, Void, Void> {
        private AppDatabase db;

        deleteAsyncTask(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(final Exchange... exchanges) {
            db.exchangeDao().delete(exchanges[0]);
            return null;
        }
    }

}
