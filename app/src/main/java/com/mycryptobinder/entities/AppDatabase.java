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

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;
import android.support.annotation.NonNull;

import com.mycryptobinder.dao.AppSettingDao;
import com.mycryptobinder.dao.CurrencyDao;
import com.mycryptobinder.dao.ExchangeDao;
import com.mycryptobinder.dao.IcoDao;
import com.mycryptobinder.dao.TransactionDao;
import com.mycryptobinder.dao.bitfinex.BitfinexAssetDao;
import com.mycryptobinder.dao.bitfinex.BitfinexOrderDao;
import com.mycryptobinder.dao.bitfinex.BitfinexSymbolDao;
import com.mycryptobinder.dao.bittrex.BittrexAssetDao;
import com.mycryptobinder.dao.bittrex.BittrexDepositDao;
import com.mycryptobinder.dao.bittrex.BittrexTradeDao;
import com.mycryptobinder.dao.bittrex.BittrexWithdrawalDao;
import com.mycryptobinder.dao.kraken.KrakenAssetDao;
import com.mycryptobinder.dao.kraken.KrakenAssetPairDao;
import com.mycryptobinder.dao.kraken.KrakenTradeDao;
import com.mycryptobinder.dao.poloniex.PoloniexAssetDao;
import com.mycryptobinder.dao.poloniex.PoloniexDepositDao;
import com.mycryptobinder.dao.poloniex.PoloniexTradeDao;
import com.mycryptobinder.dao.poloniex.PoloniexWithdrawalDao;
import com.mycryptobinder.entities.bitfinex.BitfinexAsset;
import com.mycryptobinder.entities.bitfinex.BitfinexOrder;
import com.mycryptobinder.entities.bitfinex.BitfinexSymbol;
import com.mycryptobinder.entities.bittrex.BittrexAsset;
import com.mycryptobinder.entities.bittrex.BittrexDeposit;
import com.mycryptobinder.entities.bittrex.BittrexTrade;
import com.mycryptobinder.entities.bittrex.BittrexWithdrawal;
import com.mycryptobinder.entities.kraken.KrakenAsset;
import com.mycryptobinder.entities.kraken.KrakenAssetPair;
import com.mycryptobinder.entities.kraken.KrakenTrade;
import com.mycryptobinder.entities.poloniex.PoloniexAsset;
import com.mycryptobinder.entities.poloniex.PoloniexDeposit;
import com.mycryptobinder.entities.poloniex.PoloniexTrade;
import com.mycryptobinder.entities.poloniex.PoloniexWithdrawal;
import com.mycryptobinder.helpers.DateTypeConverter;
import com.mycryptobinder.helpers.UtilsHelper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.Executors;

@Database(entities = {
        AppSetting.class,
        KrakenAsset.class,
        KrakenAssetPair.class,
        KrakenTrade.class,
        PoloniexAsset.class,
        PoloniexTrade.class,
        PoloniexDeposit.class,
        PoloniexWithdrawal.class,
        BittrexAsset.class,
        BittrexTrade.class,
        BittrexDeposit.class,
        BittrexWithdrawal.class,
        BitfinexAsset.class,
        BitfinexSymbol.class,
        BitfinexOrder.class,
        Currency.class,
        Exchange.class,
        Ico.class,
        Transaction.class}, version = 19, exportSchema = false)
@TypeConverters({DateTypeConverter.class})
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase INSTANCE;

    public abstract AppSettingDao settingsDao();

    public abstract CurrencyDao currencyDao();

    public abstract ExchangeDao exchangeDao();

    public abstract IcoDao icoDao();

    public abstract TransactionDao transactionDao();

    //region Kraken
    public abstract KrakenAssetDao krakenAssetDao();

    public abstract KrakenAssetPairDao krakenAssetPairDao();

    public abstract KrakenTradeDao krakenTradeDao();
    //endregion

    //region Poloniex
    public abstract PoloniexAssetDao poloniexAssetDao();

    public abstract PoloniexTradeDao poloniexTradeDao();

    public abstract PoloniexDepositDao poloniexDepositDao();

    public abstract PoloniexWithdrawalDao poloniexWithdrawalDao();
    //endregion

    //region Bittrex
    public abstract BittrexAssetDao bittrexAssetDao();

    public abstract BittrexTradeDao bittrexTradeDao();

    public abstract BittrexDepositDao bittrexDepositDao();

    public abstract BittrexWithdrawalDao bittrexWithdrawalDao();
    //endregion

    //region Bitfinex
    public abstract BitfinexAssetDao bitfinexAssetDao();

    public abstract BitfinexSymbolDao bitfinexSymbolDao();

    public abstract BitfinexOrderDao bitfinexOrderDao();
    //endregion

    public synchronized static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = buildDatabase(context);
        }
        return INSTANCE;
    }

    private static AppDatabase buildDatabase(final Context context) {
        return Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "cryptodatabase").fallbackToDestructiveMigration().addCallback(new Callback() {
            @Override
            public void onOpen(@NonNull SupportSQLiteDatabase db) {
                super.onOpen(db);
                Executors.newSingleThreadScheduledExecutor().execute(() -> {
                    AppDatabase appDb = getInstance(context);

                    // initialize database with exchanges if they does not exist
                    if (appDb.exchangeDao().getByName("Kraken") == null) {
                        Exchange krakenExchange = new Exchange("Kraken", "https://www.kraken.com", "Kraken exchange", null, null);
                        appDb.exchangeDao().insert(krakenExchange);
                    }
                    if (appDb.exchangeDao().getByName("Poloniex") == null) {
                        Exchange poloniexExchange = new Exchange("Poloniex", "https://poloniex.com", "Poloniex exchange", null, null);
                        appDb.exchangeDao().insert(poloniexExchange);
                    }
                    if (appDb.exchangeDao().getByName("Bittrex") == null) {
                        Exchange bittrexExchange = new Exchange("Bittrex", "https://bittrex.com", "Bittrex exchange", null, null);
                        appDb.exchangeDao().insert(bittrexExchange);
                    }
                    if (appDb.exchangeDao().getByName("Bitfinex") == null) {
                        Exchange bitfinexExchange = new Exchange("Bitfinex", "https://www.bitfinex.com", "Bitfinex exchange", null, null);
                        appDb.exchangeDao().insert(bitfinexExchange);
                    }
                });
            }
        }).build();
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

}
