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
import android.os.AsyncTask;

import com.mycryptobinder.entities.AppDatabase;
import com.mycryptobinder.entities.Currency;

public class AddCurrencyViewModel extends AndroidViewModel {

    private AppDatabase appDatabase;

    public AddCurrencyViewModel(Application application) {
        super(application);
        appDatabase = AppDatabase.getInstance(this.getApplication());
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
