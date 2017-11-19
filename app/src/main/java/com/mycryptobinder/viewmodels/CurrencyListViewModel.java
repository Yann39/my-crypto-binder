package com.mycryptobinder.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.paging.PagedList;
import android.os.AsyncTask;

import com.mycryptobinder.entities.AppDatabase;
import com.mycryptobinder.entities.Currency;

import java.util.List;

/**
 * Created by Yann
 * Created on 08/11/2017
 */

public class CurrencyListViewModel extends AndroidViewModel {

    private final LiveData<List<Currency>> currencyList;
    private AppDatabase appDatabase;
    private final LiveData<PagedList<Currency>> pagedCurrencyList;

    public CurrencyListViewModel(Application application) {
        super(application);
        appDatabase = AppDatabase.getDatabase(this.getApplication());
        currencyList = appDatabase.currencyDao().getAll();
        pagedCurrencyList = appDatabase.currencyDao().getAllPaged().create(0, new PagedList.Config.Builder().setPageSize(50).setPrefetchDistance(50).build());
    }

    public LiveData<List<Currency>> getCurrencyList() {
        return currencyList;
    }

    public LiveData<PagedList<Currency>> getPagedCurrencyList() {
        return pagedCurrencyList;
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
