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
import com.mycryptobinder.entities.Exchange;

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
