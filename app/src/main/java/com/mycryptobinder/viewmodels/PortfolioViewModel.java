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
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;

import com.mycryptobinder.entities.AppDatabase;
import com.mycryptobinder.managers.CryptoCompareManager;
import com.mycryptobinder.models.HoldingData;
import com.mycryptobinder.models.PricesFull;

import java.util.List;

public class PortfolioViewModel extends AndroidViewModel {

    private final LiveData<List<HoldingData>> holdings;
    private final LiveData<List<String>> differentCurrencies;
    private final LiveData<PricesFull> pricesFull;
    private final MutableLiveData<List<String>> codes = new MutableLiveData<>();
    private final CryptoCompareManager cryptoCompareManager;

    public PortfolioViewModel(Application application) {
        super(application);
        AppDatabase appDatabase = AppDatabase.getInstance(getApplication());
        cryptoCompareManager = new CryptoCompareManager(application.getApplicationContext());
        differentCurrencies = appDatabase.transactionDao().getDifferentCurrencies();
        holdings = appDatabase.transactionDao().getHoldings();
        pricesFull = Transformations.switchMap(codes, cryptoCompareManager::getCurrentPricesFullData);
    }

    public LiveData<List<HoldingData>> getHoldings() {
        return holdings;
    }

    public LiveData<PricesFull> getCurrentPricesFull() {
        return pricesFull;
    }

    public LiveData<List<String>> getDifferentCurrencies() {
        return differentCurrencies;
    }

    public void setCurrencyCodes(List<String> codes) {
        this.codes.setValue(codes);
    }

    public void refresh() {
        cryptoCompareManager.getCurrentPricesFull(this.codes.getValue());
    }

}