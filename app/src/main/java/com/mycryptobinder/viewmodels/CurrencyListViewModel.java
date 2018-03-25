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
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;
import android.os.AsyncTask;

import com.mycryptobinder.entities.AppDatabase;
import com.mycryptobinder.entities.Currency;

import java.util.List;

public class CurrencyListViewModel extends AndroidViewModel {

    private final LiveData<List<Currency>> currencyList;
    private AppDatabase appDatabase;
    private final LiveData<PagedList<Currency>> pagedCurrencyList;

    public CurrencyListViewModel(Application application) {
        super(application);
        appDatabase = AppDatabase.getInstance(this.getApplication());
        currencyList = appDatabase.currencyDao().getAll();
        //pagedCurrencyList = appDatabase.currencyDao().getAllPaged().create(0, new PagedList.Config.Builder().setPageSize(50).setPrefetchDistance(50).build());
        pagedCurrencyList = new LivePagedListBuilder<>(appDatabase.currencyDao().getAllPaged(), 20).build();
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
