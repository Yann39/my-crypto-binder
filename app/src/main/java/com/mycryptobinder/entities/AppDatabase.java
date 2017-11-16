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
import com.mycryptobinder.dao.TransactionDao;
import com.mycryptobinder.helpers.DateTypeConverter;

/**
 * Created by Yann
 * Created on 06/11/2017
 */

@Database(entities = {AppSetting.class, KrakenAsset.class, KrakenAssetPair.class, KrakenTrade.class, Currency.class, Exchange.class, Ico.class, Transaction.class}, version = 2)
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
