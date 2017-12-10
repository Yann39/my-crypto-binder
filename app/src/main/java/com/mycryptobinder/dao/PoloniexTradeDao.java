package com.mycryptobinder.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.mycryptobinder.entities.PoloniexTrade;

import java.util.List;

/**
 * Created by Yann
 * Created on 06/11/2017
 */

@Dao
public interface PoloniexTradeDao {

    @Query("select * from poloniex_trades")
    LiveData<List<PoloniexTrade>> getAll();

    @Query("select distinct global_trade_id from poloniex_trades")
    List<Long> getTradeIds();

    @Insert
    void insert(PoloniexTrade... poloniexTrades);

    @Delete
    void delete(PoloniexTrade poloniexTrade);

    @Query("delete from poloniex_trades")
    void deleteAll();

}