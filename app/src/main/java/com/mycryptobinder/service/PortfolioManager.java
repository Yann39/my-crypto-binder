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

package com.mycryptobinder.service;

import android.content.Context;

import com.mycryptobinder.entities.AppDatabase;

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
