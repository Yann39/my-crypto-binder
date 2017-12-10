package com.mycryptobinder.service;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.mycryptobinder.entities.Currency;
import com.mycryptobinder.models.Price;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Yann
 * Created on 04/12/2017
 */

public class CryptoCompareManager {

    private static MutableLiveData<Map<String, Price>> prices = new MutableLiveData<>();

    public CryptoCompareManager() {
    }

    public LiveData<Map<String, Price>> getCurrentPricesData(List<String> currencyCodes) {
        getCurrentPrices(currencyCodes);
        return prices;
    }

    /**
     * Get current prices from CryptoCompare public API
     */
    public void getCurrentPrices(List<String> currencyCodes) {
        String currencies = TextUtils.join(",", currencyCodes);
        // API asynchronous call
        CryptoCompareService cryptoCompareService = CryptoCompareService.retrofit.create(CryptoCompareService.class);
        Call<Map<String, Price>> call = cryptoCompareService.getCurrentPrices(currencies);
        //rest service call runs on background thread and Callback also runs on background thread
        call.enqueue(new Callback<Map<String, Price>>() {
            @Override
            public void onResponse(@NonNull Call<Map<String, Price>> call, @NonNull Response<Map<String, Price>> response) {
                Map<String, Price> resp = response.body();
                prices.postValue(resp);
            }

            @Override
            public void onFailure(@NonNull Call<Map<String, Price>> call, @NonNull Throwable t) {
            }
        });
    }

}
