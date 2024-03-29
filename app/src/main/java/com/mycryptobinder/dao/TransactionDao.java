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

@Dao
public interface TransactionDao {

    @Query("select * from transactions")
    LiveData<List<Transaction>> getAll();

    @Query("select currency1_iso_code from transactions union select currency2_iso_code " +
            "from transactions")
    LiveData<List<String>> getDifferentCurrencies();

    @Query("select null as id, 'Kraken' as exchange_name, kt.order_tx_id as transaction_id, " +
            "ka1.alt_name as currency1_iso_code, ka2.alt_name as currency2_iso_code, kt.fee, " +
            "kt.time as date, kt.type, kt.vol as quantity, kt.price, kt.cost as total, " +
            "null as comment " +
            "from kraken_trades kt " +
            "inner join kraken_asset_pairs kap on kt.pair = kap.asset_pair " +
            "inner join kraken_assets ka1 on kap.base = ka1.asset_name " +
            "inner join kraken_assets ka2 on kap.quote = ka2.asset_name")
    List<Transaction> getKrakenTransactions();

    @Query("select null as id, 'Poloniex' as exchange_name, pt.global_trade_id as transaction_id, " +
            "replace(pt.pair, rtrim(pt.pair, replace(pt.pair, '_', '' )), '') as currency1_iso_code, " +
            "replace(pt.pair, ltrim(pt.pair, replace(pt.pair, '_', '' )), '') as currency2_iso_code, " +
            "pt.fee, pt.date, pt.type, pt.amount as quantity, pt.rate as price, pt.total, " +
            "null as comment " +
            "from poloniex_trades pt " +
            "union all " +
            "select null as id, 'Poloniex' as exchange_name, pd.tx_id as transaction_id, " +
            "pd.currency as currency1_iso_code, null as currency2_iso_code, " +
            "null as fee, pd.timestamp as date, 'deposit' as type, pd.amount as quantity, null as price, null as total, " +
            "null as comment " +
            "from poloniex_deposits pd " +
            "union all " +
            "select null as id, 'Poloniex' as exchange_name, pw.withdrawal_number as transaction_id, " +
            "null as currency1_iso_code, pw.currency as currency2_iso_code, " +
            "null as fee, pw.timestamp as date, 'withdrawal' as type, pw.amount as quantity, null as price, null as total, " +
            "null as comment " +
            "from poloniex_withdrawals pw")
    List<Transaction> getPoloniexTransactions();

    @Query("select null as id, 'Bittrex' as exchange_name, bt.order_uuid as transaction_id, " +
            "replace(bt.exchange, rtrim(bt.exchange, replace(bt.exchange, '-', '' )), '') as currency1_iso_code, " +
            "replace(bt.exchange, ltrim(bt.exchange, replace(bt.exchange, '-', '' )), '') as currency2_iso_code, " +
            "bt.commission as fee, bt.timestamp as date, bt.order_type as type, bt.quantity, bt.price_per_unit as price, bt.price as total, " +
            "null as comment " +
            "from bittrex_trades bt " +
            "union all " +
            "select null as id, 'Bittrex' as exchange_name, bd.deposit_id as transaction_id, " +
            "bd.currency as currency1_iso_code, null as currency2_iso_code, " +
            "null as fee, bd.last_updated as date, 'deposit' as type, bd.amount as quantity, null as price, null as total, " +
            "null as comment " +
            "from bittrex_deposits bd " +
            "union all " +
            "select null as id, 'Bittrex' as exchange_name, bw.payment_uuid as transaction_id, " +
            "null as currency1_iso_code, bw.currency as currency2_iso_code, " +
            "null as fee, bw.opened as date, 'withdrawal' as type, bw.amount as quantity, null as price, null as total, " +
            "null as comment " +
            "from bittrex_withdrawals bw")
    List<Transaction> getBittrexTransactions();

    @Query("select null as id, 'Bitfinex' as exchange_name, bo.order_id as transaction_id, " +
            "substr(bo.symbol, 1, 3) as currency1_iso_code, " +
            "substr(bo.symbol, 4, 3) as currency2_iso_code, " +
            "null as fee, bo.timestamp as date, bo.type, null as quantity, bo.price, null as total, " +
            "null as comment " +
            "from bitfinex_orders bo ")
    List<Transaction> getBitfinexTransactions();

    @Query("select sum(total) from ( " +
            "select total * case when type = 'buy' then 1 else -1 end as Total " +
            "from transactions where currency1_iso_code = :currencyCode " +
            "union all " +
            "select total * case when type = 'buy' then -1 else 1 end as Total " +
            "from transactions where currency2_iso_code = :currencyCode " +
            ") as T")
    Double getCurrencyTotal(String currencyCode);

    @Query("select sum(total) from ( " +
            "select sum(quantity * case when type = 'buy' then 1 else -1 end) as Total " +
            "from transactions where currency1_iso_code = :currencyCode " +
            "union all " +
            "select sum(total * case when type = 'buy' then -1 else 1 end) as Total " +
            "from transactions where currency2_iso_code = :currencyCode " +
            ") as T")
    Double getCurrencyQuantity(String currencyCode);

    @Query("select iso_code as isoCode, name, symbol, " +
            "(select sum(total) from ( " +
            "select sum(quantity * case when type = 'buy' then 1 else -1 end) as Total " +
            "from transactions where currency1_iso_code = c.iso_code " +
            "union all " +
            "select sum(total * case when type = 'buy' then -1 else 1 end) as Total " +
            "from transactions where currency2_iso_code = c.iso_code " +
            ") as T) as quantity " +
            "from currencies c " +
            "where quantity is not null")
    LiveData<List<HoldingData>> getHoldings();

    @Insert
    void insert(Transaction... transactions);

    @Update
    void update(Transaction... transactions);

    @Delete
    void delete(Transaction transaction);

    @Query("delete from transactions")
    void deleteAll();

}