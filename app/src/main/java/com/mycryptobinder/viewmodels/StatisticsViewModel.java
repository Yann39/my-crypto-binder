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

import com.mycryptobinder.managers.CryptoCompareManager;
import com.mycryptobinder.models.HistoDayPrices;

import java.util.Objects;

public class StatisticsViewModel extends AndroidViewModel {

    private final LiveData<HistoDayPrices> historicalDayPricesFull;
    private final CryptoCompareManager cryptoCompareManager;
    private final MutableLiveData<DayPriceFilter> dayPriceFilter;

    public StatisticsViewModel(Application application) {
        super(application);
        cryptoCompareManager = new CryptoCompareManager(application.getApplicationContext());
        dayPriceFilter = new MutableLiveData<>();
        historicalDayPricesFull = Transformations.switchMap(dayPriceFilter, input -> cryptoCompareManager.getHistoricalDayPriceData(input.code, input.nbDays));
    }

    public LiveData<HistoDayPrices> getHistoricalDayPrice() {
        return historicalDayPricesFull;
    }

    public void setDayPriceFilter(String code, int nbDays) {
        DayPriceFilter update = new DayPriceFilter(code, nbDays);
        if (Objects.equals(dayPriceFilter.getValue(), update)) {
            return;
        }
        dayPriceFilter.setValue(update);
    }

    static class DayPriceFilter {
        final String code;
        final int nbDays;

        DayPriceFilter(String code, int nbDays) {
            this.code = code == null ? null : code.trim();
            this.nbDays = nbDays;
        }
    }

}