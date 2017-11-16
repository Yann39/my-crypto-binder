package com.mycryptobinder.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.mycryptobinder.entities.Transaction;
import com.mycryptobinder.models.HoldingData;

import java.util.List;

/**
 * Created by Yann
 * Created on 06/11/2017
 */

@Dao
public interface TransactionDao {

    @Query("select * from transactions")
    LiveData<List<Transaction>> getAll();

    @Query("select count(1) from (select distinct currency1_iso_code from transactions union all select distinct currency2_iso_code from transactions) t")
    int getNbDifferentCurrencies();

    @Query("select 'Kraken', kt.order_tx_id, ka1.alt_name, ka2.alt_name, kt.fee, kt.time, kt.type, kt.vol, kt.price, kt.cost, null " +
            "from kraken_trades kt " +
            "inner join kraken_asset_pairs kap on kt.pair = kap.asset_pair " +
            "inner join kraken_assets ka1 on kap.base = ka1.asset_name " +
            "inner join kraken_assets ka2 on kap.quote = ka2.asset_name")
    LiveData<List<Transaction>> getKrakenTransactions();

    @Query("select 'Poloniex', pt.global_trade_id, " +
            "replace(pt.pair, rtrim(pt.pair, replace(pt.pair, '_', '' )), ''), " +
            "replace(pt.pair, ltrim(pt.pair, replace(pt.pair, '_', '' )), ''), " +
            "pt.fee, pt.date, pt.type, pt.amount, pt.rate, pt.total, null " +
            "from poloniex_trades pt ")
    LiveData<List<Transaction>> getPoloniexTransactions();

    @Query("select sum(total) from ( " +
            "select total * case when type = 'buy' then 1 else -1 end as Total from transactions where currency1_iso_code = :currencyCode " +
            "union all " +
            "select total * case when type = 'buy' then -1 else 1 end as Total from transactions where currency2_iso_code = :currencyCode " +
            ") as T ")
    Double getCurrencyTotal(String currencyCode);

    @Query("select sum(total) from ( " +
            "select sum(quantity * case when type = 'buy' then 1 else -1 end) as Total from transactions where currency1_iso_code = :currencyCode " +
            "union all " +
            "select sum(total * case when type = 'buy' then -1 else 1 end) as Total from transactions where currency2_iso_code = :currencyCode " +
            ") as T ")
    Double getCurrencyQuantity(String currencyCode);

    @Query("select c.iso_code, c.name, c.symbol, 'quantity', 'currentPrice', 'currentValue' " +
            "from currencies c " +
            "inner join transactions t1 on c.iso_code = t1.currency1_iso_code " +
            "inner join transactions t2 on c.iso_code = t2.currency2_iso_code " +
            "where iso_code = :isoCode")
    LiveData<List<HoldingData>> getHoldings();

    @Insert
    void insert(Transaction... transactions);

    @Update
    void update(Transaction... transactions);

    @Delete
    void delete(Transaction transaction);

}