package com.mycryptobinder.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import com.mycryptobinder.entities.AppDatabase;
import com.mycryptobinder.models.HoldingData;

import java.util.List;

/**
 * Created by Yann
 * Created on 08/11/2017
 */

public class PortfolioViewModel extends AndroidViewModel {

    private final LiveData<Integer> nbDifferentCurrencies;
    private final LiveData<List<HoldingData>> holdings;

    public PortfolioViewModel(Application application) {
        super(application);
        AppDatabase appDatabase = AppDatabase.getDatabase(this.getApplication());
        nbDifferentCurrencies = appDatabase.transactionDao().getNbDifferentCurrencies();
        holdings = appDatabase.transactionDao().getHoldings();
    }

    public LiveData<List<HoldingData>> getHoldings() {
        return holdings;
    }

    public LiveData<Integer> getNbDifferentCurrencies() {
        return nbDifferentCurrencies;
    }

}