package com.mycryptobinder.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.Transformations;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;

import com.mycryptobinder.activities.PortfolioFragment;
import com.mycryptobinder.entities.AppDatabase;
import com.mycryptobinder.entities.Currency;
import com.mycryptobinder.models.HoldingData;
import com.mycryptobinder.models.Price;
import com.mycryptobinder.service.CryptoCompareManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Created by Yann
 * Created on 08/11/2017
 */

public class PortfolioViewModel extends AndroidViewModel {

    private final LiveData<List<HoldingData>> holdings;
    private final LiveData<List<String>> differentCurrencies;
    private final LiveData<Map<String, Price>> prices;
    private final MutableLiveData<List<String>> codes = new MutableLiveData<>();
    private final CryptoCompareManager cryptoCompareManager;

    public PortfolioViewModel(Application application) {
        super(application);
        AppDatabase appDatabase = AppDatabase.getDatabase(getApplication());
        cryptoCompareManager = new CryptoCompareManager();
        differentCurrencies = appDatabase.transactionDao().getDifferentCurrencies();
        holdings = appDatabase.transactionDao().getHoldings();
        prices = Transformations.switchMap(codes, cryptoCompareManager::getCurrentPricesData);
    }

    public LiveData<List<HoldingData>> getHoldings() {
        return holdings;
    }

    public LiveData<Map<String, Price>> getCurrentPrices() {
        return prices;
    }

    public LiveData<List<String>> getDifferentCurrencies() {
        return differentCurrencies;
    }

    public void setCurrencyCodes(List<String> codes) {
        this.codes.setValue(codes);
    }

    public void refresh() {
        cryptoCompareManager.getCurrentPrices(this.codes.getValue());
    }

}