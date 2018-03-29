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

package com.mycryptobinder.managers;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.widget.Toast;

import com.mycryptobinder.R;
import com.mycryptobinder.models.HistoDayPrices;
import com.mycryptobinder.models.PricesFull;
import com.mycryptobinder.services.CryptoCompareService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CryptoCompareManager {

    private static MutableLiveData<PricesFull> pricesFull = new MutableLiveData<>();
    private static MutableLiveData<HistoDayPrices> historicalDayPrices = new MutableLiveData<>();
    private final Context context;

    public CryptoCompareManager(Context context) {
        this.context = context;
    }

    public LiveData<PricesFull> getCurrentPricesFullData(List<String> currencyCodes) {
        getCurrentPricesFull(currencyCodes);
        return pricesFull;
    }

    public LiveData<HistoDayPrices> getHistoricalDayPriceData(String currencyCode, int nbDays) {
        getHistoricalDayPrice(currencyCode, nbDays);
        return historicalDayPrices;
    }

    /**
     * Get current prices from CryptoCompare public API
     *
     * @param currencyCodes List of currency codes to retrieve price (comma separated)
     */
    public void getCurrentPricesFull(List<String> currencyCodes) {
        String currencies = TextUtils.join(",", currencyCodes);
        // API asynchronous call
        CryptoCompareService cryptoCompareService = CryptoCompareService.retrofit.create(CryptoCompareService.class);
        Call<PricesFull> call = cryptoCompareService.getCurrentPricesFull(currencies);
        // REST service call runs on background thread and Callback also runs on background thread
        call.enqueue(new Callback<PricesFull>() {
            @Override
            public void onResponse(@NonNull Call<PricesFull> call, @NonNull Response<PricesFull> response) {
                PricesFull resp = response.body();
                pricesFull.postValue(resp);
            }

            @Override
            public void onFailure(@NonNull Call<PricesFull> call, @NonNull Throwable t) {
                System.out.println(context.getString(R.string.error_retrieving_price_full, t.getLocalizedMessage()));
            }
        });
    }

    /**
     * Get historical day prices from CryptoCompare public API
     *
     * @param currencyCode The currency code
     * @param nbDays The number of days to get data from
     */
    public void getHistoricalDayPrice(String currencyCode, int nbDays) {
        // API asynchronous call
        CryptoCompareService cryptoCompareService = CryptoCompareService.retrofit.create(CryptoCompareService.class);
        Call<HistoDayPrices> call = cryptoCompareService.getHistoricalDayPrice(currencyCode, nbDays);
        // REST service call runs on background thread and Callback also runs on background thread
        call.enqueue(new Callback<HistoDayPrices>() {
            @Override
            public void onResponse(@NonNull Call<HistoDayPrices> call, @NonNull Response<HistoDayPrices> response) {
                HistoDayPrices resp = response.body();
                // if we don't get a success message, do nothing (it may be an API rate limit exceeded)
                if (resp != null && resp.getResponse() != null && !resp.getResponse().equals("Success")) {
                    Toast.makeText(context, context.getString(R.string.error_retrieving_data_from_api, resp.getMessage()), Toast.LENGTH_SHORT).show();
                } else {
                    historicalDayPrices.postValue(resp);
                }
            }

            @Override
            public void onFailure(@NonNull Call<HistoDayPrices> call, @NonNull Throwable t) {
                System.out.println(context.getString(R.string.error_retrieving_historical_day_price, t.getLocalizedMessage()));
            }
        });
    }

}
