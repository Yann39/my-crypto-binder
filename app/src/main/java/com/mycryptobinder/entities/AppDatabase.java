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

package com.mycryptobinder.entities;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

import com.mycryptobinder.dao.AppSettingDao;
import com.mycryptobinder.dao.CurrencyDao;
import com.mycryptobinder.dao.ExchangeDao;
import com.mycryptobinder.dao.IcoDao;
import com.mycryptobinder.dao.KrakenAssetDao;
import com.mycryptobinder.dao.KrakenAssetPairDao;
import com.mycryptobinder.dao.KrakenTradeDao;
import com.mycryptobinder.dao.PoloniexAssetDao;
import com.mycryptobinder.dao.PoloniexDepositDao;
import com.mycryptobinder.dao.PoloniexTradeDao;
import com.mycryptobinder.dao.PoloniexWithdrawalDao;
import com.mycryptobinder.dao.TransactionDao;
import com.mycryptobinder.helpers.DateTypeConverter;

@Database(entities = {
        AppSetting.class,
        KrakenAsset.class,
        KrakenAssetPair.class,
        KrakenTrade.class,
        PoloniexAsset.class,
        PoloniexTrade.class,
        PoloniexDeposit.class,
        PoloniexWithdrawal.class,
        Currency.class,
        Exchange.class,
        Ico.class,
        Transaction.class}, version = 11)
@TypeConverters({DateTypeConverter.class})
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase INSTANCE;

    public abstract AppSettingDao settingsDao();

    public abstract KrakenAssetDao krakenAssetDao();

    public abstract KrakenAssetPairDao krakenAssetPairDao();

    public abstract KrakenTradeDao krakenTradeDao();

    public abstract CurrencyDao currencyDao();

    public abstract ExchangeDao exchangeDao();

    public abstract IcoDao icoDao();

    public abstract TransactionDao transactionDao();

    public abstract PoloniexAssetDao poloniexAssetDao();

    public abstract PoloniexTradeDao poloniexTradeDao();

    public abstract PoloniexDepositDao poloniexDepositDao();

    public abstract PoloniexWithdrawalDao poloniexWithdrawalDao();

    public static AppDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "crytodatabase").fallbackToDestructiveMigration().build();
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

}
