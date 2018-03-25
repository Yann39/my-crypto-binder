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

import com.mycryptobinder.R;
import com.mycryptobinder.models.PricesFull;
import com.mycryptobinder.services.CryptoCompareService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CryptoCompareManager {

    private static MutableLiveData<PricesFull> pricesFull = new MutableLiveData<>();
    private final Context context;

    public CryptoCompareManager(Context context) {
        this.context = context;
    }

    public LiveData<PricesFull> getCurrentPricesFullData(List<String> currencyCodes) {
        getCurrentPricesFull(currencyCodes);
        return pricesFull;
    }

    /**
     * Get current prices from CryptoCompare public API
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
                System.out.println(context.getString(R.string.success_app_setting_created, t.getLocalizedMessage()));
            }
        });
    }

}
