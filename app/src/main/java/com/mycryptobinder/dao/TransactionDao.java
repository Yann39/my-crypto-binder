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

    @Query("select count(1) from (select distinct currency1_iso_code from transactions " +
            "union all select distinct currency2_iso_code from transactions) t")
    LiveData<Integer> getNbDifferentCurrencies();

    @Query("select null as id, 'Kraken' as exchange_name, kt.order_tx_id as transaction_id, " +
            "ka1.alt_name as currency1_iso_code, ka2.alt_name as currency2_iso_code, kt.fee, " +
            "kt.time as date, kt.type, kt.vol as quantity, kt.price, kt.cost as total, " +
            "null as sum_currency1, null as sum_currency2, null as comment " +
            "from kraken_trades kt " +
            "inner join kraken_asset_pairs kap on kt.pair = kap.asset_pair " +
            "inner join kraken_assets ka1 on kap.base = ka1.asset_name " +
            "inner join kraken_assets ka2 on kap.quote = ka2.asset_name")
    List<Transaction> getKrakenTransactions();

    @Query("select null as id, 'Poloniex' as exchange_name, pt.global_trade_id as transaction_id, " +
            "replace(pt.pair, rtrim(pt.pair, replace(pt.pair, '_', '' )), '') as currency1_iso_code, " +
            "replace(pt.pair, ltrim(pt.pair, replace(pt.pair, '_', '' )), '') as currency2_iso_code, " +
            "pt.fee, pt.date, pt.type, pt.amount as quantity, pt.rate as price, pt.total, " +
            "null as sum_currency1, null as sum_currency2, null as comment " +
            "from poloniex_trades pt ")
    List<Transaction> getPoloniexTransactions();

    @Query("select sum(total) from ( " +
            "select total * case when type = 'buy' then 1 else -1 end as Total " +
            "from transactions where currency1_iso_code = :currencyCode " +
            "union all " +
            "select total * case when type = 'buy' then -1 else 1 end as Total " +
            "from transactions where currency2_iso_code = :currencyCode " +
            ") as T ")
    Double getCurrencyTotal(String currencyCode);

    @Query("select sum(total) from ( " +
            "select sum(quantity * case when type = 'buy' then 1 else -1 end) as Total " +
            "from transactions where currency1_iso_code = :currencyCode " +
            "union all " +
            "select sum(total * case when type = 'buy' then -1 else 1 end) as Total " +
            "from transactions where currency2_iso_code = :currencyCode " +
            ") as T ")
    Double getCurrencyQuantity(String currencyCode);

    @Query("select c.iso_code as isoCode, c.name, c.symbol, 1.0 as quantity, 2.0 as currentPrice, 3.0 as currentValue " +
            "from currencies c " +
            "inner join transactions t1 on c.iso_code = t1.currency1_iso_code " +
            "inner join transactions t2 on c.iso_code = t2.currency2_iso_code ")
    LiveData<List<HoldingData>> getHoldings();

    @Insert
    void insert(Transaction... transactions);

    @Update
    void update(Transaction... transactions);

    @Delete
    void delete(Transaction transaction);

}