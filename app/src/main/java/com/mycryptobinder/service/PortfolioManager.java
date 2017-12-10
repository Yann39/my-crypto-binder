package com.mycryptobinder.service;

import android.content.Context;

import com.mycryptobinder.entities.AppDatabase;

/**
 * Created by Yann
 * Created on 06/12/2017
 */

public class PortfolioManager {

    private static AppDatabase appDatabase;

    public PortfolioManager(Context context) {
        appDatabase = AppDatabase.getDatabase(context);
    }

    /**
     * delete all data
     */
    public void deleteAll() {
        appDatabase.transactionDao().deleteAll();
        appDatabase.currencyDao().deleteAll();
        appDatabase.exchangeDao().deleteAll();
    }

}
